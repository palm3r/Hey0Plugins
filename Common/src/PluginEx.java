import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class PluginEx extends Plugin {

	private static final String PLUGINS_DIR = "plugins";
	private static final String PROP_FILE = "plugin.ini";

	private PluginListener listener;
	private String pluginName;
	private PluginLoader.Hook[] hooks;
	private PluginListener.Priority priority;
	private List<Command> commands;
	private Map<String, String> props;

	protected void initPluginEx(String name, PluginListener listener,
			PluginListener.Priority priority, PluginLoader.Hook... hooks) {
		this.pluginName = name;
		this.listener = new Listener(listener);
		this.hooks = hooks;
		this.priority = priority;
		this.commands = new ArrayList<Command>();
		this.props = new HashMap<String, String>();
	}

	public final void addCommand(Command command) {
		commands.add(command);
	}

	public final List<Command> removeCommand(String alias) {
		List<Command> removed = new ArrayList<Command>();
		for (Command c : commands) {
			if (c.match(alias)) {
				removed.add(c);
				commands.remove(c);
			}
		}
		return removed;
	}

	public final String getProperty(String key) {
		return props.get(key);
	}

	public final String getProperty(String key, String defaultValue) {
		String value = defaultValue;
		if (props.containsKey(key)) {
			value = props.get(key);
		} else {
			setProperty(key, value);
		}
		return value;
	}

	public final void setProperty(String key, String value) {
		props.put(key, value);
		saveProperties();
	}

	public final <K, V> Map<K, V> load(String fileName,
			Converter<String, Pair<K, V>> converter) throws IOException {
		return Tools.load(PLUGINS_DIR + File.separator + pluginName
				+ File.separator + fileName, converter);
	}

	public final <K, V> void save(Map<K, V> data, String fileName,
			Converter<Pair<K, V>, String> converter) throws IOException {
		Tools.save(data, PLUGINS_DIR + File.separator + pluginName + File.separator
				+ fileName, converter);
	}

	protected void onInitialize() {
	}

	protected void onEnable() {
	}

	protected void onDisable() {
	}

	public final void initialize() {
		for (PluginLoader.Hook hook : hooks) {
			etc.getLoader().addListener(hook, listener, this, priority);
		}
		onInitialize();
	}

	public final void enable() {
		for (Command c : commands) {
			c.enable();
		}
		loadProperties();
		onEnable();
		Log.info("%s enabled", pluginName);
	}

	public final void disable() {
		for (Command c : commands) {
			c.disable();
		}
		onDisable();
		saveProperties();
		Log.info("%s disabled", pluginName);
	}

	public final void loadProperties() {
		try {
			props = load(PROP_FILE, new Converter<String, Pair<String, String>>() {
				public Pair<String, String> convert(String value) {
					String[] split = value.split("=", 2);
					return new Pair<String, String>(split[0].trim(), split[1].trim());
				}
			});
		} catch (Exception e) {
			saveProperties();
		}
	}

	public final void saveProperties() {
		try {
			save(props, PROP_FILE, new Converter<Pair<String, String>, String>() {
				public String convert(Pair<String, String> value) {
					return value.first + " = " + value.second;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final class Listener extends PluginListener {
		private PluginListener listener;

		public Listener(PluginListener listener) {
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
			for (Command c : commands) {
				if (c.match(args[0])) {
					return c.call(player, args);
				}
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
