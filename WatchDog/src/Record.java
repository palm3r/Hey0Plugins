import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.builder.*;

public class Record {

	public Integer id;
	public Long time;
	public String player;
	public String event;
	public String target;
	public Integer x;
	public Integer y;
	public Integer z;
	public Boolean denied;
	public Boolean kicked;
	public Boolean banned;

	public Record() {
	}

	private Record(ResultSet rs) throws SQLException {
		id = rs.getInt(1);
		time = rs.getLong(2);
		player = rs.getString(3);
		event = rs.getString(4);
		target = rs.getString(5);
		x = rs.getInt(6);
		y = rs.getInt(7);
		z = rs.getInt(8);
		denied = rs.getBoolean(9);
		kicked = rs.getBoolean(10);
		banned = rs.getBoolean(11);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Record))
			return false;
		Record r = (Record) obj;
		return new EqualsBuilder().append(player, r.player).append(event, r.event).append(
			target, r.target).append(x, r.x).append(y, r.y).append(z, r.z).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(time).append(player).append(event).append(
			target).append(x).append(y).append(z).append(denied).append(kicked).append(
			banned).hashCode();
	}

	public static List<Record> query(String sql) throws SQLException {
		PreparedStatement stmt = WatchDog.prepareStatement(sql);

		// System.out.println("SQL: " + stmt.toString());

		ResultSet rs = stmt.executeQuery();
		List<Record> list = new LinkedList<Record>();
		while (rs.next()) {
			list.add(new Record(rs));
		}
		return list;
	}

	public void insert() throws SQLException {
		PreparedStatement stmt =
			WatchDog.prepareStatement(
				String.format(
					"INSERT INTO %s (time,player,event,target,x,y,z,denied,kicked,banned) VALUES (?,?,?,?,?,?,?,?,?,?);",
					WatchDog.TABLE), PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setLong(1, time);
		stmt.setString(2, player);
		stmt.setString(3, event);
		stmt.setString(4, target);
		stmt.setInt(5, x);
		stmt.setInt(6, y);
		stmt.setInt(7, z);
		stmt.setBoolean(8, denied);
		stmt.setBoolean(9, kicked);
		stmt.setBoolean(10, banned);

		// System.out.println("SQL: " + stmt.toString());

		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt(1);
		}
	}

}
