package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;






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


public class Ifc2RdfModelExporter_Old extends Ifc2RdfExporterBase_Old {
	
	private IfcSchema ifcSchema;
	private IfcModel ifcModel;
	private Model jenaModel;
	
	private Ifc2RdfConversionContext context;
	private OwlProfileList owlProfileList;
	
	public Ifc2RdfModelExporter_Old(IfcModel ifcModel, Ifc2RdfConversionContext context, Model jenaModel) {
		super(context, jenaModel);
		
		this.ifcSchema = ifcModel.getSchema();
		this.ifcModel = ifcModel;		
		this.context = context;
		this.owlProfileList = context.getOwlProfileList();
		this.jenaModel = getJenaModel();
		
		String modelNamespacePrefix = context.getModelPrefix();
		String modelNamespaceUri = String.format(context.getModelNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		
		String ontologyNamespaceUri = String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		super.setOntologyNamespaceUri(ontologyNamespaceUri);		
		
		super.setModelNamespacePrefix(modelNamespacePrefix);
		super.setModelNamespaceUri(modelNamespaceUri);		
	}
	
	public Model export() throws IfcException {
		
		//
		// write header and prefixes
		//
		//adapter.startExport();
		
		jenaModel.setNsPrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());
		jenaModel.setNsPrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());
		jenaModel.setNsPrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());
		jenaModel.setNsPrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());	
		
		
//		if (context.isEnabledOption(Ifc2RdfConversionParamsEnum.ForceConvertRdfListToOloOrderedList)) {
//			adapter.setNamespacePrefix(RdfVocabulary.OLO.BASE_PREFIX, RdfVocabulary.OLO.BASE_URI);
//		}
//		
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.EXPRESS.BASE_PREFIX, Ifc2RdfVocabulary.EXPRESS.getBaseUri());		
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.IFC.BASE_PREFIX, getOntologyNamespaceUri());
		//adapter.exportEmptyLine();
		
		jenaModel.setNsPrefix(getModelNamespacePrefix(), getModelNamespaceUri());
		//adapter.exportEmptyLine();

		String conversionParamsString = context.getConversionParams().toString()
				.replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]")
				.replaceAll(",", "\r\n\t\t\t");
		
		conversionParamsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				owlProfileList.getOwlProfileIds(),
				conversionParamsString); 
		
		Resource ontology = jenaModel.createResource(getOntologyNamespaceUri());
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
	
	private void writeEntity(IfcEntity entity) {
		if (entity.isDuplicated()) {
			return;
		}
		
		Resource entityResource = convertEntityToResource(entity);
		
		IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();		
		entityResource.addProperty(RDF.type, createUriResource(super.formatTypeName(entityTypeInfo)));
		
		for (IfcLink link : entity.getOutgoingLinks()) {
			writeAttribute(entityResource, link);
		}
		
		for (IfcLiteralAttribute attribute : entity.getLiteralAttributes()) {
			writeAttribute(entityResource, attribute);
		}
		
		if ((Boolean)context.getConversionParams().getParam(Ifc2RdfConversionParams.PARAM_EXPORT_DEBUG_INFO).getValue()) {
		
//			if (entity.isLiteralValueContainer()) {
//				jenaModel.add(entityResource, RDF.type, super.createUriResource(
//						super.formatOntologyName(Ifc2RdfVocabulary.IFC.LITERAL_VALUE_CONTAINER_ENTITY)));
//			}
			
//			if (entity.isSharedBlankNode()) {
//				jenaModel.add(
//						entityResource,
//						RDF.type,
//						super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.SUPER_ENTITY)));				
//			}		
//			
//			
//			if (entity.hasName()) {
//				String entityRawName = entity.getRawName();
//				if (!entityRawName.equals(entity.getName())) {
//					jenaModel.add(
//							entityResource,
//							super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.RAW_NAME)).as(Property.class),
//									jenaModel.createTypedLiteral(entityRawName));
//				}
//			}
			
			String debugMessage = entity.getDebugMessage();
			if (debugMessage != null) {
				jenaModel.add(
				entityResource,
				super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.PROPERTY_DEBUG_MESSAGE)).as(Property.class),
						jenaModel.createTypedLiteral(debugMessage));				
			}
			
			jenaModel.add(
					entityResource,
					super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.PROPERTY_LINE_NUMBER_PROPERTY)).as(Property.class),
					jenaModel.createTypedLiteral(entity.getLineNumber()));
		}		

		//adapter.exportEmptyLine();
	}
	
	private void writeAttribute(Resource entityResource, IfcAttribute attribute) {		
		IfcAttributeInfo attributeInfo = attribute.getAttributeInfo();
		Property attributeResource = convertAttributeInfoToResource(attributeInfo);
		IfcValue value = attribute.getValue();
		jenaModel.add(entityResource, attributeResource, convertValueToNode(value, attributeInfo.getAttributeTypeInfo()));
	}
	
	public RDFNode convertValueToNode(IfcValue value, IfcTypeInfo typeInfo) {
		if (typeInfo instanceof IfcCollectionTypeInfo) {
			return convertListToResource((IfcCollectionValue<?>) value, (IfcCollectionTypeInfo)typeInfo);
		} else {
			if (value instanceof IfcEntityBase) {
				return convertEntityToResource((IfcEntity) value);
			} else {
				return convertLiteralToNode((IfcLiteralValue) value);
			}
		}
	}
	
	public Resource convertListToResource(IfcCollectionValue<? extends IfcValue> listValue, IfcCollectionTypeInfo collectionTypeInfo) {
		
		IfcTypeInfo itemTypeInfo = collectionTypeInfo.getItemTypeInfo();
		
		List<RDFNode> nodeList = new ArrayList<>();
		for (IfcValue value : listValue.getSingleValues()) {
			nodeList.add(convertValueToNode(value, itemTypeInfo));
		}
		
		int length = nodeList.size();
		
		Resource listResource = super.createAnonResource();
		Resource typeResource = super.createUriResource(super.formatTypeName(collectionTypeInfo)); 
		listResource.addProperty(RDF.type, typeResource);
		listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.size, jenaModel.createTypedLiteral(length));
		listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.isOrdered,
				collectionTypeInfo.isSorted() ? Ifc2RdfVocabulary.EXPRESS.TRUE : Ifc2RdfVocabulary.EXPRESS.FALSE);
//		listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.itemType, super.createUriResource(super.formatTypeName(itemTypeInfo)));
		
		for (int i = 0; i < nodeList.size(); ++i) {
			Resource slotResource = super.createAnonResource();
			slotResource.addProperty(Ifc2RdfVocabulary.EXPRESS.index, jenaModel.createTypedLiteral(i + 1));
			slotResource.addProperty(Ifc2RdfVocabulary.EXPRESS.item, nodeList.get(i));
			listResource.addProperty(Ifc2RdfVocabulary.EXPRESS.slot, slotResource);
		}
		
		return listResource;			
	}
	
	public Resource convertEntityToResource(IfcEntity value) {
		IfcEntity entity = (IfcEntity)value;
		if (entity.hasName()) {
			return super.createUriResource(super.formatModelName(entity.getName()));
		} else {
			return super.createAnonResource(String.format(Ifc2RdfVocabulary.IFC.BLANK_NODE_ENTITY_URI_FORMAT, entity.getLineNumber()));
		}
	}
	
	public Property convertAttributeInfoToResource(IfcAttributeInfo attributeInfo) {
		return super.createUriResource(super.formatAttributeName(attributeInfo)).as(Property.class);		
	}
	
}
