import java.util.*;

public class Handler {

	private static String lastPlayer = null;
	private static Integer lastItemId = null;
	private static Event lastEvent = null;
	private static Location lastLocation = null;
	private static List<Location> history = new LinkedList<Location>();

	public static Location getLocation(int id) {
		return history.size() >= id ? history.get(id - 1) : null;
	}

	private PluginEx plugin;
	private Set<String> allow;
	private Set<String> deny;
	private Set<String> notify;
	private boolean log = false;
	private boolean kick = false;
	private boolean ban = false;

	public Handler(PluginEx plugin) {
		this.plugin = plugin;
		this.allow = new HashSet<String>();
		this.deny = new HashSet<String>();
		this.notify = new HashSet<String>();
	}

	public void set(String property, String value) {
		if (property.equalsIgnoreCase("allow")) {
			allow = new HashSet<String>();
			for (String group : StringTools.split(value, ",")) {
				allow.add(group.toLowerCase());
			}
		}
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
			log = Boolean.valueOf(value.toLowerCase());
		}

		if (property.equalsIgnoreCase("kick")) {
			kick = Boolean.valueOf(value.toLowerCase());
		}

		if (property.equalsIgnoreCase("ban")) {
			ban = Boolean.valueOf(value.toLowerCase());
		}

		// plugin.debug("%s = %s => %s", property, value, this.toString());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!allow.isEmpty()) {
			sb.append(String.format("allow=%s", CollectionTools.join(allow, ",")));
		}
		if (!notify.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(String.format("notify=%s", CollectionTools.join(notify, ",")));
		}
		List<String> actions = new ArrayList<String>();
		if (log) {
			actions.add("log");
		}
		if (kick) {
			actions.add("kick");
		}
		if (ban) {
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

	public boolean execute(Event event, Player player, Block block,
		Location location) {
		return execute(event, player, block.getType(), location);
	}

	public boolean execute(Event event, Player player, Item item,
		Location location) {
		return execute(event, player, item.getItemId(), location);
	}

	public boolean execute(Event event, Player player, BaseVehicle vehicle,
		Location location) {
		return execute(event, player, vehicle.getId(), location);
	}

	public boolean execute(Event event, Player player, int itemId,
		Location location) {
		boolean allowed = true;
		if (deny.contains("*")) {
			allowed = false;
		}
		for (String group : player.getGroups()) {
			allowed = !deny.contains(group.toLowerCase())
				|| allow.contains(group.toLowerCase());
		}
		if (!allowed) {
			Chat.toPlayer(player, Colors.Rose + "action denied");
		}

		if ((lastPlayer == null || !lastPlayer.equalsIgnoreCase(player.getName()))
			|| (lastItemId == null || lastItemId != itemId)
			|| (lastEvent == null || lastEvent != event)
			|| (lastLocation == null || (lastLocation.x != location.x
				|| lastLocation.y != location.y || lastLocation.z != location.z))) {
			history.add(location);

			String itemName = ItemNames.getName(itemId);
			String msg1 = String.format("[%d] %s ", history.size(), player.getName());
			String msg2 = String.format("%s %s", event.toString().toLowerCase(),
				itemName);
			String msg3 = String.format(" (%d, %d, %d)", (int) player.getX(),
				(int) player.getY(), (int) player.getZ());
			String msg4 = allowed ? "" : " DENIED";

			// kick
			if (kick) {
				player.kick(String.format("Automatic kick (reason: %s)", msg2));
				Chat.toBroadcast(Colors.Rose + "%s was kicked (reason: %s)",
					player.getName(), msg2);
				msg4 += " KICKED";
			}

			// ban
			if (ban) {
				etc.getServer().ban(player.getName());
				player.kick(String.format("Automatic ban (reason: %s)", msg2));
				Chat.toBroadcast(Colors.Rose + "%s was banned (reason: %s)",
					player.getName(), msg2);
				msg4 += " BANNED";
			}

			String msg = msg1 + msg2 + msg3 + msg4;

			// log
			if (log) {
				plugin.info(msg);
			}

			// notify
			if (notify.contains("*")) {
				Chat.toBroadcast(Colors.Rose + msg);
			} else {
				for (Player p : etc.getServer().getPlayerList()) {
					for (String g : p.getGroups()) {
						if (notify.contains(g.toLowerCase())) {
							Chat.toPlayer(p, Colors.Rose + msg);
						}
					}
				}
			}

			lastPlayer = player.getName();
			lastItemId = itemId;
			lastEvent = event;
			lastLocation = location;
		}
		return allowed;
	}

}
