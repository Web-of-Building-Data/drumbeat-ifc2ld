package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld;

import static fi.aalto.cs.drumbeat.common.params.StringParam.*;

import java.util.Arrays;

import org.apache.commons.lang3.NotImplementedException;

import fi.aalto.cs.drumbeat.common.params.*;

public class Ifc2RdfConversionParams extends TypedParams {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_IGNORE_EXPRESS_SCHEMA = "IgnoreExpressSchema";
	public static final String PARAM_IGNORE_IFC_SCHEMA = "IgnoreIfcSchema";
	
	public static final String PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES_TO = "ExportPropertyDomainsAndRangesTo";
	public static final String PARAM_CONVERT_BOOLEANS_TO = "ConvertBooleansTo";
	public static final String PARAM_CONVERT_DOUBLES_TO = "ConvertDoublesTo";
	public static final String PARAM_CONVERT_ENUMS_TO = "ConvertEnumsTo";
	public static final String PARAM_EXPORT_DEBUG_INFO = "ExportDebugInfo";
	public static final String PARAM_USE_LONG_ATTRIBUTE_NAME = "UseLongAttributeName";
	public static final String PARAM_CONVERT_COLLECTIONS_TO = "ConvertCollectionsTo";
	public static final String PARAM_NAME_ALL_BLANK_NODES = "NameAllBlankNodes";
	
	public static final String VALUE_NAMED_INDIVIDUAL = "owl:NamedIndividual";
	public static final String VALUE_NAMED_INDIVIDUAL_DESCRIPTION = "owl:NamedIndividual";
	
//	public static final String VALUE_YES = StringParam.VALUE_YES;
//	public static final String VALUE_YES_DESCRIPTION = StringParam.VALUE_YES_DESCRIPTION;
	
//	public static final String VALUE_NO = StringParam.VALUE_NO;
//	public static final String VALUE_NO_DESCRIPTION = StringParam.VALUE_NO_DESCRIPTION;
	
	public static final String VALUE_XSD_STRING = "xsd:string";
	public static final String VALUE_XSD_STRING_DESCRIPTION = "xsd:string";

	public static final String VALUE_XSD_BOOLEAN = "xsd:boolean";
	public static final String VALUE_XSD_BOOLEAN_DESCRIPTION = "xsd:boolean";
	
	public static final String VALUE_XSD_DECIMAL = "xsd:decimal";
	public static final String VALUE_XSD_DOUBLE = "xsd:double";
	public static final String VALUE_OWL_REAL = "owl:real";

	public static final String VALUE_AUTO_MOST_SUPPORTED = "AutoMostSupported";
	public static final String VALUE_AUTO_MOST_SUPPORTED_DESCRIPTION = "Auto (most supported)";

	public static final String VALUE_AUTO_MOST_EFFICIENT = "AutoMostEfficient";	
	public static final String VALUE_AUTO_MOST_EFFICIENT_DESCRIPTION = "Auto (most efficient)";
	
	public static final String VALUE_DRUMMOND_LIST = "DrummondList";
	public static final String VALUE_OLO_SIMILAR_LIST = "OloSimilarList";
	
	public static final String VALUE_ATTRIBUTES_WITH_LONG_NAMES = "AttributesWithLongNames";
	

	public Ifc2RdfConversionParams() {
		
		addParam(new BooleanParam(
				PARAM_IGNORE_EXPRESS_SCHEMA,
				null,
				null,
				Arrays.asList(
						Boolean.TRUE,
						Boolean.FALSE),
				Arrays.asList(
						VALUE_YES_DESCRIPTION,
						VALUE_NO_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION),
				Boolean.FALSE
				));
		
		addParam(new BooleanParam(
				PARAM_IGNORE_IFC_SCHEMA,
				null,
				null,
				Arrays.asList(
						Boolean.TRUE,
						Boolean.FALSE),
				Arrays.asList(
						VALUE_YES_DESCRIPTION,
						VALUE_NO_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION),
				Boolean.FALSE
				));
		
		addParam(new BooleanParam(
				PARAM_EXPORT_DEBUG_INFO,
				null,
				null,
				Arrays.asList(
						Boolean.TRUE,
						Boolean.FALSE),
				Arrays.asList(
						VALUE_YES_DESCRIPTION,
						VALUE_NO_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION),
				Boolean.FALSE
				));
		
		addParam(
				new StringParam(
					PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES_TO,
					null,
					null,
					Arrays.asList(VALUE_NONE, VALUE_ATTRIBUTES_WITH_LONG_NAMES),
					Arrays.asList(VALUE_NONE_DESCRIPTION, VALUE_ATTRIBUTES_WITH_LONG_NAMES),
					VALUE_ATTRIBUTES_WITH_LONG_NAMES));
		
		addParam(
				new StringParam(
					PARAM_CONVERT_BOOLEANS_TO,
					null,
					null,
					Arrays.asList(
							VALUE_NAMED_INDIVIDUAL,
							VALUE_XSD_STRING,
							VALUE_XSD_BOOLEAN),
					Arrays.asList(
							VALUE_NAMED_INDIVIDUAL_DESCRIPTION +  VALUE_DEFAULT_DESCRIPTION,
							VALUE_XSD_STRING_DESCRIPTION,
							VALUE_XSD_BOOLEAN_DESCRIPTION + " (LOGICAL value `UNKNOWN` will be ignored!)"),
					VALUE_NAMED_INDIVIDUAL));
				
		addParam(
				new StringParam(
					PARAM_CONVERT_ENUMS_TO,
					null,
					null,
					Arrays.asList(
							VALUE_NAMED_INDIVIDUAL,
							VALUE_XSD_STRING),
					Arrays.asList(
							VALUE_NAMED_INDIVIDUAL_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION,
							VALUE_XSD_STRING_DESCRIPTION),
					VALUE_NAMED_INDIVIDUAL));

		addParam(
				new StringParam(
					PARAM_CONVERT_DOUBLES_TO,
					null,
					null,
					Arrays.asList(
//							VALUE_AUTO_MOST_SUPPORTED,
//							VALUE_AUTO_MOST_EFFICIENT,
							VALUE_XSD_DECIMAL,
							VALUE_XSD_DOUBLE,
							VALUE_OWL_REAL,
							VALUE_XSD_STRING),
					Arrays.asList(
//							VALUE_AUTO_MOST_SUPPORTED_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION,
//							VALUE_AUTO_MOST_EFFICIENT_DESCRIPTION,
							VALUE_XSD_DECIMAL + VALUE_DEFAULT_DESCRIPTION,
							VALUE_XSD_DOUBLE + " (most efficient, but not supported in OWL 2 EL/QL)",
							VALUE_OWL_REAL,
							VALUE_XSD_STRING),
					VALUE_XSD_DECIMAL));
		
		addParam(
				new StringParam(
					PARAM_CONVERT_COLLECTIONS_TO,
					null,
					null,
					Arrays.asList(
							VALUE_DRUMMOND_LIST,
							VALUE_OLO_SIMILAR_LIST),
					Arrays.asList(
							VALUE_DRUMMOND_LIST,
							VALUE_OLO_SIMILAR_LIST),
					VALUE_DRUMMOND_LIST));

		addParam(
				new BooleanParam(
					PARAM_USE_LONG_ATTRIBUTE_NAME,
					null,
					null,
					Arrays.asList(
							Boolean.TRUE,
							Boolean.FALSE),
					Arrays.asList(
							VALUE_YES_DESCRIPTION,
							VALUE_NO_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION),
					Boolean.TRUE
					));
		
		addParam(
				new BooleanParam(
					PARAM_NAME_ALL_BLANK_NODES,
					null,
					null,
					Arrays.asList(
							Boolean.TRUE,
							Boolean.FALSE),
					Arrays.asList(
							VALUE_YES_DESCRIPTION,
							VALUE_NO_DESCRIPTION + VALUE_DEFAULT_DESCRIPTION),
					Boolean.FALSE));
		
	
	}
	
	public boolean useLongAttributeName() {
		return (Boolean)getParam(PARAM_USE_LONG_ATTRIBUTE_NAME).getValue();
	}
	
	public boolean ignoreExpressSchema() {
		return (Boolean)getParam(PARAM_IGNORE_EXPRESS_SCHEMA).getValue();
	}

	public boolean ignoreIfcSchema() {
		return (Boolean)getParam(PARAM_IGNORE_IFC_SCHEMA).getValue();
	}
	
	public boolean nameAllBlankNodes() {
		return (Boolean)getParam(PARAM_NAME_ALL_BLANK_NODES).getValue();
	}

	public String convertBooleansTo() {
		return (String)getParam(PARAM_CONVERT_BOOLEANS_TO).getValue();
	}

	public String convertCollectionsTo() {
		return (String)getParam(PARAM_CONVERT_COLLECTIONS_TO).getValue();
	}
		
	public String convertEnumsTo() {
		return (String)getParam(PARAM_CONVERT_ENUMS_TO).getValue();		
	}

	public String convertDoublesTo() {
		return (String)getParam(PARAM_CONVERT_DOUBLES_TO).getValue();		
	}

	public boolean convertSetAttributesAsMultipleProperties() {
		String convertCollectionsTo = convertCollectionsTo();
		switch (convertCollectionsTo) {
		case VALUE_DRUMMOND_LIST:
			return true;
		case VALUE_OLO_SIMILAR_LIST:
			return false;
		default:
			throw new NotImplementedException("Unknown collection type");
		}		
	}
}
