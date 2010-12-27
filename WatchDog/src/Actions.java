public class Actions {

	public static void kick(String playerName, String action, String target) {
		Player player = etc.getServer().getPlayer(playerName);
		if (player != null) {
			player.kick(String.format("You have been kicked (reason: %s%s)", action, target != null ? " " + target : ""));
			Chat.broadcast(false, Colors.Rose + "%s was kicked (reason: %s%s)", playerName, action, target != null ? " "
				+ target : "");
		}
	}

	public static void ban(String playerName, String action, String target) {
		kick(playerName, action, target);
		etc.getServer().ban(playerName);
		Chat.broadcast(false, Colors.Rose + "%s was banned (reason: %s%s)", playerName, action, target != null ? " "
			+ target : "");
	}

}
