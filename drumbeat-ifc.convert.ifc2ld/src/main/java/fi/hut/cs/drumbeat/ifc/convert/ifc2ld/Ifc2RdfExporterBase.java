package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.common.params.StringParam;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.LogicalEnum;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEnumerationTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLiteralTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLogicalTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;


/**
 * 
 * @author vuhoan1
 *
 */
public abstract class Ifc2RdfExporterBase {
	
	protected final Model jenaModel;
	private Ifc2RdfConversionContext context;
//	private String ontologyNamespacePrefix;
	private String ontologyNamespaceUri;
	private String modelNamespacePrefix;
	private String modelNamespaceUri;
	private Resource xsdTypeForDoubles;
	private Resource xsdTypeForBooleans;
	private Resource xsdTypeForEnums;
	private Map<IfcTypeEnum, Property> mapOf_Type_hasXXXProperty;
	
	protected Ifc2RdfExporterBase(Ifc2RdfConversionContext context, Model jenaModel) {
		this.jenaModel = jenaModel;
		this.context = context;
		this.mapOf_Type_hasXXXProperty = new HashMap<>();
	}
	
	
	/**
	 * Returns the equivalent XSD datatype of an IFC literal type
	 * @param typeInfo
	 * @return
	 */
	public Resource getXsdDataType(IfcLiteralTypeInfo typeInfo) {
		switch (typeInfo.getName()) {
		
		case IfcVocabulary.TypeNames.BINARY:			
//			return XSD.nonNegativeInteger;
			return XSD.hexBinary;
			
		case IfcVocabulary.TypeNames.INTEGER:
			return XSD.integer;
			
		case IfcVocabulary.TypeNames.NUMBER:
		case IfcVocabulary.TypeNames.REAL:
			return getXsdTypeForDoubles();
			
		case IfcVocabulary.TypeNames.DATETIME:
			return XSD.dateTime;
			
		// TODO: Remove GUID
		case IfcVocabulary.TypeNames.GUID:
//			return XSD.NMTOKEN;
			return XSD.xstring;
			
		case IfcVocabulary.TypeNames.STRING:
			return XSD.xstring;
			
		default:
			throw new IllegalArgumentException(String.format("Unknown literal type info '%s'", typeInfo.getName()));			
		}
	}
	
	
	/**
	 * Returns the equivalent XSD datatype of an IFC literal type
	 * @param typeInfo
	 * @return
	 */
	public Resource getXsdDataType(IfcLogicalTypeInfo typeInfo) {
		switch (typeInfo.getName()) {
		
		case IfcVocabulary.TypeNames.BOOLEAN:
		case IfcVocabulary.TypeNames.LOGICAL:
			return getXsdTypeForBooleans();
			
		default:
			throw new IllegalArgumentException(String.format("Unknown logical type info '%s'", typeInfo.getName()));			
		}
	}
	

	/**
	 * Converts an IFC literal value to an RDF resource 
	 * @param literalValue
	 * @return
	 */
	public Resource convertLiteralToNode(IfcLiteralValue literalValue) {
		
		Resource resource;

		IfcTypeInfo type = literalValue.getType();
		assert(type != null) : literalValue;
		if (type instanceof IfcDefinedTypeInfo || type instanceof IfcLiteralTypeInfo) {			
			
			RDFNode valueNode;			
			IfcTypeEnum valueType = type.getValueTypes().iterator().next();
			if (valueType == IfcTypeEnum.STRING) {
				valueNode = jenaModel.createTypedLiteral((String)literalValue.getValue());				
			} else if (valueType == IfcTypeEnum.GUID) {				
				valueNode = jenaModel.createTypedLiteral(literalValue.getValue().toString());
			} else if (valueType == IfcTypeEnum.REAL) {				
				valueNode = jenaModel.createTypedLiteral(literalValue.getValue(), getXsdTypeForDoubles().getURI());				
			} else if (valueType == IfcTypeEnum.NUMBER) {				
				valueNode = jenaModel.createTypedLiteral((double)literalValue.getValue());				
			} else if (valueType == IfcTypeEnum.INTEGER) {				
				valueNode = jenaModel.createTypedLiteral((long)literalValue.getValue());				
//			} else if (valueType == IfcTypeEnum.LOGICAL) {
//				valueNode = convertBooleanValue(literalValue);
			} else {
				assert (valueType == IfcTypeEnum.DATETIME) : "Expected: valueType == IfcTypeEnum.DATETIME. Actual: valueType = " + valueType + ", " + type;
				valueNode = jenaModel.createTypedLiteral((Calendar)literalValue.getValue());				
			}

			resource = createAnonResource();
			resource.addProperty(RDF.type, createUriResource(formatTypeName(type)));
			
			Property hasXXXProperty = getHasXXXProperty(type);			
			resource.addProperty(hasXXXProperty, valueNode);			
			
		} else if (type instanceof IfcEnumerationTypeInfo) {
			
			Resource xsdType = getXsdTypeForEnums();
			if (xsdType.equals(OWL2.NamedIndividual)) {
				resource = createUriResource(formatOntologyName((String)literalValue.getValue()));
			} else {
				resource = createAnonResource();
				Property property = getHasXXXProperty(type);			
				RDFNode valueNode = jenaModel.createTypedLiteral(literalValue.getValue(), xsdType.getURI());
				resource.addProperty(property, valueNode);			
			}
			
//			adapter.exportTriple(resource, RDF.type, createUriResource(formatTypeName(type)));
//			adapter.exportTriple(resource, Ifc2RdfVocabulary.EXPRESS.value, createUriResource(formatOntologyName((String)literalValue.getValue())));
			
		} else if (type instanceof IfcLogicalTypeInfo) {
		
			Resource xsdType = getXsdTypeForBooleans();
			if (xsdType.equals(OWL2.NamedIndividual)) {
				resource = createUriResource(formatExpressOntologyName((String)literalValue.getValue()));
			} else {
				resource = createAnonResource();				
				resource.addProperty(RDF.type, createUriResource(formatTypeName(type)));				
				
				Property property = getHasXXXProperty(type);
				
				LogicalEnum value = (LogicalEnum)literalValue.getValue();
				RDFNode valueNode;
				if (value == LogicalEnum.TRUE) {
					valueNode = jenaModel.createTypedLiteral("true", xsdType.getURI());					
				} else if (value == LogicalEnum.FALSE) {
					valueNode = jenaModel.createTypedLiteral("false", xsdType.getURI());					
				} else {
					valueNode = jenaModel.createTypedLiteral("unknown");					
				}				
							
				resource.addProperty(property, valueNode);			
			}
			
//			adapter.exportTriple(resource, RDF.type, createUriResource(formatExpressOntologyName(type.getName())));
//			adapter.exportTriple(resource, Ifc2RdfVocabulary.EXPRESS.value, createUriResource(formatExpressOntologyName((String)literalValue.getValue())));
			
		} else {			
			throw new RuntimeException(String.format("Invalid literal value type: %s (%s)", type, type.getClass()));			
		}
		
		return resource;			
	}		
		
	
	protected Model getJenaModel() {
		return jenaModel;
	}
	
//	protected void setOntologyNamespacePrefix(String prefix) {
//		ontologyNamespacePrefix = prefix;
//	}
//	
//	protected String getOntologyNamespacePrefix() {
//		return ontologyNamespacePrefix;
//	}

	protected String getOntologyNamespaceUri() {
		return ontologyNamespaceUri;
	}
	
	protected void setOntologyNamespaceUri(String uri) {
		ontologyNamespaceUri = uri;
	}
	
	protected void setModelNamespacePrefix(String prefix) {
		modelNamespacePrefix = prefix;
	}
	
	protected String getModelNamespacePrefix() {
		return modelNamespacePrefix;
	}

	protected String getModelNamespaceUri() {
		return modelNamespaceUri;
	}
	
	protected void setModelNamespaceUri(String uri) {
		modelNamespaceUri = uri;
	}
	
	protected Ifc2RdfConversionContext getContext() {
		return context;
	}
	
	protected Resource createUriResource(String uri) {
		return jenaModel.createResource(uri);
	}	
	
	protected Resource createAnonResource() {
		return jenaModel.createResource();
	}
	
	protected Resource createAnonResource(String anonId) {
		return jenaModel.createResource(new AnonId(anonId));
	}
	
	protected RDFList createList(Collection<? extends RDFNode> resources) {
		return jenaModel.createList(resources.iterator());
	}
	
	/**
	 * Creates an RDF resource which has type {@link OWL.Ontology}, predefined version string and comment
	 * @param uri
	 * @param version
	 * @param comment
	 * @return
	 */
	protected Resource createOntologyResource(String uri, String version, String comment) {
		
		Resource ontology = jenaModel.createResource(uri);
		ontology.addProperty(RDF.type, OWL.Ontology);
		ontology.addProperty(OWL.versionInfo, String.format("\"v%1$s %2$tY/%2$tm/%2$te %2$tH:%2$tM:%2$tS\"", version, new Date()));
		if (comment != null) {
			ontology.addProperty(RDFS.comment, String.format("\"\"\"%s\"\"\"", comment));
		}
		return ontology;
		
	}
	

	/**
	 * Returns the equivalent XSD datatype for IFC types: BOOLEAN and LOGICAL
	 * @return
	 */
	protected Resource getXsdTypeForBooleans() {
		
		if (xsdTypeForBooleans == null) {
			String convertBooleanValuesTo = context.getConversionParams().getParamValue(Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO);
			switch (convertBooleanValuesTo) {
			case StringParam.VALUE_AUTO:
			case Ifc2RdfConversionParams.VALUE_NAMED_INDIVIDUAL:
				xsdTypeForBooleans = OWL2.NamedIndividual;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_STRING:
				xsdTypeForBooleans = XSD.xstring;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_BOOLEAN:
				xsdTypeForBooleans = XSD.xboolean;
				break;
			default:
				throw new IllegalArgumentException("Invalid value of option " + Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO);
			}
		}
		return xsdTypeForBooleans;		
	}
	
//	/**
//	 * 
//	 * @param literalValue
//	 * @return
//	 */
//	protected RDFNode convertBooleanValue(IfcLiteralValue literalValue) {
//		Resource xsdType = getXsdTypeForBooleans();
//		if (xsdType.equals(OWL2.NamedIndividual)) {
//			return createUriResource(formatExpressOntologyName((String)literalValue.getValue()));
//		} else {
//			return jenaModel.createTypedLiteral(literalValue.getValue(), xsdType.getURI());
//		}		
//	}
	
	
	
	/**
	 * Returns the equivalent XSD datatype for IFC types: ENUM and LOGICAL
	 * @return
	 */
	protected Resource getXsdTypeForEnums() {
		
		if (xsdTypeForEnums == null) {
			String convertEnumValuesTo = context.getConversionParams().getParamValue(Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO);
			switch (convertEnumValuesTo) {
			case StringParam.VALUE_AUTO:
			case Ifc2RdfConversionParams.VALUE_NAMED_INDIVIDUAL:
				xsdTypeForEnums = OWL2.NamedIndividual;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_STRING:
				xsdTypeForEnums = XSD.xstring;
				break;
			default:
				throw new IllegalArgumentException("Invalid value of option " + Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO);
			}
		}
		return xsdTypeForEnums;
		
	}
	
	/**
	 * Returns the equivalent XSD datatype for IFC types: REAL and NUMBER
	 * @return
	 */
	protected Resource getXsdTypeForDoubles() {
		
		if (xsdTypeForDoubles == null) {
			String convertDoubleValuesTo = context.getConversionParams().getParamValue(Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO);
			switch (convertDoubleValuesTo) {
			case StringParam.VALUE_AUTO:
			case Ifc2RdfConversionParams.VALUE_AUTO_MOST_SUPPORTED:
			case Ifc2RdfConversionParams.VALUE_XSD_DECIMAL:
				xsdTypeForDoubles = XSD.decimal;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_DOUBLE:
				xsdTypeForDoubles = XSD.xdouble;
				break;
			case Ifc2RdfConversionParams.VALUE_OWL_REAL:
				xsdTypeForDoubles = RdfVocabulary.OWL.real;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_STRING:
				xsdTypeForDoubles = XSD.xstring;
				break;
			case Ifc2RdfConversionParams.VALUE_AUTO_MOST_EFFICIENT:
				List<Resource> preferredTypes = Arrays.asList(XSD.xdouble, RdfVocabulary.OWL.real, XSD.decimal);				
				xsdTypeForDoubles = context.getOwlProfileList().getFirstSupportedType(preferredTypes);						
				break;
			default:
				throw new IllegalArgumentException("Invalid value of option " + Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO);
			}
		}
		return xsdTypeForDoubles;		
		
	}
	
	/**
	 * Returns an RDF property with name in format hasXXX, where XXX is the name of the original type (e.g.: hasReal, hasNumber, hasLogical, etc.)  
	 * @param baseTypeInfo
	 * @return
	 */
	protected Property getHasXXXProperty(IfcTypeInfo baseTypeInfo) {
		IfcTypeEnum valueType = baseTypeInfo.getValueTypes().iterator().next();
		Property hasXXXProperty = mapOf_Type_hasXXXProperty.get(valueType);
		if (hasXXXProperty == null) {
			String valueTypeName = valueType.toString();			
			String propertyName = String.format("has%s%s", valueTypeName.substring(0, 1).toUpperCase(), valueTypeName.substring(1).toLowerCase());
			hasXXXProperty = RdfVocabulary.DEFAULT_MODEL.createProperty(Ifc2RdfVocabulary.EXPRESS.getBaseUri() + propertyName);
			mapOf_Type_hasXXXProperty.put(valueType, hasXXXProperty);
		}
		return hasXXXProperty;
	}	
	

	/**
	 * Creates a URI in EXPRESS namespace
	 * @param name
	 * @return
	 */
	protected String formatExpressOntologyName(String name) {
		return Ifc2RdfVocabulary.EXPRESS.getBaseUri() + name;
	}
	

	/**
	 * Creates a URI in the ontology namespace 
	 * @param name
	 * @return
	 */
	protected String formatOntologyName(String name) {
		return ontologyNamespaceUri + name;
	}
	
	
	/**
	 * Creates a URI for the type
	 * @param typeInfo
	 * @return
	 */
	protected String formatTypeName(IfcTypeInfo typeInfo) {
		if (typeInfo instanceof IfcLiteralTypeInfo || typeInfo instanceof IfcLogicalTypeInfo) {
			return formatExpressOntologyName(typeInfo.getName());			
		} else {
			return formatOntologyName(typeInfo.getName());
		}
	}
	
	
	/**
	 * Creates a URI for the attribute 
	 * @param attributeInfo
	 * @return
	 */
	protected String formatAttributeName(IfcAttributeInfo attributeInfo) {
		return formatOntologyName(attributeInfo.getUniqueName()); 
	}
	
	
	/**
	 * Creates a URI in the IFC data namespace 
	 * @param name
	 * @return
	 */
	public String formatModelName(String name) {
		return modelNamespaceUri + name;
	}
	
	
	/**
	 * Creates a slot class' name for the list of the class  
	 * @param className
	 * @return
	 */
	protected String formatSlotClassName(String className) {
		return className + "_Slot";
	}

}
