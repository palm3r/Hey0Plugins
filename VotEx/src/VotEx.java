import java.util.*;
import java.util.concurrent.*;

public class VotEx extends PluginEx {

	public static final String EXPIRES_KEY = "expires";
	public static final String EXPIRES_DEFAULT = "120";

	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private final Map<String, Boolean> answers;
	private int expires;
	private final Command vote, yes, no;

	public VotEx() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		answers = new HashMap<String, Boolean>();
		vote = new VoteCommand(this);
		yes = new YesCommand(this);
		no = new NoCommand(this);

		// addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.MEDIUM);
	}

	public boolean isVoting() {
		return future != null;
	}

	public Map<String, Boolean> getAnswers() {
		return answers;
	}

	public void beginVote(final String subject) {
		Chat.broadcast((Colors.LightBlue + "[VOTE] ") + (Colors.White + subject));
		Chat.broadcast((Colors.LightGray + "Please vote ")
			+ (Colors.LightGreen + yes.getCommand()) + (Colors.LightGray + " or ")
			+ (Colors.Rose + no.getCommand()));
		answers.clear();
		future = scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				int yes = 0;
				for (Map.Entry<String, Boolean> entry : answers.entrySet()) {
					if (entry.getValue()) {
						yes++;
					}
				}
				int no = answers.size() - yes;
				int abs = etc.getServer().getPlayerList().size() - (yes + no);
				Chat.broadcast((Colors.LightBlue + "[VOTE] ")
					+ (Colors.White + subject));
				Chat.broadcast((Colors.LightGreen + "YES " + yes)
					+ (Colors.Rose + " NO " + no) + (Colors.LightGray + " ABS " + abs));
				future = null;
			}
		}, expires, TimeUnit.SECONDS);
	}

	@Override
	protected void onEnable() {
		expires = Integer.valueOf(getProperty(EXPIRES_KEY, EXPIRES_DEFAULT));
		addCommand(vote, yes, no);
	}

	@Override
	protected void onDisable() {
		removeCommand(vote, yes, no);
	}

}
