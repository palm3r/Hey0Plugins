public class Pair<T, U> {
	public T first;
	public U second;

	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}

	public static <U1, U2> Pair<U1, U2> create(U1 first, U2 second) {
		return new Pair<U1, U2>(first, second);
	}

	public String toString() {
		return first + "," + second;
	}
}
