import java.util.*;

public class DeathFixListener extends PluginListener {

	private final DeathFix plugin;

	private final Map<String, Pair<String, Map<String, String>>> death =
		new HashMap<String, Pair<String, Map<String, String>>>();

	public DeathFixListener(DeathFix plugin) {
		this.plugin = plugin;
	}

	private void showDeathMessage(Player player) {
		if (death.containsKey(player.getName())) {
			Pair<String, Map<String, String>> p = death.remove(player.getName());

			String msg = plugin.getDeathMessageFormat(p.first);
			if (msg != null) {
				for (Map.Entry<String, String> entry : p.second.entrySet()) {
					msg = msg.replace(entry.getKey(), entry.getValue());
				}
				Chat.broadcast(false, Colors.Red + msg);
				plugin.info(msg);
			}
		}
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		plugin.checkProtection(player, player.getLocation());
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		if (death.containsKey(player.getName())) {
			showDeathMessage(player);
			plugin.addProtection(player, to);
		} else {
			plugin.checkProtection(player, to);
		}
		return false;
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
			if (plugin.isGodPlayer(player) || plugin.checkProtection(player, null))
				return true;
			if (player != null && player.getHealth() <= amount && !death.containsKey(player.getName())) {
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
				death.put(player.getName(), Pair.create(key, map));
				if (plugin.isKickOnDeath()) {
					player.kick("You died. rejoin server please.");
				}
			}
		}
		return false;
	}

	@Override
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		return newValue < oldValue && plugin.isGodPlayer(player);
	}

	@Override
	public void onLogin(Player player) {
		if (plugin.isGodAllowed(player)) {
			plugin.setGodPlayer(player, plugin.isGodOnLogin());
		}
	}

	@Override
	public void onDisconnect(Player player) {
		death.remove(player.getName());
		plugin.removeProtection(player);
		plugin.setGodPlayer(player, false);
	}

}
