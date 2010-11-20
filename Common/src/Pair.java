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
}
