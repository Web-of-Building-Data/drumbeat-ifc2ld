package fi.aalto.cs.drumbeat.common.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * A List which sorts the elements using the specified comparator.
 * 
 * @param <T> the type of list entries
 * 
 * @author Nam Vu
 */
public class SortedList<T> extends LinkedList<T> {
	
    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Comparator used to sort the list.
     */
    private final Comparator<? super T> comparator;
    
    /**
     * Constructs a new instance with the list elements sorted in their
     * {@link java.lang.Comparable} natural ordering.
     */
    public SortedList() {
    	comparator = null;
    }
    
    /**
     * Constructs a new instance using the given comparator.
     * 
     * @param comparator the comparator
     */
    public SortedList(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Adds a new entry to the list. The insertion point is calculated using the
     * comparator.
     * 
     * @param t the entry to add
     */
    @Override
    public boolean add(T t) {
        int insertionIndex = Collections.binarySearch(this, t, comparator);
        super.add((insertionIndex > -1) ? insertionIndex : (-insertionIndex) - 1, t);
        return true;
    }
    
    /**
     * Adds all elements in the specified collection to the list. Each element
     * will be inserted at the correct position to keep the list sorted.
     * 
     * @param ts the collection to add
     */
    @Override
    public boolean addAll(Collection<? extends T> ts) {
        boolean result = false;
        for (T t : ts) {
            result |= add(t);
        }
        return result;
    }
    
    /**
     * Indicates whether this list contains the given Element. This method uses the binary search and it is faster than the
     * {@link #contains(Object)} method.
     * 
     * @param t the element to search
     * @return <code>true</code>, if the element is contained in this list;
     * <code>false</code>, otherwise.
     */
    public boolean containsElement(T t) {
        return (Collections.binarySearch(this, t, comparator) > -1);
    }
}