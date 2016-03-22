package fi.aalto.cs.drumbeat.common.uri;

import fi.aalto.cs.drumbeat.common.string.StringUtils;

public class UriUtils {
	
	public static String getLocalName(String uri) {
		int index = uri.indexOf(StringUtils.SLASH_CHAR);		
		return index >= 0 ? uri.substring(index + 1) : uri;
	}
	
	
	public static String getPath(String uri) {
		int index = uri.indexOf(StringUtils.SLASH_CHAR);		
		return index >= 0 ? uri.substring(0, index) : uri;		
	}

}
