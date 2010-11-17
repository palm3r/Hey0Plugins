import java.util.*;
import java.util.concurrent.*;

public class InvitEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "60";

	private ScheduledExecutorService scheduler;
	private Map<String, Pair<String, ScheduledFuture<?>>> invites;
	private int expires;

	public InvitEx() {
		initPluginEx("InvitEx", null, PluginListener.Priority.LOW,
				PluginLoader.Hook.COMMAND);

		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.invites = new HashMap<String, Pair<String, ScheduledFuture<?>>>();

		addCommand(new InviteCommand(this));
	}

	public void addInvite(String from, String to, Runnable timeout) {
		ScheduledFuture<?> future = scheduler.schedule(timeout, expires,
				TimeUnit.SECONDS);
		invites.put(to, new Pair<String, ScheduledFuture<?>>(from, future));
	}

	public Pair<String, ScheduledFuture<?>> getInvite(String key) {
		return invites.containsKey(key) ? invites.get(key) : null;
	}

	public void removeInvite(String key) {
		if (invites.containsKey(key)) {
			invites.get(key).second.cancel(true);
			invites.remove(key);
		}
	}

	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
	}

}
