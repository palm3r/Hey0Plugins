public class BorderListener extends PluginListener {

	private final Border plugin;

	public BorderListener(Border plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		if (plugin.isOutside(to.x, to.y, to.z)) {
			player.teleportTo(plugin.getLocation(player));
		} else {
			plugin.setLocation(player, to);
		}
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		if (plugin.isOutside(to.x, to.y, to.z))
			return true;
		plugin.setLocation(player, to);
		return false;
	}

	@Override
	public void onLogin(Player player) {
		Location location = null;
		if (plugin.isOutside(player.getX(), player.getY(), player.getZ())) {
			location = etc.getServer().getSpawnLocation();
			player.teleportTo(location);
		} else {
			location = player.getLocation();
		}
		plugin.setLocation(player, location);
	}

	@Override
	public void onDisconnect(Player player) {
		plugin.removeLocation(player);
	}

	@Override
	public boolean onBlockDestroy(Player player, Block block) {
		return plugin.getAllow("block") == false && plugin.isOutside(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		return plugin.getAllow("block") == false && plugin.isOutside(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public boolean onIgnite(Block block, Player player) {
		return plugin.getAllow("fire") == false && plugin.isOutside(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public boolean onExplode(Block block) {
		return plugin.getAllow("explosion") == false && plugin.isOutside(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public boolean onFlow(Block blockFrom, Block blockTo) {
		return plugin.getAllow("flow") == false && plugin.isOutside(blockTo.getX(), blockTo.getY(), blockTo.getZ());
	}

	@Override
	public boolean onMobSpawn(Mob mob) {
		return plugin.getAllow("mob") == false && plugin.isOutside(mob.getX(), mob.getY(), mob.getZ());
	}

	@Override
	public int onRedstoneChange(Block block, int oldLevel, int newLevel) {
		return plugin.getAllow("redstone") == false && plugin.isOutside(block.getX(), block.getY(), block.getZ())
			? oldLevel : newLevel;
	}

	@Override
	public boolean onBlockPhysics(Block block, boolean placed) {
		return plugin.getAllow("physics") == false && plugin.isOutside(block.getX(), block.getY(), block.getZ());
	}

	@Override
	public void onVehicleCreate(BaseVehicle vehicle) {
		if (plugin.getAllow("vehicle") == false && plugin.isOutside(vehicle.getX(), vehicle.getY(), vehicle.getZ())) {
			vehicle.destroy();
		}
	}

	@Override
	public void onVehicleUpdate(BaseVehicle vehicle) {
		if (plugin.getAllow("vehicle") == false && plugin.isOutside(vehicle.getX(), vehicle.getY(), vehicle.getZ())) {
			vehicle.destroy();
		}
	}

	@Override
	public void onVehiclePositionChange(BaseVehicle vehicle, int x, int y, int z) {
		if (plugin.getAllow("vehicle") == false && plugin.isOutside(x, y, z)) {
			vehicle.destroy();
		}
	}

	@Override
	public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
		return plugin.getAllow("block") == false && blockPlaced != null
			&& plugin.isOutside(blockPlaced.getX(), blockPlaced.getY(), blockPlaced.getZ());
	}

	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		return plugin.getAllow("block") == false
			&& plugin.isOutside(blockPlaced.getX(), blockPlaced.getY(), blockPlaced.getZ());
	}

	@Override
	public PluginLoader.HookResult onLiquidDestroy(PluginLoader.HookResult currentState, int liquidBlockId,
		Block targetBlock) {
		return plugin.getAllow("flow") == false
			&& plugin.isOutside(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ())
			? PluginLoader.HookResult.PREVENT_ACTION : PluginLoader.HookResult.DEFAULT_ACTION;
	}

}
