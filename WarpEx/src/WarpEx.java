import java.util.*;

public class WarpEx extends PluginEx {

	private enum NS {
		Personal("%s"), Global("*"), Secret("!");
		private String format;

		private NS(String format) {
			this.format = format;
		}

		public String get(Player player) {
			return String.format(format, player.getName());
		}
	}

	private static final String DATA_FILE = "data.txt";
	private static final String DEFAULT_NAMESPACE_KEY = "default-namespace";

	private Map<String, Location> warps = new HashMap<String, Location>();
	private NS defaultNamespace = NS.Personal;

	public WarpEx() {
		initPluginEx("WarpEx", null, PluginListener.Priority.LOW,
				PluginLoader.Hook.COMMAND);

		addCommand(new SetWarpCommand(this));
		addCommand(new RemoveWarpCommand(this));
		addCommand(new WarpCommand(this));
		addCommand(new ListWarpsCommand(this));
		addCommand(new ListNsCommand(this));
	}

	public Pair<String, String> normalizeKey(Player player, String key) {
		String[] s = key.split(":", 2);
		String ns = s.length > 1 ? s[0] : defaultNamespace.get(player);
		String warp = s.length > 1 ? s[1] : s[0];
		return new Pair<String, String>(ns, warp);
	}

	public Set<String> getAllWarps() {
		return warps.keySet();
	}

	public Location getWarp(Player player, String key) {
		Pair<String, String> p = normalizeKey(player, key);
		key = p.first + ":" + p.second;
		return checkPermission(false, player, p.first) && warps.containsKey(key)
				? warps.get(key) : null;
	}

	public void setWarp(Player player, String key) {
		warps.put(key, player.getLocation());
		saveWarps();
	}

	public void removeWarp(Player player, String key) {
		warps.remove(key);
		saveWarps();
	}

	public boolean checkPermission(boolean modify, Player player, String ns) {
		return !modify
				|| ns.equalsIgnoreCase(player.getName())
				|| (ns.equalsIgnoreCase("*") && (player
						.canUseCommand("/warpex-modify-*") || (!modify && player
						.canUseCommand("/warpex-*"))))
				|| (ns.equalsIgnoreCase("!") && (player
						.canUseCommand("/warpex-modify-!") || (!modify && player
						.canUseCommand("/warpex-!"))));
	}

	protected void onEnable() {
		defaultNamespace = Enum.valueOf(NS.class,
				Tools.Capitalize(getProperty(DEFAULT_NAMESPACE_KEY, "Personal")));
		loadWarps();
	}

	private void loadWarps() {
		try {
			warps = load(DATA_FILE, new Converter<String, Pair<String, Location>>() {
				public Pair<String, Location> convert(String value) {
					String[] s = value.split(",");
					if (s.length < 4 || s[0].isEmpty())
						return null;
					Location location = new Location(Double.valueOf(s[1]), Double
							.valueOf(s[2]), Double.valueOf(s[3]), s.length >= 5 ? Float
							.valueOf(s[4]) : 0.0f, s.length >= 6 ? Float.valueOf(s[5]) : 0.0f);
					return new Pair<String, Location>(s[0], location);
				}
			});
		} catch (Exception e) {
			saveWarps();
		}
	}

	private void saveWarps() {
		try {
			save(warps, DATA_FILE, new Converter<Pair<String, Location>, String>() {
				public String convert(Pair<String, Location> value) {
					return String.format("%s,%f,%f,%f,%f,%f", value.first,
							value.second.x, value.second.y, value.second.z,
							value.second.rotX, value.second.rotY);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
