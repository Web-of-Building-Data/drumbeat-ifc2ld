package fi.aalto.cs.drumbeat.common.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Utils for collections. 
 * @author vuhoan1
 *
 */
public class CollectionUtils {	
	
	/**
	 * Creates a map which is sorted by entry values.
	 *   
	 * @param map the original map
	 * 
	 * @param <K> the key type
	 * @param <V> the value type
	 * 
	 * @return a map which is by entry values.
	 */
	public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> getEntriesSortedByValues(
			Map<K,V> map)
	{
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}

}
