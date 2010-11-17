import java.io.*;

public class PlayerCountHandler implements WebApiHandler {

	public int call(PrintWriter pw, String[] args) throws IOException {
		pw.printf("%d", etc.getServer().getPlayerList().size());
		return 200;
	}

}
