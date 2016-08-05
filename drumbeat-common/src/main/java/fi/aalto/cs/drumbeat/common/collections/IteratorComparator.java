package fi.aalto.cs.drumbeat.common.collections;

import java.util.Comparator;
import java.util.Iterator;

public class IteratorComparator<T> implements Comparator<Iterator<T>> {
	
	private final Comparator<T> comparator;
	
	public IteratorComparator(Comparator<T> comparator) {
		this.comparator = comparator;		
	}
	
	public boolean areEqual(Iterable<T> it1, Iterable<T> it2) {
		return compare(it1, it2) == 0;
	}
	
	public boolean areEqual(Iterator<T> it1, Iterator<T> it2) {
		return compare(it1, it2) == 0;
	}
	
	public int compare(Iterator<T> it1, Iterator<T> it2) {
		int result;
		T entry1 = null;
		T entry2 = null;
		while (it1.hasNext()) {
			if (it2.hasNext()) {
				entry1 = it1.next();
				entry2 = it2.next();
				if (entry1 != null && entry2 != null) {
					if ((result = comparator.compare(entry1, entry2)) != 0) {
						return result;
					}
				} else {
					return entry1 == null ? -1 : 1;
				}
			} else {
				return 1;
			}
		}
		
		if (it2.hasNext()) {
			return -1;
		}
		
		return 0;
	}

	public int compare(Iterable<T> it1, Iterable<T> it2) {
		return compare(it1.iterator(), it2.iterator());
	}
	
}
