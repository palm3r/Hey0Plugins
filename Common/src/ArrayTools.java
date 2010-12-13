import java.util.*;

public final class ArrayTools {

	@SuppressWarnings("unchecked")
	public static <T> T[] combine(T[] array1, T[] array2) {
		List<T> list = Arrays.asList(array1);
		list.addAll(Arrays.asList(array2));
		return (T[]) list.toArray();
	}

	public static <T> String join(T[] array, String separator) {
		return CollectionTools.join(Arrays.asList(array), separator);
	}

	public static <T> String join(T[] array, String separator,
		Converter<T, String> converter) {
		return CollectionTools.join(Arrays.asList(array), separator, converter);
	}

}
