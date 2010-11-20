public class MarketItem {

	private int id;
	private String name;
	private boolean enabled;
	private double price;
	private double factor;
	private int balance;

	public MarketItem(String str) {
		String[] split = str.split(",");
		this.id = Integer.valueOf(split[0]);
		this.name = split[1];
		this.enabled = Integer.valueOf(split[2]) != 0 ? true : false;
		this.price = Double.valueOf(split[3]);
		this.factor = Double.valueOf(split[4]);
		this.balance = Integer.valueOf(split[5]);
	}

	public String toString() {
		return String.format("%d,%s,%d,%f,%f,%d", id, name, enabled ? 1 : 0, price,
				factor, balance);
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

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public double getFactor() {
		return factor;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}

	public int getActualPrice(boolean buy, int amount) {
		double p = price, total = 0;
		for (int i = 0; i < amount; ++i) {
			total += (p < 1 ? 1 : p);
			p += (p * (factor / (100.0 + (buy ? 0 : factor)))) * (buy ? 1 : -1);
		}
		return (int) Math.floor(total);
	}

	public boolean buy(int amount) {
		if (!enabled)
			return false;
		balance += amount;
		for (int i = 0; i < amount; ++i) {
			price += price * (factor / 100.0);
		}
		return true;
	}

	public boolean sell(int amount) {
		if (!enabled)
			return false;
		balance -= amount;
		for (int i = 0; i < amount && price > 2; ++i) {
			price -= price * (factor / (100.0 + factor));
			if (price < 2)
				price = 2;
		}
		return true;
	}

}
