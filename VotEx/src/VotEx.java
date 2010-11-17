import java.util.*;
import java.util.concurrent.*;

public class VotEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "120";

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private Map<String, Boolean> answers;
	private int expires;

	public VotEx() {
		initPluginEx("VotEx", null, PluginListener.Priority.LOW,
				PluginLoader.Hook.COMMAND);

		scheduler = Executors.newSingleThreadScheduledExecutor();

		addCommand(new VoteCommand(this));
	}

	public boolean isVoting() {
		return future != null;
	}

	public Map<String, Boolean> getAnswers() {
		return answers;
	}

	public void beginVote(final String subject) {
		answers = new HashMap<String, Boolean>();
		future = scheduler.schedule(new Runnable() {
			public void run() {
				int yes = 0;
				for (Map.Entry<String, Boolean> entry : answers.entrySet()) {
					if (entry.getValue())
						yes++;
				}
				Chat.toBroadcast(Colors.LightBlue + "[VOTE] " + Colors.White + subject);
				Chat.toBroadcast("Yes %d No %d Abs %d", yes, answers.size() - yes, etc
						.getServer().getPlayerList().size()
						- answers.size());
				future = null;
			}
		}, expires, TimeUnit.SECONDS);
	}

	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
	}

}
