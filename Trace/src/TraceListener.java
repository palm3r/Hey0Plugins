import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class TraceListener extends PluginListener {

	private final Trace plugin;

	public TraceListener(Trace plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("serial")
	@Override
	public void onPlayerMove(final Player player, final Location from, final Location to) {
		plugin.showTrace(player, PluginLoader.Hook.PLAYER_MOVE, new HashMap<String, Object>() {
			{
				put("player", player);
				put("from", from);
				put("to", to);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onTeleport(final Player player, final Location from, final Location to) {
		plugin.showTrace(player, PluginLoader.Hook.TELEPORT, new HashMap<String, Object>() {
			{
				put("player", player);
				put("from", from);
				put("to", to);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public String onLoginChecks(final String user) {
		plugin.showTrace(null, PluginLoader.Hook.LOGINCHECK, new HashMap<String, Object>() {
			{
				put("user", user);
			}
		});
		return null;
	}

	@SuppressWarnings("serial")
	@Override
	public void onLogin(final Player player) {
		plugin.showTrace(null, PluginLoader.Hook.LOGIN, new HashMap<String, Object>() {
			{
				put("player", player);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public void onDisconnect(final Player player) {
		plugin.showTrace(player, PluginLoader.Hook.DISCONNECT, new HashMap<String, Object>() {
			{
				put("player", player);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onChat(final Player player, final String msg) {
		plugin.showTrace(player, PluginLoader.Hook.CHAT, new HashMap<String, Object>() {
			{
				put("player", player);
				put("msg", msg);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onCommand(final Player player, final String[] split) {
		plugin.showTrace(player, PluginLoader.Hook.COMMAND, new HashMap<String, Object>() {
			{
				put("player", player);
				put("cmd", StringUtils.join(split, " "));
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onConsoleCommand(final String[] split) {
		plugin.showTrace(null, PluginLoader.Hook.SERVERCOMMAND, new HashMap<String, Object>() {
			{
				put("cmd", StringUtils.join(split, " "));
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void onBan(final Player mod, final Player player, final String reason) {
		plugin.showTrace(player, PluginLoader.Hook.BAN, new HashMap<String, Object>() {
			{
				put("mod", mod);
				put("player", player);
				put("reason", reason);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public void onIpBan(final Player mod, final Player player, final String reason) {
		plugin.showTrace(player, PluginLoader.Hook.IPBAN, new HashMap<String, Object>() {
			{
				put("mod", mod);
				put("player", player);
				put("reason", reason);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public void onKick(final Player mod, final Player player, final String reason) {
		plugin.showTrace(player, PluginLoader.Hook.KICK, new HashMap<String, Object>() {
			{
				put("mod", mod);
				put("player", player);
				put("reason", reason);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	@Deprecated
	public boolean onBlockCreate(final Player player, final Block placed, final Block clicked,
		final int item) {
		plugin.showTrace(player, PluginLoader.Hook.BLOCK_CREATED, new HashMap<String, Object>() {
			{
				put("player", player);
				put("placed", placed);
				put("clicked", clicked);
				put("item", item);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onBlockDestroy(final Player player, final Block block) {
		plugin.showTrace(player, PluginLoader.Hook.BLOCK_DESTROYED, new HashMap<String, Object>() {
			{
				put("player", player);
				put("block", block);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onBlockBreak(final Player player, final Block block) {
		plugin.showTrace(player, PluginLoader.Hook.BLOCK_BROKEN, new HashMap<String, Object>() {
			{
				put("player", player);
				put("block", block);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void onArmSwing(final Player player) {
		plugin.showTrace(player, PluginLoader.Hook.ARM_SWING, new HashMap<String, Object>() {
			{
				put("player", player);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onItemDrop(final Player player, final Item item) {
		plugin.showTrace(player, PluginLoader.Hook.ITEM_DROP, new HashMap<String, Object>() {
			{
				put("player", player);
				put("item", item);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onItemPickUp(final Player player, final Item item) {
		plugin.showTrace(player, PluginLoader.Hook.ITEM_PICK_UP, new HashMap<String, Object>() {
			{
				put("player", player);
				put("item", item);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onIgnite(final Block block, final Player player) {
		plugin.showTrace(player, PluginLoader.Hook.IGNITE, new HashMap<String, Object>() {
			{
				put("player", player);
				put("block", block);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onExplode(final Block block) {
		plugin.showTrace(null, PluginLoader.Hook.EXPLODE, new HashMap<String, Object>() {
			{
				put("block", block);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onFlow(final Block from, final Block to) {
		plugin.showTrace(null, PluginLoader.Hook.FLOW, new HashMap<String, Object>() {
			{
				put("from", from);
				put("to", to);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onMobSpawn(final Mob mob) {
		plugin.showTrace(null, PluginLoader.Hook.MOB_SPAWN, new HashMap<String, Object>() {
			{
				put("mob", mob);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onDamage(final PluginLoader.DamageType type, final BaseEntity attacker,
		final BaseEntity defender, int amount) {
		plugin.showTrace(defender != null ? defender.getPlayer() : null, PluginLoader.Hook.DAMAGE,
			new HashMap<String, Object>() {
				{
					put("type", type);
					put("attacker", attacker);
					put("defender", defender);
				}
			});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onHealthChange(final Player player, final int oldValue, final int newValue) {
		plugin.showTrace(player, PluginLoader.Hook.HEALTH_CHANGE, new HashMap<String, Object>() {
			{
				put("player", player);
				put("old", oldValue);
				put("new", newValue);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public int onRedstoneChange(final Block block, final int oldLevel, final int newLevel) {
		plugin.showTrace(null, PluginLoader.Hook.REDSTONE_CHANGE, new HashMap<String, Object>() {
			{
				put("block", block);
				put("old", oldLevel);
				put("new", newLevel);
			}
		});
		return newLevel;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onBlockPhysics(final Block block, final boolean placed) {
		plugin.showTrace(null, PluginLoader.Hook.BLOCK_PHYSICS, new HashMap<String, Object>() {
			{
				put("block", block);
				put("placed", placed);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void onVehicleCreate(final BaseVehicle vehicle) {
		plugin.showTrace(null, PluginLoader.Hook.VEHICLE_CREATE, new HashMap<String, Object>() {
			{
				put("vehicle", vehicle);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onVehicleDamage(final BaseVehicle vehicle, final BaseEntity attacker,
		final int damage) {
		plugin.showTrace(attacker != null ? attacker.getPlayer() : null,
			PluginLoader.Hook.VEHICLE_DAMAGE, new HashMap<String, Object>() {
				{
					put("vehicle", vehicle);
					put("attacker", attacker);
					put("damage", damage);
				}
			});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void onVehicleUpdate(final BaseVehicle vehicle) {
		plugin.showTrace(null, PluginLoader.Hook.VEHICLE_UPDATE, new HashMap<String, Object>() {
			{
				put("vehicle", vehicle);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public Boolean onVehicleCollision(final BaseVehicle vehicle, final BaseEntity collisioner) {
		plugin.showTrace(null, PluginLoader.Hook.VEHICLE_COLLISION, new HashMap<String, Object>() {
			{
				put("vehicle", vehicle);
				put("collisioner", collisioner);
			}
		});
		return Boolean.valueOf(false);
	}

	@SuppressWarnings("serial")
	@Override
	public void onVehicleDestroyed(final BaseVehicle vehicle) {
		plugin.showTrace(null, PluginLoader.Hook.VEHICLE_DESTROYED, new HashMap<String, Object>() {
			{
				put("vehicle", vehicle);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public void onVehicleEnter(final BaseVehicle vehicle, final HumanEntity player) {
		plugin.showTrace(player != null ? player.getPlayer() : null, PluginLoader.Hook.VEHICLE_ENTERED,
			new HashMap<String, Object>() {
				{
					put("vehicle", vehicle);
					put("player", player);
				}
			});
	}

	@SuppressWarnings("serial")
	@Override
	public void onVehiclePositionChange(final BaseVehicle vehicle, final int x, final int y,
		final int z) {
		plugin.showTrace(null, PluginLoader.Hook.VEHICLE_POSITIONCHANGE, new HashMap<String, Object>() {
			{
				put("vehicle", vehicle);
				put("x", x);
				put("y", y);
				put("z", z);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onItemUse(final Player player, final Block placed, final Block clicked,
		final Item item) {
		plugin.showTrace(player, PluginLoader.Hook.ITEM_USE, new HashMap<String, Object>() {
			{
				put("player", player);
				put("placed", placed);
				put("clicked", clicked);
				put("item", item);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onBlockPlace(final Player player, final Block placed, final Block clicked,
		final Item item) {
		plugin.showTrace(player, PluginLoader.Hook.BLOCK_PLACE, new HashMap<String, Object>() {
			{
				put("player", player);
				put("placed", placed);
				put("clicked", clicked);
				put("item", item);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void onBlockRightClicked(final Player player, final Block block, final Item item) {
		plugin.showTrace(player, PluginLoader.Hook.BLOCK_RIGHTCLICKED, new HashMap<String, Object>() {
			{
				put("player", player);
				put("block", block);
				put("item", item);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public PluginLoader.HookResult onLiquidDestroy(final PluginLoader.HookResult state,
		final int liquid, final Block block) {
		plugin.showTrace(null, PluginLoader.Hook.LIQUID_DESTROY, new HashMap<String, Object>() {
			{
				put("state", state);
				put("liquid", liquid);
				put("block", block);
			}
		});
		return PluginLoader.HookResult.DEFAULT_ACTION;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onAttack(final LivingEntity attacker, final LivingEntity defender,
		final Integer amount) {
		plugin.showTrace(attacker != null ? attacker.getPlayer() : null, PluginLoader.Hook.ATTACK,
			new HashMap<String, Object>() {
				{
					put("attacker", attacker);
					put("defender", defender);
					put("amount", amount);
				}
			});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onOpenInventory(final Player player, final Inventory inventory) {
		plugin.showTrace(player, PluginLoader.Hook.OPEN_INVENTORY, new HashMap<String, Object>() {
			{
				put("player", player);
				put("inventory", inventory);
			}
		});
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public void onSignShow(final Player player, final Sign sign) {
		plugin.showTrace(player, PluginLoader.Hook.SIGN_SHOW, new HashMap<String, Object>() {
			{
				put("player", player);
				put("sign", sign);
			}
		});
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onSignChange(final Player player, final Sign sign) {
		plugin.showTrace(player, PluginLoader.Hook.SIGN_CHANGE, new HashMap<String, Object>() {
			{
				put("player", player);
				put("sign", sign);
			}
		});
		return false;
	}

}
