package fi.aalto.cs.drumbeat.common.cli;

import org.apache.commons.cli.Option;

/**
 * Extends CLI {@link Option} with index, which can be used by {@link DrbOptionComparator} for sorting.
 * @author Nam Vu
 *
 */
public class DrbOption extends Option {
	
	private static final long serialVersionUID = 1L;
	
	private int index;

	/**
	 * Creates a {@link DrbOption} using the specified parameters.
	 * @param index option index
	 * @param opt short representation of the option
	 * @param description describes the function of the option
	 * @throws IllegalArgumentException if there are any non valid Option characters in opt.
	 */
	public DrbOption(int index, String opt, String description)
			throws IllegalArgumentException {
		super(opt, description);
		this.index = index;
	}

	/**
	 * Creates a {@link DrbOption} using the specified parameters.
	 * @param index option index
	 * @param opt short representation of the option
	 * @param hasArg specifies whether the Option takes an argument or not
	 * @param description describes the function of the option
	 * @throws IllegalArgumentException if there are any non valid Option characters in opt.
	 */
	public DrbOption(int index, String opt, boolean hasArg, String description)
			throws IllegalArgumentException {
		super(opt, hasArg, description);
		this.index = index;
	}

	/**
	 * Creates a {@link DrbOption} using the specified parameters.
	 * @param index option index
	 * @param opt short representation of the option
	 * @param longOpt specifies whether the Option takes an argument or not
	 * @param hasArg specifies whether the Option takes an argument or not
	 * @param description describes the function of the option
	 * @throws IllegalArgumentException if there are any non valid Option characters in opt.
	 */
	public DrbOption(int index, String opt, String longOpt, boolean hasArg,
			String description) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
		this.index = index;
	}
	
	/**
	 * Gets the index.
	 * @return the index.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the index.
	 * @param index the index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
