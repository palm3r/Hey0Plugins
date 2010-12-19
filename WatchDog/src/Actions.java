public class Actions {

	public static void kick(Player player, String action, String target) {
		if (player != null) {
			player.kick(String.format("You have been kicked (reason: %s%s)", action,
				target != null ? " " + target : ""));
			Chat.broadcast(Colors.Rose + "%s was kicked (reason: %s%s)", player,
				action, target != null ? " " + target : "");
		}
	}

	public static void kick(Log log) {
		Player player = etc.getServer().getPlayer(log.player);
		kick(player, log.action, log.targetName);
	}

	public static void ban(Log log) {
		kick(log);
		etc.getServer().ban(log.player);
		Chat.broadcast(Colors.Rose + "%s was banned (reason: %s%s)", log.player,
			log.action, log.targetName != null ? " " + log.targetName : "");
	}

}
