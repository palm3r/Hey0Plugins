import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;

public class IdleKick extends PluginEx {

	private static final String INTERVAL_KEY = "interval";
	private static final String INTERVAL_DEFAULT = "0";
	private static final String IGNORE_GROUPS_KEY = "ignore-groups";
	private static final String IGNORE_GROUPS_DEFAULT = "admins,mods";

	private long idleInterval;
	private final Map<String, Long> idleLastTimes = new HashMap<String, Long>();
	private final Set<String> idleIgnoreGroups = new HashSet<String>();
	private ScheduledExecutorService scheduler = null;
	private ScheduledFuture<?> future = null;

	public void idleUpdate(Player player) {
		if (player != null && idleIgnoreGroups != null && idleLastTimes != null) {
			for (String group : idleIgnoreGroups) {
				if (!player.isInGroup(group))
					return;
			}
			idleLastTimes.put(player.getName(), System.currentTimeMillis());
		}
	}

	public void idleRemove(Player player) {
		idleLastTimes.remove(player);
	}

	public IdleKick() {
		IdleKickListener listener = new IdleKickListener(this);
		for (PluginLoader.Hook hook : PluginLoader.Hook.values()) {
			if (hook != PluginLoader.Hook.NUM_HOOKS) {
				addHook(hook, PluginListener.Priority.MEDIUM, listener);
			}
		}
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	protected void onEnable() {
		idleInterval = Long.valueOf(getProperty(INTERVAL_KEY, INTERVAL_DEFAULT)) * 60L * 1000L;
		for (String group : StringUtils.split(getProperty(IGNORE_GROUPS_KEY, IGNORE_GROUPS_DEFAULT), ", ")) {
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
