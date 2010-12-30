import java.util.*;

public class SetHomeCommand extends Command {

	private DeathFix plugin;

	public SetHomeCommand(DeathFix plugin) {
		super("", "Set your home as spawn location");
		this.plugin = plugin;
		setRequire("/sethome");
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		return plugin.setHome(player);
	}

}
