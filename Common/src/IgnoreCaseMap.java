import java.util.*;
import org.apache.commons.collections.*;
import org.apache.commons.lang.builder.*;

public class IgnoreCaseMap<V> implements Map<String, V> {

	private class KeyValue<V2> implements Map.Entry<String, V2> {

		private final String key;
		private V2 value;

		public KeyValue(String key, V2 value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public V2 getValue() {
			return value;
		}

		@Override
		public V2 setValue(V2 value) {
			V2 old = this.value;
			this.value = value;
			return old;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (!(obj instanceof KeyValue))
				return false;
			@SuppressWarnings("unchecked") KeyValue<V> other = (KeyValue<V>) obj;
			return new EqualsBuilder().append(value, other.value).isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(27, 81).append(value).hashCode();
		}

		@Override
		public String toString() {
			return value.toString();
			// return new ToStringBuilder(this).append(key).append(value).toString();
		}
	}

	private final HashMap<String, KeyValue<V>> map;

	public IgnoreCaseMap() {
		map = new HashMap<String, IgnoreCaseMap<V>.KeyValue<V>>();
	}

	public IgnoreCaseMap(int initialCapacity, float loadFactor) {
		map =
			new HashMap<String, IgnoreCaseMap<V>.KeyValue<V>>(initialCapacity,
				loadFactor);
	}

	public IgnoreCaseMap(int initialCapacity) {
		map = new HashMap<String, IgnoreCaseMap<V>.KeyValue<V>>(initialCapacity);
	}

	public IgnoreCaseMap(Map<? extends String, ? extends V> m) {
		map = new HashMap<String, IgnoreCaseMap<V>.KeyValue<V>>();
		putAll(m);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key.toString().toLowerCase());
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(new KeyValue<Object>(null, value));
	}

	@Override
	public V get(Object key) {
		KeyValue<V> kv = map.get(key.toString().toLowerCase());
		return kv != null ? kv.getValue() : null;
	}

	@Override
	public V put(String key, V value) {
		KeyValue<V> old = map.put(key.toLowerCase(), new KeyValue<V>(key, value));
		return old != null ? old.getValue() : null;
	}

	@Override
	public V remove(Object key) {
		KeyValue<V> old = map.remove(key.toString().toLowerCase());
		return old != null ? old.getValue() : null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> m) {
		for (Map.Entry<?, ?> entry : m.entrySet()) {
			String key = entry.getKey().toString();
			@SuppressWarnings("unchecked") V value = (V) entry.getValue();
			map.put(key.toLowerCase(), new KeyValue<V>(key, value));
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> keySet() {
		return (Set<String>) CollectionUtils.collect(map.values(),
			new Transformer() {
				@Override
				public Object transform(Object obj) {
					KeyValue<V> kv = (KeyValue<V>) obj;
					return kv.getKey();
				}
			});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		return CollectionUtils.collect(map.values(), new Transformer() {
			@Override
			public Object transform(Object obj) {
				KeyValue<V> kv = (KeyValue<V>) obj;
				return kv.getValue();
			}
		});
	}

	@Override
	public Set<Map.Entry<String, V>> entrySet() {
		return new HashSet<Map.Entry<String, V>>(map.values());
	}

}
