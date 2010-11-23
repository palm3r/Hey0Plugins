import java.util.*;

public class SetWarpCommand extends Command {

	public static final String COMMAND = "/setwarp";
	private WarpEx plugin;

	public SetWarpCommand(WarpEx plugin, String[] alias) {
		super(COMMAND, alias, "<namespace:>[warp]", "Create or overwrite warp",
			COMMAND);
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		String warpName = args.get(0);
		Pair<String, String> p = plugin.normalizeKey(player, warpName);
		if (p == null) {
			Chat.toPlayer(player, (Colors.Rose + "Invalid warp name"));
			return true;
		}
		String ns = p.first;
		String warp = p.second;
		String key = ns + ":" + warp;
		if (!plugin.checkPermission(true, player, ns, warp)) {
			Chat.toPlayer(player, (Colors.Rose + "Permission denied"));
			return true;
		}
		plugin.setWarp(player, key);
		Chat.toPlayer(player, (Colors.LightGray + "Warp ")
			+ (Colors.LightGreen + key) + (Colors.LightGray + " has been created"));
		return true;
	}
}
