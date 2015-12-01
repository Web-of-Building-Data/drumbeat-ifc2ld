package fi.hut.cs.drumbeat.jython;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

public class JythonFactory {
	
	private static Logger logger = Logger.getRootLogger();
	
	private static JythonFactory instance = null;

	public synchronized static JythonFactory getInstance() {
		if (instance == null) {
			instance = new JythonFactory();
		}

		return instance;

	}
	
	public void init (String libFolderPath) {
		logger.info("Initializing python interpreter. Lib folder: " + libFolderPath);
		Properties props = new Properties();
		props.put("python.home", libFolderPath);
		props.put("python.console.encoding", "UTF-8"); // Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
		props.put("python.security.respectJavaAccessibility", "false"); //don't respect java accessibility, so that we can access protected members on subclasses
		props.put("python.import.site","false");

		Properties preprops = System.getProperties();
				
		PythonInterpreter.initialize(preprops, props, new String[0]);
	}

	public Object getJythonObject(String interfaceName, String pathToJythonModule) {

		Object javaInt = null;
		
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.execfile(pathToJythonModule);
		String tempName = pathToJythonModule.substring(pathToJythonModule
				.lastIndexOf("/") + 1);
		tempName = tempName.substring(0, tempName.indexOf("."));
		System.out.println(tempName);
		String instanceName = tempName.toLowerCase();
		String javaClassName = tempName.substring(0, 1).toUpperCase()
				+ tempName.substring(1);
		String objectDef = "=" + javaClassName + "()";
		interpreter.exec(instanceName + objectDef);
		try {
			Class<?> JavaInterface = Class.forName(interfaceName);
			javaInt = interpreter.get(instanceName).__tojava__(JavaInterface);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace(); // Add logging here
		}
		interpreter.close();

		return javaInt;
	}
}