import java.io.*;
import java.util.*;

public class DeathFix extends PluginEx {

	private static final String MESSAGES_INI = "messages.ini";
	private static final String DEFAULT_KEY = "default";

	@SuppressWarnings("serial")
	private Map<String, String> defaults = new HashMap<String, String>() {
		{
			put(DEFAULT_KEY, "%s died");
			put("cactus", "%s died by cactus");
			put("creeper_explosion", "%s died by creeper explosion");
			put("entity", "%s was killed by %s");
			put("explosion", "%s died by explosion");
			put("fall", "%s died by falling down");
			put("fire", "%s died by fire");
			put("fire_tick", "%s died by burn injury");
			put("lava", "%s died by lava");
			put("water", "%s became food for fishes");
		}
	};

	private Map<String, String> formats;
	private Map<String, Pair<String, Location>> death = new HashMap<String, Pair<String, Location>>();

	private PluginListener listener = new PluginListener() {
		public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
			BaseEntity defender, int amount) {
			if (defender.isPlayer()) {
				Player p = defender.getPlayer();
				if (p.getHealth() <= amount) {
					String attackerName = "";
					if (attacker != null) {
						if (attacker.isPlayer()) {
							attackerName = attacker.getPlayer().getName();
						} else if (attacker.isMob() || attacker.isAnimal()) {
							Mob mob = new Mob((ka) attacker.getEntity());
							attackerName = mob.getName();
						}
					}
					String format = null;
					String key = type.toString().toLowerCase();
					if (formats.containsKey(key)) {
						format = formats.get(key);
					} else {
						format = formats.containsKey(DEFAULT_KEY) ? formats
							.get(DEFAULT_KEY) : "";
					}
					if (!format.isEmpty() && !death.containsKey(p.getName())) {
						death.put(
							p.getName(),
							Pair.create(String.format(format, p.getName(), attackerName),
								p.getLocation()));
					}
				}
			}
			return false;
		}

		public boolean onHealthChange(Player player, int oldValue, int newValue) {
			if (newValue <= 0) {
				if (death.containsKey(player.getName())) {
					Pair<String, Location> p = death.remove(player.getName());
					Chat.toBroadcast(Colors.Red + p.first);
					info(p.first + " (%.0f,%.0f,%.0f)", p.second.x, p.second.y,
						p.second.z);
				}
				player.setFireTicks(0);
				player.kick("You died. reconnect server please.");
			}
			return false;
		}
	};

	public DeathFix() {
		addHook(PluginLoader.Hook.DAMAGE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.HEALTH_CHANGE, PluginListener.Priority.MEDIUM,
			listener);
	}

	protected void onEnable() {
		try {
			formats = load(new HashMap<String, String>(), MESSAGES_INI,
				new Converter<String, Pair<String, String>>() {
					public Pair<String, String> convertTo(String line) {
						try {
							String[] s = line.split("=", 2);
							String first = s[0].trim().toLowerCase();
							String second = s[1].trim();
							Pair<String, String> p = Pair.create(first, second);
							debug("%s => %s", line, p);
							return p;
						} catch (Exception e) {
						}
						return null;
					}
				});
		} catch (IOException e) {
			try {
				formats = new HashMap<String, String>(defaults);
				save(formats, MESSAGES_INI,
					new Converter<Pair<String, String>, String>() {
						public String convertTo(Pair<String, String> entry) {
							String line = entry.first.toLowerCase() + " = " + entry.second;
							debug("%s => %s", entry, line);
							return line;
						}
					});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
