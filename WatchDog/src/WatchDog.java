import java.io.*;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.*;
import java.util.regex.*;
import org.apache.commons.lang.*;

public class WatchDog extends PluginEx {

	public static final String TABLE = "log";

	private static final String WATCHDOG_INI = "watchdog.ini";
	private static final Pattern pattern =
		Pattern.compile("^\\s*([^\\/]+)\\/([^\\/]+)\\/([^-\\+=\\s]+)\\s*([-\\+]?=)\\s*(.*)$");

	private static Map<Pair<Integer, Event>, Handler> handlers =
		new HashMap<Pair<Integer, Event>, Handler>();
	private static Map<String, String> attdef = new HashMap<String, String>();
	private static Connection conn = null;

	@SuppressWarnings("serial")
	private Map<PluginLoader.Hook, PluginListener.Priority> hooks =
		new HashMap<PluginLoader.Hook, PluginListener.Priority>() {
			{
				put(PluginLoader.Hook.BLOCK_BROKEN, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.BLOCK_DESTROYED, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.BLOCK_PLACE, PluginListener.Priority.MEDIUM);
				// put(PluginLoader.Hook.COMPLEX_BLOCK_CHANGE,
				// PluginListener.Priority.MEDIUM);
				// put(PluginLoader.Hook.COMPLEX_BLOCK_SEND,
				// PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.ITEM_DROP, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.ITEM_PICK_UP, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.ITEM_USE, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.LOGIN, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.DISCONNECT, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.DAMAGE, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.HEALTH_CHANGE, PluginListener.Priority.MEDIUM);
				put(PluginLoader.Hook.TELEPORT, PluginListener.Priority.MEDIUM);
			}
		};

	private PluginListener listener = new PluginListener() {

		@Override
		public boolean onBlockBreak(Player player, Block block) {
			boolean denied = false;
			Handler handler = getHandler(block.getType(), Event.DESTROY);
			if (handler != null) {
				denied = handler.execute(Event.DESTROY, player, block);
			}
			return denied;
		}

		@Override
		public boolean onBlockDestroy(Player player, Block block) {
			boolean denied = false;
			Handler handler = getHandler(block.getType(), Event.DESTROY);
			if (handler != null) {
				denied = handler.execute(Event.DESTROY, player, block);
			}
			return denied;
		}

		@Override
		public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
			boolean denied = false;
			Handler handler = getHandler(blockPlaced.getType(), Event.PLACE);
			if (handler != null) {
				denied = handler.execute(Event.PLACE, player, blockPlaced);
			}
			return denied;
		}

		@Override
		public boolean onComplexBlockChange(Player player, ComplexBlock cb) {
			boolean denied = false;
			Block block = etc.getServer().getBlockAt(cb.getX(), cb.getY(), cb.getZ());
			if (block != null) {
				Handler handler = getHandler(block.getType(), Event.USE);
				if (handler != null) {
					denied = handler.execute(Event.USE, player, block);
				}
			}
			return denied;
		}

		@Override
		public boolean onSendComplexBlock(Player player, ComplexBlock cb) {
			boolean denied = false;
			Block block = etc.getServer().getBlockAt(cb.getX(), cb.getY(), cb.getZ());
			if (block != null) {
				Handler handler = getHandler(block.getType(), Event.USE);
				if (handler != null) {
					denied = handler.execute(Event.USE, player, block);
				}
			}
			return denied;
		}

		@Override
		public boolean onItemDrop(Player player, Item item) {
			boolean denied = false;
			Handler handler = getHandler(item.getItemId(), Event.DROP);
			if (handler != null) {
				denied = handler.execute(Event.DROP, player, item);
			}
			return denied;
		}

		@Override
		public boolean onItemPickUp(Player player, Item item) {
			boolean denied = false;
			Handler handler = getHandler(item.getItemId(), Event.PICKUP);
			if (handler != null) {
				denied = handler.execute(Event.PICKUP, player, item);
			}
			return denied;
		}

		@Override
		public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
			boolean denied = false;
			Handler handler = getHandler(item.getItemId(), Event.USE);
			if (handler != null) {
				denied = handler.execute(Event.USE, player, item);
			}
			return denied;
		}

		@Override
		public void onLogin(Player player) {
			Handler handler = getHandler(-1, Event.LOGIN);
			if (handler != null) {
				handler.execute(Event.LOGIN, player);
			}
		}

		@Override
		public void onDisconnect(Player player) {
			Handler handler = getHandler(-1, Event.LOGOUT);
			if (handler != null) {
				handler.execute(Event.LOGOUT, player);
			}
		}

		@Override
		public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
			BaseEntity defender, int amount) {
			boolean denied = false;
			Handler handler = getHandler(-1, Event.ATTACK);
			if (handler != null) {
				if (attacker != null && defender != null && attacker.isPlayer()
					&& defender.isPlayer()) {
					Player att = attacker.getPlayer();
					Player def = defender.getPlayer();
					if (att != null && def != null) {
						attdef.put(def.getName(), att.getName());
						denied = handler.execute(Event.ATTACK, att, def);
					}
				}
			}
			return denied;
		}

		@Override
		public boolean onHealthChange(Player player, int oldValue, int newValue) {
			boolean denied = false;
			Handler handler = getHandler(-1, Event.KILL);
			if (handler != null) {
				String defName = player.getName();
				if (newValue <= 0 && attdef.containsKey(defName)) {
					String attName = attdef.remove(defName);
					Player att = etc.getServer().getPlayer(attName);
					if (att != null) {
						denied = handler.execute(Event.KILL, att, player);
					}
				}
			}
			return denied;
		}

		@Override
		public boolean onTeleport(Player player, Location from, Location to) {
			boolean denied = false;
			Handler handler = getHandler(-1, Event.TELEPORT);
			if (handler != null) {
				denied = handler.execute(Event.TELEPORT, player, null, to);
			}
			return denied;
		}

	};

	private static WatchDog plugin = null;
	private Command wd = new WdCommand();

	public WatchDog() {
		plugin = this;
	}

	protected void onEnable() {
		try {
			conn = openDatabase("log", "sa", "sa");
			Statement stmt = conn.createStatement();
			stmt.execute(String.format("CREATE TABLE IF NOT EXISTS %s"
				+ " (id INT AUTO_INCREMENT PRIMARY KEY, time BIGINT NOT NULL"
				+ ", event VARCHAR NOT NULL, player VARCHAR NOT NULL, target VARCHAR"
				+ ", x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL"
				+ ", denied BOOLEAN DEFAULT FALSE NOT NULL"
				+ ", kicked BOOLEAN DEFAULT FALSE NOT NULL"
				+ ", banned BOOLEAN DEFAULT FALSE NOT NULL)", TABLE));
			stmt.execute(String.format(
				"CREATE INDEX IF NOT EXISTS time_idx ON %s (time);", TABLE));
			stmt.execute(String.format(
				"CREATE INDEX IF NOT EXISTS event_idx ON %s (event, player, target);",
				TABLE));
			stmt.execute(String.format(
				"CREATE INDEX IF NOT EXISTS location_idx ON %s (x, y, z);", TABLE));
			stmt.execute(String.format(
				"CREATE INDEX IF NOT EXISTS action_idx ON %s (denied, kicked, banned);",
				TABLE));

			Record.INSERT =
				conn.prepareStatement(String.format("INSERT INTO %s"
					+ " (time, event, player, target, x, y, z, denied, kicked, banned)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?);", TABLE),
					PreparedStatement.RETURN_GENERATED_KEYS);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String watchDogIni = getRelativePath(WATCHDOG_INI);
		try {
			BufferedReader br = new BufferedReader(new FileReader(watchDogIni));
			String line;
			for (int index = 1; (line = br.readLine()) != null; ++index) {
				if (line.trim().isEmpty() || line.trim().startsWith("#"))
					continue;
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

		addCommand(wd);
		for (Entry<PluginLoader.Hook, PluginListener.Priority> entry : hooks.entrySet()) {
			addHook(entry.getKey(), entry.getValue(), listener);
		}
	}

	protected void onDisable() {
		for (Entry<PluginLoader.Hook, PluginListener.Priority> entry : hooks.entrySet()) {
			removeHook(entry.getKey());
		}
		removeCommand(wd);

		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}

	public static boolean parse(String line) {
		boolean updated = false;
		Matcher m = pattern.matcher(line);
		if (m.matches()) {
			// items/player
			Map<Integer, String> targets = new LinkedHashMap<Integer, String>();
			for (String target : StringUtils.split(m.group(1), ",")) {
				target = target.trim();
				if (target.equalsIgnoreCase("player")) {
					targets.put(-1, "player");
				} else {
					Pattern p2 =
						Pattern.compile(target.replaceAll("\\*", ".*"),
							Pattern.CASE_INSENSITIVE);
					for (Entry<Integer, String> entry : ItemNames.all().entrySet()) {
						if (p2.matcher(entry.getKey().toString()).matches()
							|| p2.matcher(entry.getValue()).matches()) {
							targets.put(entry.getKey(), entry.getValue());
						}
					}
				}
			}

			// events
			Set<Event> events = new LinkedHashSet<Event>();
			for (String eventName : StringUtils.split(m.group(2), ",")) {
				eventName = eventName.trim();
				Pattern pattern =
					Pattern.compile(eventName.replaceAll("\\*", ".*"),
						Pattern.CASE_INSENSITIVE);
				for (Field field : Event.class.getFields()) {
					if (pattern.matcher(field.getName()).matches()) {
						field.setAccessible(true);
						try {
							Event event = (Event) field.get(null);
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
				for (Event event : events) {
					Pair<Integer, Event> action = Pair.create(target.getKey(), event);
					Handler handler =
						handlers.containsKey(action) ? handlers.get(action) : new Handler();
					for (String key : keys) {
						handler.set(key, op, values);
						plugin.info("%s/%s/%s = %s", target.getValue(),
							event.toString().toLowerCase(), key, handler.get(key));
						handlers.put(action, handler);
						updated = true;
					}
				}
			}
		}
		return updated;
	}

	public static Handler getHandler(int itemId, Event event) {
		Pair<Integer, Event> key = Pair.create(itemId, event);
		return handlers.containsKey(key) ? handlers.get(key) : null;
	}

	public static ResultSet query(String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		// System.out.println("SQL: " + stmt.toString());
		return stmt.executeQuery(sql);
	}

	public static String getColor(Record record) {
		String color = Colors.Yellow;
		if (record.denied)
			color = Colors.Gold;
		if (record.kicked)
			color = Colors.Rose;
		if (record.banned)
			color = Colors.Red;
		return color;
	}

	public static String getMessage(String player, String event, String target,
		Location location, boolean denied, boolean kicked, boolean banned) {
		StringBuilder sb = new StringBuilder();
		sb.append(player);
		sb.append(" ");
		sb.append(event + (target != null ? " " + target : ""));
		// sb.append(String.format(" (%d,%d,%d)", x, y, z));
		if (denied) {
			sb.append(" DENIED");
		}
		if (kicked) {
			sb.append(" KICKED");
		}
		if (banned) {
			sb.append(" BANNED");
		}
		return sb.toString();
	}

}
