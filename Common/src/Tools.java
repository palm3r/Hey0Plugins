import java.io.*;
import java.util.*;

public class Tools {

	public static String Capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static <T> List<T> split(String value, String separator,
			Converter<String, T> converter) {
		return split(value, separator, 0, converter);
	}

	public static <T> List<T> split(String value, String separator, int limit,
			Converter<String, T> converter) {
		return split(value, separator, limit, converter, new ArrayList<T>());
	}

	public static <T> List<T> split(String value, String separator, int limit,
			Converter<String, T> converter, List<T> list) {
		for (String s : value.split(separator, limit)) {
			T obj = converter.convert(s);
			if (obj != null) {
				list.add(obj);
			}
		}
		return list;
	}

	public static <T> String Join(Collection<T> container, String separator) {
		StringBuilder sb = new StringBuilder();
		for (Object elem : container) {
			if (elem != null) {
				if (sb.length() > 0)
					sb.append(separator);
				sb.append(elem.toString());
			}
		}
		return sb.toString();
	}

	public static <T> String Join(Collection<T> container, String separator,
			Converter<T, String> conv) {
		StringBuilder sb = new StringBuilder();
		for (T elem : container) {
			String str = conv.convert(elem);
			if (str != null) {
				if (sb.length() > 0)
					sb.append(separator);
				sb.append(str);
			}
		}
		return sb.toString();
	}

	public static <T> String Join(T[] array, String separator) {
		return Tools.Join(Arrays.asList(array), separator);
	}

	public static <T> String Join(T[] array, String separator,
			Converter<T, String> conv) {
		return Tools.Join(Arrays.asList(array), separator, conv);
	}

	public static <K, V> Map<K, V> load(String fileName,
			Converter<String, Pair<K, V>> converter) throws IOException {
		return load(fileName, converter, new TreeMap<K, V>());
	}

	public static <K, V> Map<K, V> load(String fileName,
			Converter<String, Pair<K, V>> converter, Map<K, V> map)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
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
			String line = converter.convert(new Pair<K, V>(entry.getKey(), entry
					.getValue()));
			if (line != null) {
				pw.println(line);
			}
		}
		pw.close();
	}

}
