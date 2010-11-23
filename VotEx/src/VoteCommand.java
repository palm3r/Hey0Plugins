import java.util.*;

public class VoteCommand extends Command {

	public static final String COMMAND = "/vote";
	private VotEx plugin;

	public VoteCommand(VotEx plugin) {
		super(COMMAND, null, "[subject]", "Begin vote", COMMAND);
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		StringBuilder sb = new StringBuilder();
		for (String a : args) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(a.trim());
		}
		String subject = sb.toString().trim();
		if (subject.length() == 0) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		if (plugin.isVoting()) {
			Chat.toPlayer(player, (Colors.Rose + "Another vote on progress now"));
			return true;
		}
		plugin.beginVote(subject);
		return true;
	}

}
