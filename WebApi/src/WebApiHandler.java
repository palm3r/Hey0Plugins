import java.io.*;

public interface WebApiHandler {
	int call(PrintWriter pw, String[] args) throws IOException;
}
