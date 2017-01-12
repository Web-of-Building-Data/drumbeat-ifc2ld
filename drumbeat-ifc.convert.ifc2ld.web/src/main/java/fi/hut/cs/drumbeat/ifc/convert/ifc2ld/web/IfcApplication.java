package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Properties;

import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.vaadin.server.VaadinService;

import fi.hut.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.hut.cs.drumbeat.common.config.ConfigurationPool;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.cli.Ifc2RdfExporter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.util.Ifc2RdfExportUtil;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchemaPool;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;
import fi.hut.cs.drumbeat.rdf.modelfactory.MemoryJenaModelFactory;

public class IfcApplication {
	
	private static Logger logger = Logger.getRootLogger();
	
	public final static String BASE_PATH = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath().replaceAll("\\\\", "/");
	public final static String WEB_INF_PATH = BASE_PATH + "/WEB-INF";
	public final static String TEMP_PATH = WEB_INF_PATH + "/tmp";
	public final static String TEMP_OUTPUTS_PATH = TEMP_PATH + "/outputs";
	public final static String TEMP_UPLOADS_PATH = TEMP_PATH + "/uploads";

	private static final String VIRTUOSO_FOLDER_PROPERTY_NAME = "virtuoso.folder";
	private static final String NEO4J_FOLDER_PROPERTY_NAME = "neo4j.folder";

	private static boolean systemInitialized;
	private static Object mutex = new Object();
	
	private static String loggerConfigFilePath = WEB_INF_PATH + "/config/log4j.xml";
	private static String ifc2ldConfigFilePath = WEB_INF_PATH + "/config/ifc2rdf-config.xml";
	private static String inputSchemaDirPath = WEB_INF_PATH + "/ifc/";
	private static String outputSchemaDirPath = WEB_INF_PATH + "/output/";
	
	private static List<IfcSchema> ifcSchemas;

	public IfcApplication() {
	}
	
	public static void init() throws Exception {
		
		synchronized (mutex) {
			
			if (systemInitialized) {
				return;
			}
			
			systemInitialized = true;
			
			
			Ifc2RdfExporter.init(loggerConfigFilePath, ifc2ldConfigFilePath);
			
			ifcSchemas  = Ifc2RdfExporter.parseSchemas(inputSchemaDirPath);
			logger.info("Config file: " + ifc2ldConfigFilePath);
		}
		
		
	}
	
	public static List<IfcSchema> getSchemas() {
		return ifcSchemas;
	}
	
	public static File exportSchema(String name, RDFFormat rdfFormat, boolean gzip) throws Exception {
		
		IfcSchema schema = IfcSchemaPool.getSchema(name);		
		JenaModelFactoryBase outputSchemaJenaModelFactory = new MemoryJenaModelFactory();
		
		String outputSchemaFilePath = outputSchemaDirPath + name;
		
		Ifc2RdfExporter.exportSchema(
				outputSchemaJenaModelFactory,
				schema,
				"LDAC2015",
				outputSchemaFilePath,
				rdfFormat,
				gzip);
		
		outputSchemaFilePath = RdfUtils.formatRdfFileName(outputSchemaFilePath, rdfFormat, gzip);
		
		return new File(outputSchemaFilePath);
	}

	public static Model convertIfcModelToJenaModel(String fileName, Model jenaModel) throws Exception {
		
		String inputModelFilePath = TEMP_UPLOADS_PATH + "/" + fileName;
		IfcModel ifcModel = Ifc2RdfExporter.parseModel(inputModelFilePath);
		
		if (jenaModel == null) {
			JenaModelFactoryBase outputModelJenaModelFactory = new MemoryJenaModelFactory();		
			jenaModel = outputModelJenaModelFactory.createModel();
		}		
		
		Ifc2RdfExportUtil.exportModelToJenaModel(jenaModel, ifcModel, "LDAC2015");
		return jenaModel;
	}
	
	public static File exportJenaModelToFile(Model jenaModel, String fileName, RDFFormat rdfFormat, boolean gzipOutputFile) throws IOException {
		
		String outputModelFilePath = TEMP_OUTPUTS_PATH + "/" + fileName;
		outputModelFilePath = RdfUtils.formatRdfFileName(outputModelFilePath, rdfFormat, gzipOutputFile);
		
		logger.info(String.format("Exporting Jena model to file '%s' (RDF format=%s, gzip=%b)", outputModelFilePath, rdfFormat, gzipOutputFile));
		
		RdfUtils.exportJenaModelToRdfFile(jenaModel, outputModelFilePath, rdfFormat, gzipOutputFile);
		
		return new File(outputModelFilePath);
	}
	
	
//	public static JenaModelFactoryBase getJenaModelFactory(
//			ConfigurationPool<ConfigurationItemEx> jenaModelFactoryConfigurationPool,
//			String jenaModelFactoryName) throws Exception {
//		
//		try {
//			ConfigurationItemEx configuration = jenaModelFactoryConfigurationPool.getByName(jenaModelFactoryName);
//			return JenaModelFactoryBase.getFactory(configuration);
//		} catch(InvalidParameterException e) {
//			throw new IfcException(String.format("Jena model %s is not found", jenaModelFactoryName), e);
//		}
//		
//	}
	
	
//	public static void convertRdfFileToNeo4j(File rdfFilePath) {
//		
////		String pythonLibFolderPath = WEB_INF_PATH + "/python/Lib/";
//////		logger.info(String.format("Initializing python: lib folder: %s", pythonLibFolderPath));
////		
////		JythonFactory jythonFactory = JythonFactory.getInstance();
////		jythonFactory.init(pythonLibFolderPath);
////
////		String pythonFilePath = String.format("%s/python/%sImpl.py", WEB_INF_PATH, Rdf2Neo4j.class.getSimpleName());
////		logger.info(String.format("Creating Rdf2Neo4j instance from python file '%s'", pythonFilePath));
////		
////		
////		Rdf2Neo4j ifc2Neo4j = (Rdf2Neo4j)jythonFactory.getJythonObject(Rdf2Neo4j.class.getName(), pythonFilePath);
////		
////		String filePath = IfcApplication.TEMP_UPLOADS_PATH + "/" + rdfFilePath;
////		ifc2Neo4j.run(filePath);
//		
//		String domainName = "google.com";
//		
////		//in mac oxs
////		String command = "ping -c 3 " + domainName;
//		
//		//in windows
//		String command = "ping -n 3 " + domainName;
//		
//		command = "python --help";
//		
//		String output = executeCommand(command);
//
//		logger.info(output);
//
//	}
	
	
	
//	public static String executeCommand(String command) {
//
//		StringBuffer output = new StringBuffer();
//
//		Process p;
//		try {
//			p = Runtime.getRuntime().exec(command, new String[]{ "cmd.exe" });
//			p.waitFor();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//            String line;			
//			while ((line = reader.readLine())!= null) {
//				output.append(line + "\n");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return output.toString();
//
//	}
	

	public static String getVirtuosoFolderPath() throws ConfigurationParserException, FileNotFoundException, IOException {
		Properties properties = new Properties(); 
		properties.load(new FileInputStream(WEB_INF_PATH + "/config/config.properties"));
		
		String folderPath = properties.getProperty(VIRTUOSO_FOLDER_PROPERTY_NAME);
		if (folderPath == null) {
			logger.warn(String.format(properties.toString()));
			throw new IllegalArgumentException(VIRTUOSO_FOLDER_PROPERTY_NAME + " is not defined in the config file");
		}
		return folderPath;
	}
	
	public static String getNeo4jFolderPath() throws ConfigurationParserException, FileNotFoundException, IOException {
		Properties properties = new Properties(); 
		properties.load(new FileInputStream(WEB_INF_PATH + "/config/config.properties"));
		
		String folderPath = properties.getProperty(NEO4J_FOLDER_PROPERTY_NAME);
		if (folderPath == null) {
			throw new IllegalArgumentException(NEO4J_FOLDER_PROPERTY_NAME + " is not defined in the config file");
		}
		return folderPath;
	}
	
	
	

}
