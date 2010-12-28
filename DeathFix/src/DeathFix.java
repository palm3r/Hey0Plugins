import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;

public class DeathFix extends PluginEx {

	private static final String MESSAGES_INI = "messages.ini";
	private static final String GOD_GROUPS_KEY = "god-groups";
	private static final String GOD_GROUPS_DEFAULT = "admins,mods";
	private static final String GODONLOGIN_KEY = "god-on-login";
	private static final String GODONLOGIN_DEFAULT = "true";
	private static final String KICK_ON_DEATH_KEY = "kick-on-death";
	private static final String KICK_ON_DEATH_DEFAULT = "false";
	private static final String SPAWN_PROTECTION_KEY = "spawn-protection";
	private static final String SPAWN_PROTECTION_DEFAULT = "5";

	@SuppressWarnings("serial")
	private Map<String, String> formats = new HashMap<String, String>() {
		{
			put("default", "{player} died by unknown reason");
			put("cactus", "{player} died by cactus");
			put("creeper_explosion", "{player} died by creeper explosion");
			put("player", "{player} was murdered by {murderer}");
			put("mob", "{player} was killed by {mob}");
			put("explosion", "{player} died by explosion");
			put("fall", "{player} could not fly...");
			put("fire", "{player} become roast meat");
			put("fire_tick", "{player} died by burn injury");
			put("lava", "{player} enjoyed swimming in lava");
			put("water", "{player} became food for fishes");
		}
	};

	private final Set<String> groups = new HashSet<String>();
	private final Set<Player> godPlayers = new HashSet<Player>();
	private boolean godOnLogin = false;
	private boolean kickOnDeath = false;
	private int spawnProtection = 5;
	private final Map<Player, Location> protections = new HashMap<Player, Location>();
	private Map<String, Location> homes = new HashMap<String, Location>();

	public DeathFix() {
		DeathFixListener listener = new DeathFixListener(this);
		addHook(PluginLoader.Hook.PLAYER_MOVE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.TELEPORT, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DISCONNECT, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.ATTACK, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DAMAGE, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.HEALTH_CHANGE, PluginListener.Priority.MEDIUM, listener);
		addCommand(new GodCommand(this), new HealCommand());
	}

	@Override
	protected void onEnable() {
		groups.clear();
		for (String group : StringUtils.split(getProperty(GOD_GROUPS_KEY, GOD_GROUPS_DEFAULT), ", ")) {
			groups.add(group);
		}
		godOnLogin = Boolean.valueOf(getProperty(GODONLOGIN_KEY, GODONLOGIN_DEFAULT));
		kickOnDeath = Boolean.valueOf(getProperty(KICK_ON_DEATH_KEY, KICK_ON_DEATH_DEFAULT));
		spawnProtection = Integer.valueOf(getProperty(SPAWN_PROTECTION_KEY, SPAWN_PROTECTION_DEFAULT));
		try {
			formats =
				load(new HashMap<String, String>(), MESSAGES_INI,
					new Converter<String, Pair<String, String>>() {
						@Override
						public Pair<String, String> convert(String line) {
							try {
								String[] s = line.split("=", 2);
								String first = s[0].trim().toLowerCase();
								String second = s[1].trim();
								return Pair.create(first, second);
							} catch (Exception e) {
								info("ERROR: " + line);
								e.printStackTrace();
							}
							return null;
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
			try {
				save(formats, MESSAGES_INI, new Converter<Pair<String, String>, String>() {
					@Override
					public String convert(Pair<String, String> entry) {
						return entry.first.toLowerCase() + " = " + entry.second;
					}
				});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			homes =
				MapTools.load(new HashMap<String, Location>(), "homes.txt",
					new Converter<String, Pair<String, Location>>() {
						@Override
						public Pair<String, Location> convert(String line) {
							try {
								String[] s = line.split(":");
								return Pair.create(s[0], new Location(Double.valueOf(s[1]), Double.valueOf(s[2]),
									Double.valueOf(s[3])));
							} catch (Exception e) {
							}
							return null;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDeathMessageFormat(String key) {
		return formats.containsKey(key) ? formats.get(key) : formats.get("default");
	}

	public boolean isGodPlayer(Player player) {
		return godPlayers.contains(player);
	}

	public boolean isGodAllowed(Player player) {
		for (String group : groups) {
			if (player.isInGroup(group))
				return true;
		}
		return false;
	}

	public void setGodPlayer(Player player, boolean god) {
		if (god) {
			godPlayers.add(player);
		} else {
			godPlayers.remove(player);
		}
		Chat.player(false, player, (god ? Colors.LightBlue : Colors.Rose) + "God mode %s", god ? "on"
			: "off");
	}

	public boolean isGodOnLogin() {
		return godOnLogin;
	}

	public boolean isKickOnDeath() {
		return kickOnDeath;
	}

	public Location getSpawnLocation(Player player) {
		return homes.containsKey(player.getName()) ? homes.get(player.getName())
			: etc.getServer().getSpawnLocation();
	}

	public boolean checkProtection(Player player, Location loc) {
		boolean b = protections.containsKey(player);
		Location spawn = b ? protections.get(player) : etc.getServer().getSpawnLocation();
		if (loc != null) {
			double distance =
				Math.abs(Math.sqrt(Math.pow(spawn.x - loc.x, 2.0) + Math.pow(spawn.y - loc.y, 2.0)
					+ Math.pow(spawn.z - loc.z, 2.0)));
			if (distance > spawnProtection) {
				removeProtection(player);
				// return false;
			}
		}
		return b;
	}

	public boolean isProtected(Player player) {
		return protections.containsKey(player);
	}

	public void addProtection(Player player, Location location) {
		if (location != null) {
			protections.put(player, location);
			Chat.player(false, player, Colors.LightGreen + "You are protected now");
		}
	}

	public void removeProtection(Player player) {
		protections.remove(player);
		Chat.player(false, player, Colors.Gold + "You haven't been protected any longer");
	}

}
