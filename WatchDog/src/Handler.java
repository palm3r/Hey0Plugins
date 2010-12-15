import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

	private String _event = null;
	private String _name = null;
	private String _target = null;
	private Integer _x = null;
	private Integer _y = null;
	private Integer _z = null;

	public boolean execute(String event, Player player, String target,
		Location location) {
		int id = 0;
		String name = player.getName();
		int x = (int) location.x;
		int y = (int) location.y;
		int z = (int) location.z;

		boolean denied = test("deny", player);
		boolean kicked = test("kick", player);
		boolean banned = test("ban", player);

		if (denied) {
			Chat.toPlayer(
				player,
				Colors.Rose
					+ String.format("%s%s denied", event.toString().toLowerCase(),
						target != null ? " " + target : ""));
		}
		if (kicked) {
			Actions.kick(event, name, target);
		}
		if (banned) {
			Actions.ban(event, name, target);
		}

		if ((_event == null || !_event.equalsIgnoreCase(event))
			|| (_name == null || !_name.equalsIgnoreCase(name))
			|| (_target == null || !_target.equalsIgnoreCase(target))
			|| (_x == null || !_x.equals(x)) || (_y == null || !_y.equals(y))
			|| (_z == null || !_z.equals(z))) {

			_event = event;
			_name = name;
			_target = target;
			_x = x;
			_y = y;
			_z = z;

			boolean logged = test("log", player);
			if (logged) {
				try {
					PreparedStatement stmt =
						WatchDog.CONN.prepareStatement(
							String.format(
								"INSERT INTO %s (time,player,event,target,x,y,z,denied,kicked,banned) VALUES (?,?,?,?,?,?,?,?,?,?);",
								WatchDog.TABLE), PreparedStatement.RETURN_GENERATED_KEYS);
					stmt.setLong(1, new Date().getTime());
					stmt.setString(2, name);
					stmt.setString(3, event);
					stmt.setString(4, target);
					stmt.setInt(5, x);
					stmt.setInt(6, y);
					stmt.setInt(7, z);
					stmt.setBoolean(8, denied);
					stmt.setBoolean(9, kicked);
					stmt.setBoolean(10, banned);
					stmt.executeUpdate();
					ResultSet rs = stmt.getGeneratedKeys();
					if (rs.next()) {
						id = rs.getInt(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String msg =
				String.format("[%1$d]%2$s %3$s", id,
					denied ? Colors.Rose : Colors.Gold, WatchDog.getMessage(name, event,
						target, x, y, z, denied, kicked, banned));
			for (Player p : etc.getServer().getPlayerList()) {
				if (test("notify", p)) {
					Chat.toPlayer(p, msg);
				}
			}
		}
		return denied;
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
