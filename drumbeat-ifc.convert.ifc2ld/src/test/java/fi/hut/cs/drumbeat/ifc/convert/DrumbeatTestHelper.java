package fi.hut.cs.drumbeat.ifc.convert;

import org.apache.log4j.xml.DOMConfigurator;

import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;

public class DrumbeatTestHelper {
	
	public final static double DOUBLE_DELTA = 1e-15;
	
	public final static String CONFIG_FILE_PATH = "src/test/java/ifc2rdf-config.xml";
	public final static String LOGGER_CONFIG_FILE_PATH = "src/test/java/log4j.xml";
	
	private static boolean initialized = false;
	
	public static void init() throws Exception {
		if (!initialized) {
			initialized = true;
			DOMConfigurator.configure(LOGGER_CONFIG_FILE_PATH);			
			ConfigurationDocument.load(CONFIG_FILE_PATH);
		}
	}
	
}
