import java.util.*;

public class SetWarpCommand extends Command {

	private final WarpEx plugin;

	public SetWarpCommand(WarpEx plugin, String... alias) {
		super("<namespace:>[warp]", "Create or overwrite warp");
		setRequire("/setwarp");
		setAlias(alias);
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.player(player, getUsage(false, true));
			return true;
		}
		String warpName = args.get(0);
		Pair<String, String> p = plugin.normalizeKey(player, warpName);
		if (p == null) {
			Chat.player(player, (Colors.Rose + "Invalid warp name"));
			return true;
		}
		String ns = p.first;
		String warp = p.second;
		String key = ns + ":" + warp;
		if (!plugin.checkPermission(true, player, ns, warp)) {
			Chat.player(player, (Colors.Rose + "Permission denied"));
			return true;
		}
		plugin.setWarp(player, key);
		Chat.player(player, (Colors.LightGray + "Warp ")
			+ (Colors.LightGreen + key) + (Colors.LightGray + " has been created"));
		return true;
	}
}
