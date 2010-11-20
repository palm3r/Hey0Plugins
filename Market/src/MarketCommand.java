public class MarketCommand extends Command {

	private Market plugin;

	public MarketCommand(Market market) {
		super(true, new String[] { "/market" }, "[item] <key value>",
				"Manage market");
		this.plugin = market;
	}

	public boolean call(Player player, String[] args) {
		if (args.length < 2) {
			Chat.toPlayer(player, Colors.Rose + "Usage: " + Colors.White
					+ getUsage(false));
			return true;
		}
		MarketItem g = plugin.findItem(args[1]);
		if (g == null) {
			Chat.toPlayer(player, Colors.Rose + "%s was not found in market", args[1]);
			return true;
		}
		for (int i = 2; i + 1 < args.length; i += 2) {
			try {
				String key = args[i], value = args[i + 1];
				if (key.equalsIgnoreCase("p")) {
					g.setPrice(Double.valueOf(value));
				} else if (key.equalsIgnoreCase("b")) {
					g.setBalance(Integer.valueOf(value));
				} else if (key.equalsIgnoreCase("f")) {
					g.setFactor(Double.valueOf(value));
				} else if (key.equalsIgnoreCase("e")) {
					g.enable(Integer.valueOf(value) != 0);
				}
			} catch (Exception e) {
			}
		}
		plugin.saveGoods();
		Chat.toPlayer(
				player,
				Colors.Gold + "%s A %d/%d P %.3f F %.3f B %d E %d",
				g.getName(),
				g.getActualPrice(true, 1),
				(int) Math.floor(g.getActualPrice(false, 1)
						* plugin.getSalesPriceRate()), g.getPrice(), g.getFactor(),
				g.getBalance(), g.isEnabled() ? 1 : 0);
		return true;
	}

}
