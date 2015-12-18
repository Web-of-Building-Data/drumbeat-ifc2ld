package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.*;

import org.apache.commons.lang3.NotImplementedException;

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

import fi.aalto.cs.drumbeat.common.params.StringParam;
import fi.aalto.cs.drumbeat.ifc.common.IfcException;
import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.LogicalEnum;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcCollectionTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcEnumerationTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcLiteralTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcLogicalTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSelectTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcTypeEnum;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcUniqueKeyInfo;
import fi.aalto.cs.drumbeat.rdf.OwlProfileList;
import fi.aalto.cs.drumbeat.rdf.RdfVocabulary;


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
	private Resource baseTypeForDoubles;
	private Resource baseTypeForBooleans;
	private Resource baseTypeForEnums;
	private Map<IfcTypeEnum, Property> mapOf_Type_hasXXXProperty;
	private OwlProfileList owlProfileList;	
	
	protected Ifc2RdfExporterBase(Ifc2RdfConversionContext context, Model jenaModel) {
		this.jenaModel = jenaModel;
		this.context = context;
		this.mapOf_Type_hasXXXProperty = new HashMap<>();
		this.owlProfileList = context.getOwlProfileList();		
	}
	
	
	
	/**
	 * Converts an IFC literal value to an RDF resource 
	 * @param literalValue
	 * @return
	 */
	public RDFNode convertLiteralToNode(IfcLiteralValue literalValue) {
		
		IfcTypeInfo typeInfo = literalValue.getType();
		assert(typeInfo != null) : literalValue;

		if (typeInfo instanceof IfcDefinedTypeInfo || typeInfo instanceof IfcLiteralTypeInfo) {
			
			return convertValueOfLiteralOrDefinedType(typeInfo, literalValue.getValue());
			
			
		} else if (typeInfo instanceof IfcEnumerationTypeInfo) {
			
			return convertEnumValue((IfcEnumerationTypeInfo)typeInfo, (String)literalValue.getValue());			
			
		} else if (typeInfo instanceof IfcLogicalTypeInfo) {
			
			assert(literalValue.getValue() instanceof LogicalEnum);
			return convertBooleanValue((IfcLogicalTypeInfo)typeInfo, (LogicalEnum)literalValue.getValue());
		
		} else {
			
			throw new RuntimeException(String.format("Invalid literal value type: %s (%s)", typeInfo, typeInfo.getClass()));			
		}
		
	}
	
	
	 protected void exportCollectionTypeInfo(IfcCollectionTypeInfo typeInfo) {
		 
		 final String convertCollectionsTo = context.getConversionParams().convertCollectionsTo();
		 
		 switch (convertCollectionsTo) {
		 case Ifc2RdfConversionParams.VALUE_DRUMMOND_LIST:
			 exportCollectionTypeInfoToDrummondList(typeInfo);
			 break;
		 default:
			 throw new NotImplementedException("Unknown collection type");
		 }
	
	 }
	 
	 private void exportCollectionTypeInfoToDrummondList(IfcCollectionTypeInfo typeInfo) {
		 if (typeInfo.isSorted()) {
				
			 Resource listTypeResource = createUriResource(formatTypeName(typeInfo));
			 jenaModel.add(listTypeResource, RDF.type, OWL.Class);	
			 jenaModel.add(listTypeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.List);
			 
			 exportPropertyDefinition(Ifc2RdfVocabulary.EXPRESS.hasNext.inModel(jenaModel),
					 listTypeResource, listTypeResource, true, 1, 1);
			 
			 Resource emptyListTypeResource = createUriResource(formatTypeName(typeInfo).replace("List", "EmptyList"));
			 jenaModel.add(emptyListTypeResource, RDF.type, OWL.Class);	
			 jenaModel.add(emptyListTypeResource, RDFS.subClassOf, listTypeResource);
			 jenaModel.add(listTypeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.EmptyList);
			 
			 
		 } else {
			 // TODO:
			 // throw new NotImplementedException(String.format("Unsorted list type: %s", typeInfo.getName()));			 
			 
		 }		 
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
	 * Returns an RDF property with name in format hasXXX, where XXX is the name of the original type (e.g.: hasReal, hasNumber, hasLogical, etc.)  
	 * @param baseTypeInfo
	 * @return
	 */
	protected Property getHasXXXProperty(IfcTypeEnum valueType) {
		Property hasXXXProperty = mapOf_Type_hasXXXProperty.get(valueType);
		if (hasXXXProperty == null) {
			String valueTypeName = valueType.toString();			
			String propertyName = String.format("has%s%s", valueTypeName.substring(0, 1).toUpperCase(), valueTypeName.substring(1).toLowerCase());
			hasXXXProperty = jenaModel.createProperty(Ifc2RdfVocabulary.EXPRESS.getBaseUri() + propertyName);
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
		if (context.getConversionParams().useLongAttributeName()) {
			return formatOntologyName(String.format("%s_%s", attributeInfo.getName(), attributeInfo.getEntityTypeInfo()));			
		} else {
			return formatOntologyName(attributeInfo.getName());
		}		
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

	
	
	
	//================================================================================
    // Logical types & values
    //================================================================================
	
	/**
	 * Returns the equivalent XSD datatype for IFC types: BOOLEAN and LOGICAL
	 * @return
	 */
	public Resource getBaseTypeForBooleans() {
		
		if (baseTypeForBooleans == null) {
			String convertBooleanValuesTo = context.getConversionParams().convertBooleansTo();
			switch (convertBooleanValuesTo) {
			case StringParam.VALUE_AUTO:
			case Ifc2RdfConversionParams.VALUE_NAMED_INDIVIDUAL:
				baseTypeForBooleans = OWL2.NamedIndividual;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_STRING:
				baseTypeForBooleans = XSD.xstring;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_BOOLEAN:
				baseTypeForBooleans = XSD.xboolean;
				break;
			default:
				throw new IllegalArgumentException("Invalid value of option " + Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO);
			}
		}
		return baseTypeForBooleans;		
	}
	
	
	private Resource convertBooleanValue(IfcTypeInfo typeInfo, LogicalEnum value) {
		
		Resource baseType = getBaseTypeForBooleans();
		if (baseType.equals(OWL2.NamedIndividual)) {
			
			return createUriResource(formatExpressOntologyName(value.toString()));
			
		} else {
			
			Resource resource = createAnonResource();				
			resource.addProperty(RDF.type, createUriResource(formatTypeName(typeInfo)));				
			
			Property property = getHasXXXProperty(IfcTypeEnum.LOGICAL);
			
			RDFNode valueNode;
			if (value == LogicalEnum.TRUE) {
				valueNode = jenaModel.createTypedLiteral("true", baseType.getURI());					
			} else if (value == LogicalEnum.FALSE) {
				valueNode = jenaModel.createTypedLiteral("false", baseType.getURI());					
			} else {
				valueNode = jenaModel.createTypedLiteral("unknown");					
			}				
						
			resource.addProperty(property, valueNode);
			
			return resource;
		}
		
	}
	
	
	
	
	//================================================================================
    // Enum types & values
    //================================================================================
	
	/**
	 * Returns the equivalent XSD datatype for IFC types: ENUM and LOGICAL
	 * @return
	 */
	private Resource getBaseTypeForEnums() {
		
		if (baseTypeForEnums == null) {
			String convertEnumValuesTo = (String)context.getConversionParams().getParamValue(Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO);
			switch (convertEnumValuesTo) {
			case StringParam.VALUE_AUTO:
			case Ifc2RdfConversionParams.VALUE_NAMED_INDIVIDUAL:
				baseTypeForEnums = OWL2.NamedIndividual;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_STRING:
				baseTypeForEnums = XSD.xstring;
				break;
			default:
				throw new IllegalArgumentException("Invalid value of option " + Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO);
			}
		}
		return baseTypeForEnums;
		
	}
	
	
	private Resource convertEnumValue(IfcEnumerationTypeInfo typeInfo, String value) {

		Resource baseType = getBaseTypeForEnums();
		if (baseType.equals(OWL2.NamedIndividual)) {			
			return createUriResource(formatOntologyName(value));			
		} else {			
			Resource resource = createAnonResource();
			resource.addLiteral(RDF.type, jenaModel);
			resource.addLiteral(Ifc2RdfVocabulary.EXPRESS.hasValue, baseType.getURI());
			return resource;

//			return jenaModel.createTypedLiteral(value, baseType.getURI());
		}		
		
	}
	
	protected Resource exportEnumType(IfcEnumerationTypeInfo typeInfo) {
		
		String typeUri = formatTypeName(typeInfo);
		Resource typeResource = createUriResource(typeUri);
		typeResource.addProperty(RDF.type, OWL.Class);
		typeResource.addProperty(RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Enum);		

		List<String> enumValues = typeInfo.getValues(); 
		List<RDFNode> enumValueNodes = new ArrayList<>();

		for (String value : enumValues) {
			enumValueNodes.add(convertEnumValue(typeInfo, value));
		}
		
//		assert(enumValueNodes.size() > 1) : String.format("Type %s has only 1 enum value", typeInfo);
		
		final boolean enumerationIsSupported = owlProfileList.supportsStatement(OWL.oneOf, RdfVocabulary.DUMP_URI_LIST);	
				
		if (enumerationIsSupported) {
			
			Resource equivalentTypeResource = createAnonResource();
			jenaModel.add(typeResource, OWL.equivalentClass, equivalentTypeResource);

			RDFList rdfList = createList(enumValueNodes);			
			jenaModel.add(equivalentTypeResource, OWL.oneOf, rdfList);
			
		} else {
			
			Resource baseType = getBaseTypeForEnums();	
			
			if (baseType.equals(OWL2.NamedIndividual)) {
				enumValueNodes.stream().forEach(node ->
				((Resource)node).addProperty(RDF.type, typeResource));			
			}
		}
		
		return typeResource;
		
	}

	
	
	
	//================================================================================
    // Literal types & values
    //================================================================================
	
	/**
	 * Returns the equivalent XSD datatype of an IFC literal type
	 * @param typeInfo
	 * @return
	 */
	public Resource getBaseTypeForLiterals(IfcLiteralTypeInfo typeInfo) {
		
		switch (typeInfo.getName()) {
		
		case IfcVocabulary.TypeNames.BINARY:			
//			return XSD.nonNegativeInteger;
			return XSD.hexBinary;
			
		case IfcVocabulary.TypeNames.INTEGER:
			return XSD.integer;
			
		case IfcVocabulary.TypeNames.NUMBER:
		case IfcVocabulary.TypeNames.REAL:
			return getBaseTypeForDoubles();
			
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
	 * Returns the equivalent XSD datatype for IFC types: REAL and NUMBER
	 * @return
	 */
	private Resource getBaseTypeForDoubles() {
		
		if (baseTypeForDoubles == null) {
			String convertDoubleValuesTo = (String)context.getConversionParams().getParamValue(Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO);
			switch (convertDoubleValuesTo) {
			case StringParam.VALUE_AUTO:
			case Ifc2RdfConversionParams.VALUE_AUTO_MOST_SUPPORTED:
			case Ifc2RdfConversionParams.VALUE_XSD_DECIMAL:
				baseTypeForDoubles = XSD.decimal;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_DOUBLE:
				baseTypeForDoubles = XSD.xdouble;
				break;
			case Ifc2RdfConversionParams.VALUE_OWL_REAL:
				baseTypeForDoubles = RdfVocabulary.OWL.real;
				break;
			case Ifc2RdfConversionParams.VALUE_XSD_STRING:
				baseTypeForDoubles = XSD.xstring;
				break;
			case Ifc2RdfConversionParams.VALUE_AUTO_MOST_EFFICIENT:
				List<Resource> preferredTypes = Arrays.asList(XSD.xdouble, RdfVocabulary.OWL.real, XSD.decimal);
				baseTypeForDoubles = context.getOwlProfileList().getFirstSupportedDatatype(preferredTypes);						
				break;
			default:
				throw new IllegalArgumentException("Invalid value of option " + Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO);
			}
		}
		return baseTypeForDoubles;		
		
	}
	
	
	protected void exportLiteralTypeInfo(IfcLiteralTypeInfo typeInfo) {
		
		Resource typeResource = createUriResource(formatTypeName(typeInfo)); 
		
		typeResource.addProperty(RDF.type, OWL.Class);
		
		Property property = getHasXXXProperty(typeInfo.getValueType());
		
//		jenaModel.add(property, RDF.type, OWL.DatatypeProperty);
//		if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
//			jenaModel.add(property, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
//		}
		jenaModel.add(property, RDFS.subPropertyOf, Ifc2RdfVocabulary.EXPRESS.hasValue);
//		jenaModel.add(property, RDFS.domain, typeResource);

		Resource baseDataType = getBaseTypeForLiterals(typeInfo);
//		jenaModel.add(property, RDFS.range, baseDataType);
		
		// TODO: check if it is needed to export domains and ranges
		exportPropertyDefinition(property, typeResource, baseDataType, false, 1, 1);

//		if (owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {		
//			
//			writePropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.value, OWL.allValuesFrom, getXsdDataType((IfcLiteralTypeInfo)baseTypeInfo));
//		
//		}
		
	}
	
	protected void exportLogicalTypeInfo(IfcLogicalTypeInfo typeInfo) {

		String typeUri = formatExpressOntologyName(typeInfo.getName());
		Resource typeResource = createUriResource(typeUri);
		jenaModel.add(typeResource, RDF.type, OWL.Class);

		List<LogicalEnum> enumValues = typeInfo.getValues();
		List<RDFNode> enumValueNodes = new ArrayList<>();

		for (LogicalEnum value : enumValues) {
			enumValueNodes.add(createUriResource(formatExpressOntologyName(value.toString())));
		}

		final boolean enumerationIsSupported = owlProfileList.supportsStatement(OWL.oneOf, RdfVocabulary.DUMP_URI_LIST);

		if (enumerationIsSupported) {
			RDFList rdfList = createList(enumValueNodes);
			jenaModel.add(typeResource, OWL.oneOf, rdfList);
		} else { // if
					// (!context.isEnabledOption(Ifc2RdfConversionParamsEnum.ForceConvertLogicalValuesToString))
					// {
			enumValueNodes.stream().forEach(
					node -> jenaModel.add((Resource) node, RDF.type,
							typeResource));
		}
	}
	
	
	
	
	private Resource convertValueOfLiteralOrDefinedType(IfcTypeInfo typeInfo, Object value) {
		
		assert(typeInfo instanceof IfcLiteralTypeInfo || typeInfo instanceof IfcDefinedTypeInfo);
		
		RDFNode valueNode;
		IfcTypeEnum valueType = typeInfo.getValueTypes().iterator().next();
		
		if (valueType == IfcTypeEnum.STRING) {
			valueNode = jenaModel.createTypedLiteral((String)value);				
		} else if (valueType == IfcTypeEnum.GUID) {				
			valueNode = jenaModel.createTypedLiteral(value.toString());
		} else if (valueType == IfcTypeEnum.REAL || valueType == IfcTypeEnum.NUMBER) {				
			valueNode = jenaModel.createTypedLiteral((double)value, getBaseTypeForDoubles().getURI());				
		} else if (valueType == IfcTypeEnum.INTEGER) {				
			valueNode = jenaModel.createTypedLiteral((long)value);				
		} else if (valueType == IfcTypeEnum.LOGICAL) {
//			assert(typeInfo instanceof IfcLogicalTypeInfo) : typeInfo;
			assert(value instanceof LogicalEnum) : value;
			valueNode = convertBooleanValue(typeInfo, (LogicalEnum)value);
		} else {
			assert (valueType == IfcTypeEnum.DATETIME) : "Expected: valueType == IfcTypeEnum.DATETIME. Actual: valueType = " + valueType + ", " + typeInfo;
			valueNode = jenaModel.createTypedLiteral((Calendar)value);				
		}

		Resource resource = createAnonResource();
		resource.addProperty(RDF.type, createUriResource(formatTypeName(typeInfo)));
		
		Property hasXXXProperty = getHasXXXProperty(typeInfo.getValueTypes().iterator().next());
		resource.addProperty(hasXXXProperty, valueNode);
		
		return resource;
		
	}
	
	
	protected void exportDefinedTypeInfo(IfcDefinedTypeInfo typeInfo) {
		
		Resource typeResource = createUriResource(formatTypeName(typeInfo));
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		
		IfcTypeInfo baseTypeInfo = typeInfo.getSuperTypeInfo();
		assert(baseTypeInfo != null);
		
		if (baseTypeInfo instanceof IfcLiteralTypeInfo) {
			
			Resource superTypeResource = createUriResource(formatExpressOntologyName(baseTypeInfo.getName()));
			jenaModel.add(typeResource, RDFS.subClassOf, superTypeResource);			
			jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Defined);			

		} else if (baseTypeInfo instanceof IfcLogicalTypeInfo) {
			
			Resource superTypeResource = createUriResource(formatExpressOntologyName(baseTypeInfo.getName()));			
			Property property = getHasXXXProperty(baseTypeInfo.getValueTypes().iterator().next());
			
			exportPropertyDefinition(property, typeResource, superTypeResource, true, 1, 1);			
			
		} else { // subclass of another Defined class
			
			jenaModel.add(typeResource, RDFS.subClassOf, createUriResource(formatTypeName(baseTypeInfo)));			

		}		
		
	}
	
	protected void exportSelectTypeInfo(IfcSelectTypeInfo typeInfo) {

		Resource typeResource = createUriResource(formatTypeName(typeInfo));
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		jenaModel.add(typeResource, RDFS.subClassOf,
				Ifc2RdfVocabulary.EXPRESS.Select);

		List<String> subTypeNames = typeInfo.getSelectTypeInfoNames();
		List<Resource> subTypeResources = new ArrayList<>();
		for (String typeName : subTypeNames) {
			subTypeResources.add(createUriResource(formatOntologyName(typeName)));
		}

		final boolean unionIsSupported = owlProfileList.supportsStatement(
				OWL.unionOf, RdfVocabulary.DUMP_URI_LIST);

		if (unionIsSupported && subTypeResources.size() > 1) {
			RDFList rdfList = createList(subTypeResources);
			// See samples: [2, p.250]
			jenaModel.add(typeResource, OWL.unionOf, rdfList);
		} else {
			subTypeResources.stream().forEach(
					subTypeResource -> jenaModel.add(
							(Resource) subTypeResource, RDFS.subClassOf,
							typeResource));
		}

	}

	
	
	
	protected void exportEntityTypeInfo(IfcEntityTypeInfo typeInfo) throws IfcException {
			
		 Resource typeResource = createUriResource(formatTypeName(typeInfo));
		 jenaModel.add(typeResource, RDF.type, OWL.Class);
	
		 //
		 // OWL2 supports owl:disjointUnionOf
		 // See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F1:_DisjointUnion
		 //
		 List<IfcEntityTypeInfo> disjointClasses = null;
		
		 final boolean supportsDisjointUnionOf = owlProfileList.supportsStatement(OWL2.disjointUnionOf, RdfVocabulary.DUMP_URI_LIST);
		
		 if (typeInfo.isAbstractSuperType() && supportsDisjointUnionOf) {
			 List<IfcEntityTypeInfo> allSubtypeInfos = typeInfo.getSubTypeInfos();
			 if (allSubtypeInfos.size() > 1) { // OWL2.disjointUnionOf requires at least two members
				 List<RDFNode> allSubtypeResources = new ArrayList<>(allSubtypeInfos.size());
				 for (IfcEntityTypeInfo subTypeInfo : allSubtypeInfos) {
					 allSubtypeResources.add(createUriResource(formatTypeName(subTypeInfo)));
				 }
				 jenaModel.add(typeResource, OWL2.disjointUnionOf, createList(allSubtypeResources));
			 }
		 }
	
		 //
		 // write entity info
		 //
		 IfcEntityTypeInfo superTypeInfo = typeInfo.getSuperTypeInfo();
		 if (superTypeInfo != null) {
			 jenaModel.add(typeResource, RDFS.subClassOf,
			 createUriResource(formatTypeName(superTypeInfo)));
			
			 if (!superTypeInfo.isAbstractSuperType() || !supportsDisjointUnionOf) {
			
			 List<IfcEntityTypeInfo> allSubtypeInfos = superTypeInfo.getSubTypeInfos();
	
				if (allSubtypeInfos.size() > 1) {
	
					int indexOfCurrentType = allSubtypeInfos.indexOf(typeInfo);
					
					final boolean supportDisjointWithList = owlProfileList.supportsStatement(OWL.disjointWith, RdfVocabulary.DUMP_URI_LIST); 
	
					if (allSubtypeInfos.size() > 2 && supportDisjointWithList) {
						//
						// OWL2 allow object of property "owl:disjointWith" to
						// be rdf:list
						// All classes will be pairwise disjoint
						// See:
						// http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
						//
						disjointClasses = allSubtypeInfos;
	
					} else {
						final boolean supportDisjointWithSingleClass = owlProfileList.supportsStatement(OWL.disjointWith, RdfVocabulary.DUMP_URI_1); 
						// context.getOwlVersion() < OwlProfile.OWL_VERSION_2_0
						
						if (supportDisjointWithSingleClass) {
							
							//
							// OWL1 doesn't allow object of property
							// "owl:disjointWith" to be rdf:list
							// See: http://www.w3.org/TR/owl-ref/#disjointWith-def
							//
							if (indexOfCurrentType + 1 < allSubtypeInfos.size()) {
		
								for (int i = indexOfCurrentType + 1; i < allSubtypeInfos.size(); ++i) {
									jenaModel.add(typeResource, OWL.disjointWith, createUriResource(formatTypeName(allSubtypeInfos.get(i))));
								}
		
							}							
							
						}
						
					}
	
				}
	
			}
	
		} else { // superTypeInfo == null
			jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Entity);
		}

		//
		// write unique keys
		//
		List<IfcUniqueKeyInfo> uniqueKeyInfos = typeInfo.getUniqueKeyInfos();
		
		final boolean supportHasKey = owlProfileList.supportsStatement(OWL2.hasKey, RdfVocabulary.DUMP_URI_LIST); 
		
		if (uniqueKeyInfos != null && supportHasKey) {
			for (IfcUniqueKeyInfo uniqueKeyInfo : uniqueKeyInfos) {
				List<Resource> attributeResources = new ArrayList<>();
				for (IfcAttributeInfo attributeInfo : uniqueKeyInfo.getAttributeInfos()) {
					attributeResources.add(createUriResource(formatAttributeName(attributeInfo)));
				}
				jenaModel.add(typeResource, OWL2.hasKey, createList(attributeResources));
			}
		}
	
	
		//
		// add attribute info to attribute info map
		//
		//if (context.isEnabledOption(Ifc2RdfConversionParamsEnum.ExportProperties))
		{
			List<IfcAttributeInfo> attributeInfos = typeInfo.getAttributeInfos();
			if (attributeInfos != null) {
				for (IfcAttributeInfo attributeInfo : attributeInfos) {
					Property property = jenaModel.createProperty(formatAttributeName(attributeInfo));
					
					jenaModel.add(property, RDF.type, Ifc2RdfVocabulary.EXPRESS.EntityProperty);
					
					// TODO: implement case when range is an RDFList 
					Resource range = createUriResource(formatTypeName(attributeInfo.getAttributeTypeInfo()));
					
					// TODO: add property cardinality
					exportPropertyDefinition(property, typeResource, range, true, null, null);
				}
			}
		}
	
		 
		if (disjointClasses != null) {
			 //
			 // OWL2 allow object of property "owl:disjointWith" to be rdf:list
			 // All classes will be pairwise disjoint
			 // See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
			 //
		
			 //adapter.exportEmptyLine();
			
			 Resource blankNode = createAnonResource();
			 jenaModel.add(blankNode, RDF.type, OWL2.AllDisjointClasses);
			
			 List<Resource> disjointClassResources = new ArrayList<>();
			 for (IfcTypeInfo disjointClassTypeInfo : disjointClasses) {
				 disjointClassResources.add(createUriResource(formatTypeName(disjointClassTypeInfo)));
			 }
			
			 jenaModel.add(blankNode, OWL2.members, createList(disjointClassResources));
		 }
		
	 }
	
	
	/*****************************************/
	
	private void exportPropertyDefinition(
			Property property,
			Resource domain,
			Resource range,
			boolean isObjectProperty,
			Integer min,
			Integer max) {
		
		property.addProperty(RDF.type, isObjectProperty ? OWL.ObjectProperty : OWL.DatatypeProperty);
		
		// TODO: double check if domains and ranges are really needed
		property.addProperty(RDFS.domain, domain);
		property.addProperty(RDFS.range, range);		

		if (max != null && max == 1 && owlProfileList.supportsStatement(RDF.type, OWL.FunctionalProperty)) {			
			// TODO: detect when FunctionalDataProperty is supported
			property.addProperty(RDF.type, isObjectProperty ? OWL.FunctionalProperty : RdfVocabulary.OWL.FunctionalDataProperty);
		}

		
//		jenaModel.add(attributeResource, RDF.type, Ifc2RdfVocabulary.EXPRESS.EntityProperty);						

		if (owlProfileList.supportsStatement(RDF.type, OWL.Restriction)) {

			//
			// write constraint about property type
			//
			if (owlProfileList.supportsStatement(OWL.allValuesFrom, null)) {
				exportPropertyRestriction(domain, property, OWL.allValuesFrom, range);
			}
			
			RDFNode minNode = min != null ? jenaModel.createTypedLiteral(min) : null;
			RDFNode maxNode = max != null ? jenaModel.createTypedLiteral(max) : null;
			
			if (minNode != null) {
				if (minNode.equals(maxNode)) {
					if (owlProfileList.supportsStatement(OWL.cardinality, minNode)) {
						exportPropertyRestriction(domain, property, OWL.cardinality, minNode);
						minNode = null;
						maxNode = null;
					}
				} else {
					if (owlProfileList.supportsStatement(OWL.minCardinality, minNode)) {
						exportPropertyRestriction(domain, property, OWL.minCardinality, minNode);
						minNode = null;
					}					
				}
			}
			
			if (maxNode != null) {
				if (owlProfileList.supportsStatement(OWL.maxCardinality, maxNode)) {
					exportPropertyRestriction(domain, property, OWL.maxCardinality, maxNode);
					minNode = null;
				}
			}
		}		
	}
	

	private void exportPropertyRestriction(Resource classResource, Resource propertyResource, Property restrictionProperty, RDFNode propertyValue) {
		Resource baseTypeResource = createAnonResource();
		jenaModel.add(baseTypeResource, RDF.type, OWL.Restriction);
		jenaModel.add(baseTypeResource, OWL.onProperty, propertyResource);
		jenaModel.add(baseTypeResource, restrictionProperty, propertyValue);
		
		jenaModel.add(classResource, RDFS.subClassOf, baseTypeResource);		
	}	

	
	
	

}
