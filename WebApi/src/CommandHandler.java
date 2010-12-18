import java.io.*;
import org.apache.commons.lang.*;

public class CommandHandler implements WebApiHandler {

	@Override
	public int call(PrintWriter pw, String[] args) throws IOException {
		if (args.length > 0) {
			String command = StringUtils.join(args, " ");
			etc.getServer().useConsoleCommand(command);
			pw.print("executed: " + command);
			return 200;
		}
		return 400;
	}

}
