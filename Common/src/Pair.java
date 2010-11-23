public class Pair<T1, T2> {
	public T1 first;
	public T2 second;

	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	public static <U1, U2> Pair<U1, U2> create(U1 first, U2 second) {
		return new Pair<U1, U2>(first, second);
	}

	public String toString() {
		return first + "," + second;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> pair = (Pair<?, ?>) obj;
		return pair.first.equals(first) && pair.second.equals(second);
	}

	public int hashCode() {
		final int multiplier = 42463;
		int hash = Pair.class.hashCode();
		hash = multiplier * hash + (first == null ? 102199 : first.hashCode());
		hash = multiplier * hash + (second == null ? 100237 : second.hashCode());
		return hash;
	}

}
