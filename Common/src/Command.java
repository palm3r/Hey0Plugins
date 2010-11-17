import java.util.*;

public abstract class Command {

	private List<String> aliases;
	private String params, description;

	public Command(String[] aliases, String params, String description) {
		this.aliases = Arrays.asList(aliases);
		this.params = params;
		this.description = description;
	}

	public String getUsage(boolean withDescription) {
		StringBuilder sb = new StringBuilder();
		sb.append(Colors.Rose + "Usage: " + Colors.White);
		sb.append(aliases.get(0));
		sb.append(" " + getHelp(withDescription));
		return sb.toString();
	}

	private String getHelp(boolean withDescription) {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty())
			sb.append(params);
		if (withDescription && description != null && !description.isEmpty())
			sb.append((sb.length() > 0 ? " " : "") + "- " + description);
		return sb.toString();
	}

	public final void enable() {
		etc.getInstance().addCommand(aliases.get(0), getHelp(true));
	}

	public final void disable() {
		etc.getInstance().removeCommand(aliases.get(0));
	}

	public final boolean match(String command) {
		return aliases.contains(command);
	}

	public abstract boolean call(final Player player, final String[] args);
}
