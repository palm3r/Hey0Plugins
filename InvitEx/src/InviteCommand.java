import java.util.concurrent.ScheduledFuture;

public class InviteCommand extends Command {

	private InvitEx plugin;

	public InviteCommand(InvitEx plugin) {
		super(false, new String[] { "/invite" }, "[player]", "Invite player");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		final String from = player.getName();
		final String to = args[1];
		if (to.equalsIgnoreCase(from)) {
			Chat.toPlayer(player, Colors.Rose + "You cannot invite yourself");
			return true;
		}
		Player guest = etc.getServer().getPlayer(to);
		if (guest == null) {
			Chat.toPlayer(player, Colors.Gold + "%s is not online", to);
			return true;
		}
		Pair<String, ScheduledFuture<?>> invite = plugin.getInvite(to);
		if (invite != null) {
			Chat.toPlayer(player, Colors.Gold
					+ "%s is being invited by other player now", to);
			return true;
		}
		Chat.toPlayer(player, Colors.LightGreen + "You invited %s", to);
		Chat.toPlayer(to, Colors.LightGreen + "%s invited you. type /accept to go",
				from);
		plugin.addInvite(from, to, new Runnable() {
			public void run() {
				Chat.toPlayer(from, "Your invite was cancelled by timeout");
				Chat.toPlayer(to, "%s\'s invite was cancelled by timeout", from);
			}
		});
		return true;
	}

}
