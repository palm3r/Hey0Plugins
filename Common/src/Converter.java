/**
 * Generic converter class
 * 
 * @author palm3r
 * @param <T>
 *          source type
 * @param <U>
 *          destination type
 */
public abstract class Converter<T, U> {

	/**
	 * Convert source to destination
	 * 
	 * @param value
	 * @return
	 */
	public U convertTo(T value) {
		return null;
	}

	/**
	 * Convert destination to source
	 * 
	 * @param value
	 * @return
	 */
	public T convertFrom(U value) {
		return null;
	}

}
