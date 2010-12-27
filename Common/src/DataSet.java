import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.apache.commons.lang.*;
import org.apache.commons.lang.builder.*;
import org.apache.log4j.*;

public class DataSet<T extends DataRow> {

	@SuppressWarnings("serial")
	private static Map<Class<?>, Pair<Integer, String>> types =
		new HashMap<Class<?>, Pair<Integer, String>>() {
			{
				put(java.lang.Integer.class, Pair.create(Types.INTEGER, "INT"));
				put(java.lang.Boolean.class, Pair.create(Types.BOOLEAN, "BOOLEAN"));
				put(java.lang.Byte.class, Pair.create(Types.TINYINT, "TINYINT"));
				put(java.lang.Short.class, Pair.create(Types.SMALLINT, "SMALLINT"));
				put(java.lang.Long.class, Pair.create(Types.BIGINT, "BIGINT"));
				put(java.math.BigDecimal.class, Pair.create(Types.DECIMAL, "DECIMAL"));
				put(java.lang.Double.class, Pair.create(Types.DOUBLE, "DOUBLE"));
				put(java.lang.Float.class, Pair.create(Types.REAL, "REAL"));
				put(java.sql.Time.class, Pair.create(Types.TIME, "TIME"));
				put(java.sql.Date.class, Pair.create(Types.DATE, "DATE"));
				put(java.sql.Timestamp.class, Pair.create(Types.TIMESTAMP, "TIMESTAMP"));
				put(java.lang.Byte[].class, Pair.create(Types.BINARY, "BINARY"));
				put(java.lang.String.class, Pair.create(Types.VARCHAR, "VARCHAR"));
				put(java.sql.Blob.class, Pair.create(Types.BLOB, "BLOB"));
				put(java.sql.Clob.class, Pair.create(Types.CLOB, "CLOB"));
				put(java.lang.Object[].class, Pair.create(Types.ARRAY, "ARRAY"));
				put(java.lang.Object.class, Pair.create(Types.OTHER, "OTHER"));
			}
		};

	private static Map<Class<? extends DataRow>, DataSet<? extends DataRow>> map =
		new HashMap<Class<? extends DataRow>, DataSet<? extends DataRow>>();

	public static <T extends DataRow> boolean connect(Connection connection, Class<T> clazz,
		Logger logger) {
		boolean connected = false;
		disconnect(clazz);
		try {
			DataSet<T> ds = new DataSet<T>(connection, clazz, logger);
			map.put(clazz, ds);
			connected = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connected;
	}

	public static <T extends DataRow> Connection disconnect(Class<T> clazz) {
		DataSet<? extends DataRow> old = map.containsKey(clazz) ? map.remove(clazz) : null;
		return old != null ? old.getConnection() : null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> int count(Class<T> clazz) {
		int count = 0;
		if (map.containsKey(clazz)) {
			count = ((DataSet<T>) map.get(clazz))._count(new String[] {});
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> int count(Class<T> clazz, String... conditions) {
		int count = 0;
		if (map.containsKey(clazz)) {
			count = ((DataSet<T>) map.get(clazz))._count(conditions);
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> List<T> getAll(Class<T> clazz) {
		List<T> list = null;
		if (map.containsKey(clazz)) {
			list = ((DataSet<T>) map.get(clazz))._getAll();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> List<T> get(Class<T> clazz, int count, int offset, String sort,
		String... conditions) {
		List<T> list = null;
		if (map.containsKey(clazz)) {
			list = ((DataSet<T>) map.get(clazz))._get(count, offset, sort, conditions);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> T get(Class<T> clazz, long id) {
		T obj = null;
		if (map.containsKey(clazz)) {
			obj = ((DataSet<T>) map.get(clazz))._get(id);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> T add(Class<T> clazz, Object... values) {
		T obj = null;
		if (map.containsKey(clazz)) {
			obj = ((DataSet<T>) map.get(clazz))._add(values);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> boolean update(T obj) {
		boolean updated = false;
		Class<T> clazz = (Class<T>) obj.getClass();
		if (map.containsKey(clazz)) {
			updated = ((DataSet<T>) map.get(clazz))._update(obj);
		}
		return updated;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataRow> boolean remove(T obj) {
		boolean updated = false;
		Class<T> clazz = (Class<T>) obj.getClass();
		if (map.containsKey(clazz)) {
			updated = ((DataSet<T>) map.get(clazz))._remove(obj);
		}
		return updated;
	}

	//
	//
	//

	private final Connection connection;
	private final Class<T> clazz;
	private final Logger logger;

	private final List<Field> fields;
	private final String name;
	private final PreparedStatement get, insert, update, delete;

	public DataSet(Connection connection, Class<T> clazz, Logger logger) throws SQLException,
		SecurityException, NoSuchFieldException {
		this.connection = connection;
		this.clazz = clazz;
		this.logger = logger;

		this.name = clazz.getName();
		this.fields = new LinkedList<Field>();

		final Map<String, String> columns = new LinkedHashMap<String, String>();
		Map<String, List<String>> indices = new HashMap<String, List<String>>();
		Map<String, List<String>> uniques = new HashMap<String, List<String>>();

		for (Field field : clazz.getDeclaredFields()) {
			DataRow.Column column = field.getAnnotation(DataRow.Column.class);
			if (column != null) {

				Pair<Integer, String> type =
					types.containsKey(field.getType()) ? types.get(field.getType()) : null;
				columns.put(field.getName(), type.second);

				String index = column.index();
				if (!index.isEmpty()) {
					List<String> list =
						indices.containsKey(index) ? indices.get(index) : new LinkedList<String>();
					list.add(field.getName());
					indices.put(index, list);
				}

				String unique = column.unique();
				if (!unique.isEmpty()) {
					List<String> list =
						uniques.containsKey(unique) ? uniques.get(unique) : new LinkedList<String>();
					list.add(field.getName());
					uniques.put(unique, list);
				}

				field.setAccessible(true);
				fields.add(field);
			}
		}

		// CREATE TABLE
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(name);
		sb.append(" (id BIGINT AUTO_INCREMENT PRIMARY KEY");
		for (Map.Entry<String, String> entry : columns.entrySet()) {
			sb.append(", ");
			sb.append(entry.getKey());
			sb.append(" ");
			sb.append(entry.getValue());
		}
		sb.append(");");
		String sql = sb.toString();
		logger.debug("SQL: " + sql);
		connection.createStatement().execute(sql);

		// INDEX
		for (Map.Entry<String, List<String>> entry : indices.entrySet()) {
			String index = entry.getKey();
			List<String> list = entry.getValue();
			sql =
				String.format("CREATE INDEX IF NOT EXISTS %s ON %s (%s);", index, name,
					StringUtils.join(list, ", "));
			logger.debug("SQL: " + sql);
			connection.createStatement().execute(sql);
		}

		// UNIQUE INDEX
		for (Map.Entry<String, List<String>> entry : uniques.entrySet()) {
			String unique = entry.getKey();
			List<String> list = entry.getValue();
			sql =
				String.format("CREATE UNIQUE INDEX IF NOT EXISTS %s ON %s (%s);", unique, name,
					StringUtils.join(list, ", "));
			logger.debug("SQL: " + sql);
			connection.createStatement().execute(sql);
		}

		// SELECT
		sql = String.format("SELECT * FROM %s WHERE id = ?;", name);
		logger.debug("SQL: " + sql);
		this.get = connection.prepareStatement(sql);

		// INSERT
		sql =
			String.format("INSERT INTO %s (%s) VALUES (%s);", name, MapTools.join(columns, ", ", "%s"),
				MapTools.join(columns, ", ", "?"));
		logger.debug("SQL: " + sql);
		this.insert = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		// UPDATE
		sql =
			String.format("UPDATE %s SET %s WHERE id = ?;", name, MapTools.join(columns, ", ", "%s = ?"));
		logger.debug("SQL: " + sql);
		this.update = connection.prepareStatement(sql);

		// DELETE
		sql = String.format("DELETE FROM %s WHERE id = ?;", name);
		logger.debug("SQL: " + sql);
		this.delete = connection.prepareStatement(sql);
	}

	public Connection getConnection() {
		return connection;
	}

	public Class<?> getComponentClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public boolean query(String sql) throws SQLException {
		logger.debug("SQL: " + sql);
		return connection.createStatement().execute(sql);
	}

	private boolean execute(PreparedStatement stmt) throws SQLException {
		logger.debug("SQL: " + stmt.toString());
		return stmt.execute();
	}

	public int _count(String... conditions) {
		int count = 0;
		try {
			String where = StringUtils.join(conditions, " AND ");
			String sql =
				String.format("SELECT COUNT(*) FROM %s%s", name, where.isEmpty() ? "" : " WHERE " + where);
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			if (execute(stmt)) {
				ResultSet rs = stmt.getResultSet();
				if (rs.next()) {
					count = rs.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public List<T> _getAll() {
		return _get(0, 0, null, new String[] {});
	}

	public List<T> _get(int count, int offset, String sort, String... conditions) {
		List<T> list = new LinkedList<T>();
		try {
			String where = StringUtils.join(conditions, " AND ");
			String sql =
				String.format("SELECT * FROM %s%s%s%s%s;", name, where.isEmpty() ? "" : " WHERE " + where,
					sort != null && !sort.isEmpty() ? " ORDER BY " + sort : "", count > 0 ? " LIMIT " + count
						: "", offset > 0 ? " OFFSET " + offset : "");
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			if (execute(stmt)) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					T obj = clazz.newInstance();
					obj.setId(rs.getLong("id"));
					for (Field field : clazz.getDeclaredFields()) {
						DataRow.Column column = field.getAnnotation(DataRow.Column.class);
						if (column != null) {
							field.setAccessible(true);
							field.set(obj, rs.getObject(field.getName()));
						}
					}
					list.add(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public T _get(long id) {
		T obj = null;
		try {
			get.clearParameters();
			get.setLong(1, id);
			if (execute(get)) {
				ResultSet rs = get.getResultSet();
				if (rs.next()) {
					obj = clazz.newInstance();
					obj.setId(rs.getLong("id"));
					for (Field field : clazz.getDeclaredFields()) {
						DataRow.Column column = field.getAnnotation(DataRow.Column.class);
						if (column != null) {
							field.setAccessible(true);
							field.set(obj, rs.getObject(field.getName()));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public T _add(Object... values) {
		try {
			T obj = clazz.newInstance();
			for (int index = 0; index < fields.size(); ++index) {
				Field field = fields.get(index);
				// logger.debug(String.format("_add: set field \"%s\" = %s",
				// field.getName(), values[index].toString()));
				field.set(obj, values[index]);
			}
			int index = 0;
			insert.clearParameters();
			for (Field field : fields) {
				DataRow.Column column = field.getAnnotation(DataRow.Column.class);
				if (column != null) {
					Class<?> type = field.getType();
					Pair<Integer, String> p = types.containsKey(type) ? types.get(type) : null;
					if (p != null) {
						Object value = field.get(obj);
						if (value != null) {
							insert.setObject(++index, value, p.first);
						} else {
							insert.setNull(++index, p.first);
						}
					}
				}
			}
			if (execute(insert)) {
				ResultSet rs = insert.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getLong("id"));
					return obj;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean _update(T obj) {
		boolean updated = false;
		try {
			int index = 0;
			update.clearParameters();
			for (Field field : fields) {
				Class<?> type = field.getType();
				Pair<Integer, String> p = types.containsKey(type) ? types.get(type) : null;
				if (p != null) {
					Object value = field.get(obj);
					if (value != null) {
						update.setObject(++index, value, p.first);
					} else {
						update.setNull(++index, p.first);
					}
				}
			}
			if (execute(update)) {
				updated = update.getUpdateCount() == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updated;
	}

	public boolean _remove(T obj) {
		boolean removed = false;
		try {
			delete.clearParameters();
			delete.setLong(1, obj.getId());
			if (execute(delete)) {
				removed = delete.getUpdateCount() == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return removed;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DataSet))
			return false;
		DataSet<?> other = (DataSet<?>) obj;
		return new EqualsBuilder().append(connection, other.connection).append(clazz, other.clazz).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(27, 13).append(connection).append(clazz).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(connection).append(clazz).append(fields).append(name).append(
			insert).append(update).append(delete).toString();
	}

}
