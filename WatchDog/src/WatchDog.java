import java.io.*;
import java.util.*;
import java.util.regex.*;

public class WatchDog extends PluginEx {

	private static final String BLOCKS_INI = "blocks.ini";
	private static final String PLAYERS_INI = "players.ini";

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

		public boolean onBlockBreak(Player player, Block block) {
			Handler handler = getBlockHandler(block.getType(), Event.DESTROY);
			return handler != null ? !handler.execute(Event.DESTROY, player,
				block) : false;
		}

		public boolean onBlockDestroy(Player player, Block block) {
			Handler handler = getBlockHandler(block.getType(), Event.DESTROY);
			return handler != null ? !handler.execute(Event.DESTROY, player,
				block) : false;
		}

		public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
			Handler handler = getBlockHandler(blockPlaced.getType(), Event.PLACE);
			return handler != null ? !handler.execute(Event.PLACE, player,
				blockPlaced) : false;
		}

		public boolean onItemDrop(Player player, Item item) {
			Handler handler = getBlockHandler(item.getItemId(), Event.DROP);
			return handler != null ? !handler.execute(Event.DROP, player, item)
				: false;
		}

		public boolean onItemPickUp(Player player, Item item) {
			Handler handler = getBlockHandler(item.getItemId(), Event.PICKUP);
			return handler != null ? !handler.execute(Event.PICKUP, player, item)
				: false;
		}

		public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
			Handler handler = getBlockHandler(item.getItemId(), Event.USE);
			return handler != null ? !handler.execute(Event.USE, player, item)
				: false;
		}

		public void onVehicleCreate(BaseVehicle vehicle) {
			Handler handler = getBlockHandler(vehicle.getId(), Event.PLACE);
			if (handler != null && !handler.execute(Event.PLACE, null, vehicle)) {
				vehicle.destroy();
			}
		}

		public void onVehicleDestroyed(BaseVehicle vehicle) {
			Handler handler = getBlockHandler(vehicle.getId(), Event.DESTROY);
			if (handler != null && !handler.execute(Event.DESTROY, null, vehicle)) {
			}
		}

		public boolean onComplexBlockChange(Player player, ComplexBlock block) {
			Block b =
				etc.getServer().getBlockAt(block.getX(), block.getY(), block.getZ());
			Handler handler = getBlockHandler(b.getType(), Event.PLACE);
			return handler != null ? !handler.execute(Event.PLACE, player, b)
				: false;
		}

		public void onLogin(Player player) {
			Handler handler = getPlayerHandler(Event.LOGIN);
			if (handler != null) {
				handler.execute(Event.LOGIN, player);
			}
		}

		public void onDisconnect(Player player) {
			Handler handler = getPlayerHandler(Event.LOGOUT);
			if (handler != null) {
				handler.execute(Event.LOGOUT, player);
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
						return !handler.execute(Event.ATTACK, att, def);
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
						return !handler.execute(Event.KILL, att, player);
					}
				}
			}
			return false;
		}

		public boolean onTeleport(Player player, Location from, Location to) {
			Handler handler = getPlayerHandler(Event.TELEPORT);
			return handler != null ? !handler.execute(Event.TELEPORT, player,
				null, to) : false;
		}

	};

	private Map<Pair<Integer, Event>, Handler> blocks;
	private Map<Event, Handler> players;
	private Command wd = new WdCommand();

	public WatchDog() {
		Handler.PLUGIN = this;
	}

	protected void onEnable() {
		String blocksIniPath = getName() + File.separator + BLOCKS_INI;
		String playersIniPath = getName() + File.separator + PLAYERS_INI;

		try {
			blocks = new HashMap<Pair<Integer, Event>, Handler>();
			BufferedReader br = new BufferedReader(new FileReader(blocksIniPath));
			String line;
			Pattern pattern =
				Pattern
					.compile("^\\s*([a-zA-Z0-9]+)\\.([a-zA-Z]+)\\.([a-zA-Z]+)\\s*=\\s*(.+)$");
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
	}

	public Handler getBlockHandler(int itemId, Event event) {
		Pair<Integer, Event> key = Pair.create(itemId, event);
		return blocks.containsKey(key) ? blocks.get(key) : null;
	}

	public Handler getPlayerHandler(Event event) {
		return players.containsKey(event) ? players.get(event) : null;
	}

}
