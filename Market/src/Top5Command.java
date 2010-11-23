import java.util.*;

public class Top5Command extends Command {

	private Market plugin;

	public Top5Command(Market plugin) {
		super("/top5", null, null, "Show top5 ranking of richest players",
			"/market");
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		List<Map.Entry<String, Long>> list = plugin.getRichestPlayers();
		int i = 0;
		for (Map.Entry<String, Long> entry : list) {
			if (i > 4)
				break;
			Chat.toPlayer(player,
				(Colors.LightGray + "[") + (Colors.LightBlue + (++i))
					+ (Colors.LightGray + "] ") + (Colors.LightGreen + entry.getKey())
					+ " " + plugin.formatMoney(entry.getValue()));
		}
		return true;
	}
}
