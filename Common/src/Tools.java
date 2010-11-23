import java.io.*;
import java.util.*;

public class Tools {

	public static String Capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] combine(T[] array1, T[] array2) {
		List<T> list = Arrays.asList(array1);
		list.addAll(Arrays.asList(array2));
		return (T[]) list.toArray();
	}

	public static String[] split(String value, String separator) {
		return split(value, separator, new Converter<String, String>() {
			public String convertTo(String value) {
				return value.isEmpty() ? null : value;
			}
		}).toArray(new String[0]);
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
			T obj = converter.convertTo(s);
			if (obj != null) {
				list.add(obj);
			}
		}
		return list;
	}

	public static <T> String join(Collection<T> container, String separator) {
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

	public static <T> String join(Collection<T> container, String separator,
		Converter<T, String> conv) {
		StringBuilder sb = new StringBuilder();
		for (T elem : container) {
			String str = conv.convertTo(elem);
			if (str != null) {
				if (sb.length() > 0)
					sb.append(separator);
				sb.append(str);
			}
		}
		return sb.toString();
	}

	public static <T> String join(T[] array, String separator) {
		return Tools.join(Arrays.asList(array), separator);
	}

	public static <T> String join(T[] array, String separator,
		Converter<T, String> conv) {
		return Tools.join(Arrays.asList(array), separator, conv);
	}

	public static <T> Set<T> loadSet(String fileName,
		Converter<String, T> converter) throws IOException {
		return loadSet(fileName, converter, new TreeSet<T>());
	}

	public static <T> Set<T> loadSet(String fileName,
		Converter<String, T> converter, Set<T> set) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.split("#", 1)[0].trim();
			if (line.isEmpty())
				continue;
			T result = converter.convertTo(line);
			if (result != null) {
				set.add(result);
			}
		}
		br.close();
		return set;
	}

	public static <T> void saveSet(Set<T> set, String fileName,
		Converter<T, String> converter) throws IOException {
		File file = new File(fileName);
		File dir = file.getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		for (T elem : set) {
			String line = converter.convertTo(elem);
			if (line != null) {
				pw.println(line);
			}
		}
		pw.close();
	}

	public static <K, V> Map<K, V> loadMap(String fileName,
		Converter<String, Pair<K, V>> converter) throws IOException {
		return loadMap(fileName, converter, new TreeMap<K, V>());
	}

	public static <K, V> Map<K, V> loadMap(String fileName,
		Converter<String, Pair<K, V>> converter, Map<K, V> map) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			Pair<K, V> result = converter.convertTo(line);
			if (result != null) {
				map.put(result.first, result.second);
			}
		}
		br.close();
		return map;
	}

	public static <K, V> void saveMap(Map<K, V> map, String fileName,
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
