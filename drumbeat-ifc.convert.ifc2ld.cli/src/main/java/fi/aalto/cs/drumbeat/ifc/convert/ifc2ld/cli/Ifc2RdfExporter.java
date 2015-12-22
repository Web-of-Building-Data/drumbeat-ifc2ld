package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.cli;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.EnumSet;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.hp.hpl.jena.rdf.model.Model;

import fi.aalto.cs.drumbeat.common.config.*;
import fi.aalto.cs.drumbeat.common.config.document.*;
import fi.aalto.cs.drumbeat.common.string.StringUtils;
import fi.aalto.cs.drumbeat.ifc.common.IfcCommandLineOptions;
import fi.aalto.cs.drumbeat.ifc.common.IfcException;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.util.Ifc2RdfExportUtil;
import fi.aalto.cs.drumbeat.ifc.convert.stff2ifc.IfcParserException;
import fi.aalto.cs.drumbeat.ifc.convert.stff2ifc.util.IfcParserUtil;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;
import fi.aalto.cs.drumbeat.ifc.data.schema.*;
import fi.aalto.cs.drumbeat.ifc.processing.IfcModelAnalyser;
import fi.aalto.cs.drumbeat.rdf.jena.provider.config.JenaProviderPoolConfigurationSection;
import fi.aalto.cs.drumbeat.rdf.RdfUtils;
import fi.aalto.cs.drumbeat.rdf.jena.provider.AbstractJenaProvider;
import fi.aalto.cs.drumbeat.rdf.jena.provider.MemoryJenaProvider;

import org.apache.jena.riot.RDFFormat;

public class Ifc2RdfExporter {
	
	private static final Logger logger = Logger.getRootLogger();
	
	private String inputSchemaFilePath;
	private String inputModelFilePath;
	private String outputLayerName;
	private String outputSchemaFilePath;
	private String outputSchemaName;
	private String outputModelFilePath;
	private String outputModelName;
	private String outputMetaModelFilePath;
	private String outputMetaModelName;
	private String outputFileFormatName;
	
	public Ifc2RdfExporter(
		 String inputSchemaFilePath,
		 String inputModelFilePath,
		 String outputLayerName,
		 String outputSchemaFilePath,
		 String outputSchemaName,
		 String outputModelFilePath,
		 String outputModelName,
		 String outputMetaModelFilePath,
		 String outputMetaModelName,
		 String outputFileFormatName)
	{
		this.inputSchemaFilePath = inputSchemaFilePath;
		this.inputModelFilePath = inputModelFilePath;
		this.outputLayerName = outputLayerName;
		this.outputSchemaFilePath = outputSchemaFilePath;
		this.outputSchemaName = outputSchemaName;
		this.outputModelFilePath = outputModelFilePath;
		this.outputModelName = outputModelName;
		this.outputMetaModelFilePath = outputMetaModelFilePath;
		this.outputMetaModelName = outputMetaModelName;
		this.outputFileFormatName = outputFileFormatName;		
	}
	
	public static void init(String loggerConfigFilePath, String configFilePath) throws ConfigurationParserException {
		//
		// config logger
		//
		loadLoggerConfigration(loggerConfigFilePath);
		
		//
		// load configuration document
		//
		loadConfiguration(configFilePath);
	}
	
	/**
	 * @param args
	 */	
	public void run() throws Exception {
		
		//
		// load jena model factory configuration pool
		//
		ConfigurationPool<ConfigurationItemEx> jenaProviderConfigurationPool;
		if (!StringUtils.isEmptyOrNull(outputSchemaName) || !StringUtils.isEmptyOrNull(outputModelName)) {
			jenaProviderConfigurationPool = getJenaProviderConfigurationPool();
		} else {
			jenaProviderConfigurationPool  = null;
		}
		
		//
		// define jena-model factory for the output IFC schema
		//
		AbstractJenaProvider outputSchemaJenaProvider = null;		
		if (!StringUtils.isEmptyOrNull(outputSchemaName)) {
			outputSchemaJenaProvider = getJenaProvider(jenaProviderConfigurationPool, outputSchemaName);
		} else if (!StringUtils.isEmptyOrNull(outputSchemaFilePath)) {
			outputSchemaJenaProvider =  new MemoryJenaProvider();			
		}
		
		//
		// define jena-model factory for the output IFC model 
		//
		AbstractJenaProvider outputModelJenaProvider = null;		
		if (!StringUtils.isEmptyOrNull(outputModelName)) {
			outputModelJenaProvider = getJenaProvider(jenaProviderConfigurationPool, outputModelName);
		} else if (!StringUtils.isEmptyOrNull(outputModelFilePath)) {
			outputModelJenaProvider =  new MemoryJenaProvider();			
		}
		
		//
		// define jena-model factory for the output IFC model 
		//
		AbstractJenaProvider outputMetaModelJenaProvider = null;		
		if (!StringUtils.isEmptyOrNull(outputMetaModelName)) {
			outputMetaModelJenaProvider = getJenaProvider(jenaProviderConfigurationPool, outputMetaModelName);
		} else if (!StringUtils.isEmptyOrNull(outputMetaModelFilePath)) {
			outputMetaModelJenaProvider =  new MemoryJenaProvider();			
		}

		try {
			
			RDFFormat outputFileFormat = null;
			boolean gzipOutputFile = false;
			
			if (outputFileFormatName != null) {
				outputFileFormatName = outputFileFormatName.toUpperCase();
				
				String[] tokens = outputFileFormatName.split("\\.");
				
				if (tokens.length == 2) {
					if (tokens[1].equals("GZIP") || tokens[1].equals("GZ")) {
						gzipOutputFile = true;
					} else {
						throw new IfcException(String.format("Unknown ZIP format: '%s'", tokens[1]));
					}
				}
				
				try {
					outputFileFormat = (RDFFormat) RDFFormat.class.getField(tokens[0]).get(null);		
				} catch (NoSuchFieldException e) {
					throw new IfcException(
							String.format("Unknown RDF format: '%s', see: %s", tokens[0], IfcCommandLineOptions.URL_RIOT_FORMAT));
				}
			}			
			
		
			//
			// parse and export schema
			//
			System.err.printf("Exporting schemas%n");

			final List<IfcSchema> schemas = parseSchemas(inputSchemaFilePath);
			
			for (IfcSchema schema : schemas) {
				System.out.printf("SCHEMA %s", schema.getVersion());
				schema.getAllTypeInfos().stream().filter(t -> t instanceof IfcSelectTypeInfo).forEach(t -> {
					EnumSet<IfcTypeEnum> valueTypes = t.getValueTypes();
					if (valueTypes.size() > 1) {
						System.out.printf("SELECT type %s %s%n", t, valueTypes);
					}
				});
			}
			
			
			if (outputSchemaJenaProvider != null) {
				for (IfcSchema schema : schemas) {
					exportSchema(outputSchemaJenaProvider, schema, outputLayerName, outputSchemaFilePath, outputFileFormat, gzipOutputFile);
				}
			}
			
			//
			// parse model
			//
			if (outputModelJenaProvider != null || outputMetaModelJenaProvider != null) {				
				IfcModel model = parseModel(inputModelFilePath);
				
				//
				// export model
				//
				if (outputModelJenaProvider != null) {
					exportModel(outputModelJenaProvider, model, outputLayerName, outputModelFilePath, outputFileFormat, gzipOutputFile);
				}
				
				//
				// export meta-model
				//
				if (outputMetaModelJenaProvider != null) {				
					exportMetaModel(outputMetaModelJenaProvider, model, outputLayerName, outputMetaModelFilePath, outputFileFormat, gzipOutputFile);				
				}
				
			}			

		} finally {
		
			//
			//  release jena-model factories
			//
			if (outputSchemaJenaProvider != null) {
				outputSchemaJenaProvider.release();
			}
			
			if (outputModelJenaProvider != null) {
				outputModelJenaProvider.release();
			}

		}
		
		
		logger.info("END OF PROGRAM");
		
	}

	/**
	 * Loads logger configuration
	 * @throws FactoryConfigurationError
	 */
	private static void loadLoggerConfigration(String loggerConfigFilePath) throws FactoryConfigurationError {
		if (loggerConfigFilePath.endsWith("xml")) {
			DOMConfigurator.configure(loggerConfigFilePath);			
		} else {
			PropertyConfigurator.configure(loggerConfigFilePath);			
		}
	}
	
	/**
	 * Loads configuration document
	 * @throws ConfigurationParserException
	 */
	private static void loadConfiguration(String configFilePath) throws ConfigurationParserException {
		logger.info(String.format("Loading configuration in '%s'", configFilePath));
		ConfigurationDocument.load(configFilePath);
		logger.info("Loading configuration has been completed successfully");
	}

	/**
	 * Gets Jena-model factory configuration pool  
	 * @return
	 * @throws ConfigurationParserException
	 */
	private static ConfigurationPool<ConfigurationItemEx> getJenaProviderConfigurationPool()
			throws ConfigurationParserException {
		return JenaProviderPoolConfigurationSection.getInstance().getConfigurationPool();
	}
	
	/**
	 * Gets a Jena model factory by name from a pool
	 * @param jenaProviderConfigurationPool
	 * @param jenaProviderName
	 * @return
	 * @throws Exception
	 */
	private static AbstractJenaProvider getJenaProvider(
			ConfigurationPool<ConfigurationItemEx> jenaProviderConfigurationPool,
			String jenaProviderName) throws Exception {
		
		try {
			ConfigurationItemEx configuration = jenaProviderConfigurationPool.getByName(jenaProviderName);			
			return AbstractJenaProvider.getFactory(configuration.getName(), configuration.getType(), configuration.getProperties(), null);
		} catch(InvalidParameterException e) {
			throw new IfcException(String.format("Jena model %s is not found", jenaProviderName), e);
		}
		
	}

	
	/**
	 * Imports IFC schema from EXPRESS file
	 * @param inputSchemaFilePath
	 * @return
	 * @throws IOException
	 * @throws IfcParserException
	 */
	public static List<IfcSchema> parseSchemas(String inputSchemaFilePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing schema from file or folder '%s'", inputSchemaFilePath));
		final List<IfcSchema> schemas = IfcParserUtil.parseSchemas(inputSchemaFilePath);
		logger.info("Parsing schema is compeleted");
		return schemas;
	}

	/**
	 * Exports schema
	 * @param outputSchemaJenaProvider
	 * @param schema
	 * @param outputSchemaFilePath
	 * @param outputFileFormat
	 * @param gzipOutputFile 
	 * @throws Exception
	 */
	public static Model exportSchema(
			AbstractJenaProvider outputSchemaJenaProvider,
			IfcSchema schema,
			String conversionContextName,
			String outputSchemaFilePath,
			RDFFormat outputFileFormat,
			boolean gzipOutputFile)
			throws Exception {
		// export model to RDF graph
		logger.info("Exporting schema to RDF graph");
		Model schemaGraph = outputSchemaJenaProvider.openDefaultModel();
		if (schemaGraph.supportsTransactions()) {
			schemaGraph.begin();				
		}
		schemaGraph.removeAll();
		Ifc2RdfExportUtil.exportSchemaToJenaModel(schemaGraph, schema, conversionContextName);
		if (schemaGraph.supportsTransactions()) {
			schemaGraph.commit();
		}
		logger.info("Exporting schema RDF graph is completed");
		
		// export model to RDF file
		if (!StringUtils.isEmptyOrNull(outputSchemaFilePath)) {
			RdfUtils.exportJenaModelToRdfFile(schemaGraph, outputSchemaFilePath, outputFileFormat, gzipOutputFile);
		}
		return schemaGraph;
	}

	/**
	 * Imports IFC model from a STEP file
	 * @param inputModelFilePath 
	 * @return
	 * @throws IOException
	 * @throws IfcParserException
	 */
	public static IfcModel parseModel(String inputModelFilePath) throws IOException, IfcParserException {
		logger.info(String.format("Parsing model from file '%s'", inputModelFilePath));
		IfcModel model = IfcParserUtil.parseModel(inputModelFilePath);
		logger.info("Parsing model is completed");
		return model;
	}
	
	/**
	 * Exports IFC model to a Jena model (and writes it to a file if needed)
	 * @param outputModelJenaProvider
	 * @param model
	 * @param contextName
	 * @param outputModelFilePath
	 * @param outputFileLanguage
	 * @throws Exception
	 */
	public static Model exportModel(
			AbstractJenaProvider outputModelJenaProvider,
			IfcModel model,
			String conversionContextName,
			String outputModelFilePath,
			RDFFormat outputFileFormat,
			boolean gzipOutputFile) throws Exception {
		// get default grounding rule sets
		ComplexProcessorConfiguration groundingConfiguration = IfcModelAnalyser.getDefaultGroundingRuleSets();
		
		// ground nodes in the model
		IfcModelAnalyser modelAnalyser = new IfcModelAnalyser(model);			
		modelAnalyser.groundNodes(groundingConfiguration);
		
		// export model to RDF graph
		logger.info("Exporting model to RDF graph");
		Model modelGraph = outputModelJenaProvider.openDefaultModel();
		if (modelGraph.supportsTransactions()) {
			logger.info("Enabling RDF graph transactions");
			modelGraph.begin();				
		}
		modelGraph.removeAll();
		Ifc2RdfExportUtil.exportModelToJenaModel(modelGraph, model, conversionContextName);
		if (modelGraph.supportsTransactions()) {
			logger.info("Committing RDF graph transactions");
			modelGraph.commit();
		}
		logger.info("Exporting model to RDF graph is completed");
		
		// export model to RDF file
		if (!StringUtils.isEmptyOrNull(outputModelFilePath)) {
			RdfUtils.exportJenaModelToRdfFile(modelGraph, outputModelFilePath, outputFileFormat, gzipOutputFile);
		}
		
		return modelGraph;
	}
	
	/**
	 * Exports IFC model to a Jena model (and writes it to a file if needed)
	 * @param outputModelJenaProvider
	 * @param model
	 * @param contextName
	 * @param outputModelFilePath
	 * @param outputFileLanguage
	 * @throws Exception
	 */
	public static Model exportMetaModel(
			AbstractJenaProvider outputMetaModelJenaProvider,
			IfcModel model,
			String conversionContextName,
			String outputMetaModelFilePath,
			RDFFormat outputFileFormat,
			boolean gzipOutputFile) throws Exception {
		// get default grounding rule sets
		ComplexProcessorConfiguration groundingConfiguration = IfcModelAnalyser.getDefaultGroundingRuleSets();
		
		// ground nodes in the model
		IfcModelAnalyser modelAnalyser = new IfcModelAnalyser(model);			
		modelAnalyser.groundNodes(groundingConfiguration);
		
		// export model to RDF graph
		logger.info("Exporting meta model to RDF graph");
		Model modelGraph = outputMetaModelJenaProvider.openDefaultModel();
		if (modelGraph.supportsTransactions()) {
			logger.info("Enabling RDF graph transactions");
			modelGraph.begin();				
		}
		modelGraph.removeAll();
		Ifc2RdfExportUtil.exportMetaModelToJenaModel("http://example.org", modelGraph, model, conversionContextName);
		if (modelGraph.supportsTransactions()) {
			logger.info("Committing RDF graph transactions");
			modelGraph.commit();
		}
		logger.info("Exporting meta model to RDF graph is completed");
		
		// export model to RDF file
		if (!StringUtils.isEmptyOrNull(outputMetaModelFilePath)) {
			RdfUtils.exportJenaModelToRdfFile(modelGraph, outputMetaModelFilePath, outputFileFormat, gzipOutputFile);
		}
		
		return modelGraph;
	}
	
	
//	private void logError(Object message, Throwable t) {
//	logger.error(message, t);
//	Logger.getLogger(t.getClass()).error(message, t);
//}

	
//	protected void testSchema(IfcSchema schema) {
//		for (IfcEntityTypeInfo entityInfo : schema.getEntityTypeInfos()) {
//			for (IfcInverseLinkInfo inverseLinkInfo : entityInfo.getInverseLinkInfos()) {
//				if (inverseLinkInfo.getCardinality().isSingle() && !inverseLinkInfo.getCardinality().isOptional()) {
//					IfcLinkInfo outgoingLinkInfo = inverseLinkInfo.getOutgoingLinkInfo();
//					if (outgoingLinkInfo.getCardinality().isSingle()) {
//						System.out.println(String.format("%s.%s<--%s.%s", inverseLinkInfo
//								.getDestinationEntityTypeInfo().getName(), inverseLinkInfo.getName(), outgoingLinkInfo
//								.getEntityTypeInfo().getName(), outgoingLinkInfo.getName()));
//					}
//				}
//			}
//		}
//	}


}
