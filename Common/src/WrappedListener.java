import java.util.*;

final class WrappedListener extends PluginListener {

	private final PluginEx plugin;
	private final PluginListener listener;

	public WrappedListener(PluginEx plugin, PluginListener listener) {
		this.plugin = plugin;
		this.listener = listener;
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		plugin.trace("onPlayerMove(%s, %s, %s)", player, from, to);
		if (listener != null) {
			listener.onPlayerMove(player, from, to);
		}
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		plugin.trace("onTeleport(%s, %s, %s)", player, from, to);
		return listener != null ? listener.onTeleport(player, from, to) : false;
	}

	@Override
	public String onLoginChecks(String user) {
		plugin.trace("onLoginChecks(%s)", user);
		return listener != null ? listener.onLoginChecks(user) : null;
	}

	@Override
	public void onLogin(Player player) {
		plugin.trace("onLogin(%s)", player);
		if (listener != null) {
			listener.onLogin(player);
		}
	}

	@Override
	public void onDisconnect(Player player) {
		plugin.trace("onDisconnect(%s)", player);
		if (listener != null) {
			listener.onDisconnect(player);
		}
	}

	@Override
	public boolean onChat(Player player, String msg) {
		plugin.trace("onChat(%s, %s)", player, msg);
		return listener != null ? listener.onChat(player, msg) : false;
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		plugin.trace("onCommand(%s, %s)", player, args);
		String command = args[0];
		// plugin.debug("onCommand: %s", command);
		List<String> args2 = new LinkedList<String>();
		for (int i = 1; i < args.length; ++i) {
			args2.add(args[i].trim());
		}
		for (Command c : plugin.getCommands()) {
			if (c.match(command) && c.canUseCommand(player))
				// plugin.debug("%s is corresponding to %s", command, c);
				return c.execute(player, command, args2);
		}
		// plugin.debug("%s is not corresponding to any command", command);
		return listener != null ? listener.onCommand(player, args) : false;
	}

	@Override
	public boolean onConsoleCommand(String[] args) {
		plugin.trace("onConsoleCommand(%s)", (Object[]) args);
		return listener != null ? listener.onConsoleCommand(args) : false;
	}

	@Override
	public void onBan(Player mod, Player player, String reason) {
		plugin.trace("onBan(%s, %s, %s)", mod, player, reason);
		if (listener != null) {
			listener.onBan(mod, player, reason);
		}
	}

	@Override
	public void onIpBan(Player mod, Player player, String reason) {
		plugin.trace("onIpBan(%s, %s, %s)", mod, player, reason);
		if (listener != null) {
			listener.onIpBan(mod, player, reason);
		}
	}

	@Override
	public void onKick(Player mod, Player player, String reason) {
		plugin.trace("onKick(%s, %s, %s)", mod, player, reason);
		if (listener != null) {
			listener.onKick(mod, player, reason);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockCreate(Player player, Block blockPlaced, Block blockClicked, int itemInHand) {
		plugin.trace("onBlockCreate(%s, %s, %s, %d)", player, blockPlaced, blockClicked, itemInHand);
		return listener != null ? listener.onBlockCreate(player, blockPlaced, blockClicked, itemInHand) : false;
	}

	@Override
	public boolean onBlockDestroy(Player player, Block block) {
		plugin.trace("onBlockDestroy(%s, %s)", player, block);
		return listener != null ? listener.onBlockDestroy(player, block) : false;
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		plugin.trace("onBlockBreak(%s, %s)", player, block);
		return listener != null ? listener.onBlockBreak(player, block) : false;
	}

	@Override
	public void onArmSwing(Player player) {
		plugin.trace("onArmSwing(%s)", player);
		if (listener != null) {
			listener.onArmSwing(player);
		}
	}

	@Override
	public boolean onInventoryChange(Player player) {
		plugin.trace("onInventoryChange(%s)", player);
		return listener != null ? listener.onInventoryChange(player) : false;
	}

	@Override
	public boolean onCraftInventoryChange(Player player) {
		plugin.trace("onCraftInventoryChange(%s)", player);
		return listener != null ? listener.onCraftInventoryChange(player) : false;
	}

	@Override
	public boolean onEquipmentChange(Player player) {
		plugin.trace("onEquipmentChange(%s)", player);
		return listener != null ? listener.onEquipmentChange(player) : false;
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		plugin.trace("onItemDrop(%s, %s)", player, item);
		return listener != null ? listener.onItemDrop(player, item) : false;
	}

	@Override
	public boolean onItemPickUp(Player player, Item item) {
		plugin.trace("onItemPickUp(%s, %s)", player, item);
		return listener != null ? listener.onItemPickUp(player, item) : false;
	}

	@Override
	public boolean onIgnite(Block block, Player player) {
		plugin.trace("onIgnite(%s, %s)", block, player);
		return listener != null ? listener.onIgnite(block, player) : false;
	}

	@Override
	public boolean onExplode(Block block) {
		plugin.trace("onExplode(%s)", block);
		return listener != null ? listener.onExplode(block) : false;
	}

	@Override
	public boolean onFlow(Block blockFrom, Block blockTo) {
		plugin.trace("onFlow(%s, %s)", blockFrom, blockTo);
		return listener != null ? listener.onFlow(blockFrom, blockTo) : false;
	}

	@Override
	public boolean onMobSpawn(Mob mob) {
		plugin.trace("onMobSpawn(%s)", mob);
		return listener != null ? listener.onMobSpawn(mob) : false;
	}

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
		plugin.trace("onDamage(%s, %s, %s, %d)", type, attacker, defender, amount);
		return listener != null ? listener.onDamage(type, attacker, defender, amount) : false;
	}

	@Override
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		plugin.trace("onHealthChange(%s, %d, %d)", player, oldValue, newValue);
		return listener != null ? listener.onHealthChange(player, oldValue, newValue) : false;
	}

	@Override
	public int onRedstoneChange(Block block, int oldLevel, int newLevel) {
		plugin.trace("onRedstoneChange(%s, %d, %d)", block, oldLevel, newLevel);
		return listener != null ? listener.onRedstoneChange(block, oldLevel, newLevel) : newLevel;
	}

	@Override
	public boolean onBlockPhysics(Block block, boolean placed) {
		plugin.trace("onBlockPhysics(%s, %b)", block, placed);
		return listener != null ? listener.onBlockPhysics(block, placed) : false;
	}

	@Override
	public void onVehicleCreate(BaseVehicle vehicle) {
		plugin.trace("onVehicleCreate(%s)", vehicle);
		if (listener != null) {
			listener.onVehicleCreate(vehicle);
		}
	}

	@Override
	public boolean onVehicleDamage(BaseVehicle vehicle, BaseEntity attacker, int damage) {
		plugin.trace("onVehicleDamage(%s, %s, %d)", vehicle, attacker, damage);
		return listener != null ? listener.onVehicleDamage(vehicle, attacker, damage) : false;
	}

	@Override
	public void onVehicleUpdate(BaseVehicle vehicle) {
		plugin.trace("onVehicleUpdate(%s)", vehicle);
		if (listener != null) {
			listener.onVehicleUpdate(vehicle);
		}
	}

	@Override
	public Boolean onVehicleCollision(BaseVehicle vehicle, BaseEntity collisioner) {
		plugin.trace("onVehicleCollision(%s, %s)", vehicle, collisioner);
		return listener != null ? listener.onVehicleCollision(vehicle, collisioner) : Boolean.valueOf(false);
	}

	@Override
	public void onVehicleDestroyed(BaseVehicle vehicle) {
		plugin.trace("onVehicleDestroyed(%s)", vehicle);
		if (listener != null) {
			listener.onVehicleDestroyed(vehicle);
		}
	}

	@Override
	public void onVehicleEnter(BaseVehicle vehicle, HumanEntity player) {
		plugin.trace("onVehicleEnter(%s, %s)", vehicle, player);
		if (listener != null) {
			listener.onVehicleEnter(vehicle, player);
		}
	}

	@Override
	public void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z) {
		plugin.trace("onVehiclePositionChange(%s, %d, %d, %d)", vehicle, x, y, z);
		if (listener != null) {
			listener.onVehiclePositionChange(vehicle, x, y, z);
		}
	}

	@Override
	public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
		plugin.trace("onItemUse(%s, %s, %s, %s)", player, blockPlaced, blockClicked, item);
		return listener != null ? listener.onItemUse(player, blockPlaced, blockClicked, item) : false;
	}

	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		plugin.trace("onBlockPlace(%s, %s, %s, %s)", player, blockPlaced, blockClicked, itemInHand);
		return listener != null ? listener.onBlockPlace(player, blockPlaced, blockClicked, itemInHand) : false;
	}

	@Override
	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		plugin.trace("onBlockRightClicked(%s, %s, %s)", player, blockClicked, item);
		if (listener != null) {
			listener.onBlockRightClicked(player, blockClicked, item);
		}
	}

	@Override
	public PluginLoader.HookResult onLiquidDestroy(PluginLoader.HookResult currentState, int liquidBlockId,
		Block targetBlock) {
		plugin.trace("onLiquidDestroy(%s, %d, %s)", currentState, liquidBlockId, targetBlock);
		return listener != null ? listener.onLiquidDestroy(currentState, liquidBlockId, targetBlock)
			: PluginLoader.HookResult.DEFAULT_ACTION;
	}

	@Override
	public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
		plugin.trace("onAttack(%s, %s, %s)", attacker, defender, amount);
		return listener != null ? listener.onAttack(attacker, defender, amount) : false;
	}

	@Override
	public boolean onOpenInventory(Player player, Inventory inventory) {
		plugin.trace("onOpenInventory(%s, %s)", player, inventory);
		return listener != null ? listener.onOpenInventory(player, inventory) : false;
	}

}