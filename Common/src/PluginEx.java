import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import org.apache.log4j.*;
import org.h2.jdbcx.JdbcDataSource;

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

	private Logger logger;
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

		this.logger = Logger.getLogger(getName());
		this.logger.addAppender(new ConsoleAppender(new PatternLayout(LOG_PATTERN),
			"System.out"));

		this.hooks = new HashMap<PluginLoader.Hook, HookInfo>();
		this.commands = new HashSet<Command>();
		this.config = new TreeMap<String, String>();

		addHook(PluginLoader.Hook.COMMAND, PluginListener.Priority.MEDIUM);
	}

	//
	// HOOKS
	//

	/**
	 * Add hook without listener
	 * 
	 * @param hook
	 * @param priority
	 */
	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority) {
		addHook(hook, priority, null);
	}

	/**
	 * Add hook with listener
	 * 
	 * @param hook
	 * @param priority
	 * @param listener
	 */
	public final void addHook(PluginLoader.Hook hook,
		PluginListener.Priority priority, PluginListener listener) {
		HookInfo info =
			new HookInfo(new InternalListener(this, listener), priority);
		hooks.put(hook, info);
		if (isEnabled()) {
			info.enable(hook, this);
		}
	}

	/**
	 * remove hook
	 * 
	 * @param hook
	 */
	public final void removeHook(PluginLoader.Hook hook) {
		if (hooks.containsKey(hook)) {
			HookInfo info = hooks.remove(hook);
			if (isEnabled()) {
				info.disable();
			}
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

	public final String getRelativePath(String fileName) {
		return getName() + File.separator + fileName;
	}

	public final String getAbsolutePath(String fileName) {
		return new File(".").getAbsoluteFile().getParent() + File.separator
			+ getRelativePath(fileName);
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
	public final <T> Collection<T> load(Collection<T> collection,
		String fileName, Converter<String, T> converter) throws IOException {
		return CollectionTools.load(collection, getRelativePath(fileName),
			converter);
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
	public final <T> void save(Collection<T> collection, String fileName,
		Converter<T, String> converter) throws IOException {
		CollectionTools.save(collection, getRelativePath(fileName), converter);
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
	public final <K, V> Map<K, V> load(Map<K, V> map, String fileName,
		Converter<String, Pair<K, V>> converter) throws IOException {
		return MapTools.load(map, getRelativePath(fileName), converter);
	}

	/**
	 * Save Map<K, V> to the file
	 * 
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @param fileName
	 * @param converter
	 * @throws IOException
	 */
	public final <K, V> void save(Map<K, V> map, String fileName,
		Converter<Pair<K, V>, String> converter) throws IOException {
		MapTools.save(map, getRelativePath(fileName), converter);
	}

	//
	// DB
	//

	public final Connection openDatabase(String fileName, String username,
		String password) throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		String url =
			String.format("jdbc:h2:file:%s",
				getAbsolutePath(fileName).replace('\\', '/'));
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(url);
		ds.setUser(username);
		ds.setPassword(password);
		return ds.getConnection();
	}

	//
	// LOGGING
	//

	public Logger getLogger() {
		return logger;
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
		// Load plugin configuration
		try {
			config =
				load(new HashMap<String, String>(), PLUGIN_INI,
					new Converter<String, Pair<String, String>>() {
						public Pair<String, String> convertTo(String line) {
							String[] s = line.split("=", 2);
							String key = s[0].trim().toLowerCase();
							String value = s[1].trim();
							return new Pair<String, String>(key, value);
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
		}
		// Enable hooks
		for (Map.Entry<PluginLoader.Hook, HookInfo> entry : hooks.entrySet()) {
			PluginLoader.Hook hook = entry.getKey();
			HookInfo info = entry.getValue();
			info.enable(hook, this);
		}
		info("enabled");
	}

	/**
	 * Disable plugin
	 */
	public final void disable() {
		// Disable hooks
		for (Map.Entry<PluginLoader.Hook, HookInfo> entry : hooks.entrySet()) {
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
