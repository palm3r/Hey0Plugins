import java.util.*;

public class ListWarpsCommand extends Command {

	private WarpEx plugin;

	public ListWarpsCommand(WarpEx plugin) {
		super(new String[] { "/listwarps", "/lw" }, "<namespace>", "Show warps");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		String defns = plugin.normalizeKey(player, "foo").first;
		String ns = args.length > 1 ? args[1] : defns;
		Set<String> warps = plugin.getAllWarps();
		Set<String> set = new HashSet<String>();
		for (String w : warps) {
			String[] s = w.split(":", 2);
			if (s[0].equalsIgnoreCase(ns))
				set.add(s[1]);
		}
		if (set.isEmpty()) {
			Chat.toPlayer(player, Colors.Rose + "No warps avairable");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (String s : set) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(s);
		}
		String ns2 = ns + "\'s";
		if (ns.equals("*"))
			ns2 = "Global";
		if (ns.equals("!"))
			ns2 = "Secret";
		Chat.toPlayer(player, Colors.LightGreen + ns2 + " warp: " + Colors.White
				+ sb.toString().trim());
		Chat.toPlayer(player,
				Colors.White + "type " + Colors.LightBlue
						+ (ns.equals(defns) ? "/warp [name]" : "/warp " + ns + ":[name]")
						+ Colors.White + " to go");
		return true;
	}

}
