public class YesNoCommand extends Command {

	private VotEx plugin;

	public YesNoCommand(VotEx plugin) {
		super(new String[] { "/y", "/n" }, null, "Vote yes or no");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (!plugin.isVoting()) {
			Chat.toPlayer(player, Colors.Rose + "No vote on progress");
			return true;
		}
		boolean yes = args[0].equalsIgnoreCase("/y");
		Chat.toPlayer(player, "You have voted %s", yes ? Colors.LightGreen + "YES"
				: Colors.Rose + "NO");
		plugin.getAnswers().put(player.getName(), yes);
		return true;
	}

}
