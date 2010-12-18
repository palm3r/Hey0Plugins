import java.util.*;
import java.util.concurrent.*;

public class InviteCommand extends Command {

	private final InvitEx plugin;

	public InviteCommand(InvitEx plugin) {
		super("[player]", "Invite player");
		setRequire("/invite");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.player(player, getUsage(false, true));
			return true;
		}
		final String hostName = player.getName();
		String guestName = args.get(0);
		if (guestName.equalsIgnoreCase(hostName)) {
			Chat.player(player, (Colors.Rose + "You can\'t invite yourself"));
			return true;
		}
		Player guest = null;
		for (Player p : etc.getServer().getPlayerList())
			if (p.getName().equalsIgnoreCase(guestName)) {
				guest = p;
				guestName = p.getName();
				break;
			}
		if (guest == null) {
			Chat.player(player, (Colors.LightGreen + guestName)
				+ (Colors.Rose + " is not online"));
			return true;
		}
		Pair<String, ScheduledFuture<?>> invite = plugin.getInvite(guestName);
		if (invite != null) {
			Chat.player(player, (Colors.LightGreen + guestName)
				+ (Colors.Rose + " is being invited by other player"));
			return true;
		}
		Chat.player(player, (Colors.LightGray + "You invited ")
			+ (Colors.LightGreen + guestName));
		Chat.player(guestName, (Colors.LightGreen + hostName)
			+ (Colors.LightGray + " invited you. type ")
			+ (Colors.LightPurple + "/accept")
			+ (Colors.LightGray + " if you accept"));
		final String gn = guestName;
		plugin.addInvite(hostName, guestName, new Runnable() {
			@Override
			public void run() {
				Chat.player(hostName,
					(Colors.Rose + "Your invite was cancelled by timeout"));
				Chat.player(gn, (Colors.LightGreen + hostName)
					+ (Colors.Rose + "\'s invite was cancelled by timeout"));
				plugin.removeInvite(gn);
				plugin.info("Invite from %s to %s has cancelled", hostName, gn);
			}
		});
		plugin.info("Invite from %s to %s has started", hostName, guestName);
		return true;
	}

}
