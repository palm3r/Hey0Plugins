import java.util.*;

import org.apache.commons.lang.*;

public class WatchDogCommand extends Command {

	private abstract class Option {
		private final boolean value;

		public Option(boolean value) {
			this.value = value;
		}

		boolean isValueRequired() {
			return value;
		}

		public abstract String parse(String value, double x, double y, double z);
	}

	@SuppressWarnings("serial")
	private final Map<String, Option> options = new HashMap<String, Option>() {
		{
			put("-a", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("action LIKE UPPER('%%%s%%')", value);
				}
			});
			put("-A", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("action NOT LIKE UPPER('%%%s%%')", value);
				}
			});
			put("-b", new Option(false) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("banned = TRUE");
				}
			});
			put("-B", new Option(false) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("banned = FALSE");
				}
			});
			put("-d", new Option(false) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("denied = TRUE");
				}
			});
			put("-D", new Option(false) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("denied = FALSE");
				}
			});
			put("-k", new Option(false) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("kicked = TRUE");
				}
			});
			put("-K", new Option(false) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("kicked = FALSE");
				}
			});
			put("-l", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					String[] loc = StringUtils.split(value, ", ");
					return loc.length == 3 ? String.format("x = %s AND y = %s AND z = %s", loc[0], loc[1], loc[2]) : null;
				}
			});
			put("-L", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					String[] loc = StringUtils.split(value, ", ");
					return loc.length == 3 ? String.format("x != %s OR y != %s OR z != %s", loc[0], loc[1], loc[2]) : null;
				}
			});
			put("-r", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("%s >= ABS(SQRT(POWER(%f - x, 2) + POWER(%f - y, 2) + POWER(%f - z, 2)))", value, x, y,
						z);
				}
			});
			put("-R", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("%s < ABS(SQRT(POWER(%f - x, 2) + POWER(%f - y, 2) + POWER(%f - z, 2)))", value, x, y, z);
				}
			});
			put("-s", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("LOWER(player) LIKE LOWER('%%%1$s%%')", value);
				}
			});
			put("-S", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("LOWER(player) NOT LIKE LOWER('%%%1$s%%')", value);
				}
			});
			put("-t", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("(target_id = '%1$s' OR LOWER(target_name) LIKE LOWER('%%%1$s%%'))", value);
				}
			});
			put("-T", new Option(true) {
				@Override
				public String parse(String value, double x, double y, double z) {
					return String.format("(target_id != '%1$s' AND LOWER(target_name) NOT LIKE LOWER('%%%1$s%%'))", value);
				}
			});
		}
	};

	public WatchDogCommand() {
		super("[help|log|go|kick|ban]", WatchDog.class.getSimpleName() + " commands");
		setRequire("/watchdog");
		setAlias("/wd");
	}

	@Override
	public boolean execute(Player player, String command, final List<String> args) {
		try {
			String mode = args.isEmpty() ? "help" : args.remove(0).toLowerCase();
			if (mode.equals("help")) {
				Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
					+ (Colors.LightGray + "Commands"));
				Chat.player(false, player, (Colors.LightGray + "Log: ") + (Colors.White + "/wd log <options> ")
					+ (Colors.LightGray + "(see '/wd log -h' for details)"));
				Chat.player(false, player, (Colors.LightGray + "Warp: ") + (Colors.White + "/wd go [id]"));
				Chat.player(false, player, (Colors.LightGray + "Kick: ") + (Colors.White + "/wd kick [id]"));
				Chat.player(false, player, (Colors.LightGray + "Ban: ") + (Colors.White + "/wd ban [id]"));
				/*
				 * Chat.player(false, player, (Colors.LightGray + "Config: ")
				 * + (Colors.White + "/wd config [expression]"));
				 */
			} else if (mode.equals("l") || mode.equals("log")) {
				int page = 1, line = 8;
				Long x = null, y = null, z = null;

				List<String> unprocessedArgs = new LinkedList<String>();
				for (int index = 0; index < args.size(); ++index) {
					String a = args.get(index);
					if (!a.startsWith("-")) {
						unprocessedArgs.add(a);
						continue;
					} else if (a.equalsIgnoreCase("-h")) {
						Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
							+ (Colors.LightGray + "Log options"));
						Chat.player(false, player, (Colors.White + "-a [action] ")
							+ (Colors.LightGray + "Search by action (inverted: ") + (Colors.White + "-A") + (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.LightGray + "(Item actions: destroy, place, open, use, drop, pickup)"));
						Chat.player(false, player, (Colors.LightGray + "(Player actions: login, logout, attack, kill, teleport)"));
						Chat.player(false, player, (Colors.White + "-s [player] ")
							+ (Colors.LightGray + "Search by player (inverted: ") + (Colors.White + "-S") + (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-t [target] ")
							+ (Colors.LightGray + "Search by target id or name (inverted: ") + (Colors.White + "-T")
							+ (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-l [x,y,z] ")
							+ (Colors.LightGray + "Search by location (inverted: ") + (Colors.White + "-L")
							+ (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-r [range] ")
							+ (Colors.LightGray + "In range only (inverted: ") + (Colors.White + "-R") + (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-d ") + (Colors.LightGray + "Denied only (inverted: ")
							+ (Colors.White + "-D") + (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-k ") + (Colors.LightGray + "Kicked only (inverted: ")
							+ (Colors.White + "-K") + (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-b ") + (Colors.LightGray + "Banned only (inverted: ")
							+ (Colors.White + "-B") + (Colors.LightGray + ")"));
						Chat.player(false, player, (Colors.White + "-p [page] ") + (Colors.LightGray + "Select page"));
						// Chat.player(false, player, (Colors.White + "-- ")
						// + (Colors.LightGray + "Use previous options"));
						return true;
					} else if (a.equalsIgnoreCase("-p")) {
						try {
							page = Integer.valueOf(args.get(++index));
						} catch (Exception e) {
							Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
								+ (Colors.LightGray + "'-p' option requires page number"));
							return true;
						}
					} else if (a.equalsIgnoreCase("-l")) {
						String value = unprocessedArgs.get(++index);
						try {
							String[] loc = StringUtils.split(value, ", ");
							x = Long.valueOf(loc[0]);
							y = Long.valueOf(loc[1]);
							z = Long.valueOf(loc[2]);
						} catch (Exception e) {
							Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ") + (Colors.White + value)
								+ (Colors.LightGray + " is unrecognizable as location"));
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

				List<String> conditions = new LinkedList<String>();
				for (int index = 0; index < unprocessedArgs.size(); ++index) {
					String key = unprocessedArgs.get(index);
					if (!options.containsKey(key)) {
						Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
							+ (Colors.LightGray + "'%s' is not supported"), key);
						return true;
					} else {
						Option opt = options.get(key);
						String value = null;
						if (opt.isValueRequired()) {
							try {
								value = unprocessedArgs.get(++index);
							} catch (Exception e) {
								Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
									+ (Colors.LightGray + "'%s' option requires parameter"), key);
								return true;
							}
						}
						conditions.add(opt.parse(value, x, y, z));
					}
				}

				String[] c = conditions.toArray(new String[0]);
				int count = DataSet.count(Log.class, c);
				List<Log> list = DataSet.get(Log.class, line, (page - 1) * line, "time DESC", c);

				int max = line > 0 ? (int) Math.ceil((double) count / (double) line) : 0;
				Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
					+ (Colors.LightGray + "%d log%s found (page %d/%d)"), count, count > 1 ? "s" : "", page, max);

				if (!args.isEmpty()) {
					Chat.player(false, player, (Colors.LightGray + "option: ") + (Colors.White + StringUtils.join(args, " ")));
				}

				for (Log log : list) {
					StringBuilder sb = new StringBuilder();
					sb.append("[" + log.getId() + "]");
					sb.append(log.getColor());
					sb.append(" ");
					sb.append(String.format("%1$tm/%1$td %1$tH:%1$tM", log.time));
					sb.append(" ");
					sb.append(log.getMessage());
					Chat.player(false, player, sb.toString());
				}
			} else if (mode.equalsIgnoreCase("go") || mode.equalsIgnoreCase("kick") || mode.equalsIgnoreCase("ban")) {
				Long id = Long.valueOf(args.get(0));
				Log log = DataSet.get(Log.class, id);
				if (log == null) {
					Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
						+ (Colors.LightGray + "Log (id %d) is not found"), id);
					return true;
				}
				if (mode.equalsIgnoreCase("go")) {
					player.teleportTo(new Location(log.x, log.y, log.z));
				}
				if (mode.equalsIgnoreCase("kick")) {
					Actions.kick(log.player, log.action, log.target_name);
				}
				if (mode.equalsIgnoreCase("ban")) {
					Actions.ban(log.player, log.action, log.target_name);
				}
			} else {
				Chat.player(false, player, (Colors.Rose + WatchDog.class.getSimpleName() + ": ")
					+ (Colors.LightGray + "'%s' is not supported"), mode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
