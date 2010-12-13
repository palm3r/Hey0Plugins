import java.util.*;

public final class StringTools {

	public static String Capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static String[] split(String value, String separator) {
		return split(value, separator, 0);
	}

	public static String[] split(String value, String separator, int limit) {
		return split(new ArrayList<String>(), value, separator, limit).toArray(
			new String[0]);
	}

	public static Collection<String> split(Collection<String> collection,
		String value, String separator) {
		return split(collection, value, separator, 0);
	}

	public static Collection<String> split(Collection<String> collection,
		String value, String separator, int limit) {
		return split(collection, value, separator, limit,
			new Converter<String, String>() {
				public String convertTo(String str) {
					return !str.isEmpty() ? str : null;
				}
			});
	}

	public static <T> Collection<T> split(Collection<T> collection, String value,
		String separator, Converter<String, T> converter) {
		return split(collection, value, separator, 0, converter);
	}

	public static <T> Collection<T> split(Collection<T> collection, String value,
		String separator, int limit, Converter<String, T> converter) {
		for (String s : value.split(separator, limit)) {
			T obj = converter.convertTo(s.trim());
			if (obj != null) {
				collection.add(obj);
			}
		}
		return collection;
	}

}
