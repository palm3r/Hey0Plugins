import java.util.*;
import java.util.concurrent.*;

public class InviteCommand extends Command {

	private InvitEx plugin;

	public InviteCommand(InvitEx plugin) {
		super("[player]", "Invite player");
		setRequire("/invite");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		final String hostName = player.getName();
		final String guestName = args.get(0);
		if (guestName.equalsIgnoreCase(hostName)) {
			Chat.toPlayer(player, (Colors.Rose + "You can\'t invite yourself"));
			return true;
		}
		Player guest = etc.getServer().getPlayer(guestName);
		if (guest == null) {
			Chat.toPlayer(player, (Colors.LightGreen + guestName)
				+ (Colors.Rose + " is not online"));
			return true;
		}
		Pair<String, ScheduledFuture<?>> invite = plugin.getInvite(guestName);
		if (invite != null) {
			Chat.toPlayer(player, (Colors.LightGreen + guestName)
				+ (Colors.Rose + " is being invited by other player"));
			return true;
		}
		Chat.toPlayer(player, (Colors.LightGray + "You invited ")
			+ (Colors.LightGreen + guestName));
		Chat.toPlayer(guestName, (Colors.LightGreen + hostName)
			+ (Colors.LightGray + " invited you. type ")
			+ (Colors.LightPurple + "/accept")
			+ (Colors.LightGray + " if you accept"));
		plugin.addInvite(hostName, guestName, new Runnable() {
			public void run() {
				Chat.toPlayer(hostName,
					(Colors.Rose + "Your invite was cancelled by timeout"));
				Chat.toPlayer(guestName, (Colors.LightGreen + hostName)
					+ (Colors.Rose + "\'s invite was cancelled by timeout"));
				plugin.removeInvite(guestName);
			}
		});
		return true;
	}

}
