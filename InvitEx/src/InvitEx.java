import java.util.*;
import java.util.concurrent.*;

public class InvitEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "60";

	private ScheduledExecutorService scheduler;
	private Map<String, Pair<String, ScheduledFuture<?>>> futures;
	private int expires;
	private Command invite, accept;

	public InvitEx() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		futures = new TreeMap<String, Pair<String, ScheduledFuture<?>>>();
		invite = new InviteCommand(this);
		accept = new AcceptCommand(this);

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.LOW);
	}

	public void addInvite(String hostName, String guestName, Runnable timeout) {
		ScheduledFuture<?> future = scheduler.schedule(timeout, expires,
			TimeUnit.SECONDS);
		futures.put(guestName.toLowerCase(), new Pair<String, ScheduledFuture<?>>(
			hostName.toLowerCase(), future));
	}

	public Pair<String, ScheduledFuture<?>> getInvite(String guestName) {
		String gn = guestName.toLowerCase();
		return futures.containsKey(gn) ? futures.get(gn) : null;
	}

	public void removeInvite(String guestName) {
		String gn = guestName.toLowerCase();
		if (futures.containsKey(gn)) {
			futures.get(gn).second.cancel(true);
			futures.remove(gn);
		}
	}

	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
		addCommand(invite, accept);
	}

	protected void onDisable() {
		removeCommand(invite, accept);
	}

}
