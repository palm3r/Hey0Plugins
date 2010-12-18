import java.io.*;
import java.util.*;

@Deprecated
public final class CollectionTools {

	public static <T> Collection<T> load(Collection<T> collection,
		String fileName, Converter<String, T> converter) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		while ((line = br.readLine()) != null) {
			T result = converter.convert(line);
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
			String line = converter.convert(elem);
			if (line != null) {
				pw.println(line);
			}
		}
		pw.close();
	}

}
