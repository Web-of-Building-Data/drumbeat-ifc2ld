package fi.aalto.cs.drumbeat.common.params;

import java.util.Collection;

public class StringParam extends TypedParam<String> {

	public StringParam() {
		this(null, null);
	}

	public StringParam(String name) {
		this(name, null);
	}	

	public StringParam(String name, String description) {
		super(name, description);
	}

	public StringParam(String name, String description, String value) {
		super(name, description, value);
	}

	public StringParam(String name, String description, String value,
			Collection<String> possibleValues,
			Collection<String> possibleValuesDescriptions, String defaultValue) {
		super(name, description, value, possibleValues,
				possibleValuesDescriptions, defaultValue);
	}

	@Override
	public void setStringValue(String s) {
		setValue(s);
	}

}
