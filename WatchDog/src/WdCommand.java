import java.sql.ResultSet;
import java.util.*;

public class WdCommand extends Command {

	private abstract class Option {
		private boolean value;

		public Option(boolean value) {
			this.value = value;
		}

		boolean isValueRequired() {
			return value;
		}

		public abstract String parse(String value, double x, double y, double z);
	}

	@SuppressWarnings("serial")
	private Map<String, Option> options = new HashMap<String, Option>() {
		{
			put("-b", new Option(false) {
				public String parse(String value, double x, double y, double z) {
					return String.format("banned = TRUE");
				}
			});
			put("-B", new Option(false) {
				public String parse(String value, double x, double y, double z) {
					return String.format("banned = FALSE");
				}
			});
			put("-d", new Option(false) {
				public String parse(String value, double x, double y, double z) {
					return String.format("denied = TRUE");
				}
			});
			put("-D", new Option(false) {
				public String parse(String value, double x, double y, double z) {
					return String.format("denied = FALSE");
				}
			});
			put("-e", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format("event LIKE UPPER('%%%s%%')", value);
				}
			});
			put("-E", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format("event = UPPER('%s')", value);
				}
			});
			put("-k", new Option(false) {
				public String parse(String value, double x, double y, double z) {
					return String.format("kicked = TRUE");
				}
			});
			put("-K", new Option(false) {
				public String parse(String value, double x, double y, double z) {
					return String.format("kicked = FALSE");
				}
			});
			put("-l", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					String[] loc = StringTools.split(value, ",");
					return loc.length == 3 ? String.format(
						"x = %s AND y = %s AND z = %s", loc[0], loc[1], loc[2]) : null;
				}
			});
			put("-n", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format("LOWER(player) LIKE LOWER('%%%s%%')", value);
				}
			});
			put("-N", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format("LOWER(player) = LOWER('%s')", value);
				}
			});
			put("-r", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format(
						"%s >= ABS(SQRT(POWER(%f - x, 2) + POWER(%f - y, 2) + POWER(%f - z, 2)))",
						value, x, y, z);
				}
			});
			put("-R", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format(
						"%s < ABS(SQRT(POWER(%f - x, 2) + POWER(%f - y, 2) + POWER(%f - z, 2)))",
						value, x, y, z);
				}
			});
			put("-t", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format("LOWER(target) LIKE LOWER('%%%s%%')", value);
				}
			});
			put("-T", new Option(true) {
				public String parse(String value, double x, double y, double z) {
					return String.format("LOWER(target) = LOWER('%s')", value);
				}
			});
		}
	};

	public WdCommand() {
		super("[help|log|go|kick|ban]", WatchDog.class.getSimpleName()
			+ " commands");
		setRequire("/watchdog");
	}

	public boolean execute(Player player, String command, List<String> args) {
		try {
			String action = args.isEmpty() ? "help" : args.get(0).toLowerCase();
			if (action.equals("help")) {
				Chat.toPlayer(player,
					(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
						+ (Colors.LightGray + "Commands"));
				Chat.toPlayer(player, (Colors.LightGray + "Log: ")
					+ (Colors.White + "/wd log <options> ")
					+ (Colors.LightGray + "(use '/wd log -h' for details)"));
				Chat.toPlayer(player, (Colors.LightGray + "Warp: ")
					+ (Colors.White + "/wd go [id]"));
				Chat.toPlayer(player, (Colors.LightGray + "Kick: ")
					+ (Colors.White + "/wd kick [id]"));
				Chat.toPlayer(player, (Colors.LightGray + "Ban: ")
					+ (Colors.White + "/wd ban [id]"));
				// Chat.toPlayer(player, (Colors.LightGray + "Config: ") + (Colors.White
				// + "/wd config [expression]"));
			} else if (action.equals("log")) {
				int page = 1, line = 8;
				Long x = null, y = null, z = null;

				List<String> unprocessedArgs = new LinkedList<String>();
				for (int index = 1; index < args.size(); ++index) {
					String a = args.get(index);
					if (!a.startsWith("-")) {
						unprocessedArgs.add(a);
						continue;
					} else if (a.equalsIgnoreCase("-h")) {
						Chat.toPlayer(player,
							(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
								+ (Colors.LightGray + "Log options"));
						Chat.toPlayer(player, (Colors.White + "-n [player] ")
							+ (Colors.LightGray + "Search by player (partial: "
								+ Colors.White + "-N" + Colors.LightGray + ")"));
						Chat.toPlayer(player, (Colors.White + "-e [event] ")
							+ (Colors.LightGray + "Search by event (partial: " + Colors.White
								+ "-E" + Colors.LightGray + ")"));
						Chat.toPlayer(
							player,
							(Colors.LightGray + "(Item events: destroy, place, use, drop, pickup)"));
						Chat.toPlayer(
							player,
							(Colors.LightGray + "(Player events: login, logout, attack, kill, teleport)"));
						Chat.toPlayer(player, (Colors.White + "-t [target] ")
							+ (Colors.LightGray + "Search by target (partial: "
								+ Colors.White + "-T" + Colors.LightGray + ")"));
						Chat.toPlayer(player, (Colors.White + "-l [x,y,z] ")
							+ (Colors.LightGray + "Search by location"));
						Chat.toPlayer(player, (Colors.White + "-r [range] ")
							+ (Colors.LightGray + "In range only (inverted: " + Colors.White
								+ "-R" + Colors.LightGray + ")"));
						Chat.toPlayer(player, (Colors.White + "-d ")
							+ (Colors.LightGray + "Denied only (inverted: " + Colors.White
								+ "-D" + Colors.LightGray + ")"));
						Chat.toPlayer(player, (Colors.White + "-k ")
							+ (Colors.LightGray + "Kicked only (inverted: " + Colors.White
								+ "-K" + Colors.LightGray + ")"));
						Chat.toPlayer(player, (Colors.White + "-b ")
							+ (Colors.LightGray + "Banned only (inverted: " + Colors.White
								+ "-B" + Colors.LightGray + ")"));
						return true;
					} else if (a.equalsIgnoreCase("-p")) {
						try {
							page = Integer.valueOf(args.get(++index));
						} catch (Exception e) {
							Chat.toPlayer(player,
								(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
									+ (Colors.LightGray + "'-p' option requires page number"));
							return true;
						}
					} else if (a.equalsIgnoreCase("-l")) {
						String value = unprocessedArgs.get(++index);
						try {
							String[] loc = StringTools.split(value, ",");
							x = Long.valueOf(loc[0]);
							y = Long.valueOf(loc[1]);
							z = Long.valueOf(loc[2]);
						} catch (Exception e) {
							Chat.toPlayer(player,
								(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
									+ (Colors.White + value)
									+ (Colors.LightGray + " is not unrecognizable as location"));
							return true;
						}
					} else {
						unprocessedArgs.add(a);
					}
				}
				if (x == null || y == null || z == null) {
					x = Math.round(player.getX());
					y = Math.round(player.getY());
					z = Math.round(player.getZ());
				}

				Map<String, String> map = new LinkedHashMap<String, String>();
				for (int index = 0; index < unprocessedArgs.size(); ++index) {
					String key = unprocessedArgs.get(index);
					if (!options.containsKey(key)) {
						Chat.toPlayer(player,
							(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
								+ (Colors.LightGray + "'%s' is not supported"), key);
						return true;
					} else {
						Option opt = options.get(key);
						String value = null;
						if (opt.isValueRequired()) {
							try {
								value = unprocessedArgs.get(++index);
							} catch (Exception e) {
								Chat.toPlayer(player,
									(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
										+ (Colors.LightGray + "'%s' option requires parameter"),
									key);
								return true;
							}
						}
						map.put(key, opt.parse(value, x, y, z));
					}
				}
				String where = CollectionTools.join(map.values(), " AND ");

				ResultSet rs =
					WatchDog.query(String.format("SELECT COUNT(*) FROM %s%s;",
						WatchDog.TABLE, where.isEmpty() ? "" : " WHERE " + where));
				int count = rs.next() ? rs.getInt(1) : 0;

				List<Record> list =
					Record.query(String.format(
						"SELECT * FROM %s%s ORDER BY id DESC LIMIT %d OFFSET %d;",
						WatchDog.TABLE, where.isEmpty() ? "" : " WHERE " + where, line,
						(page - 1) * line));

				if (list.isEmpty()) {
					Chat.toPlayer(player,
						(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
							+ (Colors.LightGray + "No record found"));
				} else {
					int max = (int) Math.ceil((double) count / (double) line);
					Chat.toPlayer(player,
						(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
							+ (Colors.LightGray + "%d record%s found (page %d/%d)"), count,
						count > 1 ? "s" : "", page, max);
					for (Record record : list) {
						StringBuilder sb = new StringBuilder();
						sb.append("[" + record.id + "]");
						sb.append(WatchDog.getColor(record));
						sb.append(" ");
						sb.append(String.format("%1$tm/%1$td %1$tH:%1$tM", record.time));
						sb.append(" ");
						sb.append(WatchDog.getMessage(record.player, record.event,
							record.target, record.location, record.denied, record.kicked,
							record.banned));
						Chat.toPlayer(player, sb.toString());
					}
				}
			} else if (action.equals("go") || action.equals("kick")
				|| action.equals("ban")) {
				String id = args.get(1);
				List<Record> results =
					Record.query(String.format("SELECT * FROM %s WHERE id = %s;",
						WatchDog.TABLE, id));
				if (results.isEmpty()) {
					Chat.toPlayer(player,
						(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
							+ (Colors.LightGray + "record (id = %s) is not found"), id);
					return true;
				}
				Record record = results.get(0);
				if (action.equals("go")) {
					player.teleportTo(record.location);
				}
				if (action.equals("kick")) {
					Actions.kick(record.event, record.player, record.target);
				}
				if (action.equals("ban")) {
					Actions.ban(record.event, record.player, record.target);
				}
				/*
				 * } else if (action.equals("config")) {
				 * String expr = StringUtils.join(args.subList(1, args.size()), " ");
				 * if (!WatchDog.parse(expr)) {
				 * Chat.toPlayer(player,
				 * (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
				 * + (Colors.LightGray + "Parse error"));
				 * }
				 */
			} else {
				Chat.toPlayer(player,
					(Colors.Rose + WatchDog.class.getSimpleName() + ": ")
						+ (Colors.LightGray + "'%s' is not supported"), action);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
