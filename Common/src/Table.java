import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.apache.commons.lang.*;

public class Table {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Column {
		String type() default "";

		boolean notNull() default false;

		boolean primaryKey() default false;

		boolean autoIncrement() default false;

		String index() default "";

		String unique() default "";
	}

	@SuppressWarnings("serial")
	private static final Map<Class<?>, Class<?>> primitiveMap =
		new HashMap<Class<?>, Class<?>>() {
			{
				put(boolean.class, java.lang.Boolean.class);
				put(byte.class, java.lang.Byte.class);
				put(char.class, java.lang.Character.class);
				put(short.class, java.lang.Short.class);
				put(int.class, java.lang.Integer.class);
				put(long.class, java.lang.Long.class);
				put(float.class, java.lang.Float.class);
				put(double.class, java.lang.Double.class);
			}
		};

	@SuppressWarnings("serial")
	private static final Map<String, String> typeMap =
		new HashMap<String, String>() {
			{
				put(java.lang.Integer.class.getName(), "INT");
				put(java.lang.Boolean.class.getName(), "BOOLEAN");
				put(java.lang.Byte.class.getName(), "TINYINT");
				put(java.lang.Short.class.getName(), "SMALLINT");
				put(java.lang.Long.class.getName(), "BIGINT");
				put(java.math.BigDecimal.class.getName(), "DECIMAL");
				put(java.lang.Double.class.getName(), "DOUBLE");
				put(java.lang.Float.class.getName(), "REAL");
				put(java.sql.Time.class.getName(), "TIME");
				put(java.sql.Date.class.getName(), "DATE");
				put(java.sql.Timestamp.class.getName(), "TIMESTAMP");
				put(java.lang.String.class.getName(), "VARCHAR");
				put(java.sql.Blob.class.getName(), "BLOB");
				put(java.sql.Clob.class.getName(), "CLOB");
				put(java.util.UUID.class.getName(), "UUID");
				put(java.lang.Byte[].class.getName(), "BINARY");
				put(java.lang.Object[].class.getName(), "ARRAY");
				put(java.lang.Object.class.getName(), "OTHER");
			}
		};

	private static Map<Class<?>, Table> tables = new HashMap<Class<?>, Table>();

	private static <T extends Object> Table getTable(Class<T> clazz)
		throws SQLException, ClassNotFoundException {
		return tables.containsKey(clazz) ? tables.get(clazz) : null;
	}

	public static <T extends Object> Connection connect(Connection connection,
		Class<T> clazz) throws SQLException, ClassNotFoundException {
		Table table = getTable(clazz);
		Connection old = table != null ? table.getConnection() : null;
		tables.put(clazz, new Table(connection, clazz.getName(), clazz));
		return old;
	}

	public static <T extends Object> Connection connect(Connection connection,
		String tableName, Class<T> clazz) throws SQLException,
		ClassNotFoundException {
		Table table = getTable(clazz);
		Connection old = table != null ? table.getConnection() : null;
		tables.put(clazz, new Table(connection, tableName, clazz));
		return old;
	}

	public static <T extends Object> Connection disconnect(Class<T> clazz) {
		Connection connection = null;
		Table table = tables.remove(clazz);
		if (table != null) {
			connection = table.getConnection();
		}
		return connection;
	}

	public static <T extends Object> int count(Class<T> clazz,
		String... conditions) throws SQLException, ClassNotFoundException {
		Table table = getTable(clazz);
		if (table != null) {
			String where = StringUtils.join(conditions, " AND ");
			String sql =
				String.format("SELECT COUNT(*) FROM %s%s;", table.getTableName(),
					where.isEmpty() ? "" : " WHERE " + where);
			Statement stmt = table.getConnection().createStatement();
			// System.out.println("SQL: " + sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				return rs.getInt(1);
		}
		return 0;
	}

	public static <T extends Object> List<T> select(Class<T> clazz,
		String... conditions) throws SQLException, InstantiationException,
		IllegalAccessException, ClassNotFoundException {
		return select(clazz, 0, 0, conditions);
	}

	public static <T extends Object> List<T> select(Class<T> clazz, int count,
		String... conditions) throws SQLException, InstantiationException,
		IllegalAccessException, ClassNotFoundException {
		return select(clazz, 0, count, conditions);
	}

	public static <T extends Object> List<T> select(Class<T> clazz, int index,
		int count, String... conditions) throws SQLException,
		InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<T> list = null;
		Table table = getTable(clazz);
		if (table != null) {
			String where = StringUtils.join(conditions, " AND ");
			String sql =
				String.format("SELECT * FROM %s%s ORDER BY time DESC%s%s;",
					table.getTableName(), where.isEmpty() ? "" : " WHERE " + where,
					count > 0 ? String.format(" LIMIT %d", count) : "", index > 0
						? String.format(" OFFSET %d", index) : "");
			Statement stmt = table.getConnection().createStatement();
			// System.out.println("SQL: " + sql);
			ResultSet rs = stmt.executeQuery(sql);

			list = new LinkedList<T>();
			while (rs.next()) {
				T obj = clazz.newInstance();
				for (Field field : clazz.getDeclaredFields()) {
					Column column = field.getAnnotation(Column.class);
					if (column != null) {
						field.setAccessible(true);
						field.set(obj, rs.getObject(field.getName()));
					}
				}
				list.add(obj);
			}
		}
		return list;
	}

	public static <T extends Object> T get(Class<T> clazz, Object key)
		throws SQLException, InstantiationException, IllegalAccessException,
		ClassNotFoundException {
		Table table = getTable(clazz);
		if (table != null) {
			Field pk = table.getPrimaryKeyField();
			List<T> list =
				select(clazz, String.format("%s = '%s'", pk.getName(), key.toString()));
			if (list != null && !list.isEmpty()) {
				list.get(0);
			}
		}
		return null;
	}

	public static <T extends Object> boolean insert(T obj)
		throws IllegalArgumentException, SQLException, IllegalAccessException,
		ClassNotFoundException {
		Table table = getTable(obj.getClass());
		if (table != null) {
			PreparedStatement stmt = table.getInsertStatement();
			int index = 0;
			for (Field field : obj.getClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					field.setAccessible(true);
					stmt.setObject(++index, field.get(obj));
				}
			}
			// System.out.println("SQL: " + stmt.toString());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				Field pk = table.getPrimaryKeyField();
				pk.set(obj, rs.getLong(1));
				return true;
			}
		}
		return false;
	}

	public static <T extends Object> int update(T obj)
		throws IllegalArgumentException, SecurityException, SQLException,
		IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
		List<String> fieldNames = new LinkedList<String>();
		for (Field field : obj.getClass().getDeclaredFields()) {
			fieldNames.add(field.getName());
		}
		return update(obj, fieldNames.toArray(new String[0]));
	}

	public static <T extends Object> int update(T obj, String... fields)
		throws IllegalArgumentException, SQLException, IllegalAccessException,
		SecurityException, NoSuchFieldException, ClassNotFoundException {
		Table table = getTable(obj.getClass());
		if (table != null) {
			PreparedStatement stmt = table.getUpdateStatement();
			int index = 0;
			for (String fn : fields) {
				Field field = obj.getClass().getDeclaredField(fn);
				if (field != null) {
					Column column = field.getAnnotation(Column.class);
					if (column != null) {
						field.setAccessible(true);
						stmt.setObject(++index, field.get(obj));
					}
				}
			}
			Field pk = table.getPrimaryKeyField();
			stmt.setLong(++index, pk.getLong(obj));
			// System.out.println("SQL: " + stmt.toString());
			return stmt.executeUpdate();
		}
		return 0;
	}

	public static <T extends Object> int delete(T obj) throws SQLException,
		IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		Table table = getTable(obj.getClass());
		if (table != null) {
			PreparedStatement stmt = table.getDeleteStatement();
			Field pk = table.getPrimaryKeyField();
			stmt.setLong(1, pk.getLong(obj));
			// System.out.println("SQL: " + stmt.toString());
			int count = stmt.executeUpdate();
			for (Field field : obj.getClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					field.setAccessible(true);
					field.set(obj, null);
				}
			}
			return count;
		}
		return 0;
	}

	private Connection connection;
	private String tableName;
	private PreparedStatement insertStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private Field primaryKeyField;

	private Table(Connection connection, String tableName, Class<?> clazz)
		throws SQLException, ClassNotFoundException {
		this.connection = connection;
		this.tableName = tableName;

		Map<String, String> columns = new LinkedHashMap<String, String>();
		Map<String, Set<String>> indices = new LinkedHashMap<String, Set<String>>();
		Map<String, Set<String>> uniques = new LinkedHashMap<String, Set<String>>();

		for (Field field : clazz.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				String fieldName = field.getName();
				String fieldType =
					field.getType().isPrimitive()
						? primitiveMap.get(field.getType()).getName()
						: field.getType().getName();

				StringBuilder sb = new StringBuilder();
				if (!column.type().isEmpty()) {
					sb.append(column.type());
				} else {
					sb.append(typeMap.containsKey(fieldType) ? typeMap.get(fieldType)
						: "OTHER");
				}
				if (column.notNull()) {
					sb.append(" NOT NULL");
				}
				if (column.autoIncrement()) {
					sb.append(" AUTO_INCREMENT");
				}
				if (column.primaryKey()) {
					sb.append(" PRIMARY KEY");
					primaryKeyField = field;
				}
				columns.put(fieldName, sb.toString());

				String indexName = column.index();
				if (!indexName.isEmpty()) {
					Set<String> indexColumns =
						indices.containsKey(indexName) ? indices.get(indexName)
							: new LinkedHashSet<String>();
					indexColumns.add(fieldName);
					indices.put(field.getName(), indexColumns);
				}

				String uniqueName = column.unique();
				if (!uniqueName.isEmpty()) {
					Set<String> uniqueColumns =
						uniques.containsKey(uniqueName) ? uniques.get(uniqueName)
							: new LinkedHashSet<String>();
					uniqueColumns.add(fieldName);
					uniques.put(field.getName(), uniqueColumns);
				}
			}
		}

		String sql =
			String.format("CREATE TABLE IF NOT EXISTS %s (%s);", getTableName(),
				MapTools.join(columns, ", ", "%s %s"));
		// System.out.println("SQL: " + sql);

		Statement stmt = getConnection().createStatement();
		stmt.execute(sql);

		for (Map.Entry<String, Set<String>> entry : indices.entrySet()) {
			String indexName = entry.getKey();
			Set<String> columnNames = entry.getValue();
			sql =
				String.format("CREATE INDEX IF NOT EXISTS %s ON %s (%s);", indexName,
					getTableName(), StringUtils.join(columnNames, ", "));
			// System.out.println("SQL: " + sql);
			stmt.execute(sql);
		}

		for (Map.Entry<String, Set<String>> entry : uniques.entrySet()) {
			String uniqueName = entry.getKey();
			Set<String> columnNames = entry.getValue();
			sql =
				String.format("CREATE UNIQUE INDEX IF NOT EXISTS %s ON %s (%s);",
					uniqueName, getTableName(), StringUtils.join(columnNames, ", "));
			// System.out.println("SQL: " + sql);
			stmt.execute(sql);
		}

		insertStatement =
			getConnection().prepareStatement(
				String.format("INSERT INTO %s (%s) VALUES (%s);", getTableName(),
					MapTools.join(columns, ", ",
						new Converter<Map.Entry<String, String>, String>() {
							@Override
							public String convert(Map.Entry<String, String> entry) {
								return entry.getKey();
							}
						}), MapTools.join(columns, ", ",
						new Converter<Map.Entry<String, String>, String>() {
							@Override
							public String convert(Map.Entry<String, String> entry) {
								return "?";
							}
						})), Statement.RETURN_GENERATED_KEYS);

		updateStatement =
			getConnection().prepareStatement(
				String.format("UPDATE %s SET %s WHERE %s = ?;", getTableName(),
					MapTools.join(columns, ", ", "%s = ?"), primaryKeyField.getName()));

		deleteStatement =
			getConnection().prepareStatement(
				String.format("DELETE FROM %s WHERE %s = ?;", getTableName(),
					primaryKeyField.getName()));
	}

	private Connection getConnection() {
		return connection;
	}

	private String getTableName() {
		return tableName;
	}

	private PreparedStatement getInsertStatement() {
		return insertStatement;
	}

	private PreparedStatement getUpdateStatement() {
		return updateStatement;
	}

	private PreparedStatement getDeleteStatement() {
		return deleteStatement;
	}

	private Field getPrimaryKeyField() {
		return primaryKeyField;
	}

}