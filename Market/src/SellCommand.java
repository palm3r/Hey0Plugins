import java.util.*;

public class SellCommand extends Command {
	private Market market;

	public SellCommand(Market market) {
		super(new String[] { "/sell" }, "<amount> <item>", "Sell items");
		this.market = market;
	}

	public boolean call(Player player, String[] args) {
		Map<Integer, Integer> items = new HashMap<Integer, Integer>();
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
		Map<Integer, Integer> rest = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
			int id = entry.getKey();
			int amount = entry.getValue();
			Goods item = market.getGoods().get(id);
			if (item == null || !item.isEnabled()) {
				rest.put(id, amount);
				continue;
			}
			int price = item.getActualPrice(false, amount);
			if (!item.sell(amount)) {
				rest.put(id, amount);
				Chat.toPlayer(player,
						Colors.Rose + "An error occured. %s was not sold", item.getName());
				continue;
			}
			total += price;
			Chat.toPlayer(player, Colors.Gold + "%d %s sold for %s", amount,
					item.getName(), market.currencyFormat(price));
			Log.info("Market: %s SOLD %d %s received %s", player.getName(), amount,
					item.getName(), market.currencyFormat(price));
		}
		ct.clearContents();
		for (Map.Entry<Integer, Integer> entry : rest.entrySet()) {
			int id = entry.getKey();
			int amount = entry.getValue();
			ct.giveItem(id, amount);
		}
		ct.updateInventory();
		int money = market.getMoney(player.getName()) + total;
		market.setMoney(player.getName(), money);
		market.saveGoods();
		market.saveBank();
		Chat.toPlayer(player, Colors.LightGreen + "You received %s in total",
				market.currencyFormat(total));
		Log.info("Market: %s has %s now", player.getName(),
				market.currencyFormat(money));
		return true;
	}
}
