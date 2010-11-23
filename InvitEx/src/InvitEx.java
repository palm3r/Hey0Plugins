import java.util.*;
import java.util.concurrent.*;

public class InvitEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "60";

	private ScheduledExecutorService scheduler;
	private Map<String, Pair<String, ScheduledFuture<?>>> futures;
	private int expires;
	private Command invite;

	public InvitEx() {
		super("InvitEx");

		scheduler = Executors.newSingleThreadScheduledExecutor();
		futures = new TreeMap<String, Pair<String, ScheduledFuture<?>>>();
		invite = new InviteCommand(this);

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.LOW);
	}

	public void addInvite(String hostName, String guestName, Runnable timeout) {
		ScheduledFuture<?> future = scheduler.schedule(timeout, expires,
			TimeUnit.SECONDS);
		futures.put(guestName, new Pair<String, ScheduledFuture<?>>(hostName,
			future));
	}

	public Pair<String, ScheduledFuture<?>> getInvite(String guestName) {
		return futures.containsKey(guestName) ? futures.get(guestName) : null;
	}

	public void removeInvite(String guestName) {
		if (futures.containsKey(guestName)) {
			futures.get(guestName).second.cancel(true);
			futures.remove(guestName);
		}
	}

	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
		addCommand(invite);
	}

	protected void onDisable() {
		removeCommand(invite);
	}

}
