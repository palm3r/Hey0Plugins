import java.io.*;
import java.util.*;

public class BlockPerMinutesHandler implements WebApiHandler {

	private final Pair<Integer, Integer> count = Pair.create(0, 0);
	private static Date lastInquiry = new Date();

	@Override
	public int call(PrintWriter pw, String[] args) throws IOException {
		Date now = new Date();
		double minutes = (now.getTime() - lastInquiry.getTime()) / 1000.0 / 60.0;
		double c = count.first / minutes;
		double d = count.second / minutes;
		pw.printf("%f:%f%n", c, d);
		count.first = count.second = 0;
		lastInquiry = now;
		return 200;
	}

	public void created(int type) {
		count.first++;
	}

	public void destroyed(int type) {
		count.second++;
	}

}
