package fi.aalto.cs.drumbeat.common.params;

import java.util.Arrays;
import java.util.Collection;

import fi.aalto.cs.drumbeat.common.SimpleTypes;

public class BooleanParam extends TypedParam<Boolean> {
	
	public BooleanParam() {
		this(null, null);
	}

	public BooleanParam(String name) {
		this(name, null);
	}	

	public BooleanParam(String name, String description) {
		this(name, description, null);
	}

	public BooleanParam(String name, String description, Boolean value) {
		this(name, description, value, Arrays.asList(Boolean.TRUE, Boolean.FALSE), null, Boolean.FALSE);
	}

	public BooleanParam(String name, String description, Boolean value,
			Collection<Boolean> possibleValues,
			Collection<String> possibleValuesDescriptions, Boolean defaultValue) {
		super(name, description, value, possibleValues,
				possibleValuesDescriptions, defaultValue);
	}

	@Override
	public void setStringValue(String s) {
		setValue(SimpleTypes.toBoolean(s));
	}	

}
