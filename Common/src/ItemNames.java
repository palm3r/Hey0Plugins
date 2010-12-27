import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.regex.*;

import org.apache.commons.lang.StringUtils;

public class ItemNames {

	private static final String fileName = "items.txt";
	private static Map<Integer, String> items =
		new LinkedHashMap<Integer, String>();

	static {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			for (int index = 1; (line = br.readLine()) != null; ++index) {
				if (!line.isEmpty()) {
					try {
						String[] s = StringUtils.split(line, ":");
						items.put(Integer.valueOf(s[1].trim()), s[0].trim().toLowerCase());
					} catch (Exception e) {
						// System.out.println(String.format("parse error: %s (%s:%d)", line,
						// fileName, index));
						// e.printStackTrace();
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, String> all() {
		return items;
	}

	public static String getName(int id) {
		return items.containsKey(id) ? items.get(id) : null;
	}

	public static Map<Integer, String> parse(String str) {
		Pattern pattern =
			Pattern.compile(str.replaceAll("\\*", ".*"), Pattern.CASE_INSENSITIVE);
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		for (Entry<Integer, String> entry : items.entrySet()) {
			if (pattern.matcher(entry.getKey().toString()).matches()
				|| pattern.matcher(entry.getValue()).matches()) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

}
