public class WarpCommand extends Command {

	private WarpEx plugin;

	public WarpCommand(WarpEx plugin) {
		super(new String[] { "/warp", "/go" }, "<namespace:>[warp]",
				"Jump to warp target");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		Location location = plugin.getWarp(player, args[1]);
		if (location == null) {
			Chat.toPlayer(player, Colors.Rose + "Warp %s not found.", args[1]);
			return true;
		}
		player.teleportTo(location);
		Chat.toPlayer(player, Colors.LightGreen + "Woosh!");
		return true;
	}

}
