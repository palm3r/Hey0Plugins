import java.util.*;

public class MarketCommand extends Command {

	private Market plugin;

	public MarketCommand(Market plugin) {
		super("/market", null, "[item] <key value>", "Manage market",
			"/market-admin");
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		if (args.isEmpty()) {
			Chat.toPlayer(player, getUsage(false, true));
			return true;
		}
		String idName = args.get(0);
		MarketItem item = plugin.findItem(idName);
		if (item == null) {
			Chat.toPlayer(player, (Colors.LightGreen + idName)
				+ (Colors.Rose + " was not found in market"));
			return true;
		}
		for (int i = 1; i + 1 < args.size(); i += 2) {
			String key = args.get(i), value = args.get(i + 1);
			try {
				if (key.equalsIgnoreCase("p")) {
					double price = Double.valueOf(value);
					if (price < MarketItem.MINIMUM_PRICE) {
						Chat.toPlayer(player, (Colors.Rose + "Price should be ")
							+ (Colors.LightBlue + MarketItem.MINIMUM_PRICE)
							+ (Colors.Rose + " or more"));
					} else {
						item.setPrice(price);
					}
				} else if (key.equalsIgnoreCase("v")) {
					double volatility = Double.valueOf(value);
					if (volatility < 0) {
						Chat.toPlayer(player, (Colors.Rose + "Volatility should be ")
							+ (Colors.LightBlue + 0) + (Colors.Rose + " or more"));
					} else {
						item.setVolatility(volatility);
					}
				} else if (key.equalsIgnoreCase("e")) {
					boolean enabled = Integer.valueOf(value) != 0;
					if ((!item.isEnabled() && enabled)
						&& item.getPrice() < MarketItem.MINIMUM_PRICE) {
						Chat.toPlayer(player, (Colors.Rose + "Please set price to ")
							+ (Colors.LightBlue + MarketItem.MINIMUM_PRICE)
							+ (Colors.Rose + " or more before enable"));
					} else {
						item.enable(enabled);
					}
				}
			} catch (Exception e) {
				Chat.toPlayer(player, (Colors.Rose + "Invalid key/value: ")
					+ (Colors.White + key + " " + value));
			}
		}
		plugin.saveItems();
		Chat.toPlayer(player, (Colors.LightGreen + item.getName())
			+ (Colors.Gold + " price %.3f")
			+ (Colors.LightPurple + " volatility %.3f")
			+ ((item.isEnabled() ? Colors.LightBlue : Colors.Rose) + " enabled %d"),
			item.getPrice(), item.getVolatility(), item.isEnabled() ? 1 : 0);
		return true;
	}
}
