import java.io.*;
import java.lang.reflect.*;
import java.sql.Connection;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.apache.commons.lang.*;
import org.h2.jdbcx.JdbcDataSource;

public class WatchDog extends PluginEx {

	private static final String WATCHDOG_INI = "watchdog.ini";

	private static final String IDLEKICK_INTERVAL_KEY = "idlekick-interval";
	private static final String IDLEKICK_INTERVAL_DEFAULT = "0";

	private static final String IDLEKICK_IGNORE_KEY = "idlekick-ignore";
	private static final String IDLEKICK_IGNORE_DEFAULT = "admins,mods";

	private static final Pattern configPattern =
		Pattern.compile("^\\s*([^\\/]+)\\/([^\\/]+)\\/([^-\\+=\\s]+)\\s*([-\\+]?=)\\s*(.*)$");

	private static Map<Pair<Integer, Event>, Handler> handlers;
	private static Map<String, String> attackerMap =
		new HashMap<String, String>();

	private static long idleInterval;
	private static Map<String, Long> idleLastTimes = new HashMap<String, Long>();
	private static Set<String> idleIgnoreGroups = new HashSet<String>();

	public static boolean parse(String line) {
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

	private static void idleUpdate(Player player) {
		if (player != null) {
			for (String group : idleIgnoreGroups) {
				if (!player.isInGroup(group))
					return;
			}
			idleLastTimes.put(player.getName(), System.currentTimeMillis());
		}
	}

	private final PluginListener listener = new PluginListener() {

		@Override
		public boolean onTeleport(Player player, Location from, Location to) {
			boolean denied = false;
			Handler handler = getHandler(-1, Event.TELEPORT);
			if (handler != null) {
				denied =
					handler.execute(Event.TELEPORT, player.getName(), null, null, to);
			}
			return denied;
		}

		@Override
		public void onLogin(Player player) {
			idleUpdate(player);
			Handler handler = getHandler(-1, Event.LOGIN);
			if (handler != null) {
				handler.execute(Event.LOGIN, player.getName(), null, null,
					player.getLocation());
			}
		}

		@Override
		public void onDisconnect(Player player) {
			idleLastTimes.remove(player);

			Handler handler = getHandler(-1, Event.LOGOUT);
			if (handler != null) {
				handler.execute(Event.LOGOUT, player.getName(), null, null,
					player.getLocation());
			}
		}

		@Override
		public boolean onChat(Player player, String msg) {
			idleUpdate(player);
			return false;
		}

		@Override
		public boolean onBlockDestroy(Player player, Block block) {
			idleUpdate(player);
			boolean denied = false;
			Handler handler = getHandler(block.getType(), Event.DESTROY);
			if (handler != null) {
				denied =
					handler.execute(Event.DESTROY, player.getName(), block.getType(),
						ItemNames.getName(block.getType()), new Location(block.getX(),
							block.getY(), block.getZ()));
			}
			return denied;
		}

		@Override
		public boolean onBlockBreak(Player player, Block block) {
			idleUpdate(player);
			boolean denied = false;
			Handler handler = getHandler(block.getType(), Event.DESTROY);
			if (handler != null) {
				denied =
					handler.execute(Event.DESTROY, player.getName(), block.getType(),
						ItemNames.getName(block.getType()), new Location(block.getX(),
							block.getY(), block.getZ()));
			}
			return denied;
		}

		@Override
		public void onArmSwing(Player player) {
			idleUpdate(player);
		}

		@Override
		public boolean onItemDrop(Player player, Item item) {
			idleUpdate(player);
			boolean denied = false;
			Handler handler = getHandler(item.getItemId(), Event.DROP);
			if (handler != null) {
				denied =
					handler.execute(Event.DROP, player.getName(), item.getItemId(),
						ItemNames.getName(item.getItemId()), player.getLocation());
			}
			return denied;
		}

		@Override
		public boolean onItemPickUp(Player player, Item item) {
			boolean denied = false;
			Handler handler = getHandler(item.getItemId(), Event.PICKUP);
			if (handler != null) {
				denied =
					handler.execute(Event.PICKUP, player.getName(), item.getItemId(),
						ItemNames.getName(item.getItemId()), player.getLocation());
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
					denied =
						handler.execute(Event.USE, player.getName(), block.getType(),
							ItemNames.getName(block.getType()), new Location(block.getX(),
								block.getY(), block.getZ()));
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
					denied =
						handler.execute(Event.USE, player.getName(), block.getType(),
							ItemNames.getName(block.getType()), new Location(block.getX(),
								block.getY(), block.getZ()));
				}
			}
			return denied;
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
						attackerMap.put(def.getName(), att.getName());
						denied =
							handler.execute(Event.ATTACK, att.getName(), null, def.getName(),
								def.getLocation());
					}
				}
			}
			return denied;
		}

		@Override
		public boolean onHealthChange(Player player, int oldValue, int newValue) {
			// updateLastActionTime(player);
			boolean denied = false;
			Handler handler = getHandler(-1, Event.KILL);
			if (handler != null) {
				String defName = player.getName();
				if (newValue <= 0 && attackerMap.containsKey(defName)) {
					String attName = attackerMap.remove(defName);
					denied =
						handler.execute(Event.KILL, attName, null, player.getName(),
							player.getLocation());
				}
			}
			return denied;
		}

		@Override
		public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
			idleUpdate(player);
			boolean denied = false;
			Handler handler = getHandler(item.getItemId(), Event.USE);
			if (handler != null) {
				denied =
					handler.execute(
						Event.USE,
						player.getName(),
						item.getItemId(),
						ItemNames.getName(item.getItemId()),
						new Location(blockClicked.getX(), blockClicked.getY(),
							blockClicked.getZ()));
			}
			return denied;
		}

		@Override
		public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
			idleUpdate(player);
			boolean denied = false;
			Handler handler = getHandler(blockPlaced.getType(), Event.PLACE);
			if (handler != null) {
				denied =
					handler.execute(
						Event.PLACE,
						player.getName(),
						blockPlaced.getType(),
						ItemNames.getName(blockPlaced.getType()),
						new Location(blockPlaced.getX(), blockPlaced.getY(),
							blockPlaced.getZ()));
			}
			return denied;
		}

	};

	private static WatchDog plugin = null;
	private Connection connection = null;
	private ScheduledExecutorService scheduler = null;
	private ScheduledFuture<?> future = null;

	public WatchDog() {
		addHook(PluginLoader.Hook.TELEPORT, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DISCONNECT, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.CHAT, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.BLOCK_DESTROYED, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_BROKEN, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ARM_SWING, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ITEM_DROP, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ITEM_PICK_UP, PluginListener.Priority.MEDIUM,
			listener);
		/*
		 * addHook(PluginLoader.Hook.COMPLEX_BLOCK_CHANGE,
		 * PluginListener.Priority.MEDIUM, listener);
		 * addHook(PluginLoader.Hook.COMPLEX_BLOCK_SEND,
		 * PluginListener.Priority.MEDIUM, listener);
		 */
		addHook(PluginLoader.Hook.DAMAGE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.HEALTH_CHANGE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ITEM_USE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_PLACE, PluginListener.Priority.MEDIUM,
			listener);
		addCommand(new WatchDogCommand());

		plugin = this;
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	protected void onEnable() {
		try {
			Class.forName("org.h2.Driver");
			String url =
				String.format("jdbc:h2:file:%s",
					getRelatedPath("log").replace('\\', '/'));
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL(url);
			// ds.setUser(name);
			// ds.setPassword(name);
			connection = ds.getConnection();
			Table.connect(connection, Log.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String watchDogIni = getRelatedPath(WATCHDOG_INI);
		try {
			handlers = new LinkedHashMap<Pair<Integer, Event>, Handler>();
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

		idleInterval =
			Long.valueOf(getProperty(IDLEKICK_INTERVAL_KEY, IDLEKICK_INTERVAL_DEFAULT)) * 60L * 1000L;
		for (String group : StringUtils.split(
			getProperty(IDLEKICK_IGNORE_KEY, IDLEKICK_IGNORE_DEFAULT), ", ")) {
			idleIgnoreGroups.add(group);
		}
		for (Player player : etc.getServer().getPlayerList()) {
			idleUpdate(player);
		}
		if (idleInterval > 0) {
			future = scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					long limit = System.currentTimeMillis() - idleInterval;
					for (Entry<String, Long> entry : idleLastTimes.entrySet()) {
						if (entry.getValue() < limit) {
							Player player = etc.getServer().getPlayer(entry.getKey());
							Actions.kick(player, "IDLE", null);
						}
					}
				}
			}, 1, 1, TimeUnit.MINUTES);
		}
	}

	@Override
	protected void onDisable() {
		if (future != null) {
			future.cancel(true);
			future = null;
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}

}
