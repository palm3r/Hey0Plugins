import java.util.*;

public class SellCommand extends Command {
	private Market plugin;

	public SellCommand(Market plugin) {
		super("/sell", null, null, "Sell items in craft-table", "/market");
		this.plugin = plugin;
	}

	public boolean call(Player player, String command, List<String> args) {
		int total = 0;
		Inventory ct = player.getCraftingTable();
		for (int slot = 0; slot < 4; ++slot) {
			Item i = ct.getItemFromSlot(slot);
			if (i == null)
				continue;
			int id = i.getItemId();
			int amount = i.getAmount();
			MarketItem item = plugin.getItems().get(id);
			if (item == null || !item.isEnabled()) {
				Chat.toPlayer(
					player,
					(Colors.LightGreen + (item != null ? item.getName() : "(id "
						+ i.getItemId() + ")"))
						+ (Colors.Rose + " is prohibited"));
				continue;
			}
			int price = item.getActualPrice(false, amount);
			if (!item.sell(amount)) {
				Chat.toPlayer(player, (Colors.Rose + "An error occured. ")
					+ (Colors.LightGreen + item.getName())
					+ (Colors.LightGreen + " was not sold"));
				continue;
			}
			ct.removeItem(slot);
			total += price;
			Chat.toPlayer(player, (Colors.LightBlue + amount) + " "
				+ (Colors.LightGreen + item.getName())
				+ (Colors.LightGray + " sold for ") + plugin.formatMoney(price));
			Log.info("Market: %s sold %d %s for %d", player.getName(), amount,
				item.getName(), price);
		}
		if (total == 0) {
			Chat.toPlayer(player, Colors.Rose
				+ "Put items into craft-table, and type /sell again");
			return true;
		}
		ct.updateInventory();
		long money = plugin.getMoney(player.getName());
		plugin.setMoney(player.getName(), money + total);
		plugin.saveItems();

		Chat.toPlayer(player,
			(Colors.LightGray + "You received ") + plugin.formatMoney(total));
		Log.info("Market: %s received %d total (money %d -> %d)", player.getName(),
			total, money, money + total);
		return true;
	}
}
