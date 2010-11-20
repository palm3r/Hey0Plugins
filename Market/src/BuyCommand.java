public class BuyCommand extends Command {
	private Market plugin;

	public BuyCommand(Market plugin) {
		super(false, new String[] { "/buy" }, "<amount> <item>", "Buy items");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		MarketItem item = plugin.getGoods().get(player.getItemInHand());
		int amount = 1;
		if (args.length > 1) {
			try {
				amount = Integer.valueOf(args[1]);
				if (args.length > 2) {
					item = plugin.findItem(args[2]);
				}
			} catch (Exception e) {
			}
		}
		if (item == null) {
			Chat.toPlayer(player, getUsage(false));
			return true;
		}
		int payment = item.getActualPrice(true, amount);
		int money = plugin.getMoney(player.getName());
		if (payment > money) {
			Chat.toPlayer(player, Colors.Gold + "You need %s to buy (you have %s)",
					plugin.formatMoney(payment), plugin.formatMoney(money));
			return true;
		}
		if (!item.buy(amount)) {
			Chat.toPlayer(player, Colors.Rose
					+ "Sorry, an error occured. Try again later");
			return true;
		}
		Inventory inv = player.getInventory();
		inv.giveItem(item.getId(), amount);
		inv.updateInventory();
		plugin.setMoney(player.getName(), money - payment);
		plugin.saveGoods();
		Chat.toPlayer(player, Colors.LightGreen + "You bought %d %s (paid %s)",
				amount, item.getName(), plugin.formatMoney(payment));
		Log.info("Market: %s bought %d %s for %d (money %d -> %d)",
				player.getName(), amount, item.getName(), payment, money, money
						- payment);
		return true;
	}
}
