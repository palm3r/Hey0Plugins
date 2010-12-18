import java.util.*;
import org.apache.commons.lang.builder.*;

/**
 * Command class
 * This class is designed for using with PluginEx.
 * See below example:
 * 
 * <pre>
 * public class HelloWorldCommand extends Command {
 * 
 * 	public HelloWorldCommand() {
 * 		super(null, &quot;Say hello world to you&quot;);
 * 		setAlias(&quot;/hw&quot;);
 * 	}
 * 
 * 	public boolean execute(final Player player, final String command,
 * 		final List&lt;String&gt; args) {
 * 		Chat.player(player, &quot;Hello World and you! %s&quot;, player.getName());
 * 		return true;
 * 	}
 * }
 * </pre>
 * 
 * @author palm3r
 */
public abstract class Command {

	private final String command;
	private List<String> alias;
	private final String params, description;
	private String require;
	private boolean active;

	/**
	 * @param params
	 *          Parameters part of help string
	 * @param description
	 *          Description part of help string
	 */
	public Command(String params, String description) {
		String thisClass = "Command";
		String subClass = getClass().getSimpleName();
		command =
			"/"
				+ (subClass.endsWith(thisClass) ? subClass.substring(0,
					subClass.length() - thisClass.length()) : subClass).toLowerCase();
		this.params = params;
		this.description = description;
		active = false;
	}

	/**
	 * Override this method to execute the operation of derived command
	 * 
	 * @param player
	 *          Player who execute the command
	 * @param command
	 *          Command name (it might be an alias)
	 * @param args
	 *          Command arguments (it does not include command itself)
	 * @return
	 *         Return true when the command processed the request
	 */
	public abstract boolean execute(final Player player, final String command,
		final List<String> args);

	/**
	 * Set required command authority
	 * 
	 * @param require
	 */
	public void setRequire(String require) {
		this.require = require;
	}

	/**
	 * Set aliases
	 * 
	 * @param alias
	 */
	public void setAlias(String... alias) {
		this.alias = alias != null ? Arrays.asList(alias) : null;
	}

	public String getUsage(boolean description, boolean alias) {
		StringBuilder sb = new StringBuilder();
		sb.append(Colors.Rose + "Usage: ");
		sb.append(Colors.White + command);
		sb.append(" " + getHelp(description, alias));
		return sb.toString().trim();
	}

	private String getHelp(boolean description, boolean alias) {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			sb.append(params);
		}
		if (description && this.description != null && !this.description.isEmpty()) {
			sb.append((sb.length() > 0 ? " " : "") + "- " + this.description);
		}
		if (alias && this.alias != null && !this.alias.isEmpty()) {
			sb.append(" (alias: ");
			for (int i = 0; i < this.alias.size(); ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(this.alias.get(i));
			}
			sb.append(")");
		}
		return sb.toString().trim();
	}

	public String getCommand() {
		return command;
	}

	public String getRequire() {
		return require;
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

	public boolean isActive() {
		return active;
	}

	public boolean canUseCommand(Player player) {
		return require != null
			? (player.isAdmin() || player.canUseCommand(require)) : true;
	}

	public final void enable() {
		etc.getInstance().addCommand(command, getHelp(true, true));
		active = true;
	}

	public final void disable() {
		etc.getInstance().removeCommand(command);
		active = false;
	}

	public final boolean match(String command) {
		return command.equalsIgnoreCase(this.command)
			|| (alias != null && alias.contains(command));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Command))
			return false;
		Command c = (Command) obj;
		return new EqualsBuilder().append(command, c.command).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(command).hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(command).append(alias).append(
			params).append(description).append(require).append(active).toString();
	}

}
