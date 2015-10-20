package fi.hut.cs.drumbeat.common;

public class SimpleTypes {
	
	public static final String TRUE = "true"; 
	public static final String FALSE = "false"; 
	public static final String YES = "yes"; 
	public static final String NO = "no"; 
	
	public static Boolean toBoolean(String s) {
		if (s == null) {
			return null;
		}
		
		if (s.equalsIgnoreCase(TRUE) || s.equalsIgnoreCase(YES)) {
			return Boolean.TRUE;
		}
		
		if (s.equalsIgnoreCase(FALSE) || s.equalsIgnoreCase(NO)) {
			return Boolean.FALSE;
		}
		
		throw new IllegalArgumentException("Unknown boolean value: " + s);
	}

	public static Integer toInteger(String s) {
		if (s == null) {
			return null;
		}
		
		return Integer.parseInt(s);		
	}
	
	public static String toString(Boolean b) {
		if (b == null) {
			return null;
		}
		
		return b ? TRUE : FALSE;
	}

	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		
		if (o instanceof Boolean) {
			return toString((Boolean)o);
		}
		
		return o.toString();		
	}
}
