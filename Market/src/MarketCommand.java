import java.util.*;

/**
 * market command
 * 
 * @author palm3r
 */
public class MarketCommand extends Command {

	private Market plugin;

	public MarketCommand(Market plugin) {
		super("[item] <key value>", "Manage market");
		setRequire("/market-admin");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
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

		// parse key and value
		Double currentPrice = null;
		Double targetPrice = null;
		Double volatility = null;
		Boolean enabled = null;
		for (int i = 1; i + 1 < args.size(); i += 2) {
			String key = args.get(i), value = args.get(i + 1);
			try {
				if (key.equalsIgnoreCase("p")) {
					currentPrice = Double.valueOf(value);
				} else if (key.equalsIgnoreCase("t")) {
					targetPrice = Double.valueOf(value);
				} else if (key.equalsIgnoreCase("v")) {
					volatility = Double.valueOf(value);
				} else if (key.equalsIgnoreCase("e")) {
					enabled = Integer.valueOf(value) != 0;
				}
			} catch (Exception e) {
				Chat.toPlayer(player, (Colors.Rose + "Invalid key/value: ")
					+ (Colors.White + key + " " + value));
			}
		}

		// update
		if (currentPrice != null || targetPrice != null || volatility != null
			|| enabled != null) {
			// current price changes
			if (currentPrice != null) {
				if (currentPrice < MarketItem.getMinimumPrice()) {
					Chat.toPlayer(player, (Colors.Rose + "Price should be ")
						+ (Colors.LightBlue + MarketItem.getMinimumPrice())
						+ (Colors.Rose + " or more"));
				} else {
					double old = item.getCurrentPrice();
					item.setCurrentPrice(currentPrice);
					plugin.info("%s %s %s current %f %f", player.getName(), getCommand(),
						item.getName(), old, currentPrice);
				}
			}
			// target price changes
			if (targetPrice != null) {
				if (targetPrice < MarketItem.getMinimumPrice()) {
					Chat.toPlayer(player, (Colors.Rose + "Price should be ")
						+ (Colors.LightBlue + MarketItem.getMinimumPrice())
						+ (Colors.Rose + " or more"));
				} else {
					double old = item.getTargetPrice();
					item.setTargetPrice(targetPrice);
					plugin.info("%s %s %s target %f %f", player.getName(), getCommand(),
						item.getName(), old, targetPrice);
				}
			}
			// volatility changes
			if (volatility != null) {
				if (volatility < 0) {
					Chat.toPlayer(player, (Colors.Rose + "Volatility should be ")
						+ (Colors.LightBlue + 0) + (Colors.Rose + " or more"));
				} else {
					double old = item.getVolatility();
					item.setVolatility(volatility);
					plugin.info("%s %s %s volatility %f %f", player.getName(),
						getCommand(), item.getName(), old, volatility);
				}
			}
			// enabled changes
			if (enabled != null) {
				if ((!item.isEnabled() && enabled)
					&& item.getCurrentPrice() < MarketItem.getMinimumPrice()) {
					Chat.toPlayer(player, (Colors.Rose + "Please set price to ")
						+ (Colors.LightBlue + MarketItem.getMinimumPrice())
						+ (Colors.Rose + " or more before enable"));
				} else {
					boolean old = item.isEnabled();
					item.enable(enabled);
					plugin.info("%s %s %s enabled %d %d", player.getName(), getCommand(),
						item.getName(), old ? 1 : 0, enabled ? 1 : 0);
				}
			}
			// update item info file
			plugin.saveItems();
		}

		// show message
		Chat.toPlayer(player, (Colors.LightGreen + item.getName())
			+ (Colors.Gold + " P %.3f") + (Colors.Yellow + " T %.3f")
			+ (Colors.LightPurple + " V %.3f")
			+ ((item.isEnabled() ? Colors.LightBlue : Colors.Rose) + " E %d"),
			item.getCurrentPrice(), item.getTargetPrice(), item.getVolatility(),
			item.isEnabled() ? 1 : 0);

		return true;
	}
}
