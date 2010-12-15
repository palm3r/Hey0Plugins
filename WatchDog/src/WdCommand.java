import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class WdCommand extends Command {

	public static int PAGESIZE = 8;

	private class Context {
		public Player player;
		public List<String> args;

		public Context(Player player, List<String> args) {
			this.player = player;
			this.args = args;
		}
	}

	@SuppressWarnings("serial")
	private Map<String[], Action<Context, Boolean>> handlers =
		new HashMap<String[], Action<Context, Boolean>>() {
			{
				put(new String[] { "log", "-l" },
					new Action<WdCommand.Context, Boolean>() {
						public Boolean action(Context c) {
							try {
								List<Record> list =
									WdCommand.query(String.format(
										"SELECT * FROM %s ORDER BY id DESC;", WatchDog.TABLE));
								int page =
									c.args.isEmpty() ? 1 : Integer.valueOf(c.args.get(0));
								WdCommand.show(c.player, null, list, page, null);
							} catch (Exception e) {
							}
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd log <page>"));
							return true;
						}
					});
				put(new String[] { "player", "-p" },
					new Action<WdCommand.Context, Boolean>() {
						public Boolean action(Context c) {
							try {
								String player = c.args.get(0);
								List<Record> list =
									WdCommand.query(String.format(
										"SELECT * FROM %s WHERE LOWER(player) = LOWER('%s') ORDER BY id DESC;",
										WatchDog.TABLE, player));
								int page =
									c.args.size() < 2 ? 1 : Integer.valueOf(c.args.get(1));
								WdCommand.show(c.player, String.format("(player %s)", player),
									list, page, null);
							} catch (Exception e) {
							}
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd player [player] <page>"));
							return true;
						}
					});
				put(new String[] { "search", "-s" },
					new Action<WdCommand.Context, Boolean>() {
						public Boolean action(final Context c) {
							try {
								List<Record> list =
									WdCommand.query(String.format(
										"SELECT * FROM %s ORDER BY id DESC;", WatchDog.TABLE));
								final int radius = Integer.valueOf(c.args.get(0));
								int page =
									c.args.size() < 2 ? 1 : Integer.valueOf(c.args.get(1));
								WdCommand.show(c.player, String.format("(radius %d)", radius),
									list, page, new Action<Record, String>() {
										public String action(Record record) {
											double distance =
												Math.abs(Math.sqrt(Math.pow(c.player.getX() - record.x,
													2)
													+ Math.pow(c.player.getY() - record.y, 2)
													+ Math.pow(c.player.getZ() - record.z, 2)));
											return distance > radius ? null : String.format("(%.2f)",
												distance);
										}
									});
							} catch (Exception e) {
							}
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd search [radius] <page>"));
							return true;
						}
					});
				put(new String[] { "go", "-g" }, new Action<Context, Boolean>() {
					public Boolean action(Context c) {
						try {
							int id = Integer.valueOf(c.args.get(0));
							List<Record> list =
								WdCommand.query(String.format(
									"SELECT * FROM %s WHERE id = %d;", WatchDog.TABLE, id));
							if (!list.isEmpty()) {
								Record record = list.get(0);
								Location location = new Location(record.x, record.y, record.z);
								c.player.teleportTo(location);
								return true;
							}
						} catch (Exception e) {
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd go [ID]"));
						}
						return true;
					}
				});
				put(new String[] { "kick" }, new Action<Context, Boolean>() {
					public Boolean action(Context c) {
						try {
							int id = Integer.valueOf(c.args.get(0));
							List<Record> list =
								WdCommand.query(String.format(
									"SELECT * FROM %s WHERE id = %d;", WatchDog.TABLE, id));
							if (!list.isEmpty()) {
								Record record = list.get(0);
								Actions.kick(record.event, record.player, record.target);
								return true;
							}
						} catch (Exception e) {
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd kick [ID]"));
						}
						return true;
					}
				});
				put(new String[] { "ban" }, new Action<Context, Boolean>() {
					public Boolean action(Context c) {
						try {
							int id = Integer.valueOf(c.args.get(0));
							List<Record> list =
								WdCommand.query(String.format(
									"SELECT * FROM %s WHERE id = %d;", WatchDog.TABLE, id));
							if (!list.isEmpty()) {
								Record record = list.get(0);
								Actions.ban(record.event, record.player, record.target);
								return true;
							}
						} catch (Exception e) {
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd ban [ID]"));
						}
						return true;
					}
				});
			}
		};

	public WdCommand() {
		super("[log|player|search|go|kick|ban]", "WatchDog commands");
		setRequire("/watchdog");
	}

	public boolean execute(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		String action = args.get(0).toLowerCase();
		for (Entry<String[], Action<Context, Boolean>> entry : handlers.entrySet()) {
			for (String alias : entry.getKey()) {
				if (alias.equalsIgnoreCase(action)) {
					return entry.getValue().action(
						new Context(player, args.subList(1, args.size())));
				}
			}
		}
		return true;
	}

	private static List<Record> query(String sql) throws SQLException {
		PreparedStatement stmt = WatchDog.CONN.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		List<Record> list = new LinkedList<Record>();
		while (rs.next()) {
			list.add(new Record(rs));
		}
		return list;
	}

	private static void show(Player player, String header, List<Record> list,
		int page, Action<Record, String> formatter) {
		int max = (int) Math.ceil((double) list.size() / (double) PAGESIZE);
		Chat.toPlayer(player, (Colors.LightBlue + "WatchDog %s(page %d/%d)"),
			header != null ? header + " " : "", page, max);

		int first = (page - 1) * PAGESIZE;
		int second =
			first + PAGESIZE <= list.size() ? first + PAGESIZE : list.size();
		list = list.subList(first, second);
		for (Record record : list) {
			String additional = formatter != null ? formatter.action(record) : null;
			if (formatter != null && additional == null)
				continue;

			StringBuilder sb = new StringBuilder();
			sb.append("[" + record.id + "]");
			sb.append(record.denied ? Colors.Rose : Colors.Gold);
			sb.append(" ");
			sb.append(String.format("%1$tm/%1$td %1$tH:%1$tM", record.time));
			sb.append(" ");
			sb.append(WatchDog.getMessage(record.player, record.event, record.target,
				record.x, record.y, record.z, record.denied, record.kicked,
				record.banned));
			if (additional != null) {
				sb.append(" ");
				sb.append(additional);
			}
			Chat.toPlayer(player, sb.toString());
		}
	}

}
