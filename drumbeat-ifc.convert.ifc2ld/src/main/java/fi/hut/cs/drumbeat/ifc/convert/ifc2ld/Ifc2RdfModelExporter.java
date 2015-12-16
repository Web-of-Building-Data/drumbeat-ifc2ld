package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcCollectionTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;
import fi.hut.cs.drumbeat.rdf.OwlProfileList;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;


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
		Property attributeResource = convertAttributeInfoToResource(attributeInfo);
		
		IfcValue value = attribute.getValue();
		RDFNode valueNode = convertValueToNode(value, attributeInfo.getAttributeTypeInfo(), entityResource, childNodeCount);
		
		jenaModel.add(entityResource, attributeResource, valueNode);
	}
	
	/**
	 * Converts any {@link IfcValue} value to {@link RDFNode}
	 * @param value
	 * @param typeInfo
	 * @return
	 */
	public RDFNode convertValueToNode(IfcValue value, IfcTypeInfo typeInfo, Resource entityResource, long childNodeCount) {
		if (typeInfo instanceof IfcCollectionTypeInfo) {
			return convertListToResource((IfcCollectionValue<?>) value, (IfcCollectionTypeInfo)typeInfo, entityResource, childNodeCount);
		} else {
			if (value instanceof IfcEntityBase) {
				return convertEntityToResource((IfcEntity) value);
			} else {
				assert(value instanceof IfcLiteralValue);
				return converter.convertLiteralValue((IfcLiteralValue) value, entityResource, childNodeCount, jenaModel);
			}
		}
	}
	
	public Resource convertListToResource(IfcCollectionValue<? extends IfcValue> listValue, IfcCollectionTypeInfo collectionTypeInfo,
			Resource parentResource, long childNodeCount)
	{		
		final String convertCollectionsTo = context.getConversionParams().convertCollectionsTo();
		final boolean nameAllBlanksNodes = context.getConversionParams().nameAllBlankNodes();
		
		
		
		switch (convertCollectionsTo) {
		case Ifc2RdfConversionParams.VALUE_DRUMMOND_LIST:
			
			break;

		default:
			break;
		}
		
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
		for (IfcValue value : listValue.getSingleValues()) {
			nodeList.add(convertValueToNode(value, itemTypeInfo, listResource, count++));
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
		
		return listResource;			
	}
	
	public Resource convertEntityToResource(IfcEntity value) {
		IfcEntity entity = (IfcEntity)value;		
		if (entity.hasName()) {
			return jenaModel.createResource(formatModelName(entity.getName()));
		} else {
			final boolean nameAllBlankNodes = context.getConversionParams().nameAllBlankNodes();
			String nodeName = String.format(Ifc2RdfVocabulary.IFC.BLANK_NODE_ENTITY_URI_FORMAT, value.getLineNumber());
			if (nameAllBlankNodes) {
				return jenaModel.createResource(formatModelName(nodeName));				
			} else {
				return jenaModel.createResource(new AnonId(nodeName));				
			}
		}
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
