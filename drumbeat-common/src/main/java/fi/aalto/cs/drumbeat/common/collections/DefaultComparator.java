package fi.aalto.cs.drumbeat.common.collections;

import java.util.Comparator;


/**
 * A default comparator that compares two {@link Comparable} objects. 
 * 
 * @author Nam Vu
 *
 * @param <T> {@link Comparable} type
 * 
 */
public class DefaultComparator<T extends Comparable<T>> implements Comparator<T> {
	
	/**
	 * Compares two objects by calling method {@link Comparable}.compareTo().
	 */
	@Override
	public int compare(T o1, T o2) {
		return ((Comparable<T>)o1).compareTo(o2);
	}	
	
}
