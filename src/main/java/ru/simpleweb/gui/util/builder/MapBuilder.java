package ru.simpleweb.gui.util.builder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.google.common.collect.Maps;

public class MapBuilder<Key, Value> implements Cloneable {

	private static final String KEY_DELIMITER = ":";
	private static final String DEFAULT_ENTRIES_SEPARATOR = ";";
	private static final String DEFAULT_KEY_VALUE_SEPARATOR = "=";

	public MapBuilder(Key key, Value value) {
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public MapBuilder(String stringToParse, String entriesSeparator, String keyValueSeparator) {
		String[] entries = stringToParse.split(entriesSeparator);
		for (String e : entries) {
			String[] entry = e.split(keyValueSeparator);
			map.put((Key) entry[0], (Value) entry[1]);
		}
	}

	public MapBuilder(String stringToParse) {
		this(stringToParse, DEFAULT_ENTRIES_SEPARATOR, DEFAULT_KEY_VALUE_SEPARATOR);
	}

	private Map<Key, Value> map = new LinkedHashMap<Key, Value>();

	public Map<Key, Value> build() {
		return map;
	}

	public MapBuilder<Key, Value> put(Key key, Value value) {
		map.put(key, value);
		return this;
	}

	public MapBuilder(Map<Key, Value> map) {
		this.map = map;
	}

	public MapBuilder() {

	}

	public String[][] asMatrix() {
		int currentIndex = 0;
		String[][] result = new String[map.keySet().size()][2];
		for (Entry<Key, Value> e : map.entrySet()) {
			result[currentIndex++] = new String[] { (String) e.getKey(), (String) e.getValue() };
		}
		return result;
	}

	public MapBuilder<Key, Value> clone() {
		return new MapBuilder<Key, Value>(Maps.newHashMap(map));
	}

	public MapBuilder<Key, Value> putIfNotNull(Key key, Value value) {
		if (value != null) {
			map.put(key, value);
		}
		return this;
	}

	public MapBuilder<Key, Value> putIfNotExist(Key key, Value value) {
		if (!map.containsKey(key)) {
			map.put(key, value);
		}
		return this;
	}

	public MapBuilder<Key, Value> putIfNotExistAndNotNull(Key key, Value value) {
		if (!map.containsKey(key) && value != null) {
			map.put(key, value);
		}
		return this;
	}

	public MapBuilder<Key, Value> putIfNotEquals(Key key, Value value, Value notEquals) {
		if (value == null && notEquals == null) {
			return this;
		}
		if (value != null && value.equals(notEquals)) {
			return this;
		}

		map.put(key, value);

		return this;
	}

	public static MapBuilder<String, String> newStringMapBuilder() {
		return new MapBuilder<String, String>();
	}

	public MapBuilder<Key, Value> putAll(MapBuilder<Key, Value> params) {
		return putAll(params.build());
	}

	public MapBuilder<Key, Value> putAllKeysWithSingleVal(Set<Key> keys, Value val) {
		for (Key key : keys) {
			put(key, val);
		}
		return this;
	}

	public MapBuilder<Key, Value> putAll(Map<Key, Value> values) {
		map.putAll(values);
		return this;
	}

	public static <V, K> V firstValue(Map<K, V> map) {
		for (Entry<K, V> e : map.entrySet()) {
			return e.getValue();
		}
		return null;
	}

	public static <K, V> Entry<K, V> entry(final K key, final V value) {
		return new Entry<K, V>() {

			public V getValue() {
				return value;
			}

			public K getKey() {
				return key;
			}

			public V setValue(V value) {
				return value;
			}

			public String toString() {
				return new StringBuilder().append(key).append("=").append(value).toString();
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null || this.getClass() != obj.getClass()) {
					return false;
				}

				Entry<K, V> right = (Entry<K, V>) obj;
				return new EqualsBuilder().append(key, right.getKey()).append(value, right.getValue()).isEquals();
			}
		};
	}

	public String toSemicolonDelimitedString() {
		StringBuilder result = new StringBuilder();
		String delimiter = "";
		for (Entry<Key, Value> e : build().entrySet()) {
			result.append(delimiter).append(e.getKey()).append("=").append(e.getValue());
			delimiter = ";";
		}
		return result.toString();
	}

	public static String compositeKey(Object... values) {
		String delimeter = StringUtils.EMPTY;
		StringBuilder val = new StringBuilder();
		for (Object object : values) {
			val.append(delimeter).append(String.valueOf(object));
			delimeter = KEY_DELIMITER;
		}
		return val.toString();
	}

	public Properties toProperties() {
		Properties props = new Properties();
		props.putAll(build());
		return props;
	}

	public MapBuilder<Key, Value> putAll(Collection<Entry<Key, Value>> entries) {
		for (Entry<Key, Value> entry : entries) {
			put(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public MapBuilder<Key, Value> addAll(Map<Key, Value> map) {
		this.map.putAll(map);
		return this;
	}

	public static <Key, Value> Map<Key, Value> toMap(Properties properties) {
		Map<Key, Value> map = new LinkedHashMap<Key, Value>();
		for (Entry<Object, Object> e : properties.entrySet()) {
			map.put((Key)e.getKey(), (Value)e.getValue());
		}
		return map;
	}

}
