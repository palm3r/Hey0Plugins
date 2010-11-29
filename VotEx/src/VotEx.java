import java.util.*;
import java.util.concurrent.*;

public class VotEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "120";

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private Map<String, Boolean> answers;
	private int expires;
	private Command vote, yes, no;

	public VotEx() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		answers = new TreeMap<String, Boolean>();
		vote = new VoteCommand(this);
		yes = new YesCommand(this);
		no = new NoCommand(this);

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.LOW);
	}

	public boolean isVoting() {
		return future != null;
	}

	public Map<String, Boolean> getAnswers() {
		return answers;
	}

	public void beginVote(final String subject) {
		Chat.toBroadcast((Colors.LightBlue + "[VOTE] ") + (Colors.White + subject));
		Chat.toBroadcast((Colors.LightGray + "Please vote ")
			+ (Colors.LightGreen + yes.getCommand()) + (Colors.LightGray + " or ")
			+ (Colors.Rose + no.getCommand()));
		answers.clear();
		future = scheduler.schedule(new Runnable() {
			public void run() {
				int yes = 0;
				for (Map.Entry<String, Boolean> entry : answers.entrySet()) {
					if (entry.getValue())
						yes++;
				}
				int no = answers.size() - yes;
				int abs = etc.getServer().getPlayerList().size() - (yes + no);
				Chat.toBroadcast((Colors.LightBlue + "[VOTE] ")
					+ (Colors.White + subject));
				Chat.toBroadcast((Colors.LightGreen + "YES " + yes)
					+ (Colors.Rose + " NO " + no) + (Colors.LightGray + " ABS " + abs));
				future = null;
			}
		}, expires, TimeUnit.SECONDS);
	}

	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
		addCommand(vote, yes, no);
	}

	protected void onDisable() {
		removeCommand(vote, yes, no);
	}

}
