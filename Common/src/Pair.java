import org.apache.commons.lang.builder.*;

public class Pair<T, U> {

	public T first;
	public U second;

	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}

	public static <T, U> Pair<T, U> create(T first, U second) {
		return new Pair<T, U>(first, second);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> p = (Pair<?, ?>) obj;
		return new EqualsBuilder().append(this.first, p.first).append(this.second,
			p.second).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 29).append(first).append(second).hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(first).append(second).toString();
	}

}
