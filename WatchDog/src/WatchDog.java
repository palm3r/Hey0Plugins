import java.io.*;
import java.lang.reflect.*;
import java.sql.Connection;
import java.util.*;
import java.util.Map.*;
import java.util.regex.*;
import org.apache.commons.lang.*;
import org.h2.jdbcx.JdbcDataSource;

public class WatchDog extends PluginEx {

	private static final String WATCHDOG_INI = "watchdog.ini";

	private static final Pattern configPattern =
		Pattern.compile("^\\s*([^\\/]+)\\/([^\\/]+)\\/([^-\\+=\\s]+)\\s*([-\\+]?=)\\s*(.*)$");

	private static WatchDog plugin = null;
	private Connection connection = null;
	private final Map<Pair<Integer, WatchDogEvent>, WatchDogHandler> handlers =
		new HashMap<Pair<Integer, WatchDogEvent>, WatchDogHandler>();

	public WatchDog() {
		WatchDogListener listener = new WatchDogListener(this);
		addHook(PluginLoader.Hook.TELEPORT, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DISCONNECT, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.BLOCK_DESTROYED, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.BLOCK_BROKEN, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.ITEM_DROP, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.ITEM_PICK_UP, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DAMAGE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.ITEM_USE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.BLOCK_PLACE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.BLOCK_RIGHTCLICKED, PluginListener.Priority.MEDIUM, listener);
		addCommand(new WatchDogCommand());

		plugin = this;
	}

	@Override
	protected void onEnable() {
		try {
			Class.forName("org.h2.Driver");
			String url = String.format("jdbc:h2:file:%s", getAbsolutePath("log").replace('\\', '/'));
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL(url);
			// ds.setUser(name);
			// ds.setPassword(name);
			connection = ds.getConnection();
			DataSet.connect(connection, Log.class, getLogger());
		} catch (Exception e) {
			e.printStackTrace();
		}

		String watchDogIni = getRelatedPath(WATCHDOG_INI);
		try {
			handlers.clear();
			BufferedReader br = new BufferedReader(new FileReader(watchDogIni));
			String line;
			for (int index = 1; (line = br.readLine()) != null; ++index) {
				if (line.trim().isEmpty() || line.trim().startsWith("#")) {
					continue;
				}
				if (!parse(line)) {
					warn("parse error: %s (%s:%d)", line, watchDogIni, index);
				}
			}
			br.close();
		} catch (IOException e) {
			File file = new File(watchDogIni);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDisable() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}

	public WatchDogHandler getHandler(int itemId, WatchDogEvent event) {
		Pair<Integer, WatchDogEvent> key = Pair.create(itemId, event);
		return handlers.containsKey(key) ? handlers.get(key) : null;
	}

	public boolean parse(String line) {
		boolean updated = false;
		Matcher m = configPattern.matcher(line);
		if (m.matches()) {
			// items/player
			Map<Integer, String> targets = new LinkedHashMap<Integer, String>();
			for (String target : StringUtils.split(m.group(1), ",")) {
				target = target.trim();
				if (target.equalsIgnoreCase("player")) {
					targets.put(-1, "player");
				} else {
					Pattern p2 = Pattern.compile(target.replaceAll("\\*", ".*"), Pattern.CASE_INSENSITIVE);
					for (Entry<Integer, String> entry : ItemNames.all().entrySet()) {
						if (p2.matcher(entry.getKey().toString()).matches() || p2.matcher(entry.getValue()).matches()) {
							targets.put(entry.getKey(), entry.getValue());
						}
					}
				}
			}

			// events
			Set<WatchDogEvent> events = new LinkedHashSet<WatchDogEvent>();
			for (String eventName : StringUtils.split(m.group(2), ",")) {
				eventName = eventName.trim();
				Pattern pattern = Pattern.compile(eventName.replaceAll("\\*", ".*"), Pattern.CASE_INSENSITIVE);
				for (Field field : WatchDogEvent.class.getFields()) {
					if (pattern.matcher(field.getName()).matches()) {
						field.setAccessible(true);
						try {
							WatchDogEvent event = (WatchDogEvent) field.get(null);
							events.add(event);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			// properties
			Set<String> keys = new LinkedHashSet<String>();
			for (String key : StringUtils.split(m.group(3), ",")) {
				keys.add(key.trim());
			}

			// op
			String op = m.group(4);

			// groups
			Set<String> values = new LinkedHashSet<String>();
			for (String value : StringUtils.split(m.group(5), ",")) {
				values.add(value.trim());
			}

			//
			for (Entry<Integer, String> target : targets.entrySet()) {
				for (WatchDogEvent event : events) {
					Pair<Integer, WatchDogEvent> action = Pair.create(target.getKey(), event);
					WatchDogHandler handler = handlers.containsKey(action) ? handlers.get(action) : new WatchDogHandler();
					for (String key : keys) {
						handler.set(key, op, values);
						plugin.info("%s/%s/%s = %s", target.getValue(), event.toString().toLowerCase(), key, handler.get(key));
						handlers.put(action, handler);
						updated = true;
					}
				}
			}
		}
		return updated;
	}

}
