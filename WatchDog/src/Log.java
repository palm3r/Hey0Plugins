import java.util.*;
import org.apache.commons.lang.builder.*;

public class Log {

	@Table.Column(primaryKey = true, autoIncrement = true)
	public Long id;

	@Table.Column(notNull = true)
	public Long time;

	@Table.Column(notNull = true)
	public Event action;

	@Table.Column(notNull = true)
	public String player;

	@Table.Column()
	public Integer targetId;

	@Table.Column()
	public String targetName;

	@Table.Column(notNull = true)
	public Double x, y, z;

	@Table.Column(notNull = true)
	public Boolean denied;

	@Table.Column(notNull = true)
	public Boolean kicked;

	@Table.Column(notNull = true)
	public Boolean banned;

	public Log() {
	}

	public String getColor() {
		String color = Colors.Yellow;
		if (denied) {
			color = Colors.Gold;
		}
		if (kicked) {
			color = Colors.Rose;
		}
		if (banned) {
			color = Colors.Red;
		}
		return color;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(player);
		sb.append(" ");
		sb.append(action.toString() + (targetName != null ? " " + targetName : ""));
		// sb.append(String.format(" (%d,%d,%d)", x, y, z));
		if (denied) {
			sb.append(" DENIED");
		}
		if (kicked) {
			sb.append(" KICKED");
		}
		if (banned) {
			sb.append(" BANNED");
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Log))
			return false;
		Log r = (Log) obj;
		return new EqualsBuilder().append(action, r.action).append(player, r.player).append(
			targetId, r.targetId).append(targetName, r.targetName).append(x, r.x).append(
			y, r.y).append(z, r.z).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(action).append(player).append(targetId).append(
			targetName).append(x).append(y).append(z).hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(id).append(new Date(time)).append(
			action).append(player).append(targetId).append(targetName).append(x).append(
			y).append(z).append(denied).append(kicked).append(banned).toString();
	}

}
