import java.util.*;
import java.util.Map.*;

public class Trace extends PluginEx {

	private final Map<PluginLoader.Hook, Set<Player>> map =
		new HashMap<PluginLoader.Hook, Set<Player>>();

	public Trace() {
		TraceListener listener = new TraceListener(this);
		for (PluginLoader.Hook hook : PluginLoader.Hook.values()) {
			switch (hook) {
			case COMMAND:
			case NUM_HOOKS:
				break;
			default:
				addHook(hook, PluginListener.Priority.MEDIUM, listener);
				map.put(hook, new HashSet<Player>());
				break;
			}
		}
		addCommand(new TraceCommand(this));
	}

	@Override
	protected void onEnable() {
	}

	public void showTrace(Player player, PluginLoader.Hook hook, Map<String, Object> args) {
		List<Player> players = new LinkedList<Player>();
		if (player != null) {
			if (map.get(hook).contains(player)) {
				players.add(player);
			}
		} else {
			players.addAll(map.get(hook));
		}
		if (!players.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(hook.toString());
			sb.append("(");
			int index = 0;
			for (Map.Entry<String, Object> entry : args.entrySet()) {
				if (index > 0) {
					sb.append(", ");
				}
				String key = entry.getKey();
				Object value = entry.getValue();
				sb.append(key + "=" + value != null ? value.toString() : "(null)");
			}
			sb.append(")");
			String msg = sb.toString();
			for (Player p : players) {
				p.sendMessage(msg);
			}
		}
	}

	public List<PluginLoader.Hook> getTrace(Player player) {
		List<PluginLoader.Hook> hooks = new LinkedList<PluginLoader.Hook>();
		for (Entry<PluginLoader.Hook, Set<Player>> entry : map.entrySet()) {
			if (entry.getValue().contains(player)) {
				hooks.add(entry.getKey());
			}
		}
		return hooks;
	}

	public void toggleTrace(Player player, PluginLoader.Hook hook) {
		Set<Player> set = map.get(hook);
		if (set.contains(player)) {
			set.remove(player);
		} else {
			set.add(player);
		}
		map.put(hook, set);
	}

}
