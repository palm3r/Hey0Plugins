import java.util.*;

public class RemoveWarpCommand extends Command {

	private final WarpEx plugin;

	public RemoveWarpCommand(WarpEx plugin, String... alias) {
		super("<namespace:>[warp]", "Remove warp");
		setRequire("/removewarp");
		setAlias(alias);
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.player(false, player, getUsage(false, true));
			return true;
		}
		Pair<String, String> p = plugin.normalizeKey(player, args.get(0));
		String key = null;
		Location location = null;
		if (p != null) {
			key = p.first + ":" + p.second;
			location = plugin.getWarp(player, key);
		}
		if (location == null) {
			Chat.player(false, player, (Colors.Rose + "Warp ") + (Colors.LightGreen + key) + (Colors.Rose + " not found"));
			return true;
		}
		plugin.removeWarp(player, key);
		Chat.player(false, player, (Colors.LightGray + "Warp ") + (Colors.LightGreen + key)
			+ (Colors.LightGray + " has been removed"));
		return true;
	}

}
