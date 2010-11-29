public class MarketItem {

	private static final double minimumPrice = 1.0;

	private int id;
	private String name;
	private boolean enabled;
	private double currentPrice, targetPrice;
	private double volatility;

	public MarketItem(String str) {
		String[] split = str.split(",");
		this.id = Integer.valueOf(split[0]);
		this.name = split[1];
		this.enabled = Integer.valueOf(split[2]) != 0 ? true : false;
		this.currentPrice = Double.valueOf(split[3]);
		this.targetPrice = Double.valueOf(split[4]);
		this.volatility = Double.valueOf(split[5]);
	}

	public String toString() {
		return String.format("%d,%s,%d,%f,%f,%f", id, name, enabled ? 1 : 0,
			currentPrice, targetPrice, volatility);
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

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double price) {
		currentPrice = price;
	}

	public double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(double price) {
		targetPrice = price;
	}

	public double getVolatility() {
		return volatility;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	public static double getMinimumPrice() {
		return minimumPrice;
	}

	public int getActualPrice(int amount) {
		return (int) Math.round(amount * currentPrice);
	}

	public void buy(int amount) {
		targetPrice *= 1.0 + (amount * volatility / 100.0);
	}

	public void sell(int amount) {
		targetPrice /= 1.0 + (amount * volatility / 100.0);
		if (targetPrice < minimumPrice) {
			targetPrice = minimumPrice;
		}
	}

}
