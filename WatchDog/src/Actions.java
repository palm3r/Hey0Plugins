public class Actions {

	public static void kick(String event, String name, String target) {
		Player player = etc.getServer().getPlayer(name);
		if (player != null) {
			player.kick(String.format("reason: %s%s", event, target != null ? " "
				+ target : ""));
			Chat.toBroadcast(Colors.Rose + "%s was kicked (reason: %s%s)", name,
				event, target != null ? " " + target : "");
		}
	}

	public static void ban(String event, String name, String target) {
		kick(event, name, target);
		etc.getServer().ban(name);
		Chat.toBroadcast(Colors.Rose + "%s was banned (reason: %s%s)", name, event,
			target != null ? " " + target : "");
	}

}
