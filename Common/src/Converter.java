/**
 * Generic converter class
 * 
 * @author palm3r
 * @param <T1>
 *          source type
 * @param <T2>
 *          destination type
 */
public abstract class Converter<T1, T2> {

	/**
	 * Convert source to destination
	 * 
	 * @param value
	 * @return
	 */
	public T2 convertTo(T1 value) {
		return null;
	}

	/**
	 * Convert destination to source
	 * 
	 * @param value
	 * @return
	 */
	public T1 convertFrom(T2 value) {
		return null;
	}

}
