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

	private static final String LOG_LEVEL_KEY = "log-level";
	private static final String LOG_LEVEL_DEFAULT = "info";

	private static final String LOG_CONSOLE_KEY = "log-console";
	private static final String LOG_CONSOLE_DEFAULT = "true";

	private static final String LOG_FILE_KEY = "log-file";
	private static final String LOG_FILE_DEFAULT = "false";

	private static final String LOG_FILE_DIR_KEY = "log-file-dir";
	private static final String LOG_FILE_DIR_DEFAULT = "%s/logs";

	private static final String LOG_FILE_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%p] %c: %m%n";

	private Map<PluginLoader.Hook, HookInfo> hooks;
	private Set<Command> commands;
	private Map<String, String> config;

	/**
	 * Protected constructor
	 * Derived class must call this constructor
	 * 
	 * @param name
	 */
	protected PluginEx() {
		setName(this.getClass().getSimpleName());

		this.hooks = new HashMap<PluginLoader.Hook, HookInfo>();
		this.commands = new HashSet<Command>();
		this.config = new TreeMap<String, String>();

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.LOW);
	}

	//
	// HOOKS
	//

	/**
	 * Add hooks without listener
	 * 
	 * @param hook
	 * @param priority
	 */
	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority) {
		addHook(hook, priority, null);
	}

	/**
	 * Add hooks with listener
	 * 
	 * @param hook
	 * @param priority
	 * @param listener
	 */
	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority, PluginListener listener) {
		debug("addHook(%s, %s, %s)", hook, priority, listener != null ? listener
			: "(null)");
		HookInfo info = new HookInfo(new InternalListener(this, listener), priority);
		hooks.put(hook, info);
		if (isEnabled()) {
			info.enable(hook, this);
		}
	}

	//
	// COMMANDS
	//

	/**
	 * Add Commands
	 * 
	 * @param commands
	 */
	public final void addCommand(Command... commands) {
		for (Command c : commands) {
			debug("addCommand(%s)", c);
			this.commands.add(c);
			if (isEnabled()) {
				c.enable();
			}
		}
	}

	/**
	 * Remove Commands
	 * 
	 * @param commands
	 */
	public final void removeCommand(Command... commands) {
		for (Command c : commands) {
			debug("removeCommand(%s)", c);
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
		debug("setProperty(%s, %s)", key, value);
		config.put(key, value);
	}

	/**
	 * Load file as Set<T>
	 * 
	 * @param <T>
	 * @param fileName
	 * @param converter
	 * @return
	 * @throws IOException
	 */
	public final <T> Set<T> loadSet(String fileName,
		Converter<String, T> converter) throws IOException {
		debug("loadSet(%s, %s)", fileName, converter);
		return Tools.loadSet(getName() + File.separator + fileName, converter);
	}

	/**
	 * Load file as Set<T>
	 * 
	 * @param <T>
	 * @param fileName
	 * @param converter
	 * @param set
	 * @return
	 * @throws IOException
	 */
	public final <T> Set<T> loadSet(String fileName,
		Converter<String, T> converter, Set<T> set) throws IOException {
		debug("loadSet(%s, %s)", fileName, converter, set);
		return Tools.loadSet(getName() + File.separator + fileName, converter, set);
	}

	/**
	 * Save Set<T> elements to the file
	 * 
	 * @param <T>
	 * @param data
	 * @param fileName
	 * @param converter
	 * @throws IOException
	 */
	public final <T> void saveSet(Set<T> data, String fileName,
		Converter<T, String> converter) throws IOException {
		debug("saveSet(%s, %s, %s)", data, fileName, converter);
		Tools.saveSet(data, getName() + File.separator + fileName, converter);
	}

	/**
	 * Load file as Map<K, V>
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
		debug("loadMap(%s, %s)", fileName, converter);
		return Tools.loadMap(getName() + File.separator + fileName, converter);
	}

	/**
	 * Load file as Map<K, V>
	 * 
	 * @param <K>
	 * @param <V>
	 * @param fileName
	 * @param converter
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public final <K, V> Map<K, V> loadMap(String fileName,
		Converter<String, Pair<K, V>> converter, Map<K, V> map) throws IOException {
		debug("loadMap(%s, %s, %s)", fileName, converter, map);
		return Tools.loadMap(getName() + File.separator + fileName, converter, map);
	}

	/**
	 * Save Map<K, V> to the file
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
		debug("saveMap(%s, %s, %s)", data, fileName, converter);
		Tools.saveMap(data, getName() + File.separator + fileName, converter);
	}

	//
	// LOGGING
	//

	/**
	 * Return logger
	 */
	public final Logger getLogger() {
		return Logger.getLogger(getName());
	}

	/**
	 * Log fatal message
	 * 
	 * @param format
	 * @param args
	 */
	public final void fatal(String format, Object... args) {
		log(Level.FATAL, format, args);
	}

	/**
	 * Log error message
	 * 
	 * @param format
	 * @param args
	 */
	public final void error(String format, Object... args) {
		log(Level.ERROR, format, args);
	}

	/**
	 * Log warning message
	 * 
	 * @param format
	 * @param args
	 */
	public final void warn(String format, Object... args) {
		log(Level.WARN, format, args);
	}

	/**
	 * Log info message
	 * 
	 * @param format
	 * @param args
	 */
	public final void info(String format, Object... args) {
		log(Level.INFO, format, args);
	}

	/**
	 * Log debug message
	 * 
	 * @param format
	 * @param args
	 */
	public final void debug(String format, Object... args) {
		log(Level.DEBUG, format, args);
	}

	/**
	 * Log trace message
	 * 
	 * @param format
	 * @param args
	 */
	public final void trace(String format, Object... args) {
		log(Level.TRACE, format, args);
	}

	/**
	 * Log message with specified level
	 * 
	 * @param level
	 * @param format
	 * @param args
	 */
	public final void log(Level level, String format, Object... args) {
		Logger logger = getLogger();
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

	/**
	 * Enable plugin
	 */
	public final void enable() {
		debug("enable");
		// Load plugin configuration
		try {
			config = loadMap(PLUGIN_INI,
				new Converter<String, Pair<String, String>>() {
					public Pair<String, String> convertTo(String line) {
						String[] s = line.split("=", 2);
						String key = s[0].trim().toLowerCase();
						String value = s[1].trim();
						debug("plugin.ini: %s = %s", key, value);
						return new Pair<String, String>(key, value);
					}
				});
		} catch (IOException e) {
		}
		// Set minimum logging level
		Logger logger = getLogger();
		try {
			logger.setLevel((Level) Level.class.getField(
				getProperty(LOG_LEVEL_KEY, LOG_LEVEL_DEFAULT).toUpperCase()).get(null));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// Enable console logging
		boolean enableConsoleLogging = Boolean.valueOf(getProperty(LOG_CONSOLE_KEY,
			LOG_CONSOLE_DEFAULT));
		if (enableConsoleLogging) {
			logger.addAppender(new ConsoleAppender(
				new PatternLayout(LOG_FILE_PATTERN), "System.out"));
		}
		// Enable file logging
		boolean enableFileLogging = Boolean.valueOf(getProperty(LOG_FILE_KEY,
			LOG_FILE_DEFAULT));
		if (enableFileLogging) {
			try {
				String dirName = String.format(
					getProperty(LOG_FILE_DIR_KEY, LOG_FILE_DIR_DEFAULT), getName());
				File file = new File(dirName);
				if (!file.exists()) {
					file.mkdirs();
				}
				logger.addAppender(new DailyRollingFileAppender(new PatternLayout(
					LOG_FILE_PATTERN), dirName + File.separator + getName() + ".log",
					"yyyyMMdd"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Call plugin enable handler
		onEnable();
		// Over write plugin.ini with default values
		try {
			saveMap(config, PLUGIN_INI,
				new Converter<Pair<String, String>, String>() {
					public String convertTo(Pair<String, String> value) {
						return value.first + " = " + value.second;
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Enable commands
		for (Command c : commands) {
			c.enable();
			debug("%s command enabled", c.getCommand());
		}
		// Enable hooks
		for (Map.Entry<PluginLoader.Hook, HookInfo> entry : hooks.entrySet()) {
			PluginLoader.Hook hook = entry.getKey();
			HookInfo info = entry.getValue();
			info.enable(hook, this);
			debug("%s hook enabled", hook);
		}
		info("enabled");
	}

	/**
	 * Disable plugin
	 */
	public final void disable() {
		debug("disable");
		// Disable hooks
		for (Map.Entry<PluginLoader.Hook, HookInfo> entry : hooks.entrySet()) {
			PluginLoader.Hook hook = entry.getKey();
			HookInfo info = entry.getValue();
			info.disable();
			debug("%s hook disabled", hook);
		}
		// Disable commands
		for (Command c : commands) {
			c.disable();
			info("%s command disabled", c.getCommand());
		}
		// Call plugin disable handler
		onDisable();
		info("disabled");
	}

}
