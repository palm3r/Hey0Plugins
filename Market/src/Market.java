import java.io.IOException;
import java.util.*;

public class Market extends PluginEx {

	public static final String ITEMS_FILE_KEY = "items";
	public static final String ITEMS_FILE_DEFAULT = "items.txt";

	public static final String MONEY_FILE_KEY = "money";
	public static final String MONEY_FILE_DEFAULT = "money.txt";

	public static final String UNIT_KEY = "units";
	public static final String UNIT_DEFAULT = "coin,coins";

	public static final String SALES_PRICE_RATE_KEY = "sales-price";
	public static final String SALES_PRICE_RATE_DEFAULT = "0.5";

	private Map<Integer, MarketItem> items;
	private Map<String, Integer> money;
	private Pair<String, String> unit;
	private double salesPriceRate;

	public Market() {
		initPluginEx("Market", null, PluginListener.Priority.LOW,
				PluginLoader.Hook.COMMAND, PluginLoader.Hook.LOGIN);

		items = new TreeMap<Integer, MarketItem>();
		money = new TreeMap<String, Integer>();

		addCommand(new MoneyCommand(this));
		addCommand(new MarketCommand(this));
		addCommand(new PriceCommand(this));
		addCommand(new BuyCommand(this));
		addCommand(new SellCommand(this));
	}

	public double getSalesPriceRate() {
		return salesPriceRate;
	}

	public int getMoney(String player) {
		return money.containsKey(player) ? money.get(player) : 0;
	}

	public void setMoney(String player, int amount) {
		money.put(player, amount);
		saveMoney();
	}

	public String formatMoney(int amount) {
		return amount + " " + (amount == 1 ? unit.first : unit.second);
	}

	public Map<Integer, MarketItem> getGoods() {
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
		salesPriceRate = Double.valueOf(getProperty(SALES_PRICE_RATE_KEY,
				SALES_PRICE_RATE_DEFAULT));
		loadMoney();
		loadGoods();
	}

	private void loadMoney() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			money = load(fileName, new Converter<String, Pair<String, Integer>>() {
				public Pair<String, Integer> convert(String value) {
					try {
						String[] split = value.split(":", 2);
						return new Pair<String, Integer>(split[0], Integer
								.valueOf(split[1]));
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
			save(money, fileName, new Converter<Pair<String, Integer>, String>() {
				public String convert(Pair<String, Integer> value) {
					return value.first + ":" + value.second.toString();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadGoods() {
		try {
			String fileName = getProperty(ITEMS_FILE_KEY, ITEMS_FILE_DEFAULT);
			items = load(fileName,
					new Converter<String, Pair<Integer, MarketItem>>() {
						public Pair<Integer, MarketItem> convert(String value) {
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
			saveGoods();
		}
	}

	public void saveGoods() {
		try {
			String fileName = getProperty(ITEMS_FILE_KEY, ITEMS_FILE_DEFAULT);
			save(items, fileName, new Converter<Pair<Integer, MarketItem>, String>() {
				public String convert(Pair<Integer, MarketItem> value) {
					return value.second.toString();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
