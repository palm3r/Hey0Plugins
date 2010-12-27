import java.util.List;

public class GodCommand extends Command {

	private final DeathFix plugin;

	public GodCommand(DeathFix plugin) {
		super(null, "God mode");
		setRequire("/god");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		plugin.setGodPlayer(player, !plugin.isGodPlayer(player));
		return true;
	}

}
