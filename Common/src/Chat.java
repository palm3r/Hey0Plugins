import java.text.*;
import java.util.*;

public class Chat {

	public static void toBroadcast(String format, Object... params) {
		String msg = MessageFormat.format(format, params);
		for (Player player : etc.getServer().getPlayerList()) {
			player.sendMessage(msg);
		}
	}

	public static void toPlayer(Player player, String format, Object... params) {
		String msg = String.format(format, params);
		player.sendMessage(msg);
	}

	public static void toPlayers(Player[] players, String format,
			Object... params) {
		final List<Player> t = Arrays.asList(players);
		byCondition(new FuncT1<Boolean, Player>() {
			public Boolean call(Player p) {
				return t.contains(p);
			}
		}, format, params);
	}

	public static void toAdmin(String format, Object... params) {
		byCondition(new FuncT1<Boolean, Player>() {
			public Boolean call(Player p) {
				return p.isAdmin();
			}
		}, format, params);
	}

	public static void toGroup(final String group, String format,
			Object... params) {
		byCondition(new FuncT1<Boolean, Player>() {
			public Boolean call(Player p) {
				return p.isInGroup(group);
			}
		}, format, params);
	}

	public static void hasPrivilege(final String command, String format,
			Object... params) {
		byCondition(new FuncT1<Boolean, Player>() {
			public Boolean call(Player p) {
				return p.canUseCommand(command);
			}
		}, format, params);
	}

	public static void toNeighbors(final Player player, final double distance,
			String format, Object... params) {
		byCondition(new FuncT1<Boolean, Player>() {
			public Boolean call(Player p) {
				double x = Math.pow(p.getX() - player.getX(), 2.0);
				double y = Math.pow(p.getY() - player.getY(), 2.0);
				double z = Math.pow(p.getZ() - player.getZ(), 2.0);
				double d = Math.sqrt(x + y + z);
				return d <= distance;
			}
		}, format, params);
	}

	public static void byCondition(FuncT1<Boolean, Player> func, String format,
			Object... params) {
		String msg = String.format(format, params);
		for (Player player : etc.getServer().getPlayerList()) {
			if (func.call(player)) {
				player.sendMessage(msg);
			}
		}
	}

}
