import java.util.*;

public class NoCommand extends Command {

	public static final String COMMAND = "/no";
	private VotEx plugin;

	public NoCommand(VotEx plugin) {
		super(COMMAND, new String[] { "/n" }, null, "Vote NO");
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		if (!plugin.isVoting()) {
			Chat.toPlayer(player, (Colors.Rose + "No vote on progress"));
			return true;
		}
		Chat.toPlayer(player, (Colors.LightGray + "You have voted ")
			+ (Colors.Rose + "NO"));
		plugin.getAnswers().put(player.getName(), false);
		return true;
	}

}
