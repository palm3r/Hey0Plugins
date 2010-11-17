public class VoteCommand extends Command {

	private VotEx plugin;

	public VoteCommand(VotEx plugin) {
		super(new String[] { "/vote" }, "[subject]", "Begin vote");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; ++i) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(args[i].trim());
		}
		String subject = sb.toString().trim();
		if (subject.length() == 0) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		if (plugin.isVoting()) {
			Chat.toPlayer(player, Colors.Rose + "Another vote on progress now");
			return true;
		}
		Chat.toBroadcast(Colors.LightBlue + "[VOTE] " + Colors.White + subject);
		Chat.toBroadcast("Please vote /y or /n");
		plugin.beginVote(subject);
		return true;
	}

}
