public class RemoveWarpCommand extends Command {

	private WarpEx plugin;

	public RemoveWarpCommand(WarpEx plugin) {
		super(new String[] { "/removewarp", "/rw" }, "<namespace:>[warp]",
				"Remove warp");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		Pair<String, String> p = plugin.normalizeKey(player, args[1]);
		String key = p.first + ":" + p.second;
		Location location = plugin.getWarp(player, key);
		if (location == null) {
			Chat.toPlayer(player, Colors.Rose + "Warp %s not found.", key);
			return true;
		}
		plugin.removeWarp(player, key);
		Chat.toPlayer(player, Colors.LightGreen + "Warp %s has been removed", key);
		return true;
	}

}
