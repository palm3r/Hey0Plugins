public class Actions {

	public static void kick(Log log) {
		Player player = etc.getServer().getPlayer(log.player);
		if (player != null) {
			player.kick(String.format("reason: %s%s", log.action,
				log.targetName != null ? " " + log.targetName : ""));
			Chat.broadcast(Colors.Rose + "%s was kicked (reason: %s%s)",
				log.player, log.action, log.targetName != null ? " " + log.targetName
					: "");
		}
	}

	public static void ban(Log log) {
		kick(log);
		etc.getServer().ban(log.player);
		Chat.broadcast(Colors.Rose + "%s was banned (reason: %s%s)", log.player,
			log.action, log.targetName != null ? " " + log.targetName : "");
	}

}
