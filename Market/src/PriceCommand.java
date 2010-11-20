public class PriceCommand extends Command {

	private Market plugin;

	public PriceCommand(Market market) {
		super(false, new String[] { "/price" }, "<amount> <item>", "Show price");
		this.plugin = market;
	}

	public boolean call(Player player, String[] args) {
		int amount = 1, index = 1;
		try {
			amount = Integer.valueOf(args[index]);
			index++;
		} catch (Exception e) {
		}
		String idName = args.length > index ? args[index] : String.valueOf(player
				.getItemInHand());
		MarketItem item = plugin.findItem(idName);
		if (item == null) {
			Chat.toPlayer(player, Colors.Rose + "Invalid item");
			return true;
		}
		Chat.toPlayer(
				player,
				Colors.Gold + "%d %s : buy %d sell %d",
				amount,
				item.getName(),
				item.getActualPrice(true, amount),
				(int) Math.floor(item.getActualPrice(false, amount)
						* plugin.getSalesPriceRate()));
		return true;
	}

}
