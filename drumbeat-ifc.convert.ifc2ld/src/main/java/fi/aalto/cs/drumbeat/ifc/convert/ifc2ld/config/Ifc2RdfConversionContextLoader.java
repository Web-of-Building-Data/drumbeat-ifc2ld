package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.config;

import java.util.Properties;
import java.util.regex.Matcher;

import fi.aalto.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.aalto.cs.drumbeat.common.config.document.ConverterPoolConfigurationSection;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
import fi.aalto.cs.drumbeat.rdf.OwlProfileList;

public class Ifc2RdfConversionContextLoader {
	
	/////////////////////////
	// STATIC MEMBERS
	/////////////////////////
	
	public static final String CONFIGURATION_SECTION_CONVERTER_TYPE_NAME = "Ifc2Rdf";
	public static final String CONFIGURATION_PROPERTY_ONTOLOGY_VERSION = "Ontology.Version";
	
	private static final String CONFIGURATION_PROPERTY_OWL_PROFILE = "OwlProfile";
	private static final String CONFIGURATION_PROPERTY_CONVERSION_OPTIONS_PREFIX = "Options.";
//	private static final String CONFIGURATION_PROPERTY_ONTOLOGY_PREFIX = "Ontology.Prefix";
//	private static final String CONFIGURATION_PROPERTY_ONTOLOGY_NAMESPACE_FORMAT = "Ontology.NamespaceFormat";	
	private static final String CONFIGURATION_PROPERTY_MODEL_PREFIX = "Model.Prefix";
	private static final String CONFIGURATION_PROPERTY_MODEL_NAMESPACE_FORMAT = "Model.NamespaceFormat";
	
	private static final String CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION = Matcher.quoteReplacement("$Schema.Version$");
	private static final String CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME = Matcher.quoteReplacement("$Converter.Context.Name$");
	
	private static final String CONFIGURATION_PROPERTY_MODEL_BLANK_NODE_NAMESPACE_URI_FORMAT = "Model.BlankNodeNamespaceUriFormat";
	
	
	public static Ifc2RdfConversionContext loadFromDefaultConfigurationFile(String contextName) throws ConfigurationParserException {
		ConverterPoolConfigurationSection configurationSection =
				ConverterPoolConfigurationSection.getInstance(CONFIGURATION_SECTION_CONVERTER_TYPE_NAME);
		
		ConfigurationItemEx configuration;
		if (contextName != null) {
			configuration = configurationSection.getConfigurationPool().getByName(contextName);
		} else {
			configuration = configurationSection.getConfigurationPool().getDefault(); 
		}		
				
		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
		loadConfigurationToContext(context, configuration);
		return context;
	}
	
	public static void loadConfigurationToContext(Ifc2RdfConversionContext context, ConfigurationItemEx configuration) {
		
		context.setIfcOntologyVersion(configuration.getProperties().getProperty(CONFIGURATION_PROPERTY_ONTOLOGY_VERSION, "1.0.0"));
		
		String[] owlProfileNames = configuration.getProperties().getProperty(CONFIGURATION_PROPERTY_OWL_PROFILE).split(",");
		OwlProfileList owlProfileList = new OwlProfileList(owlProfileNames);
		context.setOwlProfiles(owlProfileList);
		
		String name = configuration.getName();
		context.setName(name);
		
		Properties properties = configuration.getProperties();
		
		Ifc2RdfConversionParams conversionParams = context.getConversionParams();
		
		for (String propertyName : properties.stringPropertyNames()) {
			if (propertyName.startsWith(CONFIGURATION_PROPERTY_CONVERSION_OPTIONS_PREFIX)) {
				String conversionParamName = propertyName.substring(CONFIGURATION_PROPERTY_CONVERSION_OPTIONS_PREFIX.length());
				String conversionParamValue = properties.getProperty(propertyName);
				// TODO: enable allowing unknown params
				//conversionParams.getParam(conversionParamName, true).setValue(conversionParamValue);
				conversionParams.getParam(conversionParamName).setStringValue(conversionParamValue);
			}
		}
		
//		String ontologyPrefix = properties.getProperty(CONFIGURATION_PROPERTY_ONTOLOGY_PREFIX, Ifc2RdfVocabulary.IFC.BASE_PREFIX);
//		context.setOntologyPrefix(ontologyPrefix);
//		
//		String ontologyNamespaceFormat = properties.getProperty(CONFIGURATION_PROPERTY_ONTOLOGY_NAMESPACE_FORMAT, Ifc2RdfVocabulary.IFC.getBaseUriFormat())
//				.replaceAll(CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION, "%1s")
//				.replaceAll(CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME, "%2s");
//		context.setOntologyNamespaceUriFormat(ontologyNamespaceFormat);
		
				
//		String modelPrefix = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_PREFIX, Ifc2RdfVocabulary.DEFAULT_MODEL_PREFIX);
		String modelPrefix = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_PREFIX);
		context.setModelNamespacePrefix(modelPrefix);
		
//		String modelNamespaceFormat = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_NAMESPACE_FORMAT, Ifc2RdfVocabulary.DEFAULT_MODEL_NAMESPACE_FORMAT)
//				.replaceAll(CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION, "%1s")
//				.replaceAll(CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME, "%2s");
		String modelNamespaceFormat = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_NAMESPACE_FORMAT)
				.replaceAll(CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION, "%1s")
				.replaceAll(CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME, "%2s");
		context.setModelNamespaceUriFormat(modelNamespaceFormat);
		
		String modelBlankNodeNamespaceUriFormat = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_BLANK_NODE_NAMESPACE_URI_FORMAT);
		context.setModelBlankNodeNamespaceUriFormat(modelBlankNodeNamespaceUriFormat);
		
	}

}
