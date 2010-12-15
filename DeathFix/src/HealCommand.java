import java.util.List;

public class HealCommand extends Command {

	public HealCommand() {
		super("<player>", "Heal yourself or other player");
		setRequire("/heal");
	}

	public boolean execute(Player player, String command, List<String> args) {
		Player target =
			args.isEmpty() ? player : etc.getServer().getPlayer(args.get(0));
		if (target == null) {
			Chat.toPlayer(player, (Colors.LightGreen + args.get(0))
				+ (Colors.LightGray + " is not found"));
		} else {
			target.setHealth(20);
			Chat.toPlayer(
				target,
				(Colors.LightGray + "You healed ")
					+ (Colors.LightGreen + (target == player ? "yourself"
						: target.getName())));
			if (target != player) {
				Chat.toPlayer(target, (Colors.LightGreen + player.getName())
					+ (Colors.LightGray + " healed you"));
			}
		}
		return true;
	}

}
