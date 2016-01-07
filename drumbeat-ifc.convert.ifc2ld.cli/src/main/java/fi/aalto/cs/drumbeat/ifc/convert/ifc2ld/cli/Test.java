package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.cli;

import java.util.List;

import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.PropertyConfigurator;

import com.hp.hpl.jena.rdf.model.Model;

import fi.aalto.cs.drumbeat.common.config.ComplexProcessorConfiguration;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.util.Ifc2RdfExportUtil;
import fi.aalto.cs.drumbeat.ifc.convert.stff2ifc.util.IfcParserUtil;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.aalto.cs.drumbeat.ifc.processing.IfcModelAnalyser;
import fi.aalto.cs.drumbeat.rdf.RdfUtils;
import fi.aalto.cs.drumbeat.rdf.jena.provider.AbstractJenaProvider;
import fi.aalto.cs.drumbeat.rdf.jena.provider.MemoryJenaProvider;

public class Test {

	private String loggerConfigFilePath;
	private String converterConfigFilePath;
	private String inputSchemaFilePath;
	private String inputModelFilePath;
	private String outputSchemaFilePath;
	private String outputModelFilePath;
	RDFFormat outputFileFormat;
	boolean gzipOutputFile;

	public void run() throws Exception {
		
		//
		// load logger configuration
		//
		PropertyConfigurator.configure(loggerConfigFilePath);			
		
		//
		// load converter configuration
		//
		ConfigurationDocument.load(converterConfigFilePath);
		
		//
		// load IFC schemas
		// Note: inputSchemaFilePath can be path to folder or file
		//
		List<IfcSchema> schemas = IfcParserUtil.parseSchemas(inputSchemaFilePath);
		
		// export IFC schema(s)
		//
		final AbstractJenaProvider jenaProvider = new MemoryJenaProvider();
		
		for (IfcSchema schema : schemas) {
			// export IFC schema into in-memory Jena graph using default conversion context
			Model schemaGraph = jenaProvider.openDefaultModel().removeAll();
			Ifc2RdfExportUtil.exportSchemaToJenaModel(schemaGraph, schema);		

			// export the in-memory Jena graph to file  
			RdfUtils.exportJenaModelToRdfFile(schemaGraph, outputSchemaFilePath, outputFileFormat, gzipOutputFile);			
		}
		
		//
		// load IFC model
		//
		IfcModel model = IfcParserUtil.parseModel(inputModelFilePath);		

		// get default grounding rule sets
		ComplexProcessorConfiguration groundingConfiguration = IfcModelAnalyser.getDefaultGroundingRuleSets();
		
		// ground nodes in the model
		IfcModelAnalyser modelAnalyser = new IfcModelAnalyser(model);			
		modelAnalyser.groundNodes(groundingConfiguration);
		
		
		//
		// export IFC model
		//
		
		// export IFC model into in-memory Jena graph using default conversion context
		Model modelGraph = jenaProvider.openDefaultModel().removeAll();
		Ifc2RdfExportUtil.exportModelToJenaModel(modelGraph, model);
		
		// export the in-memory Jena graph to file  
		RdfUtils.exportJenaModelToRdfFile(modelGraph, outputModelFilePath, outputFileFormat, gzipOutputFile);
		
	}
	
}