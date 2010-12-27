public class IdleKickListener extends PluginListener {

	private final IdleKick plugin;

	public IdleKickListener(IdleKick plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		plugin.idleUpdate(player);
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public String onLoginChecks(String user) {
		return null;
	}

	@Override
	public void onLogin(Player player) {
		plugin.idleUpdate(player);
	}

	@Override
	public void onDisconnect(Player player) {
		plugin.idleRemove(player);
	}

	@Override
	public boolean onChat(Player player, String msg) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onCommand(Player player, String[] split) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onConsoleCommand(String[] split) {
		return false;
	}

	@Override
	public void onBan(Player mod, Player player, String reason) {
		plugin.idleUpdate(mod);
	}

	@Override
	public void onIpBan(Player mod, Player player, String reason) {
		plugin.idleUpdate(mod);
	}

	@Override
	public void onKick(Player mod, Player player, String reason) {
		plugin.idleUpdate(mod);
	}

	@Override
	@Deprecated
	public boolean onBlockCreate(Player player, Block placed, Block clicked, int item) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onBlockDestroy(Player player, Block block) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public void onArmSwing(Player player) {
		plugin.idleUpdate(player);
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onItemPickUp(Player player, Item item) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onIgnite(Block block, Player player) {
		return false;
	}

	@Override
	public boolean onExplode(Block block) {
		return false;
	}

	@Override
	public boolean onFlow(Block from, Block to) {
		return false;
	}

	@Override
	public boolean onMobSpawn(Mob mob) {
		return false;
	}

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
		if (attacker != null && attacker.isPlayer()) {
			plugin.idleUpdate(attacker.getPlayer());
		}
		return false;
	}

	@Override
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		return false;
	}

	@Override
	public int onRedstoneChange(Block block, int oldLevel, int newLevel) {
		return newLevel;
	}

	@Override
	public boolean onBlockPhysics(Block block, boolean placed) {
		return false;
	}

	@Override
	public void onVehicleCreate(BaseVehicle vehicle) {
	}

	@Override
	public boolean onVehicleDamage(BaseVehicle vehicle, BaseEntity attacker, int damage) {
		return false;
	}

	@Override
	public void onVehicleUpdate(BaseVehicle vehicle) {
	}

	@Override
	public Boolean onVehicleCollision(BaseVehicle vehicle, BaseEntity collisioner) {
		return Boolean.valueOf(false);
	}

	@Override
	public void onVehicleDestroyed(BaseVehicle vehicle) {
	}

	@Override
	public void onVehicleEnter(BaseVehicle vehicle, HumanEntity player) {
	}

	@Override
	public void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z) {
	}

	@Override
	public boolean onItemUse(Player player, Block placed, Block clicked, Item item) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public boolean onBlockPlace(Player player, Block placed, Block clicked, Item item) {
		plugin.idleUpdate(player);
		return false;
	}

	@Override
	public void onBlockRightClicked(Player player, Block block, Item item) {
		plugin.idleUpdate(player);
	}

	@Override
	public PluginLoader.HookResult onLiquidDestroy(PluginLoader.HookResult state, int liquid, Block block) {
		return PluginLoader.HookResult.DEFAULT_ACTION;
	}

	@Override
	public boolean onAttack(LivingEntity attacker, LivingEntity defender, Integer amount) {
		if (attacker != null && attacker.isPlayer()) {
			plugin.idleUpdate(attacker.getPlayer());
		}
		return false;
	}

	@Override
	public boolean onOpenInventory(Player player, Inventory inventory) {
		plugin.idleUpdate(player);
		return false;
	}

}
