import java.util.*;

public class VoteCommand extends Command {

	private final VotEx plugin;

	public VoteCommand(VotEx plugin) {
		super("[subject]", "Begin vote");
		setRequire("/vote");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		StringBuilder sb = new StringBuilder();
		for (String a : args) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(a.trim());
		}
		String subject = sb.toString().trim();
		if (subject.length() == 0) {
			Chat.player(player, getUsage(false, true));
			return true;
		}
		if (plugin.isVoting()) {
			Chat.player(player, (Colors.Rose + "Another vote on progress now"));
			return true;
		}
		plugin.beginVote(subject);
		return true;
	}

}
