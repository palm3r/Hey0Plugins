import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;
import java.util.regex.*;

import org.h2.jdbcx.JdbcDataSource;

public class WatchDog extends PluginEx {

	public static final String TABLE = "log";
	public static Connection CONN = null;

	private static final String BLOCKS_INI = "blocks.ini";
	private static final String PLAYERS_INI = "players.ini";

	private static final String DATABASE_KEY = "database";
	private static final String DATABASE_DEFAULT = "WatchDog/log";

	private static final String USERNAME_KEY = "username";
	private static final String USERNAME_DEFAULT = "username";

	private static final String PASSWORD_KEY = "password";
	private static final String PASSWORD_DEFAULT = "password";

	private static final String PAGESIZE_KEY = "pagesize";
	private static final String PAGESIZE_DEFAULT = "10";

	@SuppressWarnings("serial")
	private Set<PluginLoader.Hook> hooks = new HashSet<PluginLoader.Hook>() {
		{
			add(PluginLoader.Hook.BLOCK_DESTROYED);
			add(PluginLoader.Hook.BLOCK_PLACE);
			add(PluginLoader.Hook.ITEM_DROP);
			add(PluginLoader.Hook.ITEM_PICK_UP);
			add(PluginLoader.Hook.ITEM_USE);
			add(PluginLoader.Hook.VEHICLE_CREATE);
			add(PluginLoader.Hook.VEHICLE_DESTROYED);
			add(PluginLoader.Hook.LOGIN);
			add(PluginLoader.Hook.DISCONNECT);
			add(PluginLoader.Hook.DAMAGE);
			add(PluginLoader.Hook.HEALTH_CHANGE);
			add(PluginLoader.Hook.TELEPORT);
		}
	};

	private static Map<String, String> attDef = new HashMap<String, String>();

	private PluginListener listener = new PluginListener() {

		public boolean onBlockDestroy(Player player, Block block) {
			Handler handler = getBlockHandler(block.getType(), Event.DESTROY);
			return handler != null ? handler.execute("destroy", player, block)
				: false;
		}

		public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
			Handler handler = getBlockHandler(blockPlaced.getType(), Event.PLACE);
			return handler != null ? handler.execute("place", player, blockPlaced)
				: false;
		}

		public boolean onItemDrop(Player player, Item item) {
			Handler handler = getBlockHandler(item.getItemId(), Event.DROP);
			return handler != null ? handler.execute("drop", player, item) : false;
		}

		public boolean onItemPickUp(Player player, Item item) {
			Handler handler = getBlockHandler(item.getItemId(), Event.PICKUP);
			return handler != null ? handler.execute("pickup", player, item) : false;
		}

		public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
			Handler handler = getBlockHandler(item.getItemId(), Event.USE);
			return handler != null ? handler.execute("use", player, item) : false;
		}

		public void onVehicleCreate(BaseVehicle vehicle) {
			Handler handler = getBlockHandler(vehicle.getId(), Event.PLACE);
			if (handler != null && handler.execute("place", null, vehicle)) {
				vehicle.destroy();
			}
		}

		public void onVehicleDestroyed(BaseVehicle vehicle) {
			Handler handler = getBlockHandler(vehicle.getId(), Event.DESTROY);
			if (handler != null && handler.execute("destroy", null, vehicle)) {
			}
		}

		public boolean onComplexBlockChange(Player player, ComplexBlock block) {
			Block b =
				etc.getServer().getBlockAt(block.getX(), block.getY(), block.getZ());
			Handler handler = getBlockHandler(b.getType(), Event.PLACE);
			return handler != null ? handler.execute("place", player, b) : false;
		}

		public void onLogin(Player player) {
			Handler handler = getPlayerHandler(Event.LOGIN);
			if (handler != null) {
				handler.execute("login", player);
			}
		}

		public void onDisconnect(Player player) {
			Handler handler = getPlayerHandler(Event.LOGOUT);
			if (handler != null) {
				handler.execute("logout", player);
			}
		}

		public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
			BaseEntity defender, int amount) {
			Handler handler = getPlayerHandler(Event.ATTACK);
			if (handler != null) {
				if (attacker != null && defender != null && attacker.isPlayer()
					&& defender.isPlayer()) {
					Player att = attacker.getPlayer();
					Player def = defender.getPlayer();
					if (att != null && def != null) {
						attDef.put(def.getName(), att.getName());
						return handler.execute("attack", att, def);
					}
				}
			}
			return false;
		}

		public boolean onHealthChange(Player player, int oldValue, int newValue) {
			Handler handler = getPlayerHandler(Event.KILL);
			if (handler != null) {
				String defName = player.getName();
				if (newValue <= 0 && attDef.containsKey(defName)) {
					String attName = attDef.remove(defName);
					Player att = etc.getServer().getPlayer(attName);
					if (att != null) {
						return handler.execute("kill", att, player);
					}
				}
			}
			return false;
		}

		public boolean onTeleport(Player player, Location from, Location to) {
			Handler handler = getPlayerHandler(Event.TELEPORT);
			return handler != null ? handler.execute("teleport", player, null, to)
				: false;
		}

	};

	private Map<Pair<Integer, Event>, Handler> blocks;
	private Map<Event, Handler> players;
	private Command wd = new WdCommand();

	public WatchDog() {
	}

	protected void onEnable() {
		try {
			Class.forName("org.h2.Driver");
			String url =
				String.format("jdbc:h2:file:%s/%s",
					new File(".").getAbsoluteFile().getParent().replace('\\', '/'),
					getProperty(DATABASE_KEY, DATABASE_DEFAULT));
			String user = getProperty(USERNAME_KEY, USERNAME_DEFAULT);
			String password = getProperty(PASSWORD_KEY, PASSWORD_DEFAULT);

			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL(url);
			ds.setUser(user);
			ds.setPassword(password);
			CONN = ds.getConnection();

			Statement stmt = CONN.createStatement();
			stmt.execute(String.format(
				"CREATE TABLE IF NOT EXISTS %s (id INT AUTO_INCREMENT PRIMARY KEY, time BIGINT, player VARCHAR, event VARCHAR, target VARCHAR, x INT, y INT, z INT, denied BOOLEAN, kicked BOOLEAN, banned BOOLEAN)",
				TABLE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String blocksIniPath = getName() + File.separator + BLOCKS_INI;
		try {
			blocks = new HashMap<Pair<Integer, Event>, Handler>();
			BufferedReader br = new BufferedReader(new FileReader(blocksIniPath));
			String line;
			Pattern pattern =
				Pattern.compile("^\\s*([a-zA-Z0-9]+)\\.([a-zA-Z]+)\\.([a-zA-Z]+)\\s*=\\s*(.+)$");
			while ((line = br.readLine()) != null) {
				try {
					Matcher m = pattern.matcher(line);
					if (m.matches()) {
						Pair<Integer, String> item = ItemNames.parse(m.group(1).trim());
						String eventName = m.group(2).trim().toUpperCase();
						Event event = Enum.valueOf(Event.class, eventName);
						Pair<Integer, Event> key = Pair.create(item.first, event);
						Handler handler =
							blocks.containsKey(key) ? blocks.get(key) : new Handler();
						String property = m.group(3).trim().toLowerCase();
						String value = m.group(4).trim();
						handler.set(property, value);
						blocks.put(key, handler);
					}
				} catch (Exception e) {
					error("parse error: %s", line);
				}
			}
			br.close();
		} catch (IOException e) {
			File file = new File(blocksIniPath);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		String playersIniPath = getName() + File.separator + PLAYERS_INI;
		try {
			players = new HashMap<Event, Handler>();
			BufferedReader br = new BufferedReader(new FileReader(playersIniPath));
			String line;
			Pattern pattern =
				Pattern.compile("^\\s*([a-zA-Z]+)\\.([a-zA-Z]+)\\s*=\\s*(.+)$");
			while ((line = br.readLine()) != null) {
				try {
					Matcher m = pattern.matcher(line);
					if (m.matches()) {
						String eventName = m.group(1).trim().toUpperCase();
						Event event = Enum.valueOf(Event.class, eventName);
						Handler handler =
							players.containsKey(event) ? players.get(event) : new Handler();
						String property = m.group(2).trim().toLowerCase();
						String value = m.group(3).trim();
						handler.set(property, value);
						players.put(event, handler);
					}
				} catch (Exception e) {
					error("parse error: %s", line);
				}
			}
			br.close();
		} catch (IOException e) {
			File file = new File(playersIniPath);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		try {
			WdCommand.PAGESIZE =
				Integer.valueOf(getProperty(PAGESIZE_KEY, PAGESIZE_DEFAULT));
		} catch (Exception e) {
			WdCommand.PAGESIZE = 10;
		}

		addCommand(wd);
		for (PluginLoader.Hook hook : hooks) {
			addHook(hook, PluginListener.Priority.MEDIUM, listener);
		}
	}

	protected void onDisable() {
		for (PluginLoader.Hook hook : hooks) {
			removeHook(hook);
		}
		removeCommand(wd);

		try {
			CONN.close();
			CONN = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Handler getBlockHandler(int itemId, Event event) {
		Pair<Integer, Event> key = Pair.create(itemId, event);
		return blocks.containsKey(key) ? blocks.get(key) : null;
	}

	public Handler getPlayerHandler(Event event) {
		return players.containsKey(event) ? players.get(event) : null;
	}

	public static String getMessage(String player, String event, String target,
		int x, int y, int z, boolean denied, boolean kicked, boolean banned) {
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
