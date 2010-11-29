import java.util.*;

public class SellCommand extends Command {

	private Market plugin;

	public SellCommand(Market plugin) {
		super(null, "Sell items in craft-table");
		setRequire("/market");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		Map<MarketItem, Integer> sold = new HashMap<MarketItem, Integer>();

		Inventory ct = player.getCraftingTable();
		for (int slot = 0; slot < 4; ++slot) {
			Item i = ct.getItemFromSlot(slot);
			if (i == null)
				continue;
			int id = i.getItemId();
			int amount = i.getAmount();
			MarketItem item = plugin.getItems().get(id);
			if (item == null || !item.isEnabled()) {
				Chat.toPlayer(player,
					(Colors.LightGreen + (item != null ? item.getName() : "(id " + id
						+ ")"))
						+ (Colors.Rose + " is prohibited"));
				continue;
			}
			ct.removeItem(slot);
			if (sold.containsKey(item)) {
				amount += sold.get(item);
			}
			sold.put(item, amount);
		}
		ct.updateInventory();

		int received = 0;
		for (Map.Entry<MarketItem, Integer> entry : sold.entrySet()) {
			MarketItem item = entry.getKey();
			int amount = entry.getValue();
			int price = item.getActualPrice(amount);
			item.sell(amount);
			received += price;
			Chat.toPlayer(player,
				(Colors.LightGray + "You sold ") + (Colors.LightBlue + amount) + " "
					+ (Colors.LightGreen + item.getName()) + (Colors.LightGray + " for ")
					+ plugin.formatMoney(price));
			plugin.info("%s %s %d %s for %d", player.getName(), getCommand(), amount,
				item.getName(), price);
		}

		if (received == 0) {
			Chat.toPlayer(player, Colors.Rose
				+ "Put items into craft-table, and type /sell again");
			return true;
		}

		long money = plugin.getMoney(player.getName());
		plugin.setMoney(player.getName(), money + received);
		plugin.saveItems();

		Chat.toPlayer(player,
			(Colors.LightGray + "You received ") + plugin.formatMoney(received)
				+ (Colors.LightGray + " in total"));
		plugin.info("%s money %d %d (%+d)", player.getName(), money, money
			+ received, received);
		return true;
	}
}
