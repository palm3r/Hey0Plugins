import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public final class MapTools {

	public static <K, V> String join(Map<K, V> map, String entrySeparator,
		final String format) {
		return MapTools.join(map, entrySeparator,
			new Converter<Map.Entry<K, V>, String>() {
				@Override
				public String convert(Entry<K, V> entry) {
					return String.format(format, entry.getKey(), entry.getValue());
				}
			});
	}

	public static <K, V> String join(Map<K, V> map, String entrySeparator,
		Converter<Map.Entry<K, V>, String> converter) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			String s = converter.convert(entry);
			if (s != null) {
				if (sb.length() > 0) {
					sb.append(entrySeparator);
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static <K, V> Map<K, V> load(Map<K, V> map, String fileName,
		Converter<String, Pair<K, V>> converter) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			Pair<K, V> result = converter.convert(line);
			if (result != null) {
				map.put(result.first, result.second);
			}
		}
		br.close();
		return map;
	}

	public static <K, V> void save(Map<K, V> map, String fileName,
		Converter<Pair<K, V>, String> converter) throws IOException {
		File file = new File(fileName);
		File dir = file.getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		for (Map.Entry<K, V> entry : map.entrySet()) {
			String line =
				converter.convert(Pair.create(entry.getKey(), entry.getValue()));
			if (line != null) {
				pw.println(line);
			}
		}
		pw.close();
	}

}
