import java.util.*;

public class YesCommand extends Command {

	public static final String COMMAND = "/yes";
	private VotEx plugin;

	public YesCommand(VotEx plugin) {
		super(COMMAND, new String[] { "/y" }, null, "Vote YES");
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		if (!plugin.isVoting()) {
			Chat.toPlayer(player, (Colors.Rose + "No vote on progress"));
			return true;
		}
		Chat.toPlayer(player, (Colors.LightGray + "You have voted ")
			+ (Colors.LightGreen + "YES"));
		plugin.getAnswers().put(player.getName(), true);
		return true;
	}

}
