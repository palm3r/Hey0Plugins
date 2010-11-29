/**
 * Generic pair class
 * 
 * @author palm3r
 * @param <T1>
 *          type of first member
 * @param <T2>
 *          type of second member
 */
public class Pair<T1, T2> {
	public T1 first;
	public T2 second;

	/**
	 * Public constructor
	 * 
	 * @param first
	 * @param second
	 */
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Static create method
	 * Use this method instead of constructor normally
	 * 
	 * @param <U1>
	 * @param <U2>
	 * @param first
	 * @param second
	 * @return
	 */
	public static <U1, U2> Pair<U1, U2> create(U1 first, U2 second) {
		return new Pair<U1, U2>(first, second);
	}

	/**
	 * Convert to string
	 */
	public String toString() {
		return first + "," + second;
	}

	/**
	 * Return whether this object equals with specified object
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> pair = (Pair<?, ?>) obj;
		return pair.first.equals(first) && pair.second.equals(second);
	}

	/**
	 * Return hash value
	 */
	public int hashCode() {
		final int multiplier = 42463;
		int hash = Pair.class.hashCode();
		hash = multiplier * hash + (first == null ? 102199 : first.hashCode());
		hash = multiplier * hash + (second == null ? 100237 : second.hashCode());
		return hash;
	}

}
