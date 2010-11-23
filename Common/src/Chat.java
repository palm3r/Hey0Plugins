import java.util.*;

public class Chat {

	public static void toBroadcast(String format, Object... params) {
		String msg = String.format(format, params);
		for (Player player : etc.getServer().getPlayerList()) {
			player.sendMessage(msg);
		}
	}

	public static void toPlayer(String player, String format, Object... params) {
		Chat.toPlayer(etc.getServer().getPlayer(player), format, params);
	}

	public static void toPlayer(Player player, String format, Object... params) {
		if (player != null && format != null) {
			String msg = String.format(format, params);
			player.sendMessage(msg);
		}
	}

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

	public static void toAdmin(String format, Object... params) {
		if (format != null) {
			byCondition(new Converter<Player, Boolean>() {
				public Boolean convertTo(Player p) {
					return p.isAdmin();
				}
			}, format, params);
		}
	}

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
