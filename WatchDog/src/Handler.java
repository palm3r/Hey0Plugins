import java.util.*;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Handler {

	private Map<String, Set<String>> props = new HashMap<String, Set<String>>();

	public Handler() {
	}

	public void set(String key, String value) {
		Set<String> list =
			props.containsKey(key) ? props.get(key) : new HashSet<String>();
		list.clear();
		for (String group : StringTools.split(value, ",")) {
			list.add(group.toLowerCase());
		}
		props.put(key, list);
	}

	public String toString() {
		return new ToStringBuilder(this).append("props", props).toString();
	}

	public boolean execute(String event, Player player, Block block) {
		return execute(
			event,
			player,
			ItemNames.getName(block.getType()),
			new Location((int) player.getX(), (int) player.getY(),
				(int) player.getZ()));
	}

	public boolean execute(String event, Player player, Item item) {
		return execute(
			event,
			player,
			ItemNames.getName(item.getItemId()),
			new Location((int) player.getX(), (int) player.getY(),
				(int) player.getZ()));
	}

	public boolean execute(String event, Player player, BaseVehicle vehicle) {
		return execute(
			event,
			player,
			ItemNames.getName(vehicle.getId()),
			new Location((int) player.getX(), (int) player.getY(),
				(int) player.getZ()));
	}

	public boolean execute(String event, Player player) {
		return execute(event, player, null, new Location((int) player.getX(),
			(int) player.getY(), (int) player.getZ()));
	}

	public boolean execute(String event, Player player1, Player player2) {
		return execute(event, player1, player2.getName(), new Location(
			(int) player1.getX(), (int) player1.getY(), (int) player1.getZ()));
	}

	private Record _record = null;

	public boolean execute(String event, Player player, String target,
		Location location) {
		Record record = new Record();
		record.time = System.currentTimeMillis();
		record.player = player.getName();
		record.event = event;
		record.target = target;
		record.x = (int) location.x;
		record.y = (int) location.y;
		record.z = (int) location.z;
		record.denied = test("deny", player);
		record.kicked = test("kick", player);
		record.banned = test("ban", player);

		if (record.denied) {
			Chat.toPlayer(
				player,
				Colors.Rose
					+ String.format("%s%s denied", event.toString().toLowerCase(),
						target != null ? " " + target : ""));
		}
		if (record.kicked) {
			Actions.kick(event, record.player, target);
		}
		if (record.banned) {
			Actions.ban(event, record.player, target);
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
				String.format("[%1$d]%2$s %3$s", record.id, record.denied ? Colors.Rose
					: Colors.Gold, WatchDog.getMessage(record.player, record.event,
					record.target, record.x, record.y, record.z, record.denied,
					record.kicked, record.banned));
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
