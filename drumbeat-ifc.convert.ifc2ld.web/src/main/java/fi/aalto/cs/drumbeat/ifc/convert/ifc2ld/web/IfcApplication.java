package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.vaadin.server.VaadinService;

import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.cli.Ifc2RdfExporter;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.util.Ifc2RdfExportUtil;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchemaPool;
import fi.aalto.cs.drumbeat.rdf.RdfUtils;
import fi.aalto.cs.drumbeat.rdf.jena.provider.AbstractJenaProvider;
import fi.aalto.cs.drumbeat.rdf.jena.provider.MemoryJenaProvider;

public class IfcApplication {
	
	private static Logger logger = Logger.getRootLogger();
	
	public static final String BASE_DIR_PATH = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath().replaceAll("\\\\", "/");
	public static final String WEB_INF_DIR_PATH = BASE_DIR_PATH + "/WEB-INF";
	public static final String TEMP_DIR_PATH = WEB_INF_DIR_PATH + "/tmp";
	public static final String TEMP_OUTPUTS_DIR_PATH = TEMP_DIR_PATH + "/outputs";
	public static final String TEMP_UPLOADS_DIR_PATH = TEMP_DIR_PATH + "/uploads";
	
	private static final String LOGGER_CONFIG_FILE_PATH = WEB_INF_DIR_PATH + "/config/log4j.xml";
	private static final String IFC2LD_CONFIG_FILE_PATH = WEB_INF_DIR_PATH + "/config/ifc2rdf-config.xml";
	private static final String IFC_SCHEMA_INPUT_DIR_PATH = WEB_INF_DIR_PATH + "/ifc/";
	private static final String IFC_SCHEMA_OUTPUT_DIR_PATH = WEB_INF_DIR_PATH + "/output/";	

//	private static final String VIRTUOSO_FOLDER_PROPERTY_NAME = "virtuoso.folder";
//	private static final String NEO4J_FOLDER_PROPERTY_NAME = "neo4j.folder";

	private static boolean systemInitialized;	
	
	private static List<IfcSchema> ifcSchemas;

	public IfcApplication() {
	}
	
	public static void init() throws Exception {
		
		synchronized (IfcApplication.class) {
			
			if (systemInitialized) {
				return;
			}
			
			systemInitialized = true;
			
			
			Ifc2RdfExporter.init(LOGGER_CONFIG_FILE_PATH, IFC2LD_CONFIG_FILE_PATH);
			
			ifcSchemas  = Ifc2RdfExporter.parseSchemas(IFC_SCHEMA_INPUT_DIR_PATH);
			logger.info("Config file: " + IFC2LD_CONFIG_FILE_PATH);
		}
		
		
	}
	
	public static List<IfcSchema> getSchemas() {
		return ifcSchemas;
	}
	
	public static File exportSchema(String name, RDFFormat rdfFormat, boolean gzip) throws Exception {
		
		IfcSchema schema = IfcSchemaPool.getSchema(name);		
		AbstractJenaProvider outputSchemaJenaProvider = new MemoryJenaProvider();
		
		String outputSchemaFilePath = IFC_SCHEMA_OUTPUT_DIR_PATH + name;
		
		Ifc2RdfExporter.exportSchema(
				outputSchemaJenaProvider,
				schema,
				"LDAC2015",
				outputSchemaFilePath,
				rdfFormat,
				gzip);
		
		outputSchemaFilePath = RdfUtils.formatRdfFileName(outputSchemaFilePath, rdfFormat, gzip);
		
		return new File(outputSchemaFilePath);
	}

	public static Model convertIfcModelToJenaModel(String fileName, Model jenaModel) throws Exception {
		
		String inputModelFilePath = TEMP_UPLOADS_DIR_PATH + "/" + fileName;
		IfcModel ifcModel = Ifc2RdfExporter.parseModel(inputModelFilePath);
		
		if (jenaModel == null) {
			AbstractJenaProvider outputModelJenaProvider = new MemoryJenaProvider();		
			jenaModel = outputModelJenaProvider.openDefaultModel();
		}		
		
		Ifc2RdfExportUtil.exportModelToJenaModel(jenaModel, ifcModel, "LDAC2015");
		return jenaModel;
	}
	
	public static File exportJenaModelToFile(Model jenaModel, String fileName, RDFFormat rdfFormat, boolean gzipOutputFile) throws IOException {
		
		String outputModelFilePath = TEMP_OUTPUTS_DIR_PATH + "/" + fileName;
		outputModelFilePath = RdfUtils.formatRdfFileName(outputModelFilePath, rdfFormat, gzipOutputFile);
		
		logger.info(String.format("Exporting Jena model to file '%s' (RDF format=%s, gzip=%b)", outputModelFilePath, rdfFormat, gzipOutputFile));
		
		RdfUtils.exportJenaModelToRdfFile(jenaModel, outputModelFilePath, rdfFormat, gzipOutputFile);
		
		return new File(outputModelFilePath);
	}
	
	
//	public static AbstractJenaProvider getJenaProvider(
//			ConfigurationPool<ConfigurationItemEx> jenaProviderConfigurationPool,
//			String jenaProviderName) throws Exception {
//		
//		try {
//			ConfigurationItemEx configuration = jenaProviderConfigurationPool.getByName(jenaProviderName);
//			return AbstractJenaProvider.getFactory(configuration);
//		} catch(InvalidParameterException e) {
//			throw new IfcException(String.format("Jena model %s is not found", jenaProviderName), e);
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
	

//	public static String getVirtuosoFolderPath() throws ConfigurationParserException, FileNotFoundException, IOException {
//		Properties properties = new Properties(); 
//		properties.load(new FileInputStream(WEB_INF_DIR_PATH + "/config/config.properties"));
//		
//		String folderPath = properties.getProperty(VIRTUOSO_FOLDER_PROPERTY_NAME);
//		if (folderPath == null) {
//			logger.warn(String.format(properties.toString()));
//			throw new IllegalArgumentException(VIRTUOSO_FOLDER_PROPERTY_NAME + " is not defined in the config file");
//		}
//		return folderPath;
//	}
//	
//	public static String getNeo4jFolderPath() throws ConfigurationParserException, FileNotFoundException, IOException {
//		Properties properties = new Properties(); 
//		properties.load(new FileInputStream(WEB_INF_DIR_PATH + "/config/config.properties"));
//		
//		String folderPath = properties.getProperty(NEO4J_FOLDER_PROPERTY_NAME);
//		if (folderPath == null) {
//			throw new IllegalArgumentException(NEO4J_FOLDER_PROPERTY_NAME + " is not defined in the config file");
//		}
//		return folderPath;
//	}
	
	
	

}
