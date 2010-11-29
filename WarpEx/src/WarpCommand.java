import java.util.*;

public class WarpCommand extends Command {

	private WarpEx plugin;

	public WarpCommand(WarpEx plugin, String... alias) {
		super("<namespace:>[warp]", "Jump to warp position");
		setRequire("/warp");
		setAlias(alias);
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		String warpName = args.get(0);
		Location location = plugin.getWarp(player, warpName);
		if (location == null) {
			Chat.toPlayer(player, (Colors.Rose + "Warp ")
				+ (Colors.LightGreen + warpName) + (Colors.Rose + " is not found"));
			return true;
		}
		player.teleportTo(location);
		Chat.toPlayer(player, (Colors.LightGreen + "Woosh!"));
		return true;
	}

}
