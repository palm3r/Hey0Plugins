import java.util.*;

public class Log {

	public enum Actions {
		DENIED, KICKED, BANNED;
	}

	private static List<Log> logs = new LinkedList<Log>();

	public static int size() {
		return logs.size();
	}

	public static List<Log> get() {
		return logs;
	}

	public static Log last() {
		return logs.isEmpty() ? null : logs.get(logs.size() - 1);
	}

	public static Log get(int index) {
		return logs.size() >= index ? logs.get(index) : null;
	}

	public static List<Log> get(int index, int count) {
		if (index + 1 > logs.size()) {
			return null;
		}
		int end = index + count;
		if (end + 1 > logs.size()) {
			end = logs.size();
		}
		return logs.subList(index, end);
	}

	public static void clean() {
		logs.clear();
	}

	private int id;
	private Date date;
	private String player;
	private Event event;
	private String target;
	private Location location;
	private Set<Actions> actions;

	public Log(Event event, Player player, String target, Location location) {
		this.id = logs.size() + 1;
		this.date = new Date();
		this.player = player.getName();
		this.location = location;
		this.event = event;
		this.target = target;
		this.actions = new HashSet<Actions>();
	}

	public void add() {
		logs.add(this);
		this.id = logs.size();
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public String getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}

	public Event getEvent() {
		return event;
	}

	public String getTarget() {
		return target;
	}

	public Set<Actions> getActions() {
		return actions;
	}

	public boolean isAllowed() {
		return !actions.contains(Actions.DENIED);
	}

	public double getDistance(Player p) {
		return Math
			.abs(Math.sqrt(Math.pow(p.getX() - location.x, 2)
				+ Math.pow(p.getY() - location.y, 2)
				+ Math.pow(p.getZ() - location.z, 2)));
	}

	public void deny() {
		actions.add(Actions.DENIED);
	}

	public void kick() {
		Player p = etc.getServer().getPlayer(player);
		if (p != null) {
			p.kick(String.format("Auto KICK (reason: %s%s)", event, target != null
				? " " + target : ""));
			Chat.toBroadcast(Colors.Rose + "%s was kicked (reason: %s%s)", player,
				event, target != null ? " " + target : "");
			actions.add(Actions.KICKED);
		}
	}

	public void ban() {
		kick();
		etc.getServer().ban(player);
		actions.add(Actions.BANNED);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Log))
			return false;
		Log log = (Log) obj;
		return (log.event == event)
			&& (log.player.equalsIgnoreCase(player))
			&& ((log.target == null && target == null) || log.target
				.equalsIgnoreCase(target))
			&& (log.location.x == location.x && log.location.y == location.y && log.location.z == location.z);
	}

	public int hashCode() {
		final int multiplier = 42463;
		int hash = Pair.class.hashCode();
		hash = multiplier * hash + (player == null ? 102199 : player.hashCode());
		hash = multiplier * hash + (event == null ? 102199 : event.hashCode());
		hash = multiplier * hash + (target == null ? 102199 : target.hashCode());
		hash =
			multiplier * hash + (location == null ? 102199 : location.hashCode());
		hash = multiplier * hash + (actions == null ? 102199 : actions.hashCode());
		return hash;
	}

	public String getLogMessage(boolean includeLocation, boolean includeActions) {
		StringBuilder sb = new StringBuilder();
		sb.append(player);
		sb.append(" ");
		sb.append(event.toString().toLowerCase()
			+ (target != null ? " " + target : ""));
		if (includeLocation) {
			sb.append(String.format(" (%d,%d,%d)", (int) location.x,
				(int) location.y, (int) location.z));
		}
		if (includeActions) {
			String act = CollectionTools.join(actions, " ");
			if (!act.isEmpty()) {
				sb.append(" " + act);
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	public void send(Player player, Date now) {
		String msg =
			String.format("[%1$d] %2$s"
				+ (date.getDay() != now.getDay() ? "%3$tF %3$tT " : "%3$tT ") + "%4$s",
				id, isAllowed() ? Colors.Gold : Colors.Rose, date,
				getLogMessage(true, true));
		Chat.toPlayer(player, msg);
	}
}
