import java.util.*;
import org.apache.commons.lang.builder.*;

public class WatchDogHandler {

	private final Map<String, Set<String>> props = new HashMap<String, Set<String>>();

	public WatchDogHandler() {
	}

	public Set<String> get(String key) {
		return props.containsKey(key) ? props.get(key) : null;
	}

	public void set(String key, String op, Set<String> groups) {
		Set<String> list = props.containsKey(key) ? props.get(key) : new HashSet<String>();
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

	public boolean execute(boolean doActions, WatchDogEvent action, Player player, Integer targetId,
		String targetName, Location location) {
		boolean denied = test("deny", player);
		boolean kicked = test("kick", player);
		boolean banned = test("ban", player);

		if (doActions) {
			if (kicked) {
				Actions.kick(player.getName(), action.toString(), targetName);
			}
			if (banned) {
				Actions.ban(player.getName(), action.toString(), targetName);
			}

			boolean logged = test("log", player);
			Log log =
				logged ? DataSet.add(Log.class, System.currentTimeMillis(), action.toString(),
					player.getName(), targetId != null ? targetId.toString() : null, targetName != null
						? targetName : null, location.x, location.y, location.z, denied, kicked, banned) : null;

			String color = log != null ? log.getColor() : Log.getColor(denied, kicked, banned);
			String msg =
				String.format(
					"%1$s%2$s%3$s",
					log != null ? "[" + log.getId() + "] " : "",
					color,
					log != null ? log.getMessage() : Log.getMessage(player.getName(), action.toString(),
						targetName, denied, kicked, banned));
			for (Player p : etc.getServer().getPlayerList()) {
				if (test("notify", p)) {
					Chat.player(false, p, msg);
				}
			}
		}
		return denied;
	}

	private boolean test(String key, Player player) {
		if (props.containsKey(key)) {
			Set<String> groups = props.get(key);
			if (groups.contains("*"))
				return true;
			else {
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
