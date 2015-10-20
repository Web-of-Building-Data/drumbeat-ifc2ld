package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.File;
import java.util.List;

import org.apache.jena.riot.RDFFormat;

import com.vaadin.server.VaadinService;

import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.cli.Ifc2RdfExporter;
import fi.hut.cs.drumbeat.ifc.data.model.IfcModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchemaPool;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;
import fi.hut.cs.drumbeat.rdf.modelfactory.MemoryJenaModelFactory;

public class IfcApplication {
	
	public final static String BASE_PATH = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	public final static String WEB_INF_PATH = BASE_PATH + "/WEB-INF";
	public final static String TEMP_PATH = WEB_INF_PATH + "/tmp";
	public final static String TEMP_OUTPUTS_PATH = TEMP_PATH + "/outputs";
	public final static String TEMP_UPLOADS_PATH = TEMP_PATH + "/uploads";

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

	public static File exportModel(String fileName, RDFFormat rdfFormat, boolean gzip) throws Exception {
		
		String inputModelFilePath = TEMP_UPLOADS_PATH + "/" + fileName;
		IfcModel model = Ifc2RdfExporter.parseModel(inputModelFilePath);
		
		//IfcModel model = ;		
		JenaModelFactoryBase outputModelJenaModelFactory = new MemoryJenaModelFactory();
		
		String outputModelFilePath = TEMP_OUTPUTS_PATH + "/" + fileName;
		
		Ifc2RdfExporter.exportModel(
				outputModelJenaModelFactory,
				model,
				"LDAC2015",
				outputModelFilePath,
				rdfFormat,
				gzip);
		
		outputModelFilePath = RdfUtils.formatRdfFileName(outputModelFilePath, rdfFormat, gzip);

		return new File(outputModelFilePath);
	}	

}
