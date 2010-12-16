import java.util.*;
import org.apache.commons.lang.builder.*;

public class Handler {

	private Map<String, Set<String>> props = new HashMap<String, Set<String>>();

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

	public String toString() {
		return new ToStringBuilder(this).append("props", props).toString();
	}

	public boolean execute(Event event, Player player, Block block) {
		return execute(event, player, ItemNames.getName(block.getType()),
			new Location(block.getX(), block.getY(), block.getZ()));
	}

	public boolean execute(Event event, Player player, Item item) {
		return execute(event, player, ItemNames.getName(item.getItemId()),
			player.getLocation());
	}

	public boolean execute(Event event, Player player, BaseVehicle vehicle) {
		return execute(event, player, ItemNames.getName(vehicle.getId()),
			player.getLocation());
	}

	public boolean execute(Event event, Player player) {
		return execute(event, player, null, player.getLocation());
	}

	public boolean execute(Event event, Player att, Player def) {
		return execute(event, att, def.getName(), att.getLocation());
	}

	private Record _record = null;

	public boolean execute(Event event, Player player, String target,
		Location location) {
		Record record = new Record();
		record.time = System.currentTimeMillis();
		record.player = player.getName();
		record.event = event.toString();
		record.target = target;
		record.location = location;
		record.denied = test("deny", player);
		record.kicked = test("kick", player);
		record.banned = test("ban", player);

		if (record.kicked) {
			Actions.kick(record.event, record.player, record.target);
		}
		if (record.banned) {
			Actions.ban(record.event, record.player, record.target);
		}

		if (_record == null || !_record.equals(record)) {
			_record = record;

			boolean logged = test("log", player);
			if (logged) {
				try {
					record.insert();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String msg =
				String.format("%1$s%2$s%3$s", record.id != null ? "[" + record.id
					+ "] " : "", WatchDog.getColor(record), WatchDog.getMessage(
					record.player, record.event, record.target, record.location,
					record.denied, record.kicked, record.banned));
			for (Player p : etc.getServer().getPlayerList()) {
				if (test("notify", p)) {
					Chat.toPlayer(p, msg);
				}
			}
		}
		return record.denied;
	}

	private boolean test(String key, Player player) {
		if (props.containsKey(key)) {
			Set<String> groups = props.get(key);
			if (groups.contains("*")) {
				return true;
			} else {
				List<String> playerGroups = Arrays.asList(player.getGroups());
				for (String g : groups) {
					if (playerGroups.contains(g)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
