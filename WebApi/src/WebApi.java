import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;

public class WebApi extends PluginEx {

	public static final String PORT_KEY = "port";
	public static final String PLAYERS_COUNT_KEY = "players-count";
	public static final String MOBS_COUNT_KEY = "mobs-count";
	public static final String BLOCKS_PER_MINUTES_KEY = "blocks-per-minutes";
	public static final String CONSLE_COMMAND_KEY = "console-command";

	private Map<String, Pair<WebApiHandler, String>> handlers;
	private HttpServer httpd;

	@SuppressWarnings("serial")
	public WebApi() throws IOException {
		initPluginEx("WebApi", null, PluginListener.Priority.LOW,
				PluginLoader.Hook.BLOCK_BROKEN, PluginLoader.Hook.BLOCK_CREATED);

		this.handlers = new HashMap<String, Pair<WebApiHandler, String>>() {
			{
				put(PLAYERS_COUNT_KEY, new Pair<WebApiHandler, String>(
						new PlayerCountHandler(), "/count/players"));
				put(MOBS_COUNT_KEY, new Pair<WebApiHandler, String>(
						new MobCountHandler(), "/count/mobs"));
				put(CONSLE_COMMAND_KEY, new Pair<WebApiHandler, String>(
						new CommandHandler(), "/command"));
			}
		};
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
							Log.info("WebApi: [%s] %s %s", exchange.getRemoteAddress()
									.toString(), exchange.getRequestMethod(), exchange
									.getRequestURI().toString());
							String path = exchange.getRequestURI().getPath()
									.substring(exchange.getHttpContext().getPath().length());
							List<String> args = Tools.split(path, "/",
									new Converter<String, String>() {
										public String convert(String value) {
											return !value.isEmpty() ? value : null;
										}
									});
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
			Log.info("web server started on port %d", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDisable() {
		httpd.stop(0);
	}
}
