package fi.aalto.cs.drumbeat.common.collections;

import java.util.Map.Entry;

/**
 * A pair that consists of a key and a value.
 * 
 * @author vuhoan1
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class Pair<K, V> implements Entry<K, V> {

	private K key;
	private V value;

	/**
	 * Creates a Pair using the specified parameters.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Creates a Pair using a map entry.
	 * 
	 * @param entry
	 *            the map entry, which contains the key and the value
	 */
	public Pair(Entry<K, V> entry) {
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	/**
	 * Gets the key.
	 */
	@Override
	public K getKey() {
		return key;
	}

	/**
	 * Gets the value.
	 */
	@Override
	public V getValue() {
		return value;
	}

	/**
	 * Sets the new value.
	 * 
	 * @param value
	 *            the new value
	 * 
	 * @return the old value.
	 */
	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	/**
	 * Returns the hash code value of the key. It is used for supporting the benefits of hash tables. 
	 */
	@Override
	public int hashCode() {
		return key.hashCode();
	}

	/**
	 * Indicates whether some other object is "equal to" this one.  
	 */
	@Override
	public boolean equals(Object obj) {
		Class<?> cl = getClass();
		if (cl.isInstance(obj)) {
			@SuppressWarnings("unchecked")
			Pair<K, V> o = (Pair<K, V>) obj;
			return key.equals(o.key) && value.equals(o.value);			
		}
		return false;
	}

}
