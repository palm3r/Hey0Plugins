import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;

public class WebApi extends PluginEx {

	public static final String PORT_KEY = "port";
	public static final String PLAYERS_COUNT_KEY = "players-count-path";
	public static final String MOBS_COUNT_KEY = "mobs-count-path";
	public static final String BLOCKS_PER_MINUTES_KEY = "blocks-per-minutes-path";
	public static final String CONSLE_COMMAND_KEY = "console-command-path";

	private Map<String, Pair<WebApiHandler, String>> handlers;
	private HttpServer httpd;

	@SuppressWarnings("serial")
	public WebApi() throws IOException {
		this.handlers = new TreeMap<String, Pair<WebApiHandler, String>>() {
			{
				put(PLAYERS_COUNT_KEY, new Pair<WebApiHandler, String>(
					new PlayerCountHandler(), "/count/players"));
				put(MOBS_COUNT_KEY, new Pair<WebApiHandler, String>(
					new MobCountHandler(), "/count/mobs"));
				put(CONSLE_COMMAND_KEY, new Pair<WebApiHandler, String>(
					new CommandHandler(), "/command"));
			}
		};

		// addHook(PluginLoader.Hook.BLOCK_BROKEN, PluginListener.Priority.MEDIUM);
		// addHook(PluginLoader.Hook.BLOCK_CREATED, PluginListener.Priority.MEDIUM);
	}

	protected void onEnable() {
		try {
			httpd = HttpServer.create();
			for (final Map.Entry<String, Pair<WebApiHandler, String>> entry : handlers
				.entrySet()) {
				String path = getProperty(entry.getKey(), entry.getValue().second);
				if (path == null || path.isEmpty() || entry.getValue().first == null)
					continue;
				httpd.createContext(path).setHandler(new HttpHandler() {
					public void handle(HttpExchange exchange) {
						try {
							info("[%s] %s %s", exchange.getRemoteAddress().toString(),
								exchange.getRequestMethod(), exchange.getRequestURI()
									.toString());
							String path = exchange.getRequestURI().getPath()
								.substring(exchange.getHttpContext().getPath().length());
							Collection<String> args = StringTools.split(
								new ArrayList<String>(), path, "/");
							PrintWriter pw = new PrintWriter(exchange.getResponseBody());
							String[] a = args.size() > 0 ? (String[]) args
								.toArray(new String[0]) : new String[] {};
							int status = entry.getValue().first.call(pw, a);
							exchange.sendResponseHeaders(status, 0);
							pw.flush();
							pw.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			int port = Integer.valueOf(getProperty(PORT_KEY, "25580"));
			httpd.bind(new InetSocketAddress(port), 0);
			httpd.start();
			info("Web server started on port %d", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDisable() {
		httpd.stop(0);
	}
}
