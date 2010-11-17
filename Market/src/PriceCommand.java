public class PriceCommand extends Command {

	private Market market;

	public PriceCommand(Market market) {
		super(new String[] { "/price" }, "<amount> <item>", "Show price");
		this.market = market;
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
		Goods item = market.findGoods(idName);
		if (item == null) {
			Chat.toPlayer(player, Colors.Rose + "Invalid item");
			return true;
		}
		Chat.toPlayer(player, Colors.Gold + "%d %s : price %d stock %d", amount,
				item.getName(), item.getActualPrice(true, amount), item.getStock());
		return true;
	}

}
