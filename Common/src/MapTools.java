import java.io.*;
import java.util.*;

public final class MapTools {

	public static <K, V> String join(Map<K, V> map, String entrySeparator,
		final String keyValueSeparator) {
		return MapTools.join(map, entrySeparator,
			new Converter<Map.Entry<K, V>, String>() {
				public String convertTo(Map.Entry<K, V> entry) {
					return entry.getKey() + keyValueSeparator + entry.getValue();
				}
			});
	}

	public static <K, V> String join(Map<K, V> map, String entrySeparator,
		Converter<Map.Entry<K, V>, String> converter) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			String str = converter.convertTo(entry);
			if (str != null) {
				if (sb.length() > 0)
					sb.append(entrySeparator);
				sb.append(str);
			}
		}
		return sb.toString();
	}

	public static <K, V> Map<K, V> load(Map<K, V> map, String fileName,
		Converter<String, Pair<K, V>> converter) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			Pair<K, V> result = converter.convertTo(line);
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
			String line = converter.convertTo(new Pair<K, V>(entry.getKey(), entry
				.getValue()));
			if (line != null) {
				pw.println(line);
			}
		}
		pw.close();
	}

}
