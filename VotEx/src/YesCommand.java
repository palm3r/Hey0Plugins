import java.util.*;

public class YesCommand extends Command {

	private VotEx plugin;

	public YesCommand(VotEx plugin) {
		super(null, "Vote YES");
		setAlias("/y");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
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
