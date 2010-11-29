import java.util.*;

public class MoneyCommand extends Command {

	public static final String REQUIRE = "/market";
	private Market plugin;

	public MoneyCommand(Market plugin) {
		super(null, "Show money");
		setRequire(REQUIRE);
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		if (!args.isEmpty() && player.canUseCommand("/market-admin")) {
			String playerName = args.get(0);
			long money = plugin.getMoney(playerName);
			if (args.size() > 1 && player.isAdmin()) {
				String amountString = args.get(1);
				try {
					long old = money;
					money = Long.valueOf(amountString);
					plugin.setMoney(playerName, money);
					plugin.info("%s money %d %d (%+d) [%s]", playerName, old, money,
						(money - old), player.getName());
				} catch (Exception e) {
					Chat.toPlayer(player,
						(Colors.Rose + "Invalid parameter:" + amountString));
					return true;
				}
			}
			Chat.toPlayer(player, (Colors.LightGreen + playerName)
				+ (Colors.LightGray + " has ") + plugin.formatMoney(money)
				+ (Colors.LightGray + " now"));
		} else {
			long money = plugin.getMoney(player.getName());
			Chat.toPlayer(player,
				(Colors.LightGray + "You have ") + plugin.formatMoney(money)
					+ (Colors.LightGray + " now"));
		}
		return true;
	}
}
