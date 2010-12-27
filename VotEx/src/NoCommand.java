import java.util.*;

public class NoCommand extends Command {

	private final VotEx plugin;

	public NoCommand(VotEx plugin) {
		super(null, "Vote NO");
		setAlias("/n");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		if (!plugin.isVoting()) {
			Chat.player(false, player, (Colors.Rose + "No vote on progress"));
			return true;
		}
		Chat.player(false, player, (Colors.LightGray + "You have voted ") + (Colors.Rose + "NO"));
		plugin.getAnswers().put(player.getName(), false);
		return true;
	}

}
