import java.util.*;

public class NoCommand extends Command {

	private VotEx plugin;

	public NoCommand(VotEx plugin) {
		super(null, "Vote NO");
		setAlias("/n");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
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
