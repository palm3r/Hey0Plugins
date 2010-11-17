public class Goods {

	private int id;
	private String name;
	private boolean enabled;
	private int price;
	private double factor;
	private int stock;
	private int balance;

	public Goods(String str) {
		String[] split = str.split(",");
		this.id = Integer.valueOf(split[0]);
		this.name = split[1];
		this.enabled = Integer.valueOf(split[2]) != 0 ? true : false;
		this.price = Integer.valueOf(split[3]);
		this.factor = Double.valueOf(split[4]);
		this.stock = Integer.valueOf(split[5]);
		this.balance = Integer.valueOf(split[6]);
	}

	public String toString() {
		return String.format("%d,%s,%d,%d,%f,%d,%d", id, name, enabled ? 1 : 0,
				price, factor, stock, balance);
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
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

	public int getActualPrice(boolean purchase, int amount) {
		// int b = balance + (amount * (purchase ? 1 : -1));
		double up = price + (balance * (price * factor / 100.0) / 100.0);
		if (up < price * 0.1)
			up = price * 0.1;
		return (int) Math.floor(up * amount);
	}

	public boolean buy(int amount) {
		if (!enabled || stock <= amount)
			return false;
		stock -= amount;
		balance += amount;
		return true;
	}

	public boolean sell(int amount) {
		if (!enabled) {
			return false;
		}
		stock += amount;
		balance -= amount;
		return true;
	}

}
