import java.sql.*;

public class Record {

	public int id;
	public long time;
	public String player;
	public String event;
	public String target;
	public int x;
	public int y;
	public int z;
	public boolean denied;
	public boolean kicked;
	public boolean banned;

	public Record(ResultSet rs) throws SQLException {
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

}
