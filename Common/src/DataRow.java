import java.lang.annotation.*;
import org.apache.commons.lang.builder.*;

public abstract class DataRow {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Column {
		String index() default "";

		String unique() default "";
	}

	private Long id = null;

	public DataRow() {
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DataRow))
			return false;
		DataRow other = (DataRow) obj;
		return new EqualsBuilder().append(this.getClass(), other.getClass()).append(id, other.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 33).append(id).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(id).toString();
	}

}
