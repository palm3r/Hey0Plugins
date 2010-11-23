public class MarketItem {

	public static final double MINIMUM_PRICE = 1;

	private int id;
	private String name;
	private boolean enabled;
	private double price;
	private double volatility;

	public MarketItem(String str) {
		String[] split = str.split(",");
		this.id = Integer.valueOf(split[0]);
		this.name = split[1];
		this.enabled = Integer.valueOf(split[2]) != 0 ? true : false;
		this.price = Double.valueOf(split[3]);
		this.volatility = Double.valueOf(split[4]);
	}

	public String toString() {
		return String.format("%d,%s,%d,%f,%f", id, name, enabled ? 1 : 0, price,
			volatility);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void enable(boolean enabled) {
		this.enabled = enabled;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getVolatility() {
		return volatility;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	public int getActualPrice(boolean buy, int amount) {
		return (int) Math.round(amount
			* (buy ? price : price / getFluctuatedPrice(amount)));
	}

	public boolean buy(int amount) {
		if (!enabled)
			return false;
		price *= getFluctuatedPrice(amount);
		return true;
	}

	public boolean sell(int amount) {
		if (!enabled)
			return false;
		price /= getFluctuatedPrice(amount);
		if (price < MINIMUM_PRICE)
			price = MINIMUM_PRICE;
		return true;
	}

	private double getFluctuatedPrice(int amount) {
		return 1 + (amount * volatility / 100.0);
	}

}
