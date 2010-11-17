public class MoneyCommand extends Command {

	private Market market;

	public MoneyCommand(Market market) {
		super(new String[] { "/money" }, null, "Show your money");
		this.market = market;
	}

	public boolean call(Player player, String[] args) {
		if (args.length > 1) {
			int money = market.getMoney(args[1]);
			if (args.length > 2) {
				try {
					money = Integer.valueOf(args[2]);
					market.setMoney(args[1], money);
				} catch (Exception e) {
					Chat.toPlayer(player, Colors.Rose + "Invalid argument: %s", args[2]);
					return true;
				}
			}
			Chat.toPlayer(player, Colors.Gold + "%s has %s now", args[1],
					market.currencyFormat(money));
		} else {
			int money = market.getMoney(player.getName());
			Chat.toPlayer(player, Colors.Gold + "You have %s now",
					market.currencyFormat(money));
		}
		return true;
	}

}
