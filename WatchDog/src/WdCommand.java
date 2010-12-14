import java.util.*;
import java.util.Map.Entry;

public class WdCommand extends Command {

	private static final int PAGESIZE = 10;

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
								int page =
									c.args.isEmpty() ? 1 : Integer.valueOf(c.args.get(0));
								int maxPages =
									(int) Math.ceil((double) Log.size() / (double) PAGESIZE);
								List<Log> list = new LinkedList<Log>(Log.get());
								Collections.reverse(list);
								int index = (page - 1) * PAGESIZE;
								list =
									list.subList(index, index + PAGESIZE <= list.size() ? index
										+ PAGESIZE : list.size());
								Chat.toPlayer(c.player,
									(Colors.LightBlue + "WatchDog log (page %d/%d)"), page,
									maxPages);
								if (list != null) {
									Date now = new Date();
									for (Log log : list) {
										log.send(c.player, now);
									}
								}
								Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
									+ (Colors.LightBlue + "/wd log <page>"));
							} catch (Exception e) {
							}
							return true;
						}
					});
				put(new String[] { "player", "-p" },
					new Action<WdCommand.Context, Boolean>() {
						public Boolean action(Context c) {
							if (c.args.isEmpty()) {
								Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
									+ (Colors.White + "/wd player [name]"));
							} else {
								try {
									String name = c.args.get(0).toLowerCase();
									List<Log> list = new LinkedList<Log>(Log.get());
									Collections.reverse(list);
									Chat.toPlayer(c.player,
										(Colors.LightBlue + "WatchDog log (player %s)"), name);
									int count = 0;
									Date now = new Date();
									for (Log log : list) {
										if (log.getPlayer().equalsIgnoreCase(name)) {
											log.send(c.player, now);
											if (++count > 10)
												break;
										}
									}
									if (count == 0) {
										Chat.toPlayer(c.player, (Colors.Rose + "Log not found"));
									}
								} catch (Exception e) {
								}
							}
							return true;
						}
					});
				put(new String[] { "near", "-n" },
					new Action<WdCommand.Context, Boolean>() {
						public Boolean action(Context c) {
							try {
								int size =
									c.args.isEmpty() ? 10 : Integer.valueOf(c.args.get(0));
								List<Log> list = new LinkedList<Log>(Log.get());
								Collections.reverse(list);
								Chat.toPlayer(c.player,
									(Colors.LightBlue + "WatchDog log (distance %d)"), size);
								int count = 0;
								Date now = new Date();
								for (Log log : list) {
									double distance = log.getDistance(c.player);
									if (distance <= size) {
										log.send(c.player, now);
										if (++count > 10)
											break;
									}
								}
								if (count == 0) {
									Chat.toPlayer(c.player, (Colors.Rose + "Log not found"));
								}
							} catch (Exception e) {
							}
							return true;
						}
					});
				put(new String[] { "go", "-g" }, new Action<Context, Boolean>() {
					public Boolean action(Context c) {
						if (c.args.isEmpty()) {
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd go [ID]"));
						} else {
							try {
								int id = Integer.valueOf(c.args.get(0));
								Log log = Log.get(id - 1);
								c.player.teleportTo(log.getLocation());
							} catch (Exception e) {
								Chat.toPlayer(c.player, (Colors.Rose + "Invalid ID specified"));
							}
						}
						return true;
					}
				});
				put(new String[] { "kick" }, new Action<Context, Boolean>() {
					public Boolean action(Context c) {
						if (c.args.isEmpty()) {
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd kick [ID]"));
						} else {
							try {
								int id = Integer.valueOf(c.args.get(0));
								Log log = Log.get(id - 1);
								log.kick();
							} catch (Exception e) {
								Chat.toPlayer(c.player, (Colors.Rose + "Invalid ID specified"));
							}
						}
						return true;
					}
				});
				put(new String[] { "ban" }, new Action<Context, Boolean>() {
					public Boolean action(Context c) {
						if (c.args.isEmpty()) {
							Chat.toPlayer(c.player, (Colors.Rose + "Usage: ")
								+ (Colors.White + "/wd ban [ID]"));
						} else {
							try {
								int id = Integer.valueOf(c.args.get(0));
								Log log = Log.get(id - 1);
								log.ban();
							} catch (Exception e) {
								Chat.toPlayer(c.player, (Colors.Rose + "Invalid ID specified"));
							}
						}
						return true;
					}
				});
			}
		};

	public WdCommand() {
		super("[log|player|near|go|kick|ban]", "WatchDog commands");
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
}
