import java.util.*;

public class SellCommand extends Command {
	private Market plugin;

	public SellCommand(Market plugin) {
		super(false, new String[] { "/sell" }, "<amount> <item>", "Sell items");
		this.plugin = plugin;
	}

	public boolean call(Player player, String[] args) {
		Map<Integer, Integer> items = new TreeMap<Integer, Integer>();
		Inventory ct = player.getCraftingTable();
		for (int slot = 0; slot < 4; ++slot) {
			Item item = ct.getItemFromSlot(slot);
			if (item == null)
				continue;
			int amount = item.getAmount();
			int id = item.getItemId();
			if (items.containsKey(id)) {
				amount += items.get(id);
			}
			items.put(id, amount);
		}
		if (items.isEmpty()) {
			Chat.toPlayer(player, Colors.Rose + "No items in crafting table");
			return true;
		}
		int total = 0;
		Map<Integer, Integer> rest = new TreeMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
			int id = entry.getKey();
			int amount = entry.getValue();
			MarketItem item = plugin.getGoods().get(id);
			if (item == null || !item.isEnabled()) {
				rest.put(id, amount);
				continue;
			}
			int price = (int) Math.floor(item.getActualPrice(false, amount)
					* plugin.getSalesPriceRate());
			if (!item.sell(amount)) {
				rest.put(id, amount);
				Chat.toPlayer(player,
						Colors.Rose + "An error occured. %s was not sold", item.getName());
				continue;
			}
			total += price;
			Chat.toPlayer(player, Colors.LightGreen + "%d %s sold for %s", amount,
					item.getName(), plugin.formatMoney(price));
			Log.info("Market: %s sold %d %s for %d", player.getName(), amount,
					item.getName(), price);
		}
		ct.clearContents();
		for (Map.Entry<Integer, Integer> entry : rest.entrySet()) {
			int id = entry.getKey();
			int amount = entry.getValue();
			ct.giveItem(id, amount);
		}
		ct.updateInventory();
		int money = plugin.getMoney(player.getName());
		plugin.setMoney(player.getName(), money + total);
		plugin.saveGoods();
		Chat.toPlayer(player, Colors.LightGreen + "You received %s in total",
				plugin.formatMoney(total));
		Log.info("Market: %s received %d total (money %d -> %d)", player.getName(),
				total, money, money + total);
		return true;
	}
}
