import java.io.*;

public class CommandHandler implements WebApiHandler {

	public int call(PrintWriter pw, String[] args) throws IOException {
		if (args.length > 0) {
			String command = ArrayTools.join(args, " ");
			etc.getServer().useConsoleCommand(command);
			pw.print("executed: " + command);
			return 200;
		}
		return 400;
	}

}
