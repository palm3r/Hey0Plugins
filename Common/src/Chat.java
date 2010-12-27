import java.util.*;

import org.apache.commons.collections.*;

public final class Chat {

	private static Map<String, String> lastMessages =
		new HashMap<String, String>();

	public static void
		broadcast(boolean redundant, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (redundant || !lastMessages.containsKey(player.getName())
				|| !lastMessages.get(player.getName()).equals(msg)) {
				player.sendMessage(msg);
				lastMessages.put(player.getName(), msg);
			}
		}
	}

	public static void player(boolean redundant, String player, String format,
		Object... args) {
		Player p = etc.getServer().getPlayer(player);
		if (p != null) {
			Chat.player(redundant, player, format, args);
		}
	}

	public static void player(boolean redundant, Player player, String format,
		Object... args) {
		String msg = String.format(format, args);
		if (redundant || !lastMessages.containsKey(player.getName())
			|| !lastMessages.get(player.getName()).equals(msg)) {
			player.sendMessage(msg);
			lastMessages.put(player.getName(), msg);
		}
	}

	public static void players(boolean redundant, Player[] players,
		String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : players) {
			if (redundant || !lastMessages.containsKey(player.getName())
				|| !lastMessages.get(player.getName()).equals(msg)) {
				player.sendMessage(msg);
				lastMessages.put(player.getName(), msg);
			}
		}
	}

	public static void player(boolean redundant, Collection<Player> players,
		String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : players) {
			if (redundant || !lastMessages.containsKey(player.getName())
				|| !lastMessages.get(player.getName()).equals(msg)) {
				player.sendMessage(msg);
				lastMessages.put(player.getName(), msg);
			}
		}
	}

	public static void admin(boolean redundant, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (player.isAdmin()) {
				if (redundant || !lastMessages.containsKey(player.getName())
					|| !lastMessages.get(player.getName()).equals(msg)) {
					player.sendMessage(msg);
					lastMessages.put(player.getName(), msg);
				}
			}
		}
	}

	public static void group(boolean redundant, final String group,
		String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (player.isInGroup(group)) {
				if (redundant || !lastMessages.containsKey(player.getName())
					|| !lastMessages.get(player.getName()).equals(msg)) {
					player.sendMessage(msg);
					lastMessages.put(player.getName(), msg);
				}
			}
		}
	}

	public static void auth(boolean redundant, final String command,
		String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (player.canUseCommand(command)) {
				if (redundant || !lastMessages.containsKey(player.getName())
					|| !lastMessages.get(player.getName()).equals(msg)) {
					player.sendMessage(msg);
					lastMessages.put(player.getName(), msg);
				}
			}
		}
	}

	public static void neighbors(boolean redundant, final Player player,
		final double radius, String format, Object... args) {
		String msg = String.format(format, args);
		for (Player p : etc.getServer().getPlayerList()) {
			double distance =
				Math.abs(Math.sqrt(Math.pow(p.getX() - player.getX(), 2)
					+ Math.pow(p.getY() - player.getY(), 2)
					+ Math.pow(p.getZ() - player.getZ(), 2)));
			if (distance <= radius) {
				if (redundant || !lastMessages.containsKey(player.getName())
					|| !lastMessages.get(player.getName()).equals(msg)) {
					p.sendMessage(msg);
					lastMessages.put(player.getName(), msg);
				}
			}
		}
	}

	public static void condition(boolean redundant, Predicate predicate,
		String format, Object... args) {
		String msg = String.format(format, args);
		for (Player player : etc.getServer().getPlayerList()) {
			if (predicate.evaluate(player)) {
				if (redundant || !lastMessages.containsKey(player.getName())
					|| !lastMessages.get(player.getName()).equals(msg)) {
					player.sendMessage(msg);
					lastMessages.put(player.getName(), msg);
				}
			}
		}
	}

}
