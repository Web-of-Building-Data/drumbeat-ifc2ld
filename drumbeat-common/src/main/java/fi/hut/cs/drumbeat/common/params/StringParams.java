package fi.hut.cs.drumbeat.common.params;

import java.util.Collection;
import java.util.TreeMap;

public class StringParams extends TreeMap<String, StringParam> {

	private static final long serialVersionUID = 1L;

	public void addParam(StringParam param) {
		super.put(param.getName(), param);
	}
	
	public void removeParam(StringParam param) {
		super.remove(param.getName(), param);
	}
	
	public StringParam getParam(String name) {
		StringParam param = super.get(name);
		if (param == null) {
			throw new IllegalArgumentException(String.format("Unknown string param: '%s'", name));
		}
		return param;
	}
	
	public StringParam getParam(String name, boolean autoCreate) {
		StringParam param;
		try {
			param = getParam(name);
		} catch (IllegalArgumentException e) {			
			if (autoCreate) {
				param = new StringParam(name, null);
				super.put(name, param);				
			} else {
				throw e;
			}			
		}
		return param;
	}

	public Collection<StringParam> getAllParams() {
		return super.values();
	}
	
	public StringParams clone() {
		return (StringParams)super.clone();
	}
	
	public String getParamValue(String name) {
		return getParam(name).getValue();
	}
	
	public StringParam setParamValue(String name, String value) {
		StringParam param = getParam(name, true);
		param.setValue(value);
		return param;
	}
	
}
