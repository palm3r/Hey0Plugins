import java.util.*;

public class DeathFixListener extends PluginListener {

	private final DeathFix plugin;
	private Set<Player> dead = new HashSet<Player>();

	public DeathFixListener(DeathFix plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		plugin.checkProtection(player, player.getLocation());
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		return dead.contains(player);
	}

	@Override
	public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
		if (attacker != null && attacker.isPlayer()) {
			Player killer = attacker.getPlayer();
			if (killer != null && plugin.checkProtection(killer, null)) {
				plugin.removeProtection(killer);
			}
		}

		return false;
	}

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender,
		int amount) {
		if (defender != null && defender.isPlayer()) {
			Player player = defender.getPlayer();
			if (plugin.isGodPlayer(player)
				|| (attacker != null && attacker.isPlayer() && plugin.checkProtection(player, null)))
				return true;
			if (player.getHealth() <= 0 && !dead.contains(player)) {
				String key = type != null ? type.toString().toLowerCase() : "default";
				Map<String, String> map = new HashMap<String, String>();
				map.put("{player}", player.getName());
				switch (type) {
				case CACTUS:
					break;
				case CREEPER_EXPLOSION:
					key = "mob";
					map.put("{mob}", "creeper");
					break;
				case ENTITY:
					if (attacker != null) {
						if (attacker.isPlayer()) {
							Player killer = attacker.getPlayer();
							if (killer != null) {
								key = "player";
								map.put("{murderer}", killer.getName());
							}
						} else if (attacker.isMob()) {
							Mob mob = new Mob((lc) attacker.getEntity());
							if (mob != null) {
								key = "mob";
								map.put("{mob}", mob.getName());
							}
						}
					}
					break;
				case EXPLOSION:
					break;
				case FALL:
					break;
				case FIRE:
					break;
				case FIRE_TICK:
					break;
				case LAVA:
					break;
				case WATER:
					break;
				}
				if (plugin.isKickOnDeath()) {
					player.kick("You died. rejoin server please.");
				}
				dead.add(player);
				String msg = plugin.getDeathMessageFormat(key);
				if (msg != null) {
					for (Map.Entry<String, String> entry : map.entrySet()) {
						msg = msg.replace(entry.getKey(), entry.getValue());
					}
					Chat.broadcast(false, Colors.Red + msg);
					plugin.info(msg);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		if (newValue < oldValue && plugin.isGodPlayer(player))
			return true;
		if (oldValue == -99999999) {
			dead.remove(player);
			/*
			 * Location spawn = plugin.getSpawnLocation(player);
			 * Chat.player(false, player, "Teleport to (%f,%f,%f)", spawn.x, spawn.y,
			 * spawn.z);
			 * player.teleportTo(spawn);
			 */
			plugin.addProtection(player, player.getLocation());
		}
		return false;
	}

	@Override
	public void onLogin(Player player) {
		if (plugin.isGodAllowed(player)) {
			plugin.setGodPlayer(player, plugin.isGodOnLogin());
		}
		plugin.addProtection(player, player.getLocation());
	}

	@Override
	public void onDisconnect(Player player) {
		dead.remove(player);
		plugin.removeProtection(player);
		plugin.setGodPlayer(player, false);
	}

}
