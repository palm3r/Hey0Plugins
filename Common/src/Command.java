import java.util.*;

public abstract class Command {

	private String command;
	private List<String> alias;
	private String params, description;
	private String auth;
	private boolean active;

	public Command(String command, String[] alias, String params,
		String description, String auth) {
		this.command = command;
		this.alias = alias != null ? Arrays.asList(alias) : null;
		this.params = params;
		this.description = description;
		this.auth = auth;
		this.active = false;
	}

	public Command(String command, String[] alias, String params,
		String description) {
		this(command, alias, params, description, null);
	}

	public boolean isActive() {
		return active;
	}

	public boolean canUseCommand(Player player) {
		return auth != null ? player.isAdmin() || player.canUseCommand(auth) : true;
	}

	public String getUsage(boolean description, boolean alias) {
		StringBuilder sb = new StringBuilder();
		sb.append(Colors.Rose + "Usage: " + Colors.White);
		sb.append(command);
		sb.append(" " + getHelp(description, alias));
		return sb.toString().trim();
	}

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

	public abstract boolean call(final Player player, final String command,
		final List<String> args);
}
