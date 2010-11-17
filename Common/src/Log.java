import java.util.logging.*;

public class Log {

	private static Logger logger = Logger.getLogger("Minecraft");

	public static void info(String format, Object... params) {
		log(Level.INFO, format, params);
	}

	public static void warning(String format, Object... params) {
		log(Level.WARNING, format, params);
	}

	public static void severe(String format, Object... params) {
		log(Level.SEVERE, format, params);
	}

	public static void finest(String format, Object... params) {
		log(Level.FINEST, format, params);
	}

	public static void log(Level level, String format, Object... params) {
		logger.log(level, String.format(format, params));
	}
}
