public class MarketCommand extends Command {

	private Market market;

	public MarketCommand(Market market) {
		super(new String[] { "/market" },
				"[item] <key:value>", "Manage market");
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

		if (player.canUseCommand("/market-admin")) {
			for (int i = 2; i < args.length; ++i) {
				try {
					String[] s = args[i].split(":", 2);
					String key = s[0], value = s[1];
					if (key.equalsIgnoreCase("n")) {
						g.setName(value);
					} else if (key.equalsIgnoreCase("p")) {
						g.setPrice(Integer.valueOf(value));
					} else if (args[i].equalsIgnoreCase("s")) {
						g.setStock(Integer.valueOf(value));
					} else if (args[i].equalsIgnoreCase("b")) {
						g.setBalance(Integer.valueOf(value));
					} else if (args[i].equalsIgnoreCase("f")) {
						g.setFactor(Double.valueOf(value));
					} else if (args[i].equalsIgnoreCase("e")) {
						if (value.equals("1"))
							g.enable(true);
						else if (value.equals("0"))
							g.enable(false);
					}
				} catch (Exception e) {
				}
			}
			market.saveGoods();
		}
		Chat.toPlayer(player, Colors.Gold + "%s p:%d s:%d b:%d f:%f e:%d a:%d",
				g.getName(), g.getPrice(), g.getStock(), g.getBalance(), g.getFactor(),
				g.isEnabled() ? 1 : 0, g.getActualPrice(true, 1));
		return true;
	}

}
