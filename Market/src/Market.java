import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.*;

public class Market extends PluginEx {

	public static final String ITEMS_FILE_KEY = "items-file";
	public static final String ITEMS_FILE_DEFAULT = "items.txt";

	public static final String MONEY_FILE_KEY = "money-file";
	public static final String MONEY_FILE_DEFAULT = "money.txt";

	public static final String UNIT_KEY = "currency-unit";
	public static final String UNIT_DEFAULT = "coin,coins";

	private Map<Integer, MarketItem> items;
	private Map<String, Long> bank;
	private List<Map.Entry<String, Long>> richestPlayersCache;
	private Pair<String, String> unit;
	private Command market, price, buy, sell, money, top5;

	public Market() {
		super("Market");

		items = new TreeMap<Integer, MarketItem>();
		bank = new TreeMap<String, Long>();
		market = new MarketCommand(this);
		price = new PriceCommand(this);
		buy = new BuyCommand(this);
		sell = new SellCommand(this);
		money = new MoneyCommand(this);
		top5 = new Top5Command(this);

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.LOW);
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.LOW);
	}

	public List<Map.Entry<String, Long>> getRichestPlayers() {
		if (richestPlayersCache == null) {
			richestPlayersCache = new ArrayList<Map.Entry<String, Long>>(
				bank.entrySet());
			Collections.sort(richestPlayersCache,
				new Comparator<Map.Entry<String, Long>>() {
					public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
						return o1.getValue() == o2.getValue() ? 0 : (o1.getValue() < o2
							.getValue() ? 1 : -1);
					}
				});
		}
		return richestPlayersCache;
	}

	public long getMoney(String player) {
		return bank.containsKey(player) ? bank.get(player) : 0;
	}

	public void setMoney(String player, long amount) {
		bank.put(player, amount);
		saveMoney();
		richestPlayersCache = null;
	}

	public String formatMoney(long amount) {
		return Colors.Gold + NumberFormat.getInstance().format(amount)
			+ Colors.LightGray + " " + (amount == 1 ? unit.first : unit.second);
	}

	public Map<Integer, MarketItem> getItems() {
		return items;
	}

	public MarketItem findItem(String idName) {
		MarketItem item = null;
		try {
			int id = Integer.valueOf(idName);
			item = items.get(id);
		} catch (Exception e) {
			for (Map.Entry<Integer, MarketItem> entry : items.entrySet()) {
				MarketItem g = entry.getValue();
				if (g.getName().equalsIgnoreCase(idName)) {
					item = g;
					break;
				}
			}
		}
		return item;
	}

	protected void onEnable() {
		String[] u = getProperty(UNIT_KEY, UNIT_DEFAULT).split(",");
		unit = Pair.create(u[0], u.length > 1 ? u[1] : u[0]);
		loadMoney();
		loadItems();
		addCommand(market, price, buy, sell, money, top5);
	}

	protected void onDisable() {
		removeCommand(market, price, buy, sell, money, top5);
	}

	private void loadMoney() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			bank = loadMap(fileName, new Converter<String, Pair<String, Long>>() {
				public Pair<String, Long> convertTo(String value) {
					try {
						String[] split = value.split(":", 2);
						return new Pair<String, Long>(split[0], Long.valueOf(split[1]));
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			});
		} catch (IOException e) {
		}
	}

	public void saveMoney() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			saveMap(bank, fileName, new Converter<Pair<String, Long>, String>() {
				public String convertTo(Pair<String, Long> value) {
					return value.first + ":" + value.second.toString();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadItems() {
		try {
			String fileName = getProperty(ITEMS_FILE_KEY, ITEMS_FILE_DEFAULT);
			items = loadMap(fileName,
				new Converter<String, Pair<Integer, MarketItem>>() {
					public Pair<Integer, MarketItem> convertTo(String value) {
						try {
							MarketItem g = new MarketItem(value);
							return new Pair<Integer, MarketItem>(g.getId(), g);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}
				});
		} catch (Exception e) {
			saveItems();
		}
	}

	public void saveItems() {
		try {
			String fileName = getProperty(ITEMS_FILE_KEY, ITEMS_FILE_DEFAULT);
			saveMap(items, fileName,
				new Converter<Pair<Integer, MarketItem>, String>() {
					public String convertTo(Pair<Integer, MarketItem> value) {
						return value.second.toString();
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
