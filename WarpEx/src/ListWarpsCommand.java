import java.util.*;

public class ListWarpsCommand extends Command {

	private WarpEx plugin;

	public ListWarpsCommand(WarpEx plugin) {
		super(new String[] { "/listwarps", "/lw" }, "<namespace>", "Show warps");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		Pair<String, String> p = plugin.normalizeKey(player, "foo");
		Set<String> warps = plugin.getAllWarps();
		Set<String> set = new HashSet<String>();
		for (String w : warps) {
			String[] s = w.split(":", 2);
			if (!s[0].equalsIgnoreCase(p.first))
				set.add(s[1]);
		}
		if (set.isEmpty()) {
			Chat.toPlayer(player, Colors.Rose + "No warps avairable");
			return true;
		}
		String ns2 = p.first;
		if (p.first.equals("!"))
			ns2 = "Secret";
		if (p.first.equals("*"))
			ns2 = "Global";
		StringBuilder sb = new StringBuilder();
		for (String s : set) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(s);
		}
		Chat.toPlayer(player, Colors.LightGreen + ns2 + " warps: " + Colors.White
				+ sb.toString().trim());
		Chat.toPlayer(
				player,
				Colors.White
						+ "type "
						+ Colors.LightBlue
						+ (p.first.equals(player.getName()) ? "/warp [name]" : "/warp "
								+ p.first + ":[name]") + Colors.White + " to use");
		return true;
	}

}
