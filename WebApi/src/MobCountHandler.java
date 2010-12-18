import java.io.*;
import java.util.*;

public class MobCountHandler implements WebApiHandler {

	@Override
	public int call(PrintWriter pw, String[] args) throws IOException {
		Map<String, Integer> mobs = new HashMap<String, Integer>();
		for (Mob m : etc.getServer().getMobList()) {
			String name = m.getName();
			int count = 1;
			if (mobs.containsKey(name)) {
				count += mobs.get(name);
			}
			mobs.put(name, count);
		}
		for (Map.Entry<String, Integer> entry : mobs.entrySet()) {
			pw.printf("%s:%d%n", entry.getKey(), entry.getValue());
		}
		return 200;
	}

}
