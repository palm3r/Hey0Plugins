import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;

/**
 * Market plugin
 * 
 * @author palm3r
 */
public class Market extends PluginEx {

	private static final String ITEMS_FILE_KEY = "items-file";
	private static final String ITEMS_FILE_DEFAULT = "items.txt";

	private static final String MONEY_FILE_KEY = "money-file";
	private static final String MONEY_FILE_DEFAULT = "money.txt";

	private static final String UNIT_KEY = "currency-unit";
	private static final String UNIT_DEFAULT = "coin,coins";

	private static final String PRICE_CHANGE_INTERVAL_KEY = "price-change-interval";
	private static final String PRICE_CHANGE_INTERVAL_DEFAULT = "30";

	private static final String DATA_FLUSH_INTERVAL_KEY = "data-flush-interval";
	private static final String DATA_FLUSH_INTERVAL_DEFAULT = "300";

	private Map<Integer, MarketItem> items;
	private Map<String, Long> bank;
	private List<Map.Entry<String, Long>> richestPlayersCache;
	private Pair<String, String> unit;
	private Command market, price, buy, sell, money, top5;

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> priceChange, dataFlush;

	public Market() {
		items = new TreeMap<Integer, MarketItem>();
		bank = new TreeMap<String, Long>();
		scheduler = Executors.newSingleThreadScheduledExecutor();

		market = new MarketCommand(this);
		price = new PriceCommand(this);
		buy = new BuyCommand(this);
		sell = new SellCommand(this);
		money = new MoneyCommand(this);
		top5 = new Top5Command(this);

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.LOW);
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.LOW);
	}

	/**
	 * Get rich players ranking
	 * 
	 * @return
	 */
	public List<Map.Entry<String, Long>> getRichestPlayers() {
		if (richestPlayersCache == null) {
			synchronized (bank) {
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
		}
		return richestPlayersCache;
	}

	/**
	 * Get player's money
	 * 
	 * @param player
	 * @return
	 */
	public long getMoney(String player) {
		synchronized (bank) {
			return bank.containsKey(player) ? bank.get(player) : 0;
		}
	}

	/**
	 * Set player's money
	 * 
	 * @param player
	 * @param amount
	 */
	public void setMoney(String player, long amount) {
		synchronized (bank) {
			bank.put(player, amount);
		}
		saveBank();
		richestPlayersCache = null;
	}

	/**
	 * Format string for money
	 * 
	 * @param amount
	 * @return
	 */
	public String formatMoney(long amount) {
		return Colors.Gold + NumberFormat.getInstance().format(amount)
			+ Colors.LightGray + " " + (amount == 1 ? unit.first : unit.second);
	}

	/**
	 * Get all item information
	 * 
	 * @return
	 */
	public Map<Integer, MarketItem> getItems() {
		return items;
	}

	/**
	 * Get item information
	 * 
	 * @param idName
	 * @return
	 */
	public MarketItem findItem(String idName) {
		MarketItem item = null;
		synchronized (items) {
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
		}
		return item;
	}

	/**
	 * Plugin enabled
	 */
	protected void onEnable() {
		String[] u = getProperty(UNIT_KEY, UNIT_DEFAULT).split(",");
		unit = Pair.create(u[0], u.length > 1 ? u[1] : u[0]);

		long priceChangeInterval = Long.valueOf(getProperty(
			PRICE_CHANGE_INTERVAL_KEY, PRICE_CHANGE_INTERVAL_DEFAULT));

		loadBank();
		loadItems();

		addCommand(market, price, buy, sell, money, top5);

		priceChange = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				int count = 0;
				synchronized (items) {
					for (Map.Entry<Integer, MarketItem> entry : items.entrySet()) {
						MarketItem item = entry.getValue();
						double currentPrice = item.getCurrentPrice();
						double targetPrice = item.getTargetPrice();
						if (currentPrice != targetPrice) {
							double volatility = item.getVolatility();
							double increase = (targetPrice - currentPrice) * volatility
								/ 100.0;
							if (Math.abs(increase) < 1.0) {
								increase = increase > 0 ? 1.0 : -1.0;
							}
							double newPrice = currentPrice + increase;
							if ((currentPrice < targetPrice && newPrice > targetPrice)
								|| (currentPrice > targetPrice && newPrice < targetPrice)) {
								newPrice = targetPrice;
							}
							item.setCurrentPrice(newPrice);
							debug("%s price changed %f to %f", item.getName(), currentPrice,
								newPrice);
							count++;
						}
					}
				}
				if (count > 0) {
					debug("%d item price changed", count);
					// saveItems();
				}
			}
		}, priceChangeInterval, priceChangeInterval, TimeUnit.SECONDS);

		long dataFlushInterval = Long.valueOf(getProperty(DATA_FLUSH_INTERVAL_KEY,
			DATA_FLUSH_INTERVAL_DEFAULT));

		dataFlush = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				saveItems();
				saveBank();
			}
		}, dataFlushInterval, dataFlushInterval, TimeUnit.SECONDS);
	}

	/**
	 * Plugin disabled
	 */
	protected void onDisable() {
		if (priceChange != null)
			priceChange.cancel(true);
		if (dataFlush != null)
			dataFlush.cancel(true);
		removeCommand(market, price, buy, sell, money, top5);
	}

	/**
	 * Load money data
	 */
	private void loadBank() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			synchronized (bank) {
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
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Save money data
	 */
	public void saveBank() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			synchronized (bank) {
				saveMap(bank, fileName, new Converter<Pair<String, Long>, String>() {
					public String convertTo(Pair<String, Long> value) {
						return value.first + ":" + value.second.toString();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load item info
	 */
	private void loadItems() {
		try {
			String fileName = getProperty(ITEMS_FILE_KEY, ITEMS_FILE_DEFAULT);
			synchronized (items) {
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
			}
		} catch (Exception e) {
			saveItems();
		}
	}

	/**
	 * Save item info
	 */
	public void saveItems() {
		try {
			String fileName = getProperty(ITEMS_FILE_KEY, ITEMS_FILE_DEFAULT);
			synchronized (items) {
				saveMap(items, fileName,
					new Converter<Pair<Integer, MarketItem>, String>() {
						public String convertTo(Pair<Integer, MarketItem> value) {
							return value.second.toString();
						}
					});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
