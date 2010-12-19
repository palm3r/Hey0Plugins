import java.util.*;
import org.apache.commons.collections.*;

public final class Chat {

	public static void broadcast(String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			player.sendMessage(msg);
		}
	}

	public static void player(String player, String format, Object... args) {
		Player p = etc.getServer().getPlayer(player);
		if (p != null) {
			Chat.player(player, format, args);
		}
	}

	public static void player(Player player, String format, Object... args) {
		player.sendMessage(String.format(format, args));
	}

	public static void players(Player[] players, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : players) {
			player.sendMessage(msg);
		}
	}

	public static void player(Collection<Player> players, String format,
		Object... args) {
		String msg = String.format(format, args);
		for (Player player : players) {
			player.sendMessage(msg);
		}
	}

	public static void admin(String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (player.isAdmin()) {
				player.sendMessage(msg);
			}
		}
	}

	public static void group(final String group, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (player.isInGroup(group)) {
				player.sendMessage(msg);
			}
		}
	}

	public static void auth(final String command, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (player.canUseCommand(command)) {
				player.sendMessage(msg);
			}
		}
	}

	public static void neighbors(final Player player, final double radius,
		String format, Object... args) {
		String msg = String.format(format, args);
		for (Player p : etc.getServer().getPlayerList()) {
			double distance =
				Math.abs(Math.sqrt(Math.pow(p.getX() - player.getX(), 2)
					+ Math.pow(p.getY() - player.getY(), 2)
					+ Math.pow(p.getZ() - player.getZ(), 2)));
			if (distance <= radius) {
				p.sendMessage(msg);
			}
		}
	}

	public static void condition(Predicate predicate, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (predicate.evaluate(player)) {
				player.sendMessage(msg);
			}
		}
	}

}
