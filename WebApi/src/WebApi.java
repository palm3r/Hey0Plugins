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

	@SuppressWarnings("serial")
	private Map<String, Pair<WebApiHandler, String>> handlers = new HashMap<String, Pair<WebApiHandler, String>>() {
		{
			put(PLAYERS_COUNT_KEY, new Pair<WebApiHandler, String>(
					new PlayerCountHandler(), "/count/players"));
			put(MOBS_COUNT_KEY, new Pair<WebApiHandler, String>(
					new MobCountHandler(), "/count/mobs"));
			put(BLOCKS_PER_MINUTES_KEY, new Pair<WebApiHandler, String>(bpmHandler,
					"/count/blocks"));
			put(CONSLE_COMMAND_KEY, new Pair<WebApiHandler, String>(
					new CommandHandler(), "/command"));
		}
	};

	private BlockPerMinutesHandler bpmHandler = new BlockPerMinutesHandler();
	private HttpServer httpd;

	private final PluginListener listener = new PluginListener() {
		public boolean onBlockBreak(Player player, Block block) {
			bpmHandler.destroyed(block.getType());
			return false;
		}

		public boolean onBlockCreate(Player player, Block placed, Block clicked,
				int item) {
			bpmHandler.created(placed.getType());
			return false;
		}
	};

	public WebApi() throws IOException {
		initPluginEx("WebApi", listener, PluginListener.Priority.LOW,
				PluginLoader.Hook.BLOCK_BROKEN, PluginLoader.Hook.BLOCK_CREATED);
	}

	protected void onEnable() {
		try {
			httpd = HttpServer.create();
			for (final Map.Entry<String, Pair<WebApiHandler, String>> entry : handlers
					.entrySet()) {
				String path = getProperty(entry.getKey(), entry.getValue().second);
				if (path == null || path.isEmpty())
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
							int status = entry.getValue().first.call(pw,
									(String[]) args.toArray(new String[0]));
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
