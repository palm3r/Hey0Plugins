import java.io.*;
import java.util.*;

public final class CollectionTools {

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
		Converter<T, String> converter) {
		StringBuilder sb = new StringBuilder();
		for (T elem : container) {
			String str = converter.convertTo(elem);
			if (str != null) {
				if (sb.length() > 0)
					sb.append(separator);
				sb.append(str);
			}
		}
		return sb.toString();
	}

	public static <T> Collection<T> load(Collection<T> collection,
		String fileName, Converter<String, T> converter) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			T result = converter.convertTo(line);
			if (result != null) {
				collection.add(result);
			}
		}
		br.close();
		return collection;
	}

	public static <T> void save(Collection<T> collection, String fileName,
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
		for (T elem : collection) {
			String line = converter.convertTo(elem);
			if (line != null) {
				pw.println(line);
			}
		}
		pw.close();
	}

}
