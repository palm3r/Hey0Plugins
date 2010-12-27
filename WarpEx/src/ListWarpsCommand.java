import java.util.*;

public class ListWarpsCommand extends Command {

	private final WarpEx plugin;

	public ListWarpsCommand(WarpEx plugin, String... alias) {
		super("<namespace>", "Show warps");
		setAlias(alias);
		setRequire("/listwarps");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		Pair<String, String> key = plugin.normalizeKey(player, "foo");
		Set<String> set = new TreeSet<String>();
		String defns = null, ns = null;
		if (key != null) {
			defns = plugin.normalizeKey(player, "foo").first;
			ns = args.isEmpty() ? defns : args.get(0);
			for (Pair<String, String> w : plugin.getAllWarps(player)) {
				if (w.first.equalsIgnoreCase(ns)) {
					set.add(w.second);
				}
			}
		}
		if (set.isEmpty()) {
			Chat.player(false, player, (Colors.Rose + "No warps avairable"));
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (String s : set) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(s);
		}
		String ns2 = ns + "\'s";
		if (ns.equals(Namespace.Global.get(player))) {
			ns2 = "Global";
		}
		if (ns.equals(Namespace.Secret.get(player))) {
			ns2 = "Secret";
		}
		Chat.player(false, player, (Colors.LightGreen + ns2) + (Colors.LightGray + " warps: ")
			+ (Colors.White + sb.toString().trim()));
		Chat.player(false, player, (Colors.LightGray + "Type ")
			+ (Colors.LightPurple + "/warp " + (ns.equals(defns) ? "[name]" : ns + ":[name]"))
			+ (Colors.LightGray + " to use"));
		return true;
	}

}
