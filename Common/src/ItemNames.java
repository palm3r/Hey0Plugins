import java.util.*;

public class ItemNames {

	private static final String ITEMS_TXT = "items.txt";
	private static Map<Integer, String> items;

	static {
		try {
			items = MapTools.load(new HashMap<Integer, String>(), ITEMS_TXT,
				new Converter<String, Pair<Integer, String>>() {
					public Pair<Integer, String> convertTo(String line) {
						if (line.startsWith("#") || line.isEmpty())
							return null;
						String[] s = StringTools.split(line, ":", 2);
						return Pair.create(Integer.valueOf(s[1].trim()), s[0].trim()
							.toLowerCase());
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getId(String name) {
		String itemName = name.trim().toLowerCase();
		for (Map.Entry<Integer, String> entry : items.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(itemName)) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public static String getName(int id) {
		return items.containsKey(id) ? items.get(id) : null;
	}

	public static Pair<Integer, String> parse(String str) {
		String idName = str.trim().toLowerCase();
		Integer id = null;
		String name = null;
		try {
			id = Integer.valueOf(idName);
		} catch (Exception e) {
			id = getId(idName);
		}
		name = getName(id);
		return id != null && name != null ? Pair.create(id, name) : null;
	}
}
