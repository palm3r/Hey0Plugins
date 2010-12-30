public class WatchDogListener extends PluginListener {

	private final WatchDog plugin;

	public WatchDogListener(WatchDog plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onTeleport(Player player, Location from, Location to) {
		boolean denied = false;
		WatchDogHandler handler = plugin.getHandler(-1, WatchDogEvent.TELEPORT);
		if (handler != null) {
			denied = handler.execute(true, WatchDogEvent.TELEPORT, player, null, null, to);
		}
		return denied;
	}

	@Override
	public void onLogin(Player player) {
		WatchDogHandler handler = plugin.getHandler(-1, WatchDogEvent.LOGIN);
		if (handler != null) {
			handler.execute(true, WatchDogEvent.LOGIN, player, null, null, player.getLocation());
		}
	}

	@Override
	public void onDisconnect(Player player) {
		WatchDogHandler handler = plugin.getHandler(-1, WatchDogEvent.LOGOUT);
		if (handler != null) {
			handler.execute(true, WatchDogEvent.LOGOUT, player, null, null, player.getLocation());
		}
	}

	@Override
	public boolean onBlockDestroy(Player player, Block block) {
		boolean denied = false;
		WatchDogHandler handler = plugin.getHandler(block.getType(), WatchDogEvent.DESTROY);
		if (handler != null) {
			denied =
				handler.execute(block.getStatus() == 3, WatchDogEvent.DESTROY, player, block.getType(),
					ItemNames.getName(block.getType()),
					new Location(block.getX(), block.getY(), block.getZ()));
		}
		return denied;
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		boolean denied = false;
		WatchDogHandler handler = plugin.getHandler(block.getType(), WatchDogEvent.DESTROY);
		if (handler != null) {
			denied =
				handler.execute(block.getStatus() == 3, WatchDogEvent.DESTROY, player, block.getType(),
					ItemNames.getName(block.getType()),
					new Location(block.getX(), block.getY(), block.getZ()));
		}
		return denied;
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		boolean denied = false;
		WatchDogHandler handler = plugin.getHandler(item.getItemId(), WatchDogEvent.DROP);
		if (handler != null) {
			denied =
				handler.execute(true, WatchDogEvent.DROP, player, item.getItemId(),
					ItemNames.getName(item.getItemId()), player.getLocation());
		}
		return denied;
	}

	@Override
	public boolean onItemPickUp(Player player, Item item) {
		boolean denied = false;
		WatchDogHandler handler = plugin.getHandler(item.getItemId(), WatchDogEvent.PICKUP);
		if (handler != null) {
			denied =
				handler.execute(true, WatchDogEvent.PICKUP, player, item.getItemId(),
					ItemNames.getName(item.getItemId()), player.getLocation());
		}
		return denied;
	}

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender,
		int amount) {
		boolean denied = false;
		if (attacker != null && attacker.isPlayer() && defender != null && defender.isPlayer()) {
			Player att = attacker.getPlayer();
			Player def = defender.getPlayer();
			if (att != null && def != null) {
				WatchDogEvent event = def.getHealth() > amount ? WatchDogEvent.ATTACK : WatchDogEvent.KILL;
				WatchDogHandler handler = plugin.getHandler(-1, event);
				if (handler != null) {
					denied = handler.execute(true, event, att, null, def.getName(), def.getLocation());
				}
			}
		}
		return denied;
	}

	@Override
	public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
		boolean denied = false;
		if (blockClicked != null && item != null) {
			WatchDogHandler handler = plugin.getHandler(item.getItemId(), WatchDogEvent.USE);
			if (handler != null) {
				denied =
					handler.execute(true, WatchDogEvent.USE, player, item.getItemId(),
						ItemNames.getName(item.getItemId()),
						new Location(blockClicked.getX(), blockClicked.getY(), blockClicked.getZ()));
			}
		}
		return denied;
	}

	@Override
	public boolean
		onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		boolean denied = false;
		WatchDogHandler handler = plugin.getHandler(blockPlaced.getType(), WatchDogEvent.PLACE);
		if (handler != null) {
			denied =
				handler.execute(true, WatchDogEvent.PLACE, player, blockPlaced.getType(),
					ItemNames.getName(blockPlaced.getType()),
					new Location(blockPlaced.getX(), blockPlaced.getY(), blockPlaced.getZ()));
		}
		return denied;
	}

	@Override
	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		if (blockClicked.getType() == 54) {
			WatchDogHandler handler = plugin.getHandler(blockClicked.getType(), WatchDogEvent.OPEN);
			if (handler != null) {
				handler.execute(true, WatchDogEvent.OPEN, player, blockClicked.getType(),
					ItemNames.getName(blockClicked.getType()),
					new Location(blockClicked.getX(), blockClicked.getY(), blockClicked.getZ()));
			}
		}
	}

}
