import java.io.*;
import java.util.*;
import java.util.regex.*;

public class WatchDog extends PluginEx {

	private static final String WATCHDOG_INI = "watchdog.ini";

	@SuppressWarnings("serial")
	private Set<PluginLoader.Hook> hooks = new HashSet<PluginLoader.Hook>() {
		{
			add(PluginLoader.Hook.BLOCK_BROKEN);
			add(PluginLoader.Hook.BLOCK_DESTROYED);
			add(PluginLoader.Hook.BLOCK_PLACE);
			add(PluginLoader.Hook.ITEM_DROP);
			add(PluginLoader.Hook.ITEM_PICK_UP);
			add(PluginLoader.Hook.ITEM_USE);
			add(PluginLoader.Hook.VEHICLE_CREATE);
			add(PluginLoader.Hook.VEHICLE_DESTROYED);
			add(PluginLoader.Hook.COMPLEX_BLOCK_CHANGE);
		}
	};

	private PluginListener listener = new PluginListener() {

		public boolean onBlockBreak(Player player, Block block) {
			Location location = new Location((int) block.getX(), (int) block.getY(),
				(int) block.getZ());
			Handler handler = getHandler(block.getType(), Event.DESTROY);
			return handler != null ? !handler.execute(Event.DESTROY, player, block,
				location) : false;
		}

		public boolean onBlockDestroy(Player player, Block block) {
			Location location = new Location((int) block.getX(), (int) block.getY(),
				(int) block.getZ());
			Handler handler = getHandler(block.getType(), Event.DESTROY);
			return handler != null ? !handler.execute(Event.DESTROY, player, block,
				location) : false;
		}

		public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
			Location location = new Location((int) blockPlaced.getX(),
				(int) blockPlaced.getY(), (int) blockPlaced.getZ());
			Handler handler = getHandler(blockPlaced.getType(), Event.PLACE);
			return handler != null ? !handler.execute(Event.PLACE, player,
				blockPlaced, location) : false;
		}

		public boolean onItemDrop(Player player, Item item) {
			Location location = new Location((int) player.getX(),
				(int) player.getY(), (int) player.getZ());
			Handler handler = getHandler(item.getItemId(), Event.DROP);
			return handler != null ? !handler.execute(Event.DROP, player, item,
				location) : false;
		}

		public boolean onItemPickUp(Player player, Item item) {
			Location location = new Location((int) player.getX(),
				(int) player.getY(), (int) player.getZ());
			Handler handler = getHandler(item.getItemId(), Event.PICKUP);
			return handler != null ? !handler.execute(Event.PICKUP, player, item,
				location) : false;
		}

		public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
			Location location = new Location((int) blockPlaced.getX(),
				(int) blockPlaced.getY(), (int) blockPlaced.getZ());
			Handler handler = getHandler(item.getItemId(), Event.USE);
			return handler != null ? !handler.execute(Event.USE, player, item,
				location) : false;
		}

		public void onVehicleCreate(BaseVehicle vehicle) {
			Location location = new Location((int) vehicle.getX(),
				(int) vehicle.getY(), (int) vehicle.getZ());
			Handler handler = getHandler(vehicle.getId(), Event.PLACE);
			if (!handler.execute(Event.PLACE, null, vehicle, location)) {
				vehicle.destroy();
			}
		}

		public void onVehicleDestroyed(BaseVehicle vehicle) {
			Location location = new Location((int) vehicle.getX(),
				(int) vehicle.getY(), (int) vehicle.getZ());
			Handler handler = getHandler(vehicle.getId(), Event.DESTROY);
			if (!handler.execute(Event.DESTROY, null, vehicle, location)) {
			}
		}

		public boolean onComplexBlockChange(Player player, ComplexBlock block) {
			Block b = etc.getServer().getBlockAt(block.getX(), block.getY(),
				block.getZ());
			Location location = new Location((int) block.getX(), (int) block.getY(),
				(int) block.getZ());
			Handler handler = getHandler(b.getType(), Event.PLACE);
			return handler != null ? !handler.execute(Event.PLACE, player, b,
				location) : false;
		}

	};

	private Map<Pair<Integer, Event>, Handler> handlers;
	private Command wd = new WdCommand();

	public WatchDog() {
	}

	protected void onEnable() {
		try {
			handlers = new HashMap<Pair<Integer, Event>, Handler>();
			BufferedReader br = new BufferedReader(new FileReader(getName()
				+ File.separator + WATCHDOG_INI));
			String line;
			Pattern pattern = Pattern
				.compile("^\\s*([a-zA-Z0-9]+)\\.([a-zA-Z]+)\\.([a-zA-Z]+)\\s*=\\s*(.+)$");
			while ((line = br.readLine()) != null) {
				try {
					Matcher m = pattern.matcher(line);
					if (m.matches()) {
						Pair<Integer, String> item = ItemNames.parse(m.group(1).trim());
						String eventName = m.group(2).trim().toUpperCase();
						Event event = Enum.valueOf(Event.class, eventName);
						Pair<Integer, Event> key = Pair.create(item.first, event);
						Handler handler = handlers.containsKey(key) ? handlers.get(key)
							: new Handler(this);
						String property = m.group(3).trim().toLowerCase();
						String value = m.group(4).trim();
						handler.set(property, value);
						handlers.put(key, handler);
						info("%s.%s.%s = %s", item.second, event.toString().toLowerCase(),
							property, value);
					}
				} catch (Exception e) {
					error("parse error: %s", line);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
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

	public Handler getHandler(int itemId, Event event) {
		Pair<Integer, Event> key = Pair.create(itemId, event);
		return handlers.containsKey(key) ? handlers.get(key) : null;
	}

}
