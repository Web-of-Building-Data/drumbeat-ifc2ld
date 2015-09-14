package fi.hut.cs.drumbeat.common.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringParam  {
	
	public static final String VALUE_NONE = "None";	
	public static final String VALUE_NONE_DESCRIPTION = "None";

	public static final String VALUE_NOT_SUPPORTED = "NotSupported";
	public static final String VALUE_NOT_SUPPORTED_DESCRIPTION = "Not supported";	
	
	public static final String VALUE_AUTO = "Auto";
	public static final String VALUE_AUTO_DESCRIPTION = "Auto";
	
	public static final String VALUE_DEFAULT = "Default";
	public static final String VALUE_DEFAULT_DESCRIPTION = "Default";
	
	public static final String VALUE_UNCHANGE = "Unchange";
	public static final String VALUE_UNCHANGE_DESCRIPTION = "Unchange";
	
	public static final String VALUE_TRUE = "True";
	public static final String VALUE_TRUE_DESCRIPTION = "True";

	public static final String VALUE_FALSE = "False";
	public static final String VALUE_FALSE_DESCRIPTION = "False";
	
	public static final String VALUE_YES = "Yes";	
	public static final String VALUE_YES_DESCRIPTION = "Yes";	

	public static final String VALUE_NO = "No";	
	public static final String VALUE_NO_DESCRIPTION = "No";	
	
	private String name;
	private String description;
	private String value;
	private List<String> possibleValues;
	private List<String> possibleValuesDescriptions;
	private String defaultValue;
	private boolean isMandatory;
	
	public StringParam(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public StringParam(String name, String description, String value) {
		this(name, description);
		setValue(value);
	}
	
	public StringParam(String name, String description, String value, Collection<String> possibleValues, Collection<String> possibleValuesDescriptions, String defaultValue) {
		this(name, description);
		setPossibleValues(possibleValues, possibleValuesDescriptions);
		setDefaultValue(defaultValue);
		setValue(value);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getValue() {
		return value != null ? value : defaultValue;
	}
	
	public Boolean getBooleanValue() {
		String value = getValue();
		return value != null ? Boolean.getBoolean(value) : null;
	}
	
	public Integer getIntegerValue() {
		String value = getValue();
		return value != null ? Integer.getInteger(value) : null;
	}

	public void setValue(String value) throws IllegalArgumentException {
		checkValue(value, possibleValues);
		this.value = value;
	}
	
	public List<String> getPossibleValues() {
		return possibleValues;
	}
	
	public void setPossibleValues(Collection<String> possibleValues, Collection<String> possibleValuesDescriptions) throws IllegalArgumentException {		
		checkValue(defaultValue, possibleValues);
		this.possibleValues = new ArrayList<>(possibleValues);
		if (possibleValuesDescriptions != null) {
			if (possibleValuesDescriptions.size() != possibleValues.size()) {
				throw new IllegalArgumentException(String.format("Invalid size of description list %s", possibleValuesDescriptions));
			}
			
			this.possibleValuesDescriptions = new ArrayList<>(possibleValuesDescriptions);
			for (int i = 0; i < possibleValues.size(); ++i) {
				if (this.possibleValuesDescriptions.get(i) == null) {
					this.possibleValuesDescriptions.set(i, this.possibleValues.get(i));
				}
			}
		} else {
			possibleValuesDescriptions = new ArrayList<>(possibleValues);	
		}
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue)  throws IllegalArgumentException {
		checkValue(defaultValue, possibleValues);
		this.defaultValue = defaultValue;
	}
	
	private void checkValue(String value, Collection<String> possibleValues) throws IllegalArgumentException {
		if (possibleValues != null && value != null) {
			for (String possibleValue : possibleValues) {
				if (possibleValue.equalsIgnoreCase(value)) {
					return;
				}
			}
			throw new IllegalArgumentException(
					String.format("Option '%s': illegal value '%s' (allowed values are: %s)", name, value, possibleValues));
		}
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public StringParam clone() {
		return new StringParam(name, description, value, possibleValues, possibleValuesDescriptions, defaultValue);
	}
	
}
