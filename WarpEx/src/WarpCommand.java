import java.util.*;

public class WarpCommand extends Command {

	private final WarpEx plugin;

	public WarpCommand(WarpEx plugin, String... alias) {
		super("<namespace:>[warp]", "Jump to warp position");
		setRequire("/warp");
		setAlias(alias);
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.player(false, player, getUsage(false, true));
			return true;
		}
		String warpName = args.get(0);
		Location location = plugin.getWarp(player, warpName);
		if (location == null) {
			Chat.player(false, player, (Colors.Rose + "Warp ") + (Colors.LightGreen + warpName)
				+ (Colors.Rose + " is not found"));
			return true;
		}
		player.teleportTo(location);
		Chat.player(false, player, (Colors.LightGreen + "Woosh!"));
		return true;
	}

}
