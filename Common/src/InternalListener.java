import java.util.*;

/**
 * Internal listener
 * 
 * @author palm3r
 */
final class InternalListener extends PluginListener {
	private PluginEx plugin;
	private PluginListener listener;

	public InternalListener(PluginEx plugin, PluginListener listener) {
		this.plugin = plugin;
		this.listener = listener;
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		if (listener != null) {
			listener.onPlayerMove(player, from, to);
		}
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		return listener != null ? listener.onTeleport(player, from, to) : false;
	}

	@Override
	public String onLoginChecks(String user) {
		return listener != null ? listener.onLoginChecks(user) : null;
	}

	@Override
	public void onLogin(Player player) {
		if (listener != null) {
			listener.onLogin(player);
		}
	}

	@Override
	public void onDisconnect(Player player) {
		if (listener != null) {
			listener.onDisconnect(player);
		}
	}

	@Override
	public boolean onChat(Player player, String msg) {
		return listener != null ? listener.onChat(player, msg) : false;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		String command = args[0];
		plugin.debug("onCommand: %s", command);
		List<String> args2 = new LinkedList<String>();
		for (int i = 1; i < args.length; ++i) {
			args2.add(args[i].trim());
		}
		for (Command c : plugin.getCommands()) {
			if (c.match(command) && c.canUseCommand(player)) {
				plugin.debug("%s is corresponding to %s", command, c);
				return c.execute(player, command, args2);
			}
		}
		plugin.debug("%s is not corresponding to any command", command);
		return listener != null ? listener.onCommand(player, args) : false;
	}

	@Override
	public boolean onConsoleCommand(String[] args) {
		return listener != null ? listener.onConsoleCommand(args) : false;
	}

	@Override
	public void onBan(Player mod, Player player, String reason) {
		if (listener != null) {
			listener.onBan(mod, player, reason);
		}
	}

	@Override
	public void onIpBan(Player mod, Player player, String reason) {
		if (listener != null) {
			listener.onIpBan(mod, player, reason);
		}
	}

	@Override
	public void onKick(Player mod, Player player, String reason) {
		if (listener != null) {
			listener.onKick(mod, player, reason);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockCreate(Player player, Block blockPlaced,
		Block blockClicked, int itemInHand) {
		return listener != null ? listener.onBlockCreate(player, blockPlaced,
			blockClicked, itemInHand) : false;
	}

	@Override
	public boolean onBlockDestroy(Player player, Block block) {
		return listener != null ? listener.onBlockDestroy(player, block) : false;
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		return listener != null ? listener.onBlockBreak(player, block) : false;
	}

	@Override
	public void onArmSwing(Player player) {
		if (listener != null) {
			listener.onArmSwing(player);
		}
	}

	@Override
	public boolean onInventoryChange(Player player) {
		return listener != null ? listener.onInventoryChange(player) : false;
	}

	@Override
	public boolean onCraftInventoryChange(Player player) {
		return listener != null ? listener.onCraftInventoryChange(player) : false;
	}

	@Override
	public boolean onEquipmentChange(Player player) {
		return listener != null ? listener.onEquipmentChange(player) : false;
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		return listener != null ? listener.onItemDrop(player, item) : false;
	}

	@Override
	public boolean onItemPickUp(Player player, Item item) {
		return listener != null ? listener.onItemPickUp(player, item) : false;
	}

	@Override
	public boolean onComplexBlockChange(Player player, ComplexBlock block) {
		return listener != null ? listener.onComplexBlockChange(player, block)
			: false;
	}

	@Override
	public boolean onSendComplexBlock(Player player, ComplexBlock block) {
		return listener != null ? listener.onSendComplexBlock(player, block)
			: false;
	}

	@Override
	public boolean onIgnite(Block block, Player player) {
		return listener != null ? listener.onIgnite(block, player) : false;
	}

	@Override
	public boolean onExplode(Block block) {
		return listener != null ? listener.onExplode(block) : false;
	}

	@Override
	public boolean onFlow(Block blockFrom, Block blockTo) {
		return listener != null ? listener.onFlow(blockFrom, blockTo) : false;
	}

	@Override
	public boolean onMobSpawn(Mob mob) {
		return listener != null ? listener.onMobSpawn(mob) : false;
	}

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
		BaseEntity defender, int amount) {
		return listener != null ? listener.onDamage(type, attacker, defender,
			amount) : false;
	}

	@Override
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		return listener != null ? listener.onHealthChange(player, oldValue,
			newValue) : false;
	}

	@Override
	public int onRedstoneChange(Block block, int oldLevel, int newLevel) {
		return listener != null ? listener.onRedstoneChange(block, oldLevel,
			newLevel) : newLevel;
	}

	@Override
	public boolean onBlockPhysics(Block block, boolean placed) {
		return listener != null ? listener.onBlockPhysics(block, placed) : false;
	}

	@Override
	public void onVehicleCreate(BaseVehicle vehicle) {
		if (listener != null) {
			listener.onVehicleCreate(vehicle);
		}
	}

	@Override
	public boolean onVehicleDamage(BaseVehicle vehicle, BaseEntity attacker,
		int damage) {
		return listener != null ? listener.onVehicleDamage(vehicle, attacker,
			damage) : false;
	}

	@Override
	public void onVehicleUpdate(BaseVehicle vehicle) {
		if (listener != null) {
			listener.onVehicleUpdate(vehicle);
		}
	}

	@Override
	public Boolean
		onVehicleCollision(BaseVehicle vehicle, BaseEntity collisioner) {
		return listener != null ? listener.onVehicleCollision(vehicle, collisioner)
			: Boolean.valueOf(false);
	}

	@Override
	public void onVehicleDestroyed(BaseVehicle vehicle) {
		if (listener != null) {
			listener.onVehicleDestroyed(vehicle);
		}
	}

	@Override
	public void onVehicleEnter(BaseVehicle vehicle, HumanEntity player) {
		if (listener != null) {
			listener.onVehicleEnter(vehicle, player);
		}
	}

	@Override
	public void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z) {
		if (listener != null) {
			listener.onVehiclePositionChange(vehicle, x, y, z);
		}
	}

	@Override
	public boolean onItemUse(Player player, Block blockPlaced,
		Block blockClicked, Item item) {
		return listener != null ? listener.onItemUse(player, blockPlaced,
			blockClicked, item) : false;
	}

	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced,
		Block blockClicked, Item itemInHand) {
		return listener != null ? listener.onBlockPlace(player, blockPlaced,
			blockClicked, itemInHand) : false;
	}

	@Override
	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		if (listener != null) {
			listener.onBlockRightClicked(player, blockClicked, item);
		}
	}

	@Override
	public PluginLoader.HookResult onLiquidDestroy(
		PluginLoader.HookResult currentState, int liquidBlockId, Block targetBlock) {
		return listener != null ? listener.onLiquidDestroy(currentState,
			liquidBlockId, targetBlock) : PluginLoader.HookResult.DEFAULT_ACTION;
	}

	@Override
	public boolean onAttack(LivingEntity attacker, LivingEntity defender,
		Integer amount) {
		return listener != null ? listener.onAttack(attacker, defender, amount)
			: false;
	}

}