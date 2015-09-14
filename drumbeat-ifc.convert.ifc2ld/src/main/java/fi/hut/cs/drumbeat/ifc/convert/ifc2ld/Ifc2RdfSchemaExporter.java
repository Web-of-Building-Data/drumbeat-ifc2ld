package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

/**
 * Followed:
 *    [1] OWL template of Jyrki Oraskari (version 11.04.2012)
 *    [2] Allemang, Dean; Hendler, Jim. Semantic Web for the working ontologies: effective modeling in RDFS and OWL. - 2nd ed., 2011   
 */

import java.io.IOException;
import java.util.*;

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

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.schema.*;
import fi.hut.cs.drumbeat.rdf.OwlProfile;
import fi.hut.cs.drumbeat.rdf.OwlProfileList;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;
import fi.hut.cs.drumbeat.rdf.OwlProfile.RdfTripleObjectTypeEnum;


/**
 * Exports IFC schema to RDF file
 * 
 * @author Nam
 * 
 */
public class Ifc2RdfSchemaExporter extends Ifc2RdfExporterBase {
	
	private IfcSchema ifcSchema;
	private Ifc2RdfConversionContext context;
	private OwlProfileList owlProfileList;

	private Map<String, IfcCollectionTypeInfo> additionalCollectionTypeDictionary = new HashMap<>();
	
	public Ifc2RdfSchemaExporter(IfcSchema ifcSchema, Ifc2RdfConversionContext context, Model jenaModel) {
		super(context, jenaModel);
		this.ifcSchema = ifcSchema;
		this.context = context;
		this.owlProfileList = context.getOwlProfileList();
		
		if (context.getOntologyNamespaceFormat() != null) {		
			super.setOntologyNamespaceUri(
				String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName()));
		}
	}

	public Model export() throws IfcException, IOException {
		
		//
		// write header and prefixes
		//
//		adapter.startExport();
		
		// define owl:
		jenaModel.setNsPrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());

		// define rdf:
		jenaModel.setNsPrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());
		
		// define rdfs:
		jenaModel.setNsPrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());
		
		// define xsd:
		jenaModel.setNsPrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());
		
		// define expr:
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.EXPRESS.BASE_PREFIX, Ifc2RdfVocabulary.EXPRESS.getBaseUri());
		
		// define ifc:		
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.IFC.BASE_PREFIX, getOntologyNamespaceUri());
		
//		//adapter.exportEmptyLine();
		
		String conversionParamsString = context.getConversionParams().toString()
				.replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]")
				.replaceAll(",", "\r\n\t\t\t");
		
		// TODO: Format ontology comment here
		conversionParamsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				owlProfileList.getOwlProfileIds(),
				conversionParamsString); 
		
//		adapter.exportOntologyHeader(getOntologyNamespaceUri(), "1.0", conversionParamsString);
		
		Resource ontology = jenaModel.createResource(getOntologyNamespaceUri());
		ontology.addProperty(RDF.type, OWL.Ontology);
		String version = "1.0";
		ontology.addProperty(OWL.versionInfo, String.format("v%1$s %2$tY/%2$tm/%2$te %2$tH:%2$tM:%2$tS", version, new Date()));
		if (conversionParamsString != null) {
			//ontology.addProperty(RDFS.comment, String.format("\"\"\"%s\"\"\"", comment));
			ontology.addProperty(RDFS.comment, conversionParamsString);
		}
		
		
		exportExpressOntology();
		
		
		//
		// write non-entity types section
		//
		Collection<IfcNonEntityTypeInfo> nonEntityTypeInfos = ifcSchema.getNonEntityTypeInfos(); 

		// simple types
		//adapter.startSection("SIMPLE TYPES");
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcLiteralTypeInfo) {
				exportLiteralTypeInfo((IfcLiteralTypeInfo)nonEntityTypeInfo);
				//adapter.exportEmptyLine();
			} else if (nonEntityTypeInfo instanceof IfcLogicalTypeInfo) {
				exportLogicalTypeInfo((IfcLogicalTypeInfo)nonEntityTypeInfo);				
			}
		}
		//adapter.endSection();
		
		// enumeration types
		//adapter.startSection("ENUMERATION TYPES");
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcEnumerationTypeInfo) {
				exportEnumerationTypeInfo((IfcEnumerationTypeInfo)nonEntityTypeInfo);
				//adapter.exportEmptyLine();
			} 
		}
		//adapter.endSection();
		
		// defined types
		//adapter.startSection("DEFINED TYPES");
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcDefinedTypeInfo) {
				exportDefinedTypeInfo((IfcDefinedTypeInfo)nonEntityTypeInfo);
				//adapter.exportEmptyLine();
			}
		}
		//adapter.endSection();

		

		// select types
		//adapter.startSection("SELECT TYPES");
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcSelectTypeInfo) {
				exportSelectTypeInfo((IfcSelectTypeInfo)nonEntityTypeInfo);
				//adapter.exportEmptyLine();
			} 
		}
		//adapter.endSection();

		// collection types
		//adapter.startSection("COLLECTION TYPES");			
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcCollectionTypeInfo) {
				exportCollectionTypeInfo((IfcCollectionTypeInfo)nonEntityTypeInfo);
				//adapter.exportEmptyLine();
			} 
		}			
		//adapter.endSection();		
		

		//
		// write entity types section
		//
//		if (!context.isEnabledOption(Ifc2RdfConversionParamsEnum.IgnoreEntityTypes)) {
			//adapter.startSection("ENTITY TYPES");
			for (IfcEntityTypeInfo entityTypeInfo : ifcSchema.getEntityTypeInfos()) {
				exportEntityTypeInfo(entityTypeInfo);
				//adapter.exportEmptyLine();
			}
			//adapter.endSection();
//		}

		//
		// write entity types section
		//
//		if (!context.isEnabledOption(Ifc2RdfConversionParamsEnum.IgnoreCollectionTypes)) {
			//adapter.startSection("ADDITIONAL COLLECTION TYPES");
			for (IfcCollectionTypeInfo collectionTypeInfo : additionalCollectionTypeDictionary.values()) {
				exportCollectionTypeInfo(collectionTypeInfo);
				//adapter.exportEmptyLine();
			}
			//adapter.endSection();
//		}

		//adapter.endExport();
		
		return super.getJenaModel();

	}
	
	private void exportExpressOntology() {
		
		boolean declareFunctionalProperties = owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.LOGICAL, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.BOOLEAN, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.BOOLEAN, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.LOGICAL);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS .TRUE, RDF.type, Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.FALSE, RDF.type, Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.UNKNOWN, RDF.type, Ifc2RdfVocabulary.EXPRESS.LOGICAL);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Enum, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Defined, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Select, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Entity, RDF.type, OWL.Class);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Collection, RDF.type, OWL.Class);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.slot, RDF.type, OWL.ObjectProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.slot, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.slot, RDFS.range, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDF.type, OWL.FunctionalProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDFS.range, Ifc2RdfVocabulary.EXPRESS.BOOLEAN);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {		
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDFS.range, XSD.integer);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {		
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDFS.range, XSD.integer);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Collection);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDFS.range, XSD.integer);		
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.List, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.List, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Array, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Array, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Set, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Set, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Bag, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Bag, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Collection);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Slot, RDF.type, OWL.Class);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.item, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.item, RDF.type, OWL.FunctionalProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.item, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDF.type, OWL.DatatypeProperty);
		if (declareFunctionalProperties) {
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDFS.range, XSD.integer);		
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDF.type, OWL.FunctionalProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDFS.range, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDF.type, OWL.ObjectProperty);
		if (declareFunctionalProperties) {
			jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDF.type, OWL.FunctionalProperty);
		}
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Slot);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDFS.range, Ifc2RdfVocabulary.EXPRESS.Slot);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.EntityProperty, RDF.type, OWL.Class);
//		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.EntityProperty, RDFS.subClassOf, OWL.ObjectProperty);

//		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDF.type, OWL.DatatypeProperty);
//		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.EntityProperty);
//		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDFS.range, XSD.integer);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasValue, RDF.type, OWL.DatatypeProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasBinary, RDF.type, OWL.DatatypeProperty);
//		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasBoolean, RDF.type, OWL.DatatypeProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasInteger, RDF.type, OWL.DatatypeProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasLogical, RDF.type, OWL.DatatypeProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasNumber, RDF.type, OWL.DatatypeProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasReal, RDF.type, OWL.DatatypeProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasString, RDF.type, OWL.DatatypeProperty);
		
	}
	
	private void exportLiteralTypeInfo(IfcLiteralTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(formatTypeName(typeInfo)); 
//		jenaModel.add(typeResource, RDF.type, RDFS.Datatype);
//		jenaModel.add(typeResource, OWL.sameAs, super.getXsdDataType(typeInfo));
		
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		
		Resource xsdDataType = super.getXsdDataType(typeInfo);
		Property property = getHasXXXProperty(typeInfo);		
		jenaModel.add(property, RDF.type, OWL.DatatypeProperty);
		if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
			jenaModel.add(property, RDF.type, RdfVocabulary.OWL.FunctionalDataProperty);
		}
		jenaModel.add(property, RDFS.subPropertyOf, Ifc2RdfVocabulary.EXPRESS.hasValue);
		jenaModel.add(property, RDFS.domain, typeResource);
		jenaModel.add(property, RDFS.range, xsdDataType);
		
//		if (owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {		
//			
//			writePropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.value, OWL.allValuesFrom, super.getXsdDataType((IfcLiteralTypeInfo)baseTypeInfo));
//		
//		}
		
	}
	
	private void exportLogicalTypeInfo(IfcLogicalTypeInfo typeInfo) {
		
		String typeUri = super.formatExpressOntologyName(typeInfo.getName());
		Resource typeResource = createUriResource(typeUri);
		jenaModel.add(typeResource, RDF.type, OWL.Class);

		List<String> enumValues = typeInfo.getValues(); 
		List<RDFNode> enumValueNodes = new ArrayList<>();

		for (String value : enumValues) {
			enumValueNodes.add(super.createUriResource(super.formatExpressOntologyName(value)));
		}
		
		if (owlProfileList.supportsRdfProperty(OWL.oneOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany))) {			
			RDFList rdfList = super.createList(enumValueNodes);			
			jenaModel.add(typeResource, OWL.oneOf, rdfList);
		} else { // if (!context.isEnabledOption(Ifc2RdfConversionParamsEnum.ForceConvertLogicalValuesToString)) {
			enumValueNodes.stream().forEach(node ->
					jenaModel.add((Resource)node, RDF.type, typeResource));			
		}		
	}	

	private void exportEnumerationTypeInfo(IfcEnumerationTypeInfo typeInfo) {
		
		// TODO: Consider case of converting enums to strings		
		String typeUri = super.formatTypeName(typeInfo);
		Resource typeResource = createUriResource(typeUri);
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Enum);		

		List<String> enumValues = typeInfo.getValues(); 
		List<RDFNode> enumValueNodes = new ArrayList<>();

		for (String value : enumValues) {
			enumValueNodes.add(super.createUriResource(super.formatOntologyName(value)));
		}		
		
		if (owlProfileList.supportsRdfProperty(OWL.oneOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany))) {			
			Resource equivalentTypeResource = super.createAnonResource();
			jenaModel.add(typeResource, OWL.equivalentClass, equivalentTypeResource);

			RDFList rdfList = super.createList(enumValueNodes);			
			jenaModel.add(equivalentTypeResource, OWL.oneOf, rdfList);
		} else { // if (!context.isEnabledOption(Ifc2RdfConversionParamsEnum.ForceConvertEnumerationValuesToString)) {
			enumValueNodes.stream().forEach(node ->
					jenaModel.add((Resource)node, RDF.type, typeResource));			
		}	
		
	}

	private void exportSelectTypeInfo(IfcSelectTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Select);
		

		List<String> subTypeNames = typeInfo.getSelectTypeInfoNames();
		List<Resource> subTypeResources = new ArrayList<>();
		for (String typeName : subTypeNames) {
			subTypeResources.add(super.createUriResource(super.formatOntologyName(typeName)));
		}
		
		if (owlProfileList.supportsRdfProperty(OWL.unionOf, EnumSet.of(RdfTripleObjectTypeEnum.ZeroOrOneOrMany)) && subTypeResources.size() > 1) {			
			RDFList rdfList = super.createList(subTypeResources);			
			// See samples: [2, p.250]
			jenaModel.add(typeResource, OWL.unionOf, rdfList);
		} else {
			subTypeResources.stream().forEach(subTypeResource ->
					jenaModel.add((Resource)subTypeResource, RDFS.subClassOf, typeResource));			
		}
		
	}
	
	private void exportDefinedTypeInfo(IfcDefinedTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo));
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Defined);			
		
		IfcTypeInfo baseTypeInfo = typeInfo.getSuperTypeInfo();
		assert(baseTypeInfo != null);
		
		if (baseTypeInfo instanceof IfcLiteralTypeInfo) {
			
			Resource superTypeResource = super.createUriResource(formatExpressOntologyName(baseTypeInfo.getName()));
			jenaModel.add(typeResource, RDFS.subClassOf, superTypeResource);			
			
		} else if (baseTypeInfo instanceof IfcLogicalTypeInfo) {
			
			Resource superTypeResource = super.createUriResource(formatExpressOntologyName(baseTypeInfo.getName()));

			Property property = getHasXXXProperty(baseTypeInfo);		
			jenaModel.add(property, RDF.type, OWL.ObjectProperty);
			if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
				jenaModel.add(property, RDF.type, OWL.FunctionalProperty);
			}
			jenaModel.add(property, RDFS.domain, typeResource);
			jenaModel.add(property, RDFS.range, superTypeResource);
			
			
		} else {
			
			jenaModel.add(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(baseTypeInfo)));			

		}		
		
	}

	private void exportCollectionTypeInfo(IfcCollectionTypeInfo typeInfo) {
		
		Resource typeResource = super.createUriResource(super.formatTypeName(typeInfo)); 
		jenaModel.add(typeResource, RDF.type, OWL.Class);
		
		IfcCollectionKindEnum collectionKind = typeInfo.getCollectionKind();
		IfcCollectionTypeInfo superCollectionTypeWithItemTypeAndNoCardinalities = typeInfo.getSuperCollectionTypeWithItemTypeAndNoCardinalities();
		IfcCollectionTypeInfo superCollectionTypeWithCardinalititesAndNoItemType = typeInfo.getSuperCollectionTypeWithCardinalitiesAndNoItemType();
		
		if (superCollectionTypeWithItemTypeAndNoCardinalities == null) {
			
			if (superCollectionTypeWithCardinalititesAndNoItemType == null) { // both supertypes are null			
				jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.getCollectionClass(collectionKind));
			}			
			
			Resource slotClassResource = createUriResource(super.formatOntologyName(super.formatSlotClassName(typeInfo.getName())));
			jenaModel.add(slotClassResource, RDF.type, OWL.Class);
			jenaModel.add(slotClassResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Slot);
			
			//
			// write restriction on type of property olo:slot
			//
			if (typeInfo.getItemTypeInfo() != null && owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {
	
				exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.allValuesFrom, slotClassResource);
				
				//adapter.exportEmptyLine();
			
				exportPropertyRestriction(slotClassResource, Ifc2RdfVocabulary.EXPRESS.item, OWL.allValuesFrom,
						createUriResource(super.formatTypeName(typeInfo.getItemTypeInfo())));
			}
			
		} else {
			
			String superTypeName = super.formatTypeName(superCollectionTypeWithItemTypeAndNoCardinalities);			
			jenaModel.add(typeResource, RDFS.subClassOf, createUriResource(superTypeName));
			additionalCollectionTypeDictionary.put(superTypeName, superCollectionTypeWithItemTypeAndNoCardinalities);
			
		}
		
		if (superCollectionTypeWithCardinalititesAndNoItemType == null) {
			
// TODO: uncomment this part
//			//
//			// write restriction on type of property olo:slot
//			//
//			Cardinality cardinality = typeInfo.getCardinality();
//			if (cardinality != null &&
//					context.isEnabledOption(Ifc2RdfConversionParamsEnum.ExportPropertyCardinalities) &&
//					owlProfileList.supportsRdfProperty(OWL.cardinality, null)) {
//				
//
//				int minCardinality = cardinality.getMinCardinality();
//				int maxCardinality = cardinality.getMaxCardinality();				
//
//				if (minCardinality != maxCardinality) {
//					
//					if (minCardinality != Cardinality.UNBOUNDED) {
//						
//						exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.minCardinality,
//								getJenaModel().createTypedLiteral(cardinality.getMinCardinality()));
//						
//						
//					}
//					
//					if (maxCardinality != Cardinality.UNBOUNDED) {
//						
//						exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.maxCardinality,
//								getJenaModel().createTypedLiteral(cardinality.getMaxCardinality()));						
//						
//					}
//					
//				} else {
//					
//					exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot, OWL.cardinality,
//							getJenaModel().createTypedLiteral(cardinality.getMaxCardinality()));					
//					
//				}
//				
//				
//				//adapter.exportEmptyLine();
//			
//			}

		} else {
			
			String superTypeName = super.formatTypeName(superCollectionTypeWithCardinalititesAndNoItemType);			
			jenaModel.add(typeResource, RDFS.subClassOf, createUriResource(superTypeName));
			additionalCollectionTypeDictionary.put(superTypeName, superCollectionTypeWithCardinalititesAndNoItemType);

		}		

	}
	
//	private void writeAdditionalListTypeInfos() {
//		if (context.isEnabledOption(Ifc2RdfConversionParamsEnum.ForceConvertRdfListToOloOrderedList)) {
//			for (Entry<Resource, Cardinality> entry : collectionSuperTypes.entrySet()) {
//				Resource oloCardinalityClassResource = entry.getKey();
//				Cardinality cardinality = entry.getValue();
//				
//				jenaModel.add(oloCardinalityClassResource, RDF.type, OWL.Class);
//				
//				if (cardinality != null && context.supportsRdfProperty(OWL.oneOf, OwlProfile.RdfTripleObjectTypeEnum.DataList)) {
//					
//					int min = cardinality.getMin();
//					int max = cardinality.getMax();
//				
//					if (max != Cardinality.UNBOUNDED) {
//						//
//						// write restriction on length of list as [ owl:oneOf (min, min + 1, ..., max) ]
//						//
//						Resource blankNode1 = super.createAnonResource();
//						jenaModel.add(blankNode1, RDF.type, RDFS.Datatype);
//						jenaModel.add(blankNode1, OWL.oneOf, getCardinalityValueList(min, max));
//	
//						jenaModel.add(oloCardinalityClassResource, RDF.type, OWL.Restriction);
//						jenaModel.add(oloCardinalityClassResource, OWL.onProperty, RdfVocabulary.OLO.length);		
//						jenaModel.add(oloCardinalityClassResource, OWL.allValuesFrom, blankNode1);
//						
//					} else if (min != Cardinality.ZERO && context.supportsRdfProperty(OWL.complementOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
//						
//						//
//						// write restriction on length of list as [ owl:compelmentOf [ owl:oneOf (0, 1, ..., min - 1) ]
//						//
//						Resource blankNode2 = super.createAnonResource();
//						jenaModel.add(blankNode2, RDF.type, RDFS.Datatype);
//						jenaModel.add(blankNode2, OWL.oneOf, getCardinalityValueList(Cardinality.ZERO, min - 1));
//	
//						Resource blankNode1 = super.createAnonResource();
//						jenaModel.add(blankNode1, RDF.type, OWL.Class);
//						jenaModel.add(blankNode1, OWL.complementOf, blankNode2); 
//	
//						jenaModel.add(oloCardinalityClassResource, RDF.type, OWL.Restriction);
//						jenaModel.add(oloCardinalityClassResource, OWL.onProperty, RdfVocabulary.OLO.length);
//						jenaModel.add(oloCardinalityClassResource, OWL.allValuesFrom, blankNode1);
//						
//					}
//					
//					//adapter.exportEmptyLine();					
//					
//				} // for
//				
//			}
//		} else { // !context.isEnabledOption(Ifc2RdfConversionParamsEnum.ConvertRdfListToOloOrderedList)
//
//			// define ifc:EmptyList
//			Resource emptyListTypeUri = createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.EMPTY_LIST)); 
//			jenaModel.add(emptyListTypeUri, RDF.type, OWL.Class);
//			jenaModel.add(emptyListTypeUri, RDFS.subClassOf, RDF.List);
//			//adapter.exportEmptyLine();
//
//		}
//	}

	private void exportEntityTypeInfo(IfcEntityTypeInfo typeInfo) throws IOException, IfcException {
		
		Resource typeResource = createUriResource(super.formatTypeName(typeInfo));
		jenaModel.add(typeResource, RDF.type, OWL.Class);

		//
		// OWL2 supports owl:disjointUnionOf
		// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F1:_DisjointUnion
		//
		List<IfcEntityTypeInfo> disjointClasses = null;
		if (typeInfo.isAbstractSuperType() && owlProfileList.supportsRdfProperty(OWL2.disjointUnionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
			List<IfcEntityTypeInfo> allSubtypeInfos = typeInfo.getSubTypeInfos();
			if (allSubtypeInfos.size() > 1) { // OWL2.disjointUnionOf requires at least two members
				List<RDFNode> allSubtypeResources = new ArrayList<>(allSubtypeInfos.size());
				for (IfcEntityTypeInfo subTypeInfo : allSubtypeInfos) {
					allSubtypeResources.add(createUriResource(super.formatTypeName(subTypeInfo)));
				}
				jenaModel.add(typeResource, OWL2.disjointUnionOf, super.createList(allSubtypeResources));
			}
		}

		//
		// write entity info
		//
		IfcEntityTypeInfo superTypeInfo = typeInfo.getSuperTypeInfo();
		if (superTypeInfo != null) {
			jenaModel.add(typeResource, RDFS.subClassOf, createUriResource(super.formatTypeName(superTypeInfo)));
			
			if (!superTypeInfo.isAbstractSuperType() || !owlProfileList.supportsRdfProperty(OWL2.disjointUnionOf, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
				
				List<IfcEntityTypeInfo> allSubtypeInfos = superTypeInfo.getSubTypeInfos();
				
				if (allSubtypeInfos.size() > 1) {
					
					int indexOfCurrentType = allSubtypeInfos.indexOf(typeInfo);
					
					if (allSubtypeInfos.size() > 2 && owlProfileList.supportsRdfProperty(OWL.disjointWith, OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
						//
						// OWL2 allow object of property "owl:disjointWith" to be rdf:list
						// All classes will be pairwise disjoint
						// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
						//
						disjointClasses = allSubtypeInfos;
						
					} else if (owlProfileList.supportsRdfProperty(OWL.disjointWith, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) { // context.getOwlVersion() < OwlProfile.OWL_VERSION_2_0
						
						//
						// OWL1 doesn't allow object of property "owl:disjointWith" to be rdf:list
						// See: http://www.w3.org/TR/owl-ref/#disjointWith-def
						//
						if (indexOfCurrentType + 1 < allSubtypeInfos.size()) {

							for (int i = indexOfCurrentType + 1; i < allSubtypeInfos.size(); ++i) {
								jenaModel.add(typeResource, OWL.disjointWith, createUriResource(super.formatTypeName(allSubtypeInfos.get(i))));
							}
							
						}
					}
					
				}
				
			}
			
		} else {
			jenaModel.add(typeResource, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.Entity);			
		}
		
		//
		// write unique keys
		//
		List<IfcUniqueKeyInfo> uniqueKeyInfos = typeInfo.getUniqueKeyInfos();
		if (uniqueKeyInfos != null && owlProfileList.supportsRdfProperty(OWL2.hasKey, OwlProfile.RdfTripleObjectTypeEnum.ANY)) {
			for (IfcUniqueKeyInfo uniqueKeyInfo : uniqueKeyInfos) {
				List<Resource> attributeResources = new ArrayList<>();
				for (IfcAttributeInfo attributeInfo : uniqueKeyInfo.getAttributeInfos()) {
					attributeResources.add(super.createUriResource(super.formatAttributeName(attributeInfo)));
				}
				jenaModel.add(typeResource, OWL2.hasKey, super.createList(attributeResources));				
			}
		}
		
		//
		// add attribute info to attribute info map
		//
//		if (context.isEnabledOption(Ifc2RdfConversionParamsEnum.ExportProperties)) {
			List<IfcAttributeInfo> attributeInfos = typeInfo.getAttributeInfos();
			if (attributeInfos != null) {
				for (IfcAttributeInfo attributeInfo : attributeInfos) {
					Resource attributeResource = createUriResource(super.formatAttributeName(attributeInfo));
					
					jenaModel.add(attributeResource, RDF.type, OWL.ObjectProperty);						
					jenaModel.add(attributeResource, RDF.type, Ifc2RdfVocabulary.EXPRESS.EntityProperty);						
//					jenaModel.add(attributeResource, Ifc2RdfVocabulary.EXPRESS.propertyIndex, jenaModel.createTypedLiteral(attributeInfo.getAttributeIndex(), XSD.integer.getURI()) );						

					if (owlProfileList.supportsRdfProperty(OWL.FunctionalProperty, null)) {
						jenaModel.add(attributeResource, RDF.type, OWL.FunctionalProperty);
					}
					
					if (owlProfileList.supportsRdfProperty(OWL.Restriction, null)) {

						//
						// write constraint about property type
						//
						if (owlProfileList.supportsRdfProperty(OWL.allValuesFrom, null)) {
							exportPropertyRestriction(typeResource, attributeResource, OWL.allValuesFrom,
									createUriResource(super.formatTypeName(attributeInfo.getAttributeTypeInfo())));
						}
						
// TODO: Uncomment the following condition
//						if (context.isEnabledOption(Ifc2RdfConversionParamsEnum.ExportPropertyCardinalities) &&
//								owlProfileList.supportsRdfProperty(OWL.cardinality, null)) {
							
							// write attribute cardinality
	//							if (attributeInfo.isOptional()) {
	//								writePropertyRestriction(typeResource, attributeResource, OWL.minCardinality,
	//										getJenaModel().createTypedLiteral(0));
						
								exportPropertyRestriction(typeResource, attributeResource, OWL.maxCardinality,
										getJenaModel().createTypedLiteral(1));								
						
	//							} else {								
	//								writePropertyRestriction(typeResource, attributeResource, OWL.cardinality,
	//										getJenaModel().createTypedLiteral(1));
	//							}
							
//						}
					}
					
				}
			}
//		}
		
		
		if (disjointClasses != null) {
			//
			// OWL2 allow object of property "owl:disjointWith" to be rdf:list
			// All classes will be pairwise disjoint
			// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses
			//
			
			//adapter.exportEmptyLine();
			
			Resource blankNode = super.createAnonResource();
			jenaModel.add(blankNode, RDF.type, OWL2.AllDisjointClasses);
			
			List<Resource> disjointClassResources = new ArrayList<>();
			for (IfcTypeInfo disjointClassTypeInfo : disjointClasses) {
				disjointClassResources.add(super.createUriResource(super.formatTypeName(disjointClassTypeInfo)));
			}
			
			jenaModel.add(blankNode, OWL2.members, super.createList(disjointClassResources));			
		}

	}
	
//	private void writeAttributeConstraint(Resource typeResource, Resource attributeResource, Property constraintKindProperty, RDFNode constraintValueTypeResource) {		
//		Resource blankNode = super.createAnonResource();
//		jenaModel.add(blankNode, RDF.type, OWL.Restriction);
//		jenaModel.add(blankNode, OWL.onProperty, attributeResource);
//		jenaModel.add(blankNode, constraintKindProperty, constraintValueTypeResource);
//		jenaModel.add(typeResource, RDFS.subClassOf, blankNode);		
//	}

//	private void writeAttributeInfo(String attributeName, List<IfcAttributeInfo> attributeInfoList) {
//
//		Set<String> domainTypeNames = new TreeSet<>();
//		Set<String> rangeTypeNames = new TreeSet<>();
//		EnumSet<IfcTypeEnum> valueTypes = EnumSet.noneOf(IfcTypeEnum.class);
//		boolean isFunctionalProperty = true;
//		boolean isInverseFunctionalProperty = true;
//		
//		if (!context.supportsRdfProperty(OWL.FunctionalProperty, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
//			isFunctionalProperty = false;
//			isInverseFunctionalProperty = false;
//		}
//		
//		for (IfcAttributeInfo attributeInfo : attributeInfoList) {
//			IfcEntityTypeInfo domainTypeInfo = attributeInfo.getEntityTypeInfo();
//			domainTypeNames.add(super.formatTypeName(domainTypeInfo));
//			
//			IfcTypeInfo rangeTypeInfo = attributeInfo.getAttributeTypeInfo(); 
//			rangeTypeNames.add(super.formatTypeName(rangeTypeInfo));
//			valueTypes.addAll(rangeTypeInfo.getValueTypes());
//			
//			isFunctionalProperty = isFunctionalProperty && attributeInfo.isFunctional();
//			isInverseFunctionalProperty = isInverseFunctionalProperty && attributeInfo.isInverseFunctional();			
//		}
//		
//		//
//		// owl:DataProperty, owl:ObjectProperty, or rdf:Property
//		//
//		Resource attributeResource = createUriResource(super.formatOntologyName(attributeName));
//		if (valueTypes.size() == 1 && !valueTypes.contains(IfcTypeEnum.ENTITY)) {
//			jenaModel.add(attributeResource, RDF.type, OWL.DatatypeProperty);
//		} else {
//			jenaModel.add(attributeResource, RDF.type, OWL.ObjectProperty);
//		}
//
//		//
//		// owl:FunctionalProperty
//		//
//		if (isFunctionalProperty) {
//			jenaModel.add(attributeResource, RDF.type, OWL.FunctionalProperty);			
//		}
//		
//		//
//		// owl:InverseFunctionalProperty
//		//
//		if (isInverseFunctionalProperty) {
//			jenaModel.add(attributeResource, RDF.type, OWL.InverseFunctionalProperty);
//		}
//		
//		//
//		// rdfs:domain, rdfs:range
//		//
//		if (context.isEnabledOption(Ifc2RdfConversionParamsEnum.PrintPropertyDomainAndRange)) {
//		
//			if (domainTypeNames.size() == 1) {
//				jenaModel.add(attributeResource, RDFS.domain, createUriResource(domainTypeNames.iterator().next()));
//			} else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
//				//
//				// In OWL Lite the value of rdfs:domain must be a class identifier. 
//				// See: http://www.w3.org/TR/owl-ref/#domain-def
//				//
//				
//				List<RDFNode> domainTypeResources = new ArrayList<>();
//				for (String domainTypeName : domainTypeNames) {
//					domainTypeResources.add(createUriResource(domainTypeName));
//				}				
//				
//				Resource domainTypeResource = super.createAnonResource();
//				jenaModel.add(domainTypeResource, RDF.type, OWL.Class);
//				jenaModel.add(domainTypeResource, OWL.unionOf, createList(domainTypeResources));	
//				jenaModel.add(attributeResource, RDFS.domain, domainTypeResource);			
//			}
//	
//			//
//			// Caution: rdfs:range should be used with care
//			// See: http://www.w3.org/TR/owl-ref/#range-def
//			//
//			if (rangeTypeNames.size() == 1) {
//				jenaModel.add(attributeResource, RDFS.range, createUriResource(rangeTypeNames.iterator().next()));
//			} else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
//				//
//				// In OWL Lite the only type of class descriptions allowed as objects of rdfs:range are class names. 
//				// See: http://www.w3.org/TR/owl-ref/#range-def
//				//				
//				List<RDFNode> rangeTypeResources = new ArrayList<>();
//				for (String rangeTypeName : rangeTypeNames) {
//					rangeTypeResources.add(createUriResource(rangeTypeName));
//				}				
//				
//				Resource rangeTypeResource = super.createAnonResource();
//				jenaModel.add(rangeTypeResource, RDF.type, OWL.Class);
//				jenaModel.add(rangeTypeResource, OWL.unionOf, createList(rangeTypeResources));	
//				jenaModel.add(attributeResource, RDFS.range, rangeTypeResource);				
//			}
//			
//		}
//		
//		if (attributeInfoList.size() == 1) {
//			//
//			// write owl:inverseOf
//			// See: http://www.w3.org/TR/owl-ref/#inverseOf-def
//			//
//			if (attributeInfoList.get(0) instanceof IfcInverseLinkInfo && context.supportsRdfProperty(OWL.inverseOf, OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
//				IfcInverseLinkInfo inverseLinkInfo = (IfcInverseLinkInfo)attributeInfoList.get(0);
//				jenaModel.add(attributeResource, OWL.inverseOf, createUriResource(super.formatAttributeName(inverseLinkInfo.getOutgoingLinkInfo())));
//			}
//		} else {
//			if (avoidDuplicationOfPropertyNames) {
//				for (IfcAttributeInfo subAttributeInfo : attributeInfoList) {
//					//adapter.exportEmptyLine();
//					List<IfcAttributeInfo> attributeInfoSubList = new ArrayList<>();
//					attributeInfoSubList.add(subAttributeInfo);
//					String subAttributeName = subAttributeInfo.getUniqueName();
//					writeAttributeInfo(subAttributeName, attributeInfoSubList);
//					jenaModel.add(createUriResource(super.formatOntologyName(subAttributeName)), RDFS.subPropertyOf, attributeResource);
//				}
//			}
//		}
//	}
	
//	private RDFList getCardinalityValueList(int min, int max) {
//		List<Literal> literals = new ArrayList<>();
//		for (int i = min; i <= max; ++i) {
//			literals.add(getJenaModel().createTypedLiteral(i));
//		}
//		return super.createList(literals);
//	}	
	
	private void exportPropertyRestriction(Resource classResource, Resource propertyResource, Property restrictionProperty, RDFNode propertyValue) {
		Resource baseTypeResource = super.createAnonResource();
		jenaModel.add(baseTypeResource, RDF.type, OWL.Restriction);
		jenaModel.add(baseTypeResource, OWL.onProperty, propertyResource);
		jenaModel.add(baseTypeResource, restrictionProperty, propertyValue);
		
		jenaModel.add(classResource, RDFS.subClassOf, baseTypeResource);		
	}	

	
	
}