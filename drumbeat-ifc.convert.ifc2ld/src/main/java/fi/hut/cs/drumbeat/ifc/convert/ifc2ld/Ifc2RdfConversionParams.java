package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.Arrays;

import fi.hut.cs.drumbeat.common.params.StringParam;
import fi.hut.cs.drumbeat.common.params.StringParams;

public class Ifc2RdfConversionParams extends StringParams {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES = "ExportPropertyDomainsAndRanges";
	public static final String PARAM_CONVERT_BOOLEANS_TO = "ConvertBooleansTo";
	public static final String PARAM_CONVERT_DOUBLES_TO = "ConvertDoublesTo";
	public static final String PARAM_CONVERT_ENUMS_TO = "ConvertEnumsTo";
	public static final String PARAM_EXPORT_DEBUG_INFO = "ExportDebugInfo";
	
	public static final String VALUE_AUTO = StringParam.VALUE_AUTO;
	public static final String VALUE_AUTO_DESCRIPTION = StringParam.VALUE_AUTO_DESCRIPTION;

	public static final String VALUE_NONE = StringParam.VALUE_NONE;
	public static final String VALUE_NONE_DESCRIPTION = StringParam.VALUE_NONE_DESCRIPTION;	
	
	public static final String VALUE_NAMED_INDIVIDUAL = "owl:NamedIndividual";
	public static final String VALUE_NAMED_INDIVIDUAL_DESCRIPTION = "owl:NamedIndividual";
	
	public static final String VALUE_TRUE = StringParam.VALUE_TRUE;
	public static final String VALUE_TRUE_DESCRIPTION = StringParam.VALUE_TRUE_DESCRIPTION;
	
	public static final String VALUE_FALSE = StringParam.VALUE_FALSE;
	public static final String VALUE_FALSE_DESCRIPTION = StringParam.VALUE_FALSE_DESCRIPTION;	
	
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
	
	public Ifc2RdfConversionParams() {
		
		addParam(new StringParam(
				PARAM_EXPORT_DEBUG_INFO,
				null,
				null,
				Arrays.asList(
						VALUE_TRUE,
						VALUE_FALSE),
				Arrays.asList(
						VALUE_TRUE,
						VALUE_FALSE),
				VALUE_FALSE
				));
		
		addParam(
				new StringParam(
					PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES,
					null,
					null,
					Arrays.asList(VALUE_NONE),
					Arrays.asList(VALUE_NONE_DESCRIPTION),
					VALUE_NONE));
		
		addParam(
				new StringParam(
					PARAM_CONVERT_BOOLEANS_TO,
					null,
					null,
					Arrays.asList(
							VALUE_AUTO,
							VALUE_NAMED_INDIVIDUAL,
							VALUE_XSD_STRING,
							VALUE_XSD_BOOLEAN),
					Arrays.asList(
							VALUE_AUTO_DESCRIPTION + "(=" + VALUE_NAMED_INDIVIDUAL_DESCRIPTION + ")",
							VALUE_NAMED_INDIVIDUAL_DESCRIPTION + " (most supported)",
							VALUE_XSD_STRING_DESCRIPTION,
							VALUE_XSD_BOOLEAN_DESCRIPTION + " (LOGICAL value `UNKNOWN` will be ignored!)"),
					VALUE_NAMED_INDIVIDUAL));
				
		addParam(
				new StringParam(
					PARAM_CONVERT_ENUMS_TO,
					null,
					null,
					Arrays.asList(
							VALUE_AUTO,
							VALUE_NAMED_INDIVIDUAL,
							VALUE_XSD_STRING),
					Arrays.asList(
							VALUE_AUTO_DESCRIPTION + "(=" + VALUE_NAMED_INDIVIDUAL_DESCRIPTION + ")",
							VALUE_NAMED_INDIVIDUAL_DESCRIPTION + " (most supported)",
							VALUE_XSD_STRING_DESCRIPTION),
					VALUE_NAMED_INDIVIDUAL));

		addParam(
				new StringParam(
					PARAM_CONVERT_DOUBLES_TO,
					null,
					null,
					Arrays.asList(
							VALUE_AUTO_MOST_SUPPORTED,
							VALUE_AUTO_MOST_EFFICIENT,
							VALUE_XSD_DECIMAL,
							VALUE_XSD_DOUBLE,
							VALUE_OWL_REAL,
							VALUE_XSD_STRING),
					Arrays.asList(
							VALUE_AUTO_MOST_SUPPORTED_DESCRIPTION,
							VALUE_AUTO_MOST_EFFICIENT_DESCRIPTION,
							VALUE_XSD_DECIMAL,
							VALUE_XSD_DOUBLE,
							VALUE_OWL_REAL,
							VALUE_XSD_STRING),
					VALUE_AUTO_MOST_SUPPORTED));	
		
	
	}
	

}
