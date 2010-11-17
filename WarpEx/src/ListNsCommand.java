import java.util.*;

public class ListNsCommand extends Command {

	private WarpEx plugin;

	public ListNsCommand(WarpEx plugin) {
		super(new String[] { "/listns" }, null, "Show namespaces");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		Set<String> set = new HashSet<String>();
		for (String key : plugin.getAllWarps()) {
			String[] s = key.split(":", 2);
			String ns = s[0];
			if (!ns.equals("*") && !ns.equals("!"))
				set.add(ns);
		}
		StringBuilder sb = new StringBuilder();
		for (String s : set) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(s);
		}
		Chat.toPlayer(player, Colors.LightGreen + "Namespaces: " + Colors.White
				+ sb.toString());
		Chat.toPlayer(player, Colors.White + "type " + Colors.LightBlue
				+ "/lw [name]" + Colors.White + " to see warps");
		return true;
	}

}
