public class MarketCommand extends Command {

	private Market market;

	public MarketCommand(Market market) {
		super(true, new String[] { "/market" }, "[item] <key value>",
				"Manage market");
		this.market = market;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, Colors.Rose + "Usage: " + Colors.White
					+ getUsage(false));
			return true;
		}
		Goods g = market.findGoods(args[1]);
		if (g == null) {
			Chat.toPlayer(player, Colors.Rose + "%s was not found in market", args[1]);
			return true;
		}
		for (int i = 2; i + 1 < args.length; i += 2) {
			try {
				String key = args[i], value = args[i + 1];
				if (key.equalsIgnoreCase("p")) {
					g.setPrice(Integer.valueOf(value));
				} else if (args[i].equalsIgnoreCase("s")) {
					g.setStock(Integer.valueOf(value));
				} else if (args[i].equalsIgnoreCase("b")) {
					g.setBalance(Integer.valueOf(value));
				} else if (args[i].equalsIgnoreCase("f")) {
					g.setFactor(Double.valueOf(value));
				} else if (args[i].equalsIgnoreCase("e")) {
					g.enable(Integer.valueOf(value) != 0);
				}
			} catch (Exception e) {
			}
		}
		market.saveGoods();
		Chat.toPlayer(player, Colors.Gold + "%s P %d S %d B %d F %f E %d A %d",
				g.getName(), g.getPrice(), g.getStock(), g.getBalance(), g.getFactor(),
				g.isEnabled() ? 1 : 0, g.getActualPrice(true, 1));
		return true;
	}

}
