package fi.aalto.cs.drumbeat.common.cli;

import java.util.Comparator;

import org.apache.commons.cli.Option;

/**
 * Comparator, which compares {@link Option}s by indexes if they are {@link DrbOption}s.
 * 
 * @author Nam Vu
 *
 */
public class DrbOptionComparator implements Comparator<Option> {

	/**
	 * Compares two {@link Option}s by indexes if they are {@link DrbOption}s. If
	 * the indexes are equal or if one of the options is not a {@link DrbOption}
	 * then compares them by their names.
	 * 
	 * @param o1
	 *            the first option to compare
	 * @param o2
	 *            the first option to compare
	 */
	@Override
	public int compare(Option o1, Option o2) {
		if (o1 instanceof DrbOption && o2 instanceof DrbOption) {
			return Integer.compare(((DrbOption) o1).getIndex(), ((DrbOption) o2).getIndex());
		}
		return o1.getOpt().compareTo(o2.getOpt());
	}

}
