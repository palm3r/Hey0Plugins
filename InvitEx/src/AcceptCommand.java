import java.util.concurrent.*;

public class AcceptCommand extends Command {

	private InvitEx plugin;

	public AcceptCommand(InvitEx plugin) {
		super(false, new String[] { "/accept" }, null, "Accept invite");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		String key = args[1];
		Pair<String, ScheduledFuture<?>> invite = plugin.getInvite(key);
		if (invite == null) {
			Chat.toPlayer(player, Colors.Rose + "Nobody invited you");
			return true;
		}
		Player from = etc.getServer().getPlayer(invite.first);
		if (from == null) {
			Chat.toPlayer(player, Colors.Gold
					+ "%s is not online. Invite has cancelled", invite.first);
		} else {
			Chat.toPlayer(from, Colors.LightGreen + "%s accepted your invite",
					player.getName());
		}
		plugin.removeInvite(key);
		return true;
	}

}
