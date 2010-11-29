import java.util.*;

/**
 * Command class
 * This class is designed for using with PluginEx.
 * See below example:
 * 
 * <pre>
 * public class HelloWorldCommand extends Command {
 * 	private PluginEx plugin;
 * 
 * 	public HelloWorldCommand(PluginEx plugin) {
 * 		super(null, &quot;Say hello world to you&quot;, null);
 * 		setAlias(&quot;/hw&quot;);
 * 		this.plugin = plugin;
 * 	}
 * 
 * 	public boolean execute(final Player player, final String command,
 * 		final List&lt;String&gt; args) {
 * 		Chat.toPlayer(player, &quot;Hello World and you! %s&quot;, player.getName());
 * 		plugin.debug(&quot;HelloWorldCommand called&quot;);
 * 		return true;
 * 	}
 * }
 * </pre>
 * 
 * @author palm3r
 */
public abstract class Command {

	private String command;
	private List<String> alias;
	private String params, description;
	private String require;
	private boolean active;

	/**
	 * @param command
	 * @param alias
	 * @param params
	 * @param description
	 * @param require
	 */
	public Command(String params, String description) {
		String thisClass = "Command";
		String subClass = getClass().getSimpleName();
		this.command = "/"
			+ (subClass.endsWith(thisClass) ? subClass.substring(0, subClass.length()
				- thisClass.length()) : subClass).toLowerCase();
		this.params = params;
		this.description = description;
		this.active = false;
	}

	public String getCommand() {
		return command;
	}

	public void setRequire(String require) {
		this.require = require;
	}

	public String getRequire() {
		return require;
	}

	public void setAlias(String... alias) {
		this.alias = alias != null ? Arrays.asList(alias) : null;
	}

	public List<String> getAlias() {
		return alias;
	}

	public String getParams() {
		return params;
	}

	public String getDescription() {
		return description;
	}

	public String getRequiredCommand() {
		return require;
	}

	/**
	 * Return state whether plugin is active
	 * 
	 * @return true = active / false = inactive
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Return whether player can use this command
	 * 
	 * @param player
	 * @return
	 */
	public boolean canUseCommand(Player player) {
		return require != null
			? (player.isAdmin() || player.canUseCommand(require)) : true;
	}

	/**
	 * Return usage message
	 * 
	 * @param description
	 * @param alias
	 * @return
	 */
	public String getUsage(boolean description, boolean alias) {
		StringBuilder sb = new StringBuilder();
		sb.append(Colors.Rose + "Usage: " + Colors.White);
		sb.append(command);
		sb.append(" " + getHelp(description, alias));
		return sb.toString().trim();
	}

	/**
	 * Return help message
	 * This is for message displayed in /help <page>
	 * 
	 * @param description
	 * @param alias
	 * @return
	 */
	private String getHelp(boolean description, boolean alias) {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty())
			sb.append(params);
		if (description && this.description != null && !this.description.isEmpty())
			sb.append((sb.length() > 0 ? " " : "") + "- " + this.description);
		if (alias && this.alias != null && !this.alias.isEmpty()) {
			sb.append(" (alias: ");
			for (int i = 0; i < this.alias.size(); ++i) {
				if (i > 0)
					sb.append(", ");
				sb.append(this.alias.get(i));
			}
			sb.append(")");
		}
		return sb.toString().trim();
	}

	/**
	 * Enable command
	 */
	public final void enable() {
		etc.getInstance().addCommand(command, getHelp(true, true));
		active = true;
	}

	/**
	 * Disable command
	 */
	public final void disable() {
		etc.getInstance().removeCommand(command);
		active = false;
	}

	/**
	 * Return whether specified command is matched with name or alias
	 * 
	 * @param command
	 * @return
	 */
	public final boolean match(String command) {
		return command.equalsIgnoreCase(this.command)
			|| (alias != null && alias.contains(command));
	}

	/**
	 * Execute command
	 * 
	 * @param player
	 * @param command
	 * @param args
	 * @return
	 */
	public abstract boolean execute(final Player player, final String command,
		final List<String> args);

	/**
	 * Return whether this object equals with specified object
	 */
	public boolean equals(Object obj) {
		return (obj instanceof Command) && ((Command) obj).command.equals(command);
	}

	/**
	 * Return hash value
	 */
	public int hashCode() {
		return Command.class.hashCode() + command.hashCode();
	}
}
