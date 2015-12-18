package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld;

/**
 * Followed:
 *    [1] OWL template of Jyrki Oraskari (version 11.04.2012)
 *    [2] Allemang, Dean; Hendler, Jim. Semantic Web for the working ontologies: effective modeling in RDFS and OWL. - 2nd ed., 2011   
 */

import java.io.IOException;
import java.util.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.aalto.cs.drumbeat.ifc.common.IfcException;
import fi.aalto.cs.drumbeat.ifc.data.schema.*;
import fi.aalto.cs.drumbeat.rdf.RdfVocabulary;

/**
 * Exports IFC schema to RDF file
 * 
 * @author Nam
 * 
 */
public class Ifc2RdfSchemaExporter {

	private final Ifc2RdfConverter converter;
	private final IfcSchema ifcSchema;
	private final Ifc2RdfConversionContext context;
	private final Model jenaModel;

//	private Map<String, IfcCollectionTypeInfo> additionalCollectionTypeDictionary = new HashMap<>();

	public Ifc2RdfSchemaExporter(IfcSchema ifcSchema, Ifc2RdfConversionContext context, Model jenaModel) {
		this.ifcSchema = ifcSchema;
		this.context = context;
		this.jenaModel = jenaModel;		
		
		converter = new Ifc2RdfConverter(context, ifcSchema);
//		if (context.getOntologyNamespaceUriFormat() != null) {
//			converter.setIfcOntologyNamespaceUri(String.format(
//					context.getOntologyNamespaceUriFormat(),
//					ifcSchema.getVersion(), context.getName()));
//		}		
	}

	public Model export() throws IfcException, IOException {

		//
		// write header and prefixes
		//
		exportOntologyHeader();

		if (!context.getConversionParams().ignoreExpressSchema()) {
			exportExpressOntology();
		}

		if (!context.getConversionParams().ignoreIfcSchema()) {

			//
			// write non-entity types section
			//
			Collection<IfcNonEntityTypeInfo> nonEntityTypeInfos = ifcSchema
					.getNonEntityTypeInfos();


			// enumeration types
			// adapter.startSection("ENUMERATION TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcEnumerationTypeInfo) {
					converter.convertEnumerationTypeInfo((IfcEnumerationTypeInfo) nonEntityTypeInfo, jenaModel);
					
				}
			}


			// defined types
			// adapter.startSection("DEFINED TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcDefinedTypeInfo) {
					converter.convertDefinedTypeInfo((IfcDefinedTypeInfo) nonEntityTypeInfo, jenaModel);					
				}
			}


			// select types
			// adapter.startSection("SELECT TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcSelectTypeInfo) {
					converter.convertSelectTypeInfo((IfcSelectTypeInfo) nonEntityTypeInfo, jenaModel);					
				}
			}


			// collection types
			//adapter.startSection("COLLECTION TYPES");
			for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
				if (nonEntityTypeInfo instanceof IfcCollectionTypeInfo) {
					// TODO: check if additional collection types must be exported
					converter.convertCollectionTypeInfo((IfcCollectionTypeInfo)nonEntityTypeInfo, jenaModel);					
				}
			}


			//
			// write entity types section
			//
			for (IfcEntityTypeInfo entityTypeInfo : ifcSchema.getEntityTypeInfos()) {
				converter.convertEntityTypeInfo(entityTypeInfo, jenaModel);			 
			}			 

		}

		return jenaModel;
	}

	private void exportOntologyHeader() {
		
		// define owl:
		jenaModel.setNsPrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());

		// define rdf:
		jenaModel.setNsPrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());

		// define rdfs:
		jenaModel.setNsPrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());

		// define xsd:
		jenaModel.setNsPrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());

		// define expr:
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.EXPRESS.BASE_PREFIX,
				Ifc2RdfVocabulary.EXPRESS.getBaseUri());

		if (!context.getConversionParams().ignoreIfcSchema()) {
			// define ifc:
			jenaModel.setNsPrefix(Ifc2RdfVocabulary.IFC.BASE_PREFIX,
					converter.getIfcOntologyNamespaceUri());
		}

		// 

		String conversionParamsString = context.getConversionParams()
				.toString().replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]").replaceAll(",", "\r\n\t\t\t");

		// TODO: Format ontology comment here
		conversionParamsString = String.format(
				"OWL profile: %s.\r\n\t\tConversion options: %s",
				context.getOwlProfileList().getOwlProfileIds(), conversionParamsString);

		// adapter.exportOntologyHeader(converter.getIfcOntologyNamespaceUri(), "1.0",
		// conversionParamsString);

		Resource ontology = jenaModel.createResource(converter.getIfcOntologyNamespaceUri());
		ontology.addProperty(RDF.type, OWL.Ontology);
		String version = "1.0";
		ontology.addProperty(OWL.versionInfo, String.format(
				"v%1$s %2$tY/%2$tm/%2$te %2$tH:%2$tM:%2$tS", version,
				new Date()));
		if (conversionParamsString != null) {
			// ontology.addProperty(RDFS.comment,
			// String.format("\"\"\"%s\"\"\"", comment));
			ontology.addProperty(RDFS.comment, conversionParamsString);
		}
		
	}
	
	private void exportExpressOntology() {

//		final boolean declareFunctionalProperties = context
//				.getOwlProfileList()
//				.supportsStatement(RDF.type, OWL.FunctionalProperty);

		// TODO: Generate literal and logical types automatically (not manually
		// as below)

		// simple types
		// adapter.startSection("SIMPLE TYPES");
		Collection<IfcNonEntityTypeInfo> nonEntityTypeInfos = ifcSchema
				.getNonEntityTypeInfos();
		for (IfcNonEntityTypeInfo nonEntityTypeInfo : nonEntityTypeInfos) {
			if (nonEntityTypeInfo instanceof IfcLiteralTypeInfo) {
				converter.convertLiteralTypeInfo((IfcLiteralTypeInfo) nonEntityTypeInfo, jenaModel);				
			} else if (nonEntityTypeInfo instanceof IfcLogicalTypeInfo) {
				converter.convertLogicalTypeInfo((IfcLogicalTypeInfo) nonEntityTypeInfo, jenaModel);
			}
		}
		// adapter.endSection();

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Enum, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Defined, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Select, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Entity, RDF.type, OWL.Class);

		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.LOGICAL, RDF.type,
		// OWL.Class);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.BOOLEAN, RDF.type,
		// OWL.Class);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.BOOLEAN, RDFS.subClassOf,
		// Ifc2RdfVocabulary.EXPRESS.LOGICAL);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.TRUE, RDF.type,
		// Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.FALSE, RDF.type,
		// Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.UNKNOWN, RDF.type,
		// Ifc2RdfVocabulary.EXPRESS.LOGICAL);
		//
		//
//		jenaModel
//				.add(Ifc2RdfVocabulary.EXPRESS.Collection, RDF.type, OWL.Class);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.List, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.EmptyList, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.EmptyList, RDFS.subClassOf, Ifc2RdfVocabulary.EXPRESS.List);
		
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasContent, RDF.type, OWL.ObjectProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasNext, RDF.type, OWL.ObjectProperty);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasSetItem, RDF.type, OWL.ObjectProperty);

		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasContent, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.List);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasNext, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.List);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasSetItem, RDFS.domain, Ifc2RdfVocabulary.EXPRESS.Set);
		

		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.slot, RDF.type,
		// OWL.ObjectProperty);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.slot, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.slot, RDFS.range,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDF.type,
		// OWL.ObjectProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDF.type,
		// OWL.FunctionalProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.isOrdered, RDFS.range,
		// Ifc2RdfVocabulary.EXPRESS.BOOLEAN);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDF.type,
		// OWL.DatatypeProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDF.type,
		// RdfVocabulary.OWL.FunctionalDataProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.size, RDFS.range,
		// XSD.integer);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDF.type,
		// OWL.DatatypeProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDF.type,
		// RdfVocabulary.OWL.FunctionalDataProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.startIndex, RDFS.range,
		// XSD.integer);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDF.type,
		// OWL.DatatypeProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDF.type,
		// RdfVocabulary.OWL.FunctionalDataProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.endIndex, RDFS.range,
		// XSD.integer);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.List, RDF.type, OWL.Class);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.List, RDFS.subClassOf,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Array, RDF.type, OWL.Class);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Array, RDFS.subClassOf,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Set, RDF.type, OWL.Class);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Set, RDFS.subClassOf,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Bag, RDF.type, OWL.Class);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Bag, RDFS.subClassOf,
		// Ifc2RdfVocabulary.EXPRESS.Collection);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.Slot, RDF.type, OWL.Class);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.item, RDF.type,
		// OWL.ObjectProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.item, RDF.type,
		// OWL.FunctionalProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.item, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDF.type,
		// OWL.DatatypeProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDF.type,
		// RdfVocabulary.OWL.FunctionalDataProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.index, RDFS.range,
		// XSD.integer);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDF.type,
		// OWL.ObjectProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDF.type,
		// OWL.FunctionalProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.previous, RDFS.range,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		//
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDF.type,
		// OWL.ObjectProperty);
		// if (declareFunctionalProperties) {
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDF.type,
		// OWL.FunctionalProperty);
		// }
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDFS.domain,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.next, RDFS.range,
		// Ifc2RdfVocabulary.EXPRESS.Slot);
		//
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.EntityProperty, RDF.type, OWL.Class);
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.EntityProperty, RDFS.subClassOf, OWL.ObjectProperty);
		//
		// // jenaModel.add(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDF.type,
		// // OWL.DatatypeProperty);
		// // jenaModel.add(Ifc2RdfVocabulary.EXPRESS.propertyIndex,
		// RDFS.domain,
		// // Ifc2RdfVocabulary.EXPRESS.EntityProperty);
		// // jenaModel.add(Ifc2RdfVocabulary.EXPRESS.propertyIndex, RDFS.range,
		// // XSD.integer);
		//
		jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasValue, RDF.type,
				OWL.DatatypeProperty);

		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasBinary, RDF.type,
		// OWL.DatatypeProperty);
		// // jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasBoolean, RDF.type,
		// // OWL.DatatypeProperty);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasInteger, RDF.type,
		// OWL.DatatypeProperty);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasLogical, RDF.type,
		// OWL.DatatypeProperty);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasNumber, RDF.type,
		// OWL.DatatypeProperty);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasReal, RDF.type,
		// OWL.DatatypeProperty);
		// jenaModel.add(Ifc2RdfVocabulary.EXPRESS.hasString, RDF.type,
		// OWL.DatatypeProperty);

	}

	// private void exportCollectionTypeInfo(IfcCollectionTypeInfo typeInfo) {
	//
	// Resource typeResource =
	// converter.createUriResource(converter.formatTypeName(typeInfo));
	// jenaModel.add(typeResource, RDF.type, OWL.Class);
	//
	// IfcCollectionKindEnum collectionKind = typeInfo.getCollectionKind();
	// IfcCollectionTypeInfo superCollectionTypeWithItemTypeAndNoCardinalities =
	// typeInfo.getSuperCollectionTypeWithItemTypeAndNoCardinalities();
	// IfcCollectionTypeInfo superCollectionTypeWithCardinalititesAndNoItemType
	// = typeInfo.getSuperCollectionTypeWithCardinalitiesAndNoItemType();
	//
	// if (superCollectionTypeWithItemTypeAndNoCardinalities == null) {
	//
	// if (superCollectionTypeWithCardinalititesAndNoItemType == null) { // both
	// supertypes are null
	// jenaModel.add(typeResource, RDFS.subClassOf,
	// Ifc2RdfVocabulary.EXPRESS.getCollectionClass(collectionKind));
	// }
	//
	// Resource slotClassResource =
	// createUriResource(converter.formatOntologyName(converter.formatSlotClassName(typeInfo.getName())));
	// jenaModel.add(slotClassResource, RDF.type, OWL.Class);
	// jenaModel.add(slotClassResource, RDFS.subClassOf,
	// Ifc2RdfVocabulary.EXPRESS.Slot);
	//
	// //
	// // write restriction on type of property olo:slot
	// //
	// if (typeInfo.getItemTypeInfo() != null &&
	// owlProfileList.supportsStatement(OWL.allValuesFrom, null)) {
	//
	// exportPropertyRestriction(typeResource, Ifc2RdfVocabulary.EXPRESS.slot,
	// OWL.allValuesFrom, slotClassResource);
	//
	// 
	//
	// exportPropertyRestriction(slotClassResource,
	// Ifc2RdfVocabulary.EXPRESS.item, OWL.allValuesFrom,
	// createUriResource(converter.formatTypeName(typeInfo.getItemTypeInfo())));
	// }
	//
	// } else {
	//
	// String superTypeName =
	// converter.formatTypeName(superCollectionTypeWithItemTypeAndNoCardinalities);
	// jenaModel.add(typeResource, RDFS.subClassOf,
	// createUriResource(superTypeName));
	// additionalCollectionTypeDictionary.put(superTypeName,
	// superCollectionTypeWithItemTypeAndNoCardinalities);
	//
	// }
	//
	// if (superCollectionTypeWithCardinalititesAndNoItemType == null) {
	//
	// // TODO: uncomment this part
	// // //
	// // // write restriction on type of property olo:slot
	// // //
	// // Cardinality cardinality = typeInfo.getCardinality();
	// // if (cardinality != null &&
	// //
	// context.isEnabledOption(Ifc2RdfConversionParamsEnum.ExportPropertyCardinalities)
	// &&
	// // owlProfileList.supportsRdfProperty(OWL.cardinality, null)) {
	// //
	// //
	// // int minCardinality = cardinality.getMinCardinality();
	// // int maxCardinality = cardinality.getMaxCardinality();
	// //
	// // if (minCardinality != maxCardinality) {
	// //
	// // if (minCardinality != Cardinality.UNBOUNDED) {
	// //
	// // exportPropertyRestriction(typeResource,
	// Ifc2RdfVocabulary.EXPRESS.slot, OWL.minCardinality,
	// // getJenaModel().createTypedLiteral(cardinality.getMinCardinality()));
	// //
	// //
	// // }
	// //
	// // if (maxCardinality != Cardinality.UNBOUNDED) {
	// //
	// // exportPropertyRestriction(typeResource,
	// Ifc2RdfVocabulary.EXPRESS.slot, OWL.maxCardinality,
	// // getJenaModel().createTypedLiteral(cardinality.getMaxCardinality()));
	// //
	// // }
	// //
	// // } else {
	// //
	// // exportPropertyRestriction(typeResource,
	// Ifc2RdfVocabulary.EXPRESS.slot, OWL.cardinality,
	// // getJenaModel().createTypedLiteral(cardinality.getMaxCardinality()));
	// //
	// // }
	// //
	// //
	// // 
	// //
	// // }
	//
	// } else {
	//
	// String superTypeName =
	// converter.formatTypeName(superCollectionTypeWithCardinalititesAndNoItemType);
	// jenaModel.add(typeResource, RDFS.subClassOf,
	// createUriResource(superTypeName));
	// additionalCollectionTypeDictionary.put(superTypeName,
	// superCollectionTypeWithCardinalititesAndNoItemType);
	//
	// }
	//
	// }

	// private void writeAdditionalListTypeInfos() {
	// if
	// (context.isEnabledOption(Ifc2RdfConversionParamsEnum.ForceConvertRdfListToOloOrderedList))
	// {
	// for (Entry<Resource, Cardinality> entry :
	// collectionSuperTypes.entrySet()) {
	// Resource oloCardinalityClassResource = entry.getKey();
	// Cardinality cardinality = entry.getValue();
	//
	// jenaModel.add(oloCardinalityClassResource, RDF.type, OWL.Class);
	//
	// if (cardinality != null && context.supportsRdfProperty(OWL.oneOf,
	// OwlProfile.RdfTripleObjectTypeEnum.DataList)) {
	//
	// int min = cardinality.getMin();
	// int max = cardinality.getMax();
	//
	// if (max != Cardinality.UNBOUNDED) {
	// //
	// // write restriction on length of list as [ owl:oneOf (min, min + 1, ...,
	// max) ]
	// //
	// Resource blankNode1 = converter.createAnonResource();
	// jenaModel.add(blankNode1, RDF.type, RDFS.Datatype);
	// jenaModel.add(blankNode1, OWL.oneOf, getCardinalityValueList(min, max));
	//
	// jenaModel.add(oloCardinalityClassResource, RDF.type, OWL.Restriction);
	// jenaModel.add(oloCardinalityClassResource, OWL.onProperty,
	// RdfVocabulary.OLO.length);
	// jenaModel.add(oloCardinalityClassResource, OWL.allValuesFrom,
	// blankNode1);
	//
	// } else if (min != Cardinality.ZERO &&
	// context.supportsRdfProperty(OWL.complementOf,
	// OwlProfile.RdfTripleObjectTypeEnum.ObjectList)) {
	//
	// //
	// // write restriction on length of list as [ owl:compelmentOf [ owl:oneOf
	// (0, 1, ..., min - 1) ]
	// //
	// Resource blankNode2 = converter.createAnonResource();
	// jenaModel.add(blankNode2, RDF.type, RDFS.Datatype);
	// jenaModel.add(blankNode2, OWL.oneOf,
	// getCardinalityValueList(Cardinality.ZERO, min - 1));
	//
	// Resource blankNode1 = converter.createAnonResource();
	// jenaModel.add(blankNode1, RDF.type, OWL.Class);
	// jenaModel.add(blankNode1, OWL.complementOf, blankNode2);
	//
	// jenaModel.add(oloCardinalityClassResource, RDF.type, OWL.Restriction);
	// jenaModel.add(oloCardinalityClassResource, OWL.onProperty,
	// RdfVocabulary.OLO.length);
	// jenaModel.add(oloCardinalityClassResource, OWL.allValuesFrom,
	// blankNode1);
	//
	// }
	//
	// 
	//
	// } // for
	//
	// }
	// } else { //
	// !context.isEnabledOption(Ifc2RdfConversionParamsEnum.ConvertRdfListToOloOrderedList)
	//
	// // define ifc:EmptyList
	// Resource emptyListTypeUri =
	// createUriResource(converter.formatOntologyName(Ifc2RdfVocabulary.IFC.EMPTY_LIST));
	// jenaModel.add(emptyListTypeUri, RDF.type, OWL.Class);
	// jenaModel.add(emptyListTypeUri, RDFS.subClassOf, RDF.List);
	// 
	//
	// }
	// }

	// private void writeAttributeConstraint(Resource typeResource, Resource
	// attributeResource, Property constraintKindProperty, RDFNode
	// constraintValueTypeResource) {
	// Resource blankNode = converter.createAnonResource();
	// jenaModel.add(blankNode, RDF.type, OWL.Restriction);
	// jenaModel.add(blankNode, OWL.onProperty, attributeResource);
	// jenaModel.add(blankNode, constraintKindProperty,
	// constraintValueTypeResource);
	// jenaModel.add(typeResource, RDFS.subClassOf, blankNode);
	// }

	// private void writeAttributeInfo(String attributeName,
	// List<IfcAttributeInfo> attributeInfoList) {
	//
	// Set<String> domainTypeNames = new TreeSet<>();
	// Set<String> rangeTypeNames = new TreeSet<>();
	// EnumSet<IfcTypeEnum> valueTypes = EnumSet.noneOf(IfcTypeEnum.class);
	// boolean isFunctionalProperty = true;
	// boolean isInverseFunctionalProperty = true;
	//
	// if (!context.supportsRdfProperty(OWL.FunctionalProperty,
	// OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
	// isFunctionalProperty = false;
	// isInverseFunctionalProperty = false;
	// }
	//
	// for (IfcAttributeInfo attributeInfo : attributeInfoList) {
	// IfcEntityTypeInfo domainTypeInfo = attributeInfo.getEntityTypeInfo();
	// domainTypeNames.add(converter.formatTypeName(domainTypeInfo));
	//
	// IfcTypeInfo rangeTypeInfo = attributeInfo.getAttributeTypeInfo();
	// rangeTypeNames.add(converter.formatTypeName(rangeTypeInfo));
	// valueTypes.addAll(rangeTypeInfo.getValueTypes());
	//
	// isFunctionalProperty = isFunctionalProperty &&
	// attributeInfo.isFunctional();
	// isInverseFunctionalProperty = isInverseFunctionalProperty &&
	// attributeInfo.isInverseFunctional();
	// }
	//
	// //
	// // owl:DataProperty, owl:ObjectProperty, or rdf:Property
	// //
	// Resource attributeResource =
	// createUriResource(converter.formatOntologyName(attributeName));
	// if (valueTypes.size() == 1 && !valueTypes.contains(IfcTypeEnum.ENTITY)) {
	// jenaModel.add(attributeResource, RDF.type, OWL.DatatypeProperty);
	// } else {
	// jenaModel.add(attributeResource, RDF.type, OWL.ObjectProperty);
	// }
	//
	// //
	// // owl:FunctionalProperty
	// //
	// if (isFunctionalProperty) {
	// jenaModel.add(attributeResource, RDF.type, OWL.FunctionalProperty);
	// }
	//
	// //
	// // owl:InverseFunctionalProperty
	// //
	// if (isInverseFunctionalProperty) {
	// jenaModel.add(attributeResource, RDF.type,
	// OWL.InverseFunctionalProperty);
	// }
	//
	// //
	// // rdfs:domain, rdfs:range
	// //
	// if
	// (context.isEnabledOption(Ifc2RdfConversionParamsEnum.PrintPropertyDomainAndRange))
	// {
	//
	// if (domainTypeNames.size() == 1) {
	// jenaModel.add(attributeResource, RDFS.domain,
	// createUriResource(domainTypeNames.iterator().next()));
	// } else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
	// //
	// // In OWL Lite the value of rdfs:domain must be a class identifier.
	// // See: http://www.w3.org/TR/owl-ref/#domain-def
	// //
	//
	// List<RDFNode> domainTypeResources = new ArrayList<>();
	// for (String domainTypeName : domainTypeNames) {
	// domainTypeResources.add(createUriResource(domainTypeName));
	// }
	//
	// Resource domainTypeResource = converter.createAnonResource();
	// jenaModel.add(domainTypeResource, RDF.type, OWL.Class);
	// jenaModel.add(domainTypeResource, OWL.unionOf,
	// createList(domainTypeResources));
	// jenaModel.add(attributeResource, RDFS.domain, domainTypeResource);
	// }
	//
	// //
	// // Caution: rdfs:range should be used with care
	// // See: http://www.w3.org/TR/owl-ref/#range-def
	// //
	// if (rangeTypeNames.size() == 1) {
	// jenaModel.add(attributeResource, RDFS.range,
	// createUriResource(rangeTypeNames.iterator().next()));
	// } else if (context.allowPrintingPropertyDomainAndRangeAsUnion()) {
	// //
	// // In OWL Lite the only type of class descriptions allowed as objects of
	// rdfs:range are class names.
	// // See: http://www.w3.org/TR/owl-ref/#range-def
	// //
	// List<RDFNode> rangeTypeResources = new ArrayList<>();
	// for (String rangeTypeName : rangeTypeNames) {
	// rangeTypeResources.add(createUriResource(rangeTypeName));
	// }
	//
	// Resource rangeTypeResource = converter.createAnonResource();
	// jenaModel.add(rangeTypeResource, RDF.type, OWL.Class);
	// jenaModel.add(rangeTypeResource, OWL.unionOf,
	// createList(rangeTypeResources));
	// jenaModel.add(attributeResource, RDFS.range, rangeTypeResource);
	// }
	//
	// }
	//
	// if (attributeInfoList.size() == 1) {
	// //
	// // write owl:inverseOf
	// // See: http://www.w3.org/TR/owl-ref/#inverseOf-def
	// //
	// if (attributeInfoList.get(0) instanceof IfcInverseLinkInfo &&
	// context.supportsRdfProperty(OWL.inverseOf,
	// OwlProfile.RdfTripleObjectTypeEnum.SingleObject)) {
	// IfcInverseLinkInfo inverseLinkInfo =
	// (IfcInverseLinkInfo)attributeInfoList.get(0);
	// jenaModel.add(attributeResource, OWL.inverseOf,
	// createUriResource(converter.formatAttributeName(inverseLinkInfo.getOutgoingLinkInfo())));
	// }
	// } else {
	// if (avoidDuplicationOfPropertyNames) {
	// for (IfcAttributeInfo subAttributeInfo : attributeInfoList) {
	// 
	// List<IfcAttributeInfo> attributeInfoSubList = new ArrayList<>();
	// attributeInfoSubList.add(subAttributeInfo);
	// String subAttributeName = subAttributeInfo.getUniqueName();
	// writeAttributeInfo(subAttributeName, attributeInfoSubList);
	// jenaModel.add(createUriResource(converter.formatOntologyName(subAttributeName)),
	// RDFS.subPropertyOf, attributeResource);
	// }
	// }
	// }
	// }

	// private RDFList getCardinalityValueList(int min, int max) {
	// List<Literal> literals = new ArrayList<>();
	// for (int i = min; i <= max; ++i) {
	// literals.add(getJenaModel().createTypedLiteral(i));
	// }
	// return converter.createList(literals);
	// }

}