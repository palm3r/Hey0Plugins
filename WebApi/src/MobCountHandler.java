import java.io.*;
import java.util.*;

public class MobCountHandler implements WebApiHandler {

	@Override
	public int call(PrintWriter pw, String[] args) throws IOException {
		List<Mob> mobs = new ArrayList<Mob>(etc.getServer().getMobList());
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Mob m : mobs) {
			String name = m.getName();
			int count = 1;
			if (map.containsKey(name)) {
				count += map.get(name);
			}
			map.put(name, count);
		}
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			pw.printf("%s:%d%n", entry.getKey(), entry.getValue());
		}
		return 200;
	}

}
