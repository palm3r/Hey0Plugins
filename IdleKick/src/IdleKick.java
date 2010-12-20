import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;

public class IdleKick extends PluginEx {

	private static final String INTERVAL_KEY = "interval";
	private static final String INTERVAL_DEFAULT = "0";

	private static final String IGNORE_GROUPS_KEY = "ignore-groups";
	private static final String IGNORE_GROUPS_DEFAULT = "admins,mods";

	private static long idleInterval;
	private static Map<String, Long> idleLastTimes = new HashMap<String, Long>();
	private static Set<String> idleIgnoreGroups = new HashSet<String>();

	private static void idleUpdate(Player player) {
		if (player != null && idleIgnoreGroups != null && idleLastTimes != null) {
			for (String group : idleIgnoreGroups) {
				if (!player.isInGroup(group))
					return;
			}
			idleLastTimes.put(player.getName(), System.currentTimeMillis());
		}
	}

	private final PluginListener listener = new PluginListener() {

		@Override
		public void onLogin(Player player) {
			idleUpdate(player);
		}

		@Override
		public void onDisconnect(Player player) {
			idleLastTimes.remove(player);
		}

		@Override
		public boolean onChat(Player player, String msg) {
			idleUpdate(player);
			return false;
		}

		@Override
		public boolean onBlockDestroy(Player player, Block block) {
			idleUpdate(player);
			return false;
		}

		@Override
		public boolean onBlockBreak(Player player, Block block) {
			idleUpdate(player);
			return false;
		}

		@Override
		public void onArmSwing(Player player) {
			idleUpdate(player);
		}

		@Override
		public boolean onItemDrop(Player player, Item item) {
			idleUpdate(player);
			return false;
		}

		@Override
		public boolean onItemUse(Player player, Block blockPlaced,
			Block blockClicked, Item item) {
			idleUpdate(player);
			return false;
		}

		@Override
		public boolean onBlockPlace(Player player, Block blockPlaced,
			Block blockClicked, Item itemInHand) {
			idleUpdate(player);
			return false;
		}

	};

	private ScheduledExecutorService scheduler = null;
	private ScheduledFuture<?> future = null;

	public IdleKick() {
		addHook(PluginLoader.Hook.LOGIN, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.DISCONNECT, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.CHAT, PluginListener.Priority.MEDIUM, listener);
		addHook(PluginLoader.Hook.BLOCK_DESTROYED, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_BROKEN, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ARM_SWING, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ITEM_DROP, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.ITEM_USE, PluginListener.Priority.MEDIUM,
			listener);
		addHook(PluginLoader.Hook.BLOCK_PLACE, PluginListener.Priority.MEDIUM,
			listener);
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	protected void onEnable() {
		idleInterval =
			Long.valueOf(getProperty(INTERVAL_KEY, INTERVAL_DEFAULT)) * 60L * 1000L;
		for (String group : StringUtils.split(
			getProperty(IGNORE_GROUPS_KEY, IGNORE_GROUPS_DEFAULT), ", ")) {
			idleIgnoreGroups.add(group);
		}
		for (Player player : etc.getServer().getPlayerList()) {
			idleUpdate(player);
		}
		if (idleInterval > 0) {
			future = scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					long limit = System.currentTimeMillis() - idleInterval;
					for (Entry<String, Long> entry : idleLastTimes.entrySet()) {
						if (entry.getValue() < limit) {
							Player player = etc.getServer().getPlayer(entry.getKey());
							if (player != null) {
								for (String group : idleIgnoreGroups) {
									if (player.isInGroup(group))
										return;
								}
								player.kick(String.format("You have been kicked (reason: IDLE)"));
							}
						}
					}
				}
			}, 1, 1, TimeUnit.MINUTES);
		}
	}

	@Override
	protected void onDisable() {
		if (future != null) {
			future.cancel(true);
			future = null;
		}
	}

}
