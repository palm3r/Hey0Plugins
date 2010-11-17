public class SetWarpCommand extends Command {

	private WarpEx plugin;

	public SetWarpCommand(WarpEx plugin) {
		super(new String[] { "/setwarp", "/sw" }, "<namespace:>[warp]",
				"Create or overwrite warp");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		Pair<String, String> p = plugin.normalizeKey(player, args[1]);
		String key = p.first + ":" + p.second;
		if (!p.second.matches("[^ ,:]+")) {
			Chat.toPlayer(player, Colors.Rose + "Invalid warp name");
			return true;
		}
		if (!plugin.checkPermission(true, player, p.first)) {
			Chat.toPlayer(player, Colors.Rose + "Permission denied.");
			return true;
		}
		plugin.setWarp(player, key);
		Chat.toPlayer(player, Colors.LightGreen + "Warp %s has been created", key);
		return true;
	}

}
