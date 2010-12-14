import java.util.*;

public class Handler {

	public static WatchDog PLUGIN = null;

	private Set<String> deny;
	private Set<String> notify;
	private boolean doLog = false;
	private boolean doKick = false;
	private boolean doBan = false;

	public Handler() {
		this.deny = new HashSet<String>();
		this.notify = new HashSet<String>();
	}

	public void set(String property, String value) {
		if (property.equalsIgnoreCase("deny")) {
			deny = new HashSet<String>();
			for (String group : StringTools.split(value, ",")) {
				deny.add(group.toLowerCase());
			}
		}
		if (property.equalsIgnoreCase("notify")) {
			notify = new HashSet<String>();
			for (String group : StringTools.split(value, ",")) {
				notify.add(group.toLowerCase());
			}
		}

		if (property.equalsIgnoreCase("log")) {
			doLog = Boolean.valueOf(value.toLowerCase());
		}

		if (property.equalsIgnoreCase("kick")) {
			doKick = Boolean.valueOf(value.toLowerCase());
		}

		if (property.equalsIgnoreCase("ban")) {
			doBan = Boolean.valueOf(value.toLowerCase());
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!deny.isEmpty()) {
			sb.append(String.format("deny=%s", CollectionTools.join(deny, ",")));
		}
		if (!notify.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(String.format("notify=%s", CollectionTools.join(notify, ",")));
		}
		List<String> actions = new ArrayList<String>();
		if (doLog) {
			actions.add("log");
		}
		if (doKick) {
			actions.add("kick");
		}
		if (doBan) {
			actions.add("ban");
		}
		if (!actions.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(String.format("actions=%s", CollectionTools.join(actions, ",")));
		}
		return sb.toString();
	}

	public boolean execute(Event event, Player player, Block block) {
		return execute(
			event,
			player,
			ItemNames.getName(block.getType()),
			new Location((int) player.getX(), (int) player.getY(), (int) player
				.getZ()));
	}

	public boolean execute(Event event, Player player, Item item) {
		return execute(
			event,
			player,
			ItemNames.getName(item.getItemId()),
			new Location((int) player.getX(), (int) player.getY(), (int) player
				.getZ()));
	}

	public boolean execute(Event event, Player player, BaseVehicle vehicle) {
		return execute(
			event,
			player,
			ItemNames.getName(vehicle.getId()),
			new Location((int) player.getX(), (int) player.getY(), (int) player
				.getZ()));
	}

	public boolean execute(Event event, Player player) {
		return execute(event, player, null, new Location((int) player.getX(),
			(int) player.getY(), (int) player.getZ()));
	}

	public boolean execute(Event event, Player player1, Player player2) {
		return execute(event, player1, player2.getName(), new Location(
			(int) player1.getX(), (int) player1.getY(), (int) player1.getZ()));
	}

	public boolean execute(Event event, Player player, String target,
		Location location) {
		boolean allowed = !deny.contains("*");
		String[] groups = player.getGroups();
		for (int i = 0; i < groups.length && allowed; ++i) {
			if (deny.contains(groups[i].toLowerCase())) {
				allowed = false;
			}
		}
		if (!allowed) {
			Chat.toPlayer(player, Colors.Rose + "action denied");
		}
		Log log = new Log(event, player, target, location);
		if (Log.size() == 0 || !Log.last().equals(log)) {
			if (!allowed) {
				log.deny();
			}
			if (doKick) {
				log.kick();
			}
			if (doBan) {
				log.ban();
			}
			String msg = log.getLogMessage(true, true);
			if (doLog) {
				log.add();
				PLUGIN.info(msg);
			}
			if (!notify.isEmpty()) {
				String color = log.isAllowed() ? Colors.Gold : Colors.Rose;
				if (notify.contains("*")) {
					Chat.toBroadcast((Colors.White + String.format("[%d] ", log.getId()))
						+ (color + msg));
				} else {
					for (Player p : etc.getServer().getPlayerList()) {
						for (String g : p.getGroups()) {
							if (notify.contains(g.toLowerCase())) {
								Chat.toPlayer(p,
									(Colors.White + String.format("[%d] ", log.getId()))
										+ (color + msg));
							}
						}
					}
				}
			}
		}
		return allowed;
	}
}
