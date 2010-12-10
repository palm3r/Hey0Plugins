import java.util.*;

/**
 * Chat helper class
 * 
 * @author palm3r
 */
public final class Chat {

	/**
	 * Broadcast message
	 * Message is sent to all players
	 * 
	 * @param format
	 * @param params
	 */
	public static void toBroadcast(String format, Object... params) {
		String msg = String.format(format, params);
		for (Player player : etc.getServer().getPlayerList()) {
			player.sendMessage(msg);
		}
	}

	/**
	 * Send message to the player which the name is corresponding
	 * 
	 * @param player
	 * @param format
	 * @param params
	 */
	public static void toPlayer(String player, String format, Object... params) {
		Chat.toPlayer(etc.getServer().getPlayer(player), format, params);
	}

	/**
	 * Send messsage to the player instance
	 * 
	 * @param player
	 * @param format
	 * @param params
	 */
	public static void toPlayer(Player player, String format, Object... params) {
		if (player != null && format != null) {
			String msg = String.format(format, params);
			player.sendMessage(msg);
		}
	}

	/**
	 * Send message to specified players
	 * 
	 * @param players
	 * @param format
	 * @param params
	 */
	public static void toPlayers(Player[] players, String format,
		Object... params) {
		if (players != null && format != null) {
			final List<Player> t = Arrays.asList(players);
			byCondition(new Converter<Player, Boolean>() {
				public Boolean convertTo(Player p) {
					return t.contains(p);
				}
			}, format, params);
		}
	}

	/**
	 * Send message to all admin players
	 * 
	 * @param format
	 * @param params
	 */
	public static void toAdmin(String format, Object... params) {
		if (format != null) {
			byCondition(new Converter<Player, Boolean>() {
				public Boolean convertTo(Player p) {
					return p.isAdmin();
				}
			}, format, params);
		}
	}

	/**
	 * Send message to group members which the name is corresponding
	 * 
	 * @param group
	 * @param format
	 * @param params
	 */
	public static void toGroup(final String group, String format,
		Object... params) {
		if (group != null && format != null) {
			byCondition(new Converter<Player, Boolean>() {
				public Boolean convertTo(Player p) {
					return p.isInGroup(group);
				}
			}, format, params);
		}
	}

	/**
	 * Send message to players which can use specified command
	 * 
	 * @param command
	 * @param format
	 * @param params
	 */
	public static void hasPrivilege(final String command, String format,
		Object... params) {
		if (command != null && format != null) {
			byCondition(new Converter<Player, Boolean>() {
				public Boolean convertTo(Player p) {
					return p.canUseCommand(command);
				}
			}, format, params);
		}
	}

	/**
	 * Send message to neighbors of specified player
	 * 
	 * @param player
	 * @param distance
	 * @param format
	 * @param params
	 */
	public static void toNeighbors(final Player player, final double distance,
		String format, Object... params) {
		if (player != null && format != null) {
			byCondition(new Converter<Player, Boolean>() {
				public Boolean convertTo(Player p) {
					double x = Math.pow(p.getX() - player.getX(), 2.0);
					double y = Math.pow(p.getY() - player.getY(), 2.0);
					double z = Math.pow(p.getZ() - player.getZ(), 2.0);
					double d = Math.sqrt(x + y + z);
					return d <= distance;
				}
			}, format, params);
		}
	}

	/**
	 * Send message to players who matched with specified condition
	 * 
	 * @param converter
	 * @param format
	 * @param params
	 */
	public static void byCondition(Converter<Player, Boolean> converter,
		String format, Object... params) {
		if (converter != null && format != null) {
			String msg = String.format(format, params);
			for (Player player : etc.getServer().getPlayerList()) {
				if (converter.convertTo(player)) {
					player.sendMessage(msg);
				}
			}
		}
	}

}
