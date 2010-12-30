import java.util.*;
import org.apache.commons.lang.*;

public class TraceCommand extends Command {

	private final Trace plugin;

	public TraceCommand(Trace plugin) {
		super("", "");
		setRequire("/trace");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		while (!args.isEmpty()) {
			String a = args.remove(0).toUpperCase();
			try {
				PluginLoader.Hook hook = Enum.valueOf(PluginLoader.Hook.class, a);
				plugin.toggleTrace(player, hook);
			} catch (Exception e) {
				player.sendMessage(Colors.LightGray + "\"" + a + "\" is not supported");
			}
		}
		List<PluginLoader.Hook> hooks = plugin.getTrace(player);
		String s = hooks.isEmpty() ? "(empty)" : StringUtils.join(hooks, " ");
		player.sendMessage((Colors.Rose + Trace.class.getSimpleName() + ":") + (Colors.White + " " + s));
		return true;
	}
}
