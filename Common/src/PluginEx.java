import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * Useful class for implement hMod plugins
 * Example:
 * 
 * <pre>
 * public class HelloWorldPlugin extends PluginEx {
 * 	private Command helloWorld;
 * 
 * 	public HelloWorldPlugin() {
 * 		super(&quot;HelloWorldPlugin&quot;);
 * 		helloWorld = new HelloWorldCommand(this);
 * 	}
 * 
 * 	protected void onEnable() {
 * 		addCommand(helloWorld);
 * 	}
 * 
 * 	protected void onDisable() {
 * 		removeCommand(helloWorld);
 * 	}
 * }
 * </pre>
 * 
 * @author palm3r
 */
public abstract class PluginEx extends Plugin {

	private static final String PLUGIN_INI = "plugin.ini";
	private static final String LOG_PATTERN =
		"%d{yyyy-MM-dd HH:mm:ss} [%p] %c: %m%n";

	private static final String LOG_ENABLE_KEY = "log-enable";
	private static final String LOG_ENABLE_DEFAULT = "false";

	private static final String LOG_LEVEL_KEY = "log-level";
	private static final String LOG_LEVEL_DEFAULT = "info";

	private final Logger logger;
	private final Map<PluginLoader.Hook, HookListener> hooks =
		new HashMap<PluginLoader.Hook, HookListener>();
	private final Set<Command> commands = new HashSet<Command>();
	private Map<String, String> config = new HashMap<String, String>();

	protected PluginEx() {
		setName(this.getClass().getName());
		logger = Logger.getLogger(getName());
		logger.addAppender(new ConsoleAppender(new PatternLayout(LOG_PATTERN),
			"System.out"));
		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.MEDIUM, null);
	}

	//
	// HOOKS
	//

	/**
	 * Add hook
	 * 
	 * @param hook
	 * @param priority
	 * @param listener
	 */
	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority, PluginListener listener) {
		HookListener hl =
			new HookListener(new WrappedListener(this, listener), priority);
		hooks.put(hook, hl);
		hl.enable(hook, this);
	}

	/**
	 * Remove hook
	 * 
	 * @param hook
	 */
	@Deprecated
	public final void removeHook(PluginLoader.Hook hook) {
		if (hooks.containsKey(hook)) {
			HookListener hl = hooks.remove(hook);
			hl.disable();
		}
	}

	//
	// COMMANDS
	//

	/**
	 * Return command set
	 */
	public final Set<Command> getCommands() {
		return commands;
	}

	/**
	 * Add Commands
	 * 
	 * @param commands
	 */
	public final void addCommand(Command... commands) {
		for (Command c : commands) {
			this.commands.add(c);
			c.enable();
		}
	}

	/**
	 * Remove Commands
	 * 
	 * @param commands
	 */
	@Deprecated
	public final void removeCommand(Command... commands) {
		for (Command c : commands) {
			this.commands.remove(c);
			c.disable();
		}
	}

	//
	// CONFIGURE AND DATA FILES
	//

	/**
	 * Get config value
	 * Throw exception when key was not found
	 * 
	 * @param key
	 * @return
	 */
	public final String getProperty(String key) {
		return config.get(key.toLowerCase());
	}

	/**
	 * Get config value
	 * Return default value when key was not found
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public final String getProperty(String key, String value) {
		key = key.toLowerCase();
		String v = value;
		if (config.containsKey(key)) {
			v = config.get(key);
		} else {
			setProperty(key, v);
		}
		return v;
	}

	/**
	 * Set config value
	 * 
	 * @param key
	 * @param value
	 */
	public final void setProperty(String key, String value) {
		key = key.toLowerCase();
		config.put(key, value);
	}

	public final String getRelatedPath(String fileName) {
		return getName() + File.separator + fileName;
	}

	@Deprecated
	public final String getAbsolutePath(String fileName) {
		return new File(".").getAbsoluteFile().getParent() + File.separator
			+ getRelatedPath(fileName);
	}

	@Deprecated
	public final <T> Collection<T> load(Collection<T> collection,
		String fileName, Converter<String, T> converter) throws IOException {
		return CollectionTools.load(collection, getRelatedPath(fileName), converter);
	}

	@Deprecated
	public final <T> void save(Collection<T> collection, String fileName,
		Converter<T, String> converter) throws IOException {
		CollectionTools.save(collection, getRelatedPath(fileName), converter);
	}

	public final <K, V> Map<K, V> load(Map<K, V> map, String fileName,
		Converter<String, Pair<K, V>> converter) throws IOException {
		return MapTools.load(map, getRelatedPath(fileName), converter);
	}

	public final <K, V> void save(Map<K, V> map, String fileName,
		Converter<Pair<K, V>, String> converter) throws IOException {
		MapTools.save(map, getRelatedPath(fileName), converter);
	}

	//
	// LOGGING
	//

	public final void fatal(String format, Object... args) {
		log(Level.FATAL, format, args);
	}

	public final void error(String format, Object... args) {
		log(Level.ERROR, format, args);
	}

	public final void warn(String format, Object... args) {
		log(Level.WARN, format, args);
	}

	public final void info(String format, Object... args) {
		log(Level.INFO, format, args);
	}

	public final void debug(String format, Object... args) {
		log(Level.DEBUG, format, args);
	}

	public final void trace(String format, Object... args) {
		log(Level.TRACE, format, args);
	}

	public final void log(Level level, String format, Object... args) {
		if (logger.isEnabledFor(level)) {
			logger.log(level, String.format(format, args));
		}
	}

	//
	// FOR OVERRIDE
	//

	/**
	 * Called when plugin enabled
	 */
	protected void onEnable() {
	}

	/**
	 * Called when plugin disabled
	 */
	protected void onDisable() {
	}

	//
	// FOR PLUGIN API
	//

	@Override
	public final void enable() {
		// Load plugin configuration
		try {
			config =
				load(new HashMap<String, String>(), PLUGIN_INI,
					new Converter<String, Pair<String, String>>() {
						@Override
						public Pair<String, String> convert(String line) {
							String[] s = line.split("=", 2);
							String key = s[0].trim().toLowerCase();
							String value = s[1].trim();
							return Pair.create(key, value);
						}
					});
		} catch (IOException e) {
		}
		// configure plugin independent logger
		try {
			Level level =
				(Level) Level.class.getField(
					getProperty(LOG_LEVEL_KEY, LOG_LEVEL_DEFAULT).toUpperCase()).get(null);
			logger.setLevel(level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean enabled =
			Boolean.valueOf(getProperty(LOG_ENABLE_KEY, LOG_ENABLE_DEFAULT));
		if (enabled) {
			try {
				logger.addAppender(new DailyRollingFileAppender(new PatternLayout(
					LOG_PATTERN), "logs/" + getName() + ".log", ".yyyy-MM-dd"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Call plugin enable handler
		onEnable();
		// Over write plugin.ini with default values
		try {
			save(config, PLUGIN_INI, new Converter<Pair<String, String>, String>() {
				@Override
				public String convert(Pair<String, String> value) {
					return value.first + " = " + value.second;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Enable commands
		for (Command c : commands) {
			c.enable();
		}
		// Enable hooks
		for (Map.Entry<PluginLoader.Hook, HookListener> entry : hooks.entrySet()) {
			PluginLoader.Hook hook = entry.getKey();
			HookListener hl = entry.getValue();
			hl.enable(hook, this);
		}
		info("enabled");
	}

	@Override
	public final void disable() {
		// Disable hooks
		for (Map.Entry<PluginLoader.Hook, HookListener> entry : hooks.entrySet()) {
			entry.getValue().disable();
		}
		// Disable commands
		for (Command c : commands) {
			c.disable();
		}
		// Call plugin disable handler
		onDisable();
		info("disabled");
	}

}
