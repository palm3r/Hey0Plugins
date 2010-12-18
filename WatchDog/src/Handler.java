import java.util.*;
import org.apache.commons.lang.builder.*;

public class Handler {

	private final Map<String, Set<String>> props =
		new HashMap<String, Set<String>>();

	public Handler() {
	}

	public Set<String> get(String key) {
		return props.containsKey(key) ? props.get(key) : null;
	}

	public void set(String key, String op, Set<String> groups) {
		Set<String> list =
			props.containsKey(key) ? props.get(key) : new HashSet<String>();
		if (op.equals("=")) {
			list.clear();
			for (String group : groups) {
				list.add(group);
			}
		} else if (op.equals("+=")) {
			for (String group : groups) {
				list.add(group);
			}
		} else if (op.equals("-=")) {
			for (String group : groups) {
				list.remove(group);
			}
		}
		props.put(key, list);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("props", props).toString();
	}

	private Log _log = null;

	public boolean execute(Event action, String playerName, Integer targetId,
		String targetName, Location location) {
		Log log = new Log();
		log.time = System.currentTimeMillis();
		log.action = action;
		log.player = playerName;
		log.targetId = targetId;
		log.targetName = targetName;
		log.x = location.x;
		log.y = location.y;
		log.z = location.z;
		log.denied = test("deny", playerName);
		log.kicked = test("kick", playerName);
		log.banned = test("ban", playerName);

		if (log.kicked) {
			Actions.kick(log);
		}
		if (log.banned) {
			Actions.ban(log);
		}

		if (_log == null || !_log.equals(log)) {
			_log = log;

			boolean logged = test("log", playerName);
			if (logged) {
				try {
					Table.insert(log);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String msg =
				String.format("%1$s%2$s%3$s",
					log.id != null ? "[" + log.id + "] " : "", log.getColor(),
					log.getMessage());
			for (Player p : etc.getServer().getPlayerList()) {
				if (test("notify", p.getName())) {
					Chat.player(p, msg);
				}
			}
		}
		return log.denied;
	}

	private boolean test(String key, String srcName) {
		if (props.containsKey(key)) {
			Set<String> groups = props.get(key);
			if (groups.contains("*"))
				return true;
			else {
				Player player = etc.getServer().getPlayer(srcName);
				if (player != null) {
					List<String> playerGroups = Arrays.asList(player.getGroups());
					for (String g : groups) {
						if (playerGroups.contains(g))
							return true;
					}
				}
			}
		}
		return false;
	}

}
