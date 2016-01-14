package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.aalto.cs.drumbeat.ifc.common.IfcException;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfVocabulary.EXPRESS;
import fi.aalto.cs.drumbeat.ifc.data.model.*;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcCollectionTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcInverseLinkInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.aalto.cs.drumbeat.rdf.OwlProfileList;
import fi.aalto.cs.drumbeat.rdf.RdfVocabulary;


public class Ifc2RdfModelExporter {
	
	private final IfcSchema ifcSchema;
	private final IfcModel ifcModel;
	private final Ifc2RdfConversionContext context;
	private final Model jenaModel;
	private final Ifc2RdfConverter converter;
	private final String modelNamespacePrefix;
	private final String modelNamespaceUri;
	
	private final OwlProfileList owlProfileList;
	
	public Ifc2RdfModelExporter(IfcModel ifcModel, Ifc2RdfConversionContext context, Model jenaModel) {
		
		this.ifcSchema = ifcModel.getSchema();
		this.ifcModel = ifcModel;		
		this.context = context;
		this.owlProfileList = context.getOwlProfileList();
		this.jenaModel = jenaModel;
		
		converter = new Ifc2RdfConverter(context, ifcSchema);
//		if (context.getOntologyNamespaceUriFormat() != null) {
//			converter.setIfcOntologyNamespaceUri(String.format(
//					context.getOntologyNamespaceUriFormat(),
//					ifcSchema.getVersion(), context.getName()));
//		}
		
		if (context.getModelNamespacePrefix() != null) {
			modelNamespacePrefix = context.getModelNamespacePrefix();
		} else {
			throw new IllegalArgumentException("Model's namespace prefix is undefined");
		}
		
		if (context.getModelNamespaceUriFormat() != null) {
			modelNamespaceUri = String.format(context.getModelNamespaceUriFormat(), ifcSchema.getVersion(), context.getName());
		} else {
			throw new IllegalArgumentException("Model's namespace URI format is undefined");
		}
		
		
//		String ontologyNamespaceUri = String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		
		// TODO: check URIs
//		converter.setOntologyNamespaceUri(ontologyNamespaceUri);		
//		
//		converter.setModelNamespacePrefix(modelNamespacePrefix);
//		converter.setModelNamespaceUri(modelNamespaceUri);		
	}
	
	public Model export() throws IfcException {
		
		//
		// write header and prefixes
		//
		exportOntologyHeader();
		
		jenaModel.setNsPrefix(modelNamespacePrefix, modelNamespaceUri);
		//adapter.exportEmptyLine();

		String conversionParamsString = context.getConversionParams().toString()
				.replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]")
				.replaceAll(",", "\r\n\t\t\t");
		
		conversionParamsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				owlProfileList.getOwlProfileIds(),
				conversionParamsString); 
		
		Resource ontology = jenaModel.createResource(converter.getIfcOntologyNamespaceUri());
		ontology.addProperty(RDF.type, OWL.Ontology);
		String version = "1.0";
		ontology.addProperty(OWL.versionInfo, String.format("v%1$s %2$tY/%2$tm/%2$te %2$tH:%2$tM:%2$tS", version, new Date()));
		if (conversionParamsString != null) {
			//ontology.addProperty(RDFS.comment, String.format("\"\"\"%s\"\"\"", comment));
			ontology.addProperty(RDFS.comment, conversionParamsString);
		}
		
		for (IfcEntity entity : ifcModel.getEntities()) {
			writeEntity(entity);
		}
		
		//adapter.endExport();
		
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
	
	
	private void writeEntity(IfcEntity entity) {
		if (entity.isDuplicated()) {
			return;
		}
		
		Resource entityResource = convertEntityToResource(entity);
		
		long childNodeCount = 1L;
		
		IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();		
		entityResource.addProperty(RDF.type, jenaModel.createResource(converter.formatTypeName(entityTypeInfo)));
		
		
		
		for (IfcLink link : entity.getOutgoingLinks()) {
			writeAttribute(entityResource, link, childNodeCount++);
		}
		
		for (IfcLiteralAttribute attribute : entity.getLiteralAttributes()) {
			writeAttribute(entityResource, attribute, childNodeCount++);
		}
		
		if ((Boolean)context.getConversionParams().getParam(Ifc2RdfConversionParams.PARAM_EXPORT_DEBUG_INFO).getValue()) {
		
//			if (entity.isLiteralValueContainer()) {
//				jenaModel.add(entityResource, RDF.type, jenaModel.createResource(
//						converter.formatOntologyName(Ifc2RdfVocabulary.IFC.LITERAL_VALUE_CONTAINER_ENTITY)));
//			}
			
//			if (entity.isSharedBlankNode()) {
//				jenaModel.add(
//						entityResource,
//						RDF.type,
//						jenaModel.createResource(converter.formatOntologyName(Ifc2RdfVocabulary.IFC.SUPER_ENTITY)));				
//			}		
//			
//			
//			if (entity.hasName()) {
//				String entityRawName = entity.getRawName();
//				if (!entityRawName.equals(entity.getName())) {
//					jenaModel.add(
//							entityResource,
//							jenaModel.createResource(converter.formatOntologyName(Ifc2RdfVocabulary.IFC.RAW_NAME)).as(Property.class),
//									jenaModel.createTypedLiteral(entityRawName));
//				}
//			}
			
			String debugMessage = entity.getDebugMessage();
			if (debugMessage != null) {
				jenaModel.add(
				entityResource,
				jenaModel.createResource(converter.formatIfcOntologyName(Ifc2RdfVocabulary.IFC.PROPERTY_DEBUG_MESSAGE)).as(Property.class),
						jenaModel.createTypedLiteral(debugMessage));				
			}
			
			jenaModel.add(
					entityResource,
					jenaModel.createResource(converter.formatIfcOntologyName(Ifc2RdfVocabulary.IFC.PROPERTY_LINE_NUMBER_PROPERTY)).as(Property.class),
					jenaModel.createTypedLiteral(entity.getLineNumber()));
		}		

		//adapter.exportEmptyLine();
	}
	
	private void writeAttribute(Resource entityResource, IfcAttribute attribute, long childNodeCount) {		
		IfcAttributeInfo attributeInfo = attribute.getAttributeInfo();
		Property attributeProperty = convertAttributeInfoToResource(attributeInfo);
		Property inverseAttributeProperty = null;
		if (attribute instanceof IfcLink) {
			IfcInverseLinkInfo inverseLinkInfo = ((IfcLink)attribute).getInverseLinkInfo();
			if (inverseLinkInfo != null) {				
				inverseAttributeProperty = convertAttributeInfoToResource(inverseLinkInfo);
			}
		}
		
		IfcValue value = attribute.getValue();
		List<RDFNode> valueNodes = convertValueToNode(value, attributeInfo.getAttributeTypeInfo(), entityResource, childNodeCount);
		
		for (RDFNode valueNode : valueNodes) {		
			jenaModel.add(entityResource, attributeProperty, valueNode);
			if (inverseAttributeProperty != null) {
				jenaModel.add((Resource)valueNode, inverseAttributeProperty, entityResource);
			}
		}
	}
	
	/**
	 * Converts any {@link IfcValue} value to {@link RDFNode}
	 * @param value
	 * @param typeInfo
	 * @return
	 */
	public RDFNode convertSingleValueToNode(IfcSingleValue value, IfcTypeInfo typeInfo, Resource entityResource, long childNodeCount) {
		if (typeInfo instanceof IfcCollectionTypeInfo) {
			throw new IllegalArgumentException("Expected non-collection type info: " + typeInfo);
		} else {
			if (value instanceof IfcEntity) {
				return convertEntityToResource((IfcEntity) value);
			} if (value instanceof IfcShortEntity) {
				return convertShortEntityToResource((IfcShortEntity) value, childNodeCount);				
			} else {
				assert(value instanceof IfcLiteralValue);
				return converter.convertLiteralValue((IfcLiteralValue) value, entityResource, childNodeCount, jenaModel);
			}
		}
	}
	
	public List<RDFNode> convertValueToNode(IfcValue value, IfcTypeInfo typeInfo, Resource entityResource, long childNodeCount) {
		if (value instanceof IfcSingleValue) {
			List<RDFNode> nodes = new ArrayList<>();
			nodes.add(convertSingleValueToNode((IfcSingleValue)value, typeInfo, entityResource, childNodeCount));
			return nodes;
		} else {
			assert(typeInfo instanceof IfcCollectionTypeInfo);
			return convertListToResource((IfcCollectionValue<?>) value, (IfcCollectionTypeInfo)typeInfo, entityResource, childNodeCount);		
		}
	}
	
	
	public List<RDFNode> convertListToResource(IfcCollectionValue<? extends IfcValue> listValue, IfcCollectionTypeInfo collectionTypeInfo,
			Resource parentResource, long childNodeCount)
	{		
		final String convertCollectionsTo = context.getConversionParams().convertCollectionsTo();		
		
		switch (convertCollectionsTo) {
		case Ifc2RdfConversionParams.VALUE_DRUMMOND_LIST:			
			return convertListToDrummondList(listValue, collectionTypeInfo, parentResource, childNodeCount);
			
		case Ifc2RdfConversionParams.VALUE_OLO_SIMILAR_LIST:
			return convertListToOloSimilarList(listValue, collectionTypeInfo, parentResource, childNodeCount);

		default:
			 throw new NotImplementedException("Unknown collection type");
		}
	}
		
	private List<RDFNode> convertListToDrummondList(IfcCollectionValue<? extends IfcValue> listValue, IfcCollectionTypeInfo collectionTypeInfo,
			Resource parentResource, long childNodeCount)
	{
		final boolean nameAllBlanksNodes = context.getConversionParams().nameAllBlankNodes();
		
		if (collectionTypeInfo.isSorted()) {
			
			Resource listTypeResource = jenaModel.createResource(converter.formatTypeName(collectionTypeInfo)); 
			Resource emptyListTypeResource = jenaModel.createResource(converter.formatTypeName(collectionTypeInfo).replace("List", "EmptyList"));
			IfcTypeInfo itemTypeInfo = collectionTypeInfo.getItemTypeInfo();			
			
			List<? extends IfcSingleValue> values = listValue.getSingleValues(); 

			int index = values.size();
			
			Resource currentListResource;
			assert(parentResource != null);
			if (nameAllBlanksNodes) {
				String currentResourceName = String.format("%s_%d_%d", parentResource.getURI(), childNodeCount, index);
				currentListResource = jenaModel.createResource(currentResourceName);			
			} else {
				currentListResource = jenaModel.createResource();
			}
			
			currentListResource.addProperty(RDF.type, emptyListTypeResource);
			
			while (index > 0) {
				index--;
				Resource nextListResource = currentListResource;
				if (nameAllBlanksNodes) {
					String currentResourceName = String.format("%s_%d_%d", parentResource.getURI(), childNodeCount, index);
					currentListResource = jenaModel.createResource(currentResourceName);			
				} else {
					currentListResource = jenaModel.createResource();
				}

				currentListResource.addProperty(RDF.type, listTypeResource);
				currentListResource.addProperty(Ifc2RdfVocabulary.EXPRESS.hasNext, nextListResource);
				
				IfcSingleValue value = values.get(index);
				RDFNode valueNode = convertSingleValueToNode(value, itemTypeInfo, currentListResource, 0);
				
				currentListResource.addProperty(Ifc2RdfVocabulary.EXPRESS.hasValue, valueNode);
			}

			List<RDFNode> nodes = new ArrayList<>();
			nodes.add(currentListResource);			
			return nodes;
			
		} else {
			List<RDFNode> nodes = new ArrayList<>();
			
			for (IfcSingleValue value : listValue.getSingleValues()) {
				RDFNode node = convertSingleValueToNode(value, collectionTypeInfo.getItemTypeInfo(), parentResource, childNodeCount);
				nodes.add(node);
			}
			
			return nodes;
		}

	}

	private List<RDFNode> convertListToOloSimilarList(IfcCollectionValue<? extends IfcValue> listValue, IfcCollectionTypeInfo collectionTypeInfo,
			Resource parentResource, long childNodeCount)
	{
		final boolean nameAllBlanksNodes = context.getConversionParams().nameAllBlankNodes();

		Resource listResource;
		if (nameAllBlanksNodes) {
			assert(parentResource != null);
			String listResourceName = String.format("%s_%d", parentResource.getURI(), childNodeCount);
			listResource = jenaModel.createResource(listResourceName);			
		} else {
			listResource = jenaModel.createResource();
		}
		
		IfcTypeInfo itemTypeInfo = collectionTypeInfo.getItemTypeInfo();
		
		List<RDFNode> nodeList = new ArrayList<>();
		long count = 1;
		for (IfcSingleValue value : listValue.getSingleValues()) {
			nodeList.add(convertSingleValueToNode(value, itemTypeInfo, listResource, count++));
		}
		
		int length = nodeList.size();
		
		
		Resource typeResource = jenaModel.createResource(converter.formatTypeName(collectionTypeInfo)); 
		listResource.addProperty(RDF.type, typeResource);
		listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.size, jenaModel.createTypedLiteral(length));
		listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.isOrdered,
				collectionTypeInfo.isSorted() ? Ifc2RdfVocabulary.EXPRESS.TRUE : Ifc2RdfVocabulary.EXPRESS.FALSE);
//		listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.itemType, jenaModel.createResource(converter.formatTypeName(itemTypeInfo)));
		
		for (int i = 0; i < nodeList.size(); ++i) {
			Resource slotResource;
			if (nameAllBlanksNodes) {
				String slotResourceName = String.format("%s_slot_%d", listResource.getLocalName(), i+1);
				slotResource = jenaModel.createResource(formatModelName(slotResourceName));
			} else {
				slotResource = jenaModel.createResource();
			}
			slotResource.addProperty(Ifc2RdfVocabulary.EXPRESS.index, jenaModel.createTypedLiteral(i + 1));
			slotResource.addProperty(Ifc2RdfVocabulary.EXPRESS.item, nodeList.get(i));
			listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.slot, slotResource);
		}
		
		List<RDFNode> nodes = new ArrayList<RDFNode>();
		nodes.add(listResource);
		return nodes;
	}
	
	public Resource convertEntityToResource(IfcEntity entity) {
		if (entity.hasName()) {
			return jenaModel.createResource(formatModelName(entity.getName()));
		} else {
			final boolean nameAllBlankNodes = context.getConversionParams().nameAllBlankNodes();
			String nodeName = String.format(Ifc2RdfVocabulary.IFC.BLANK_NODE_ENTITY_URI_FORMAT, entity.getLineNumber());
			if (nameAllBlankNodes) {
				return jenaModel.createResource(formatModelName(nodeName));				
			} else {
				return jenaModel.createResource(new AnonId(nodeName));				
			}
		}
	}
	
	public Resource convertShortEntityToResource(IfcShortEntity entity, long childNodeCount) {
		final boolean nameAllBlankNodes = context.getConversionParams().nameAllBlankNodes();
		String nodeName = String.format("%s_%s", entity.getTypeInfo(), entity.getValue());
		Resource entityResource;
		if (nameAllBlankNodes) {
			entityResource = jenaModel.createResource(formatModelName(nodeName));				
		} else {
			entityResource = jenaModel.createResource(new AnonId(nodeName));				
		}
		
		entityResource.addProperty(RDF.type, jenaModel.createResource(converter.formatTypeName(entity.getTypeInfo())));
		IfcLiteralValue value = entity.getValue();
		RDFNode valueNode = converter.convertLiteralValue((IfcLiteralValue) value, entityResource, childNodeCount, jenaModel);
		entityResource.addProperty(EXPRESS.hasValue, valueNode);
		return entityResource;
	}

	public Property convertAttributeInfoToResource(IfcAttributeInfo attributeInfo) {
		return jenaModel.createResource(converter.formatAttributeName(attributeInfo)).as(Property.class);		
	}
	
	/**
	 * Creates a URI in the IFC data namespace 
	 * @param name
	 * @return
	 */
	public String formatModelName(String name) {
		return modelNamespaceUri + name;
	}
	
	
}
