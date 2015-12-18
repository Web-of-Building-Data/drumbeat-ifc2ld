package fi.aalto.cs.drumbeat.common.params;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.TreeMap;

public class TypedParams extends TreeMap<String, TypedParam<?>> {
	
	private static final long serialVersionUID = 1L;

	public void addParam(TypedParam<?> param) {
		super.put(param.getName(), param);
	}
	
	public void removeParam(TypedParam<?> param) {
		super.remove(param.getName(), param);
	}
	
	public TypedParam<?> getParam(String name) {
		TypedParam<?> param = super.get(name);
		if (param == null) {
			throw new IllegalArgumentException(String.format("Unknown typed param: '%s'", name));
		}
		return param;
	}
	
	public <T> TypedParam<T> getParamEx(String name) {
		@SuppressWarnings("unchecked")
		TypedParam<T> param = (TypedParam<T>)super.get(name);
		if (param == null) {
			throw new IllegalArgumentException(String.format("Unknown typed param: '%s'", name));
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	public <T> TypedParam<T> getParamEx(String name, Class<T> typedParamClass, boolean autoCreate) {
		TypedParam<T> param;
		try {
			param = (TypedParam<T>)getParam(name);
		} catch (IllegalArgumentException e) {			
			if (autoCreate) {
				try {
					Constructor<T> constructor = typedParamClass.getConstructor(String.class, String.class);
					param = (TypedParam<T>)constructor.newInstance(name, null);
					super.put(name, param);
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			} else {
				throw e;
			}			
		}
		return param;
	}

	public TypedParams clone() {
		TypedParams params = (TypedParams)super.clone();
		for (String name : params.keySet()) {
			TypedParam<?> param = params.getParam(name);
			params.put(name, param.clone());
		}
		return params;
	}
	
	public Object getParamValue(String name) {
		return getParam(name).getValue();
	}
	
	public <T> TypedParam<T> setParamValue(String name, T value) {
		TypedParam<T> param = this.<T>getParamEx(name);
		param.setValue(value);
		return param;
	}
	
	public Collection<TypedParam<?>> getAllParams() {
		return super.values();
	}
	

}
