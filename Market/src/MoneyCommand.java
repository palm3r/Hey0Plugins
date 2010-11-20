public class MoneyCommand extends Command {

	private Market plugin;

	public MoneyCommand(Market plugin) {
		super(false, new String[] { "/money" }, null, "Show your money");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		if (args.length > 1 && player.canUseCommand("/market")) {
			int money = plugin.getMoney(args[1]);
			if (args.length > 2 && player.isAdmin()) {
				try {
					int old = plugin.getMoney(args[1]);
					money = Integer.valueOf(args[2]);
					plugin.setMoney(args[1], money);
					Log.info("Market: %s set %s money %d to %d", player.getName(),
							args[1], old, money);
				} catch (Exception e) {
					Chat.toPlayer(player, Colors.Rose + "Invalid argument: %s", args[2]);
					return true;
				}
			}
			Chat.toPlayer(player, Colors.Gold + "%s has %s now", args[1],
					plugin.formatMoney(money));
		} else {
			int money = plugin.getMoney(player.getName());
			Chat.toPlayer(player, Colors.Gold + "You have %s now",
					plugin.formatMoney(money));
		}
		return true;
	}

}
