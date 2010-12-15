import java.io.*;
import java.util.*;

public class DeathFix extends PluginEx {

	private static final String MESSAGES_INI = "messages.ini";
	private static final String GOD_GROUPS_KEY = "god-groups";
	private static final String GOD_GROUPS_DEFAULT = "admins";
	private static final String GOD_ON_LOGIN_KEY = "god-on-login";
	private static final String GOD_ON_LOGIN_DEFAULT = "false";

	@SuppressWarnings("serial")
	private Set<PluginLoader.Hook> hooks = new HashSet<PluginLoader.Hook>() {
		{
			add(PluginLoader.Hook.DAMAGE);
			add(PluginLoader.Hook.HEALTH_CHANGE);
			add(PluginLoader.Hook.LOGIN);
		}
	};

	@SuppressWarnings("serial")
	private Map<String, String> defaults = new HashMap<String, String>() {
		{
			put("default", "%s died");
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

	private PluginListener listener = new PluginListener() {
		public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
			BaseEntity defender, int amount) {
			if (defender.isPlayer()) {
				Player p = defender.getPlayer();
				if (godPlayers.contains(p.getName())) {
					return true;
				}
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
						format =
							formats.containsKey("default") ? formats.get("default") : "";
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

		public void onLogin(Player player) {
			if (godOnLogin && !isGod(player) && canBeGod(player)) {
				toggleGod(player);
			}
		}
	};

	private Map<String, String> formats;
	private Map<String, Pair<String, Location>> death =
		new HashMap<String, Pair<String, Location>>();
	private Collection<String> godGroups;
	private Collection<String> godPlayers = new HashSet<String>();
	private boolean godOnLogin = false;
	private HealCommand heal = new HealCommand();
	private GodCommand god = new GodCommand(this);

	public DeathFix() {
	}

	protected void onEnable() {
		godGroups =
			StringTools.split(new HashSet<String>(),
				getProperty(GOD_GROUPS_KEY, GOD_GROUPS_DEFAULT), ",");
		godOnLogin =
			Boolean.valueOf(getProperty(GOD_ON_LOGIN_KEY, GOD_ON_LOGIN_DEFAULT));

		try {
			formats =
				load(new HashMap<String, String>(), MESSAGES_INI,
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

		addCommand(heal, god);
		for (PluginLoader.Hook hook : hooks) {
			addHook(hook, PluginListener.Priority.MEDIUM, listener);
		}
	}

	protected void onDisable() {
		for (PluginLoader.Hook hook : hooks) {
			removeHook(hook);
		}
		removeCommand(heal, god);
	}

	public boolean canBeGod(Player player) {
		for (String group : player.getGroups()) {
			if (godGroups.contains(group)) {
				return true;
			}
		}
		return false;
	}

	public boolean isGod(Player player) {
		return godPlayers.contains(player.getName());
	}

	public boolean toggleGod(Player player) {
		boolean god = false;
		if (canBeGod(player)) {
			String name = player.getName();
			if (godPlayers.contains(name)) {
				godPlayers.remove(name);
			} else {
				godPlayers.add(name);
				god = true;
			}
			Chat.toPlayer(player, (god ? Colors.LightBlue : Colors.Rose)
				+ "God mode %s", god ? "on" : "off");
		}
		return false;
	}

}
