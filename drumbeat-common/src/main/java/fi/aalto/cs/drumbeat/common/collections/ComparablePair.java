package fi.aalto.cs.drumbeat.common.collections;

import java.util.Map.Entry;

/**
 * A pair that consists of a key and a value, which are both comparable.
 * 
 * @author vuhoan1
 *
 * @param <K>
 *            the comparable key type
 * @param <V>
 *            the comparable value type
 */
public class ComparablePair<K extends Comparable<K>, V extends Comparable<V>> extends Pair<K, V>
		implements Comparable<Entry<K, V>> {

	/**
	 * Creates a Pair using the specified parameters.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public ComparablePair(K key, V value) {
		super(key, value);
	}

	/**
	 * Creates a Pair using a map entry.
	 * 
	 * @param entry
	 *            the map entry, which contains the key and the value
	 */
	public ComparablePair(Entry<K, V> entry) {
		super(entry);
	}

	/**
	 * Compares this Pair with another Pair. First it compares the keys and then
	 * the values.
	 * 
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 * 
	 * @throws NullPointerException if the specified object is null
	 * 
	 * @throws ClassCastException if the specified object's type prevents it from being compared to this object. 
	 * 
	 */
	@Override
	public int compareTo(Entry<K, V> o) {
		int result = getKey().compareTo(o.getKey());
		if (result != 0) {
			return result;
		}
		return getValue().compareTo(o.getValue());
	}

}
