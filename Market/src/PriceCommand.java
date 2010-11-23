import java.util.*;

public class PriceCommand extends Command {

	private Market plugin;

	public PriceCommand(Market plugin) {
		super("/price", null, "[item] <amount>", "Show price", "/market");
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
				+ (Colors.Rose + " is not found in market"));
			return true;
		}
		if (!item.isEnabled()) {
			Chat.toPlayer(player, (Colors.LightGreen + idName)
				+ (Colors.Rose + " is prohibited"));
			return true;
		}
		int amount = 1;
		if (args.size() > 1) {
			String amountString = args.get(1);
			try {
				amount = Integer.valueOf(amountString);
			} catch (Exception e) {
				Chat.toPlayer(player, (Colors.Rose + "Invalid parameter: ")
					+ (Colors.LightBlue + amountString));
				return true;
			}
		}
		Chat.toPlayer(
			player,
			(Colors.LightGreen + item.getName()) + (Colors.LightGray + " (amount ")
				+ (Colors.LightBlue + amount) + (Colors.LightGray + ") : ")
				+ plugin.formatMoney(item.getActualPrice(true, amount)));
		return true;
	}

}
