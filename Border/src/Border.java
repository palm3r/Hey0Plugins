import java.io.*;
import java.util.*;

public class Border extends PluginEx {

	private static final String SIZE_KEY = "size";
	private static final String SIZE_DEFAULT = "100";
	private static final String MAPBOUNDS_INI = "mapbounds.ini";

	@SuppressWarnings("serial")
	private Map<String, Boolean> map = new HashMap<String, Boolean>() {
		{
			put("block", true);
			put("explosion", true);
			put("fire", true);
			put("flow", true);
			put("mob", true);
			put("redstone", true);
			put("physics", true);
			put("vehicle", true);
		}
	};

	private int size = 100;
	private final Map<Player, Location> locations =
		new HashMap<Player, Location>();

	public Border() {
		BorderListener listener = new BorderListener(this);
		addHook(PluginLoader.Hook.PLAYER_MOVE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.TELEPORT, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DISCONNECT, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_DESTROYED, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_BROKEN, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.IGNITE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.EXPLODE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.FLOW, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.MOB_SPAWN, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.REDSTONE_CHANGE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_PHYSICS, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.VEHICLE_CREATE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.VEHICLE_UPDATE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.VEHICLE_POSITIONCHANGE,
			PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.ITEM_USE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_PLACE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.LIQUID_DESTROY, PluginListener.Priority.MEDIUM,
			listener);
		addCommand(new BorderCommand(this));
	}

	@Override
	protected void onEnable() {
		size = Integer.valueOf(getProperty(SIZE_KEY, SIZE_DEFAULT));
		try {
			map =
				load(new HashMap<String, Boolean>(), MAPBOUNDS_INI,
					new Converter<String, Pair<String, Boolean>>() {
						@Override
						public Pair<String, Boolean> convert(String value) {
							try {
								String[] s = value.split("= ", 2);
								return Pair.create(s[0], Boolean.valueOf(s[1]));
							} catch (Exception e) {
							}
							return null;
						}
					});
		} catch (IOException e) {
			try {
				save(map, MAPBOUNDS_INI,
					new Converter<Pair<String, Boolean>, String>() {
						@Override
						public String convert(Pair<String, Boolean> value) {
							return value.first + " = " + value.second;
						}
					});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean isOutside(double x, double y, double z) {
		Location spawn = etc.getServer().getSpawnLocation();
		double distance =
			Math.abs(Math.sqrt(Math.pow(x - spawn.x, 2.0)
				+ Math.pow(z - spawn.z, 2.0)));
		return distance > getSize();
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean allowContains(String key) {
		return map.containsKey(key);
	}

	public Boolean getAllow(String key) {
		return map.containsKey(key) ? map.get(key) : false;
	}

	public void setAllow(String key, boolean allow) {
		map.put(key, allow);
	}

	public Location getLocation(Player player) {
		return locations.containsKey(player) ? locations.get(player)
			: etc.getServer().getSpawnLocation();
	}

	public void setLocation(Player player, Location location) {
		locations.put(player, location);
	}

	public void removeLocation(Player player) {
		locations.remove(player);
	}

}
