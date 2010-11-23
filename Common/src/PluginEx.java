import java.io.*;
import java.util.*;

public abstract class PluginEx extends Plugin {

	public static final String INI_FILE = "plugin.ini";

	private Map<PluginLoader.Hook, HookInfo> listeners;
	private String pluginName;
	private List<Command> commands;
	private Map<String, String> props;

	protected PluginEx(String name) {
		this.pluginName = name;
		this.listeners = new HashMap<PluginLoader.Hook, HookInfo>();
		this.commands = new ArrayList<Command>();
		this.props = new TreeMap<String, String>();
	}

	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority) {
		addHook(hook, priority, null);
	}

	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority, PluginListener listener) {
		listeners.put(hook, new HookInfo(new Shifter(listener), priority));
	}

	/**
	 * 
	 * @param command
	 */
	public final void addCommand(Command... commands) {
		for (Command c : commands) {
			this.commands.add(c);
		}
	}

	/**
	 * Remove Command
	 * 
	 * @param alias
	 * @return
	 */
	public final void removeCommand(Command... commands) {
		for (Command c : commands) {
			this.commands.remove(c);
			c.disable();
		}
	}

	/**
	 * Get property
	 * 
	 * @param key
	 * @return
	 */
	public final String getProperty(String key) {
		return props.get(key);
	}

	/**
	 * Get property from plugin.ini
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public final String getProperty(String key, String defaultValue) {
		String value = defaultValue;
		if (props.containsKey(key)) {
			value = props.get(key);
		} else {
			setProperty(key, value);
		}
		return value;
	}

	/**
	 * Set property
	 * 
	 * @param key
	 * @param value
	 */
	public final void setProperty(String key, String value) {
		props.put(key, value);
	}

	public final <T> Set<T> loadSet(String fileName,
		Converter<String, T> converter) throws IOException {
		return Tools.loadSet(pluginName + File.separator + fileName, converter);
	}

	public final <T> Set<T> loadSet(String fileName,
		Converter<String, T> converter, Set<T> set) throws IOException {
		return Tools
			.loadSet(pluginName + File.separator + fileName, converter, set);
	}

	public final <T> void saveSet(Set<T> data, String fileName,
		Converter<T, String> converter) throws IOException {
		Tools.saveSet(data, pluginName + File.separator + fileName, converter);
	}

	/**
	 * Load file from the directory only for the plugin.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param fileName
	 * @param converter
	 * @return
	 * @throws IOException
	 */
	public final <K, V> Map<K, V> loadMap(String fileName,
		Converter<String, Pair<K, V>> converter) throws IOException {
		return Tools.loadMap(pluginName + File.separator + fileName, converter);
	}

	public final <K, V> Map<K, V> loadMap(String fileName,
		Converter<String, Pair<K, V>> converter, Map<K, V> map) throws IOException {
		return Tools
			.loadMap(pluginName + File.separator + fileName, converter, map);
	}

	/**
	 * Save file to the directory only for the plugin.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param data
	 * @param fileName
	 * @param converter
	 * @throws IOException
	 */
	public final <K, V> void saveMap(Map<K, V> data, String fileName,
		Converter<Pair<K, V>, String> converter) throws IOException {
		Tools.saveMap(data, pluginName + File.separator + fileName, converter);
	}

	/**
	 * For override
	 */
	protected void onEnable() {
	}

	/**
	 * For override
	 */
	protected void onDisable() {
	}

	public final void enable() {
		try {
			props = loadMap(INI_FILE, new Converter<String, Pair<String, String>>() {
				public Pair<String, String> convertTo(String value) {
					String[] split = value.split("=", 2);
					return new Pair<String, String>(split[0].trim(), split[1].trim());
				}
			});
		} catch (IOException e) {
		}
		onEnable();
		try {
			saveMap(props, INI_FILE, new Converter<Pair<String, String>, String>() {
				public String convertTo(Pair<String, String> value) {
					return value.first + " = " + value.second;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Command c : commands) {
			c.enable();
		}
		for (Map.Entry<PluginLoader.Hook, HookInfo> entry : listeners.entrySet()) {
			entry.getValue().enable(entry.getKey(), this);
		}
		Log.info("%s enabled", pluginName);
	}

	public final void disable() {
		for (Map.Entry<PluginLoader.Hook, HookInfo> entry : listeners.entrySet()) {
			entry.getValue().disable();
		}
		for (Command c : commands) {
			c.disable();
		}
		onDisable();
		Log.info("%s disabled", pluginName);
	}

	private class HookInfo {
		private PluginListener listener;
		private PluginListener.Priority priority;
		private PluginRegisteredListener registered;

		public HookInfo(PluginListener listener, PluginListener.Priority priority) {
			this.listener = listener;
			this.priority = priority;
		}

		public void enable(PluginLoader.Hook hook, Plugin plugin) {
			if (registered == null) {
				registered = etc.getLoader().addListener(hook, listener, plugin,
					priority);
			}
		}

		public void disable() {
			if (registered != null) {
				etc.getLoader().removeListener(registered);
				registered = null;
			}
		}
	}

	private final class Shifter extends PluginListener {
		private PluginListener listener;

		public Shifter(PluginListener listener) {
			this.listener = listener;
		}

		public void onArmSwing(Player player) {
			if (this.listener != null) {
				this.listener.onArmSwing(player);
			}
		}

		public void onBan(Player mod, Player player, String reason) {
			if (this.listener != null) {
				this.listener.onBan(mod, player, reason);
			}
		}

		public boolean onBlockBreak(Player player, Block block) {
			return this.listener != null ? this.listener.onBlockBreak(player, block)
				: false;
		}

		public boolean onBlockCreate(Player player, Block placed, Block clicked,
			int item) {
			return this.listener != null ? this.listener.onBlockCreate(player,
				placed, clicked, item) : false;
		}

		public boolean onBlockDestroy(Player player, Block block) {
			return this.listener != null ? this.listener
				.onBlockDestroy(player, block) : false;
		}

		public boolean onChat(Player player, String msg) {
			return this.listener != null ? this.listener.onChat(player, msg) : false;
		}

		public boolean onCommand(Player player, String[] args) {
			String command = args[0];
			List<String> args2 = new LinkedList<String>();
			for (int i = 1; i < args.length; ++i) {
				args2.add(args[i].trim());
			}
			for (Command c : commands) {
				if (c.match(command) && c.canUseCommand(player))
					return c.call(player, command, args2);
			}
			return this.listener != null ? this.listener.onCommand(player, args)
				: false;
		}

		public boolean onComplexBlockChange(Player player, ComplexBlock block) {
			return this.listener != null ? this.listener.onComplexBlockChange(player,
				block) : false;
		}

		public boolean onConsoleCommand(String[] args) {
			return this.listener != null ? this.listener.onConsoleCommand(args)
				: false;
		}

		public boolean onCraftInventoryChange(Player player) {
			return this.listener != null ? this.listener
				.onCraftInventoryChange(player) : false;
		}

		public void onDisconnect(Player player) {
			if (this.listener != null) {
				this.listener.onDisconnect(player);
			}
		}

		public boolean onEquipmentChange(Player player) {
			return this.listener != null ? this.listener.onEquipmentChange(player)
				: false;
		}

		public boolean onInventoryChange(Player player) {
			return this.listener != null ? this.listener.onInventoryChange(player)
				: false;
		}

		public void onIpBan(Player mod, Player player, String reason) {
			if (this.listener != null) {
				this.listener.onIpBan(mod, player, reason);
			}
		}

		public boolean onItemDrop(Player player, Item item) {
			return this.listener != null ? this.listener.onItemDrop(player, item)
				: false;
		}

		public void onKick(Player mod, Player player, String reason) {
			if (this.listener != null) {
				this.listener.onKick(mod, player, reason);
			}
		}

		public void onLogin(Player player) {
			if (this.listener != null) {
				this.listener.onLogin(player);
			}
		}

		public String onLoginChecks(String user) {
			return this.listener != null ? this.listener.onLoginChecks(user) : null;
		}

		public void onPlayerMove(Player player, Location from, Location to) {
			if (this.listener != null) {
				this.listener.onPlayerMove(player, from, to);
			}
		}

		public boolean onSendComplexBlock(Player player, ComplexBlock block) {
			return this.listener != null ? this.listener.onSendComplexBlock(player,
				block) : false;
		}

		public boolean onTeleport(Player player, Location from, Location to) {
			return this.listener != null ? this.listener.onTeleport(player, from, to)
				: false;
		}
	}

}
