package fi.aalto.cs.drumbeat.common.params;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import fi.aalto.cs.drumbeat.common.SimpleTypes;

public abstract class TypedParam<T> {
	
	public static final String VALUE_NONE = "None";	
	public static final String VALUE_NONE_DESCRIPTION = "None";

	public static final String VALUE_NOT_SUPPORTED = "NotSupported";
	public static final String VALUE_NOT_SUPPORTED_DESCRIPTION = "Not supported";	
	
	public static final String VALUE_AUTO = "Auto";
	public static final String VALUE_AUTO_DESCRIPTION = "Auto";
	
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
	
//	public static final String VALUE_DEFAULT = "Default";
	public static final String VALUE_DEFAULT_DESCRIPTION = " (Default)";	

	private String name;
	private String description;
	private T value;
	private List<T> possibleValues;
	private List<String> possibleValuesDescriptions;
	private T defaultValue;
	private boolean isMandatory;
	
	public TypedParam() {
		this(null, null);
	}

	public TypedParam(String name) {
		this(name, null);
	}
	
	public TypedParam(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public TypedParam(String name, String description, T value) {
		this(name, description);
		setValue(value);
	}
	
	public TypedParam(String name, String description, T value, Collection<T> possibleValues, Collection<String> possibleValuesDescriptions, T defaultValue) {
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
	
	public T getValue() {
		return value != null ? value : defaultValue;
	}
	
	public String getStringValue() {
		return SimpleTypes.toString(getValue());
	}
	
	public void setValue(T value) throws IllegalArgumentException {
		checkValue(value);
		this.value = value;
	}
	
	public abstract void setStringValue(String s);
	
	public List<T> getPossibleValues() {
		return possibleValues;
	}
	
	public void setPossibleValues(Collection<T> possibleValues, Collection<String> possibleValuesDescriptions) throws IllegalArgumentException {
		checkValue(defaultValue);
		
		if (possibleValues != null) {
			this.possibleValues = new ArrayList<>(possibleValues);
			if (possibleValuesDescriptions != null) {
				if (possibleValuesDescriptions.size() != possibleValues.size()) {
					throw new IllegalArgumentException(String.format("Invalid size of description list %s", possibleValuesDescriptions));
				}
				
				this.possibleValuesDescriptions = new ArrayList<>(possibleValuesDescriptions);
				for (int i = 0; i < possibleValues.size(); ++i) {
					if (this.possibleValuesDescriptions.get(i) == null) {
						this.possibleValuesDescriptions.set(i, this.possibleValues.get(i).toString());
					}
				}
			} else {
				possibleValuesDescriptions = possibleValues.stream().map(x -> x.toString()).collect(Collectors.toList());
			}
		} else {
			this.possibleValues = null;
			this.possibleValuesDescriptions = null;
		}
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(T defaultValue)  throws IllegalArgumentException {
		checkValue(defaultValue);
		this.defaultValue = defaultValue;
	}
	
	public void checkValue(T value) throws IllegalArgumentException {
		if (possibleValues != null && value != null) {
			for (T possibleValue : possibleValues) {
				if (possibleValue.equals(value)) {
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
	
	@SuppressWarnings("unchecked")
	public TypedParam<T> clone() {
		try {
			Constructor<T> constructor =  (Constructor<T>)getClass().getConstructor(
					String.class,
					String.class);
			TypedParam<T> param = (TypedParam<T>)constructor.newInstance(name, null);
			param.setPossibleValues(possibleValues, possibleValuesDescriptions);			
			param.setValue(value);
			param.setDefaultValue(defaultValue);
			return param;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
}
