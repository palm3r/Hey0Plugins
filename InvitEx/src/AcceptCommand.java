import java.util.*;
import java.util.concurrent.*;

public class AcceptCommand extends Command {

	private final InvitEx plugin;

	public AcceptCommand(InvitEx plugin) {
		super(null, "Accept invite");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		String guestName = player.getName();
		Pair<String, ScheduledFuture<?>> invite = plugin.getInvite(guestName);
		if (invite == null) {
			Chat.player(false, player, (Colors.Rose + "Nobody invited you"));
			return true;
		}
		Player host = etc.getServer().getPlayer(invite.first);
		if (host == null) {
			Chat.player(false, player, (Colors.LightGreen + invite.first)
				+ (Colors.Rose + " is not online. Invite has cancelled"));
		} else {
			Chat.player(false, host, (Colors.LightGreen + guestName) + (Colors.LightGray + " accepted your invite"));
			player.teleportTo(host);
			plugin.info("Invite from %s to %s has accepted", host.getName(), guestName);
		}
		plugin.removeInvite(guestName);
		return true;
	}

}
