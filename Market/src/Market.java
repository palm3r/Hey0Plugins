import java.util.*;

public class Market extends PluginEx {

	private static final String GOODS_FILE_KEY = "goods-file";
	private static final String GOODS_FILE_DEFAULT = "goods.txt";

	private static final String MONEY_FILE_KEY = "money-file";
	private static final String MONEY_FILE_DEFAULT = "money.txt";

	private static final String CURRENCY_UNIT_KEY = "units";
	private static final String CURRENCY_UNIT_DEFAULT = "coin,coins";

	private Map<Integer, Goods> goods;
	private Map<String, Integer> bank;
	private Pair<String, String> units;

	private final PluginListener listener = new PluginListener() {
		public void onLogin(Player player) {
			if (!bank.containsKey(player.getName())) {
				bank.put(player.getName(), 0);
				saveBank();
			}
		}
	};

	public Market() {
		initPluginEx("Market", listener, PluginListener.Priority.LOW,
				PluginLoader.Hook.COMMAND, PluginLoader.Hook.LOGIN);

		addCommand(new MarketCommand(this));
		addCommand(new MoneyCommand(this));
		addCommand(new PriceCommand(this));
		addCommand(new BuyCommand(this));
		addCommand(new SellCommand(this));

		this.goods = new HashMap<Integer, Goods>();
		this.bank = new HashMap<String, Integer>();
	}

	public int getMoney(String player) {
		return bank.containsKey(player) ? bank.get(player) : 0;
	}

	public void setMoney(String player, int money) {
		bank.put(player, money);
	}

	public Map<Integer, Goods> getGoods() {
		return goods;
	}

	public String currencyFormat(int money) {
		return money + " " + (money == 1 ? units.first : units.second);
	}

	public Goods findGoods(String idName) {
		Goods item = null;
		try {
			int id = Integer.valueOf(idName);
			item = goods.get(id);
		} catch (Exception e) {
			for (Map.Entry<Integer, Goods> entry : goods.entrySet()) {
				Goods g = entry.getValue();
				if (g.getName().equalsIgnoreCase(idName)) {
					item = g;
					break;
				}
			}
		}
		return item;
	}

	protected void onEnable() {
		String[] u = getProperty(CURRENCY_UNIT_KEY, CURRENCY_UNIT_DEFAULT).split(
				",");
		units = new Pair<String, String>(u[0], u.length > 1 ? u[1] : u[0]);
		loadGoods();
		loadBank();
	}

	protected void onDisable() {
		setProperty(CURRENCY_UNIT_KEY, units.toString());
	}

	private void loadGoods() {
		try {
			String fileName = getProperty(GOODS_FILE_KEY, GOODS_FILE_DEFAULT);
			goods = load(fileName, new Converter<String, Pair<Integer, Goods>>() {
				public Pair<Integer, Goods> convert(String value) {
					try {
						Goods g = new Goods(value);
						return new Pair<Integer, Goods>(g.getId(), g);
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
			String fileName = getProperty(GOODS_FILE_KEY, GOODS_FILE_DEFAULT);
			save(goods, fileName, new Converter<Pair<Integer, Goods>, String>() {
				public String convert(Pair<Integer, Goods> value) {
					return value.second.toString();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadBank() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			bank = load(fileName, new Converter<String, Pair<String, Integer>>() {
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
		} catch (Exception e) {
			saveBank();
		}
	}

	public void saveBank() {
		try {
			String fileName = getProperty(MONEY_FILE_KEY, MONEY_FILE_DEFAULT);
			save(bank, fileName, new Converter<Pair<String, Integer>, String>() {
				public String convert(Pair<String, Integer> value) {
					return value.first + ":" + value.second.toString();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
