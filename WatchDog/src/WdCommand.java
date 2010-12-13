import java.util.*;

public class WdCommand extends Command {

	public WdCommand() {
		super("[event]", "jump to event location");
		setRequire("/watchdog");
	}

	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		try {
			int id = Integer.valueOf(args.get(0));
			Location loc = Handler.getLocation(id);
			player.teleportTo(loc);
		} catch (Exception e) {
			Chat.toPlayer(player, (Colors.Rose + "Invalid id specified"));
		}
		return true;
	}
}
