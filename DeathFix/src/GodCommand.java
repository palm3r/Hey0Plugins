import java.util.List;

public class GodCommand extends Command {

	private DeathFix plugin;

	public GodCommand(DeathFix plugin) {
		super(null, "God mode");
		setRequire("/god");
		this.plugin = plugin;
	}

	public boolean execute(Player player, String command, List<String> args) {
		if (plugin.canBeGod(player)) {
			plugin.toggleGod(player);
			return true;
		}
		return false;
	}

}
