import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.*;

public class Record {

	public static PreparedStatement INSERT = null;

	public Integer id;
	public Long time;
	public String event;
	public String player;
	public String target;
	public Location location;
	public Boolean denied;
	public Boolean kicked;
	public Boolean banned;

	public Record() {
	}

	private Record(ResultSet rs) throws SQLException {
		int index = 0;
		id = rs.getInt(++index);
		time = rs.getLong(++index);
		event = rs.getString(++index);
		player = rs.getString(++index);
		target = rs.getString(++index);
		location =
			new Location(rs.getDouble(++index), rs.getDouble(++index),
				rs.getDouble(++index));
		denied = rs.getBoolean(++index);
		kicked = rs.getBoolean(++index);
		banned = rs.getBoolean(++index);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Record))
			return false;
		Record r = (Record) obj;
		return (new EqualsBuilder().append(event, r.event).append(player, r.player).append(
			target, r.target).isEquals())
			&& Math.round(location.x) == Math.round(r.location.x)
			&& Math.round(location.y) == Math.round(r.location.y)
			&& Math.round(location.z) == Math.round(r.location.z);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public static List<Record> query(String sql) throws SQLException {
		ResultSet rs = WatchDog.query(sql);
		List<Record> list = new LinkedList<Record>();
		while (rs.next()) {
			list.add(new Record(rs));
		}
		return list;
	}

	public void insert() throws SQLException {
		int index = 0;
		INSERT.setLong(++index, time);
		INSERT.setString(++index, event);
		INSERT.setString(++index, player);
		INSERT.setString(++index, target);
		INSERT.setDouble(++index, location.x);
		INSERT.setDouble(++index, location.y);
		INSERT.setDouble(++index, location.z);
		INSERT.setBoolean(++index, denied);
		INSERT.setBoolean(++index, kicked);
		INSERT.setBoolean(++index, banned);

		// System.out.println("SQL: " + INSERT.toString());

		INSERT.executeUpdate();
		ResultSet rs = INSERT.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt(1);
		}
	}

}
