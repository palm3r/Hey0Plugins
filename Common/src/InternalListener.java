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

	public void onPlayerMove(Player player, Location from, Location to) {
		if (listener != null) {
			listener.onPlayerMove(player, from, to);
		}
	}

	public boolean onTeleport(Player player, Location from, Location to) {
		return listener != null ? listener.onTeleport(player, from, to) : false;
	}

	public String onLoginChecks(String user) {
		return listener != null ? listener.onLoginChecks(user) : null;
	}

	public void onLogin(Player player) {
		if (listener != null) {
			listener.onLogin(player);
		}
	}

	public void onDisconnect(Player player) {
		if (listener != null) {
			listener.onDisconnect(player);
		}
	}

	public boolean onChat(Player player, String msg) {
		return listener != null ? listener.onChat(player, msg) : false;
	}

	public boolean onCommand(Player player, String[] args) {
		String command = args[0];
		plugin.debug("onCommand: %s", command);
		List<String> args2 = new LinkedList<String>();
		for (int i = 1; i < args.length; ++i) {
			args2.add(args[i].trim());
		}
		for (Command c : plugin.commands) {
			if (c.match(command) && c.canUseCommand(player)) {
				plugin.debug("%s is corresponding to %s", command, c);
				return c.execute(player, command, args2);
			}
		}
		plugin.debug("%s is not corresponding to any command", command);
		return listener != null ? listener.onCommand(player, args) : false;
	}

	public boolean onConsoleCommand(String[] args) {
		return listener != null ? listener.onConsoleCommand(args) : false;
	}

	public void onBan(Player mod, Player player, String reason) {
		if (listener != null) {
			listener.onBan(mod, player, reason);
		}
	}

	public void onIpBan(Player mod, Player player, String reason) {
		if (listener != null) {
			listener.onIpBan(mod, player, reason);
		}
	}

	public void onKick(Player mod, Player player, String reason) {
		if (listener != null) {
			listener.onKick(mod, player, reason);
		}
	}

	public boolean onBlockDestroy(Player player, Block block) {
		return listener != null ? listener.onBlockDestroy(player, block) : false;
	}

	public boolean onBlockBreak(Player player, Block block) {
		return listener != null ? listener.onBlockBreak(player, block) : false;
	}

	public void onArmSwing(Player player) {
		if (listener != null) {
			listener.onArmSwing(player);
		}
	}

	public boolean onInventoryChange(Player player) {
		return listener != null ? listener.onInventoryChange(player) : false;
	}

	public boolean onCraftInventoryChange(Player player) {
		return listener != null ? listener.onCraftInventoryChange(player) : false;
	}

	public boolean onEquipmentChange(Player player) {
		return listener != null ? listener.onEquipmentChange(player) : false;
	}

	public boolean onItemDrop(Player player, Item item) {
		return listener != null ? listener.onItemDrop(player, item) : false;
	}

	public boolean onItemPickUp(Player player, Item item) {
		return listener != null ? listener.onItemPickUp(player, item) : false;
	}

	public boolean onComplexBlockChange(Player player, ComplexBlock block) {
		return listener != null ? listener.onComplexBlockChange(player, block)
			: false;
	}

	public boolean onSendComplexBlock(Player player, ComplexBlock block) {
		return listener != null ? listener.onSendComplexBlock(player, block)
			: false;
	}

	public boolean onIgnite(Block block, Player player) {
		return listener != null ? listener.onIgnite(block, player) : false;
	}

	public boolean onExplode(Block block) {
		return listener != null ? listener.onExplode(block) : false;
	}

	public boolean onFlow(Block blockFrom, Block blockTo) {
		return listener != null ? listener.onFlow(blockFrom, blockTo) : false;
	}

	public boolean onMobSpawn(Mob mob) {
		return listener != null ? listener.onMobSpawn(mob) : false;
	}

	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
		BaseEntity defender, int amount) {
		return listener != null ? listener.onDamage(type, attacker, defender,
			amount) : false;
	}

	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		return listener != null ? listener.onHealthChange(player, oldValue,
			newValue) : false;
	}

	public int onRedstoneChange(Block block, int oldLevel, int newLevel) {
		return listener != null ? listener.onRedstoneChange(block, oldLevel,
			newLevel) : newLevel;
	}

	public boolean onBlockPhysics(Block block, boolean placed) {
		return listener != null ? listener.onBlockPhysics(block, placed) : false;
	}

	public void onVehicleCreate(BaseVehicle vehicle) {
		if (listener != null) {
			listener.onVehicleCreate(vehicle);
		}
	}

	public boolean onVehicleDamage(BaseVehicle vehicle, BaseEntity attacker,
		int damage) {
		return listener != null ? listener.onVehicleDamage(vehicle, attacker,
			damage) : false;
	}

	public void onVehicleUpdate(BaseVehicle vehicle) {
		if (listener != null) {
			listener.onVehicleUpdate(vehicle);
		}
	}

	public void onVehicleCollision(BaseVehicle vehicle, BaseEntity collisioner) {
		if (listener != null) {
			listener.onVehicleCollision(vehicle, collisioner);
		}
	}

	public void onVehicleDestroyed(BaseVehicle vehicle) {
		if (listener != null) {
			listener.onVehicleDestroyed(vehicle);
		}
	}

	public void onVehicleEnter(BaseVehicle vehicle, HumanEntity player) {
		if (listener != null) {
			listener.onVehicleEnter(vehicle, player);
		}
	}

	public void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z) {
		if (listener != null) {
			listener.onVehiclePositionChange(vehicle, x, y, z);
		}
	}

	public boolean onItemUse(Player player, Block blockPlaced,
		Block blockClicked, Item item) {
		return listener != null ? listener.onItemUse(player, blockPlaced,
			blockClicked, item) : false;
	}

	public boolean onBlockPlace(Player player, Block blockPlaced,
		Block blockClicked, Item itemInHand) {
		return listener != null ? listener.onBlockPlace(player, blockPlaced,
			blockClicked, itemInHand) : false;
	}

	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		if (listener != null) {
			listener.onBlockRightClicked(player, blockClicked, item);
		}
	}

	public PluginLoader.HookResult onLiquidDestroy(
		PluginLoader.HookResult currentState, int liquidBlockId, Block targetBlock) {
		return listener != null ? listener.onLiquidDestroy(currentState,
			liquidBlockId, targetBlock) : PluginLoader.HookResult.DEFAULT_ACTION;
	}

}