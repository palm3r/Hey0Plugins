import java.util.*;

public class BorderCommand extends Command {

	private final Border plugin;

	public BorderCommand(Border plugin) {
		super("[key] <value>", "Config map border");
		this.plugin = plugin;
	}

	private void show(Player player, String key, String value) {
		Chat.player(false, player,
			(Colors.LightGreen + Border.class.getSimpleName() + ": ")
				+ (Colors.White + key + " = " + value));
	}

	@Override
	public boolean execute(Player player, String command, List<String> args) {
		if (!args.isEmpty()) {
			try {
				String key = args.remove(0);
				if (key.equalsIgnoreCase("radius")) {
					Integer radius =
						args.isEmpty() ? null : Integer.valueOf(args.remove(0));
					if (radius != null) {
						plugin.setSize(radius);
						Location spawn = etc.getServer().getSpawnLocation();
						for (Player p : etc.getServer().getPlayerList()) {
							if (plugin.isOutside(p.getX(), p.getY(), p.getZ())) {
								p.teleportTo(spawn);
								Chat.player(
									false,
									p,
									(Colors.LightGreen + Border.class.getSimpleName() + ": ")
										+ (Colors.White + "Map bounds has changed. You moved to spawn from outside of map bounds"));
							}
						}
					}
					show(player, key, plugin.getSize().toString());
					return true;
				} else if (plugin.allowContains(key)) {
					String value = args.isEmpty() ? null : args.remove(0);
					if (value != null) {
						plugin.setAllow(key, Boolean.valueOf(value));
					}
					show(player, key, plugin.getAllow(key)
						? (Colors.LightGreen + "allow") : (Colors.Rose + "forbid"));
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Chat.player(false, player, getUsage(false, true));
		return true;
	}

}
