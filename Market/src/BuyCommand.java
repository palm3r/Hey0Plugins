import java.util.*;

public class BuyCommand extends Command {

	private Market plugin;

	public BuyCommand(Market plugin) {
		super("[item] <amount>", "Buy items");
		setRequire("/market");
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
			try {
				amount = Integer.valueOf(args.get(1));
			} catch (Exception e) {
				Chat.toPlayer(player, (Colors.Rose + "Amount must be integer"));
				return true;
			}
		}
		int payment = item.getActualPrice(amount);
		long money = plugin.getMoney(player.getName());
		if (payment > money) {
			Chat.toPlayer(player,
				(Colors.Rose + "You need ") + plugin.formatMoney(payment)
					+ (Colors.Rose + " to buy (you have ") + plugin.formatMoney(money)
					+ (Colors.Rose + ")"));
			return true;
		}

		// Check how many items the player can buy
		int maxItems = 0;
		Inventory inv = player.getInventory();
		for (int slot = 0; slot < 36; ++slot) {
			Item slotItem = inv.getItemFromSlot(slot);
			maxItems += slotItem == null ? 64 : (slotItem.getItemId() == item.getId()
				? 64 - slotItem.getAmount() : 0);
		}
		if (maxItems < amount) {
			Chat.toPlayer(player, maxItems == 0
				? (Colors.Rose + "Your inventory is full")
				: (Colors.Rose + "You can buy ") + (Colors.LightBlue + maxItems)
					+ (Colors.Rose + " items or less"));
			return true;
		}

		item.buy(amount);
		player.giveItem(item.getId(), amount);
		plugin.setMoney(player.getName(), money - payment);
		plugin.saveItems();
		Chat.toPlayer(player, (Colors.LightGray + "You bought ")
			+ (Colors.LightBlue + amount) + " "
			+ (Colors.LightGreen + item.getName()) + (Colors.LightGray + " for ")
			+ plugin.formatMoney(payment));
		plugin.info("%s %s %d %s for %d", player.getName(), getCommand(), amount,
			item.getName(), payment);
		plugin.info("%s money %d %d (%+d)", player.getName(), money, money
			- payment, -payment);
		return true;
	}
}
