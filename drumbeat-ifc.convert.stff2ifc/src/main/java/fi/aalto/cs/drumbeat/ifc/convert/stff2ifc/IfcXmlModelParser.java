package fi.aalto.cs.drumbeat.ifc.convert.stff2ifc;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fi.aalto.cs.drumbeat.common.string.StringUtils;
import fi.aalto.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary.Formatter;
import fi.aalto.cs.drumbeat.ifc.data.LogicalEnum;
import fi.aalto.cs.drumbeat.ifc.data.model.*;
import fi.aalto.cs.drumbeat.ifc.data.schema.*;

public class IfcXmlModelParser {

	public static final String SCHEMA_LOCATION_BASE_URI = "http://www.buildingsmart-tech.org/ifcXML/";

	private static final String XML_ATTRIBUTE_TYPE = "xsi:type";
	private static final String XML_ATTRIBUTE_NIL = "xsi:nil";
	private static final String XML_ATTRIBUTE_ID = "id";
	private static final String XML_ATTRIBUTE_REF = "ref";
	private static final String XML_ATTRIBUTE_HREF = "href";

	private static final String XML_ELEMENT_TAG_WRAPPER_SUFFIX = "-wrapper";

	private Element documentElement;
	private IfcSchema schema;
	private IfcModel model;

	private Map<String, IfcEntity> entityMap = new HashMap<>(); // map of entities
																// indexed by
																// line numbers

	public IfcXmlModelParser(InputStream input) {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(input);
			documentElement = document.getDocumentElement();
		} catch (SAXException | IOException | ParserConfigurationException e) {
		}
	}

	public IfcModel parseModel() throws IfcParserException {

		try {

			String schemaLocation = documentElement.getAttribute("xsi:schemaLocation");

			if (!schemaLocation.startsWith(SCHEMA_LOCATION_BASE_URI)) {
				throw new IfcParserException("Unknown schema location: " + schemaLocation);
			}

			schemaLocation = schemaLocation.substring(SCHEMA_LOCATION_BASE_URI.length());

			String[] schemaNameParts = schemaLocation.split("/");
			String schemaName = schemaNameParts[0] + "_" + schemaNameParts[1];

			schema = IfcSchemaPool.getSchema(schemaName);

			if (schema == null) {
				throw new IfcNotFoundException("Schema " + schemaName + " not found");
			}

			model = new IfcModel(schema, null);

			parseEntities(documentElement, "");

			return model;

			// } catch (IfcParserException e) {
			// throw e;
		} catch (Exception e) {
			throw new IfcParserException(e);
		}

	}

	private List<IfcEntityBase> parseEntities(Element parentElement, String parentEntityId) throws IfcParserException, IfcNotFoundException {
		List<IfcEntityBase> entities = new ArrayList<>();
		int childCount = 0;
		for (Node childNode = parentElement.getFirstChild(); childNode != null; childNode = childNode
				.getNextSibling()) {
			if (childNode instanceof Element) {
				String typeName = ((Element) childNode).getTagName();
				if (!typeName.endsWith(XML_ELEMENT_TAG_WRAPPER_SUFFIX)) {
					// normal entity
					IfcEntityBase entity = parseEntity((Element) childNode, typeName, parentEntityId, childCount++);
					entities.add(entity);
				} else {
					// short entity
					String literalTypeName = typeName.substring(0,
							typeName.length() - XML_ELEMENT_TAG_WRAPPER_SUFFIX.length());
					IfcNonEntityTypeInfo literalTypeInfo = schema.getNonEntityTypeInfo(literalTypeName);
					IfcLiteralValue literalValue = parseSingleLiteralValue(literalTypeInfo,
							((Element) childNode).getTextContent());
					IfcShortEntity entity = new IfcShortEntity(literalTypeInfo, literalValue);
					entities.add(entity);
				}
			}
		}
		return entities;
	}

	/**
	 * Gets an entity from the map by its line number, or creates a new entity
	 * if it doesn't exist
	 * 
	 * @param localId
	 * @return
	 */
	private IfcEntity getEntity(String localId) {
		IfcEntity entity = entityMap.get(localId);
		if (entity == null) {
			entity = new IfcEntity(null, localId);
			entityMap.put(localId, entity);
		}
		return entity;
	}

	private IfcEntityBase parseEntity(Element entityElement, String entityTypeName, String parentEntityId, int childCount)
			throws IfcParserException, IfcNotFoundException {
		System.out.println("Parsing " + entityElement);

		String id = entityElement.getAttribute(XML_ATTRIBUTE_ID);

		//
		// create entity
		//
		IfcEntity entity;
		if (!StringUtils.isEmptyOrNull(id)) {
			entity = getEntity(id);
		} else {
			// TODO: Support href (not ref)
			String ref = entityElement.getAttribute(XML_ATTRIBUTE_REF);
			if (!StringUtils.isEmptyOrNull(ref)) {
				entity = getEntity(ref);
				return entity;
			} else {
				String href = entityElement.getAttribute(XML_ATTRIBUTE_HREF);				
				if (!StringUtils.isEmptyOrNull(href)) {
					ref = href.substring(href.lastIndexOf('/'));
					entity = getEntity(ref);
					return entity;
				} else {
					entity = new IfcEntity(Formatter.formatChildEntityId(parentEntityId, childCount));
				}
			}
		}

		//
		// set entity type
		//
		if (entityTypeName == null) {
			entityTypeName = entityElement.getAttribute(XML_ATTRIBUTE_TYPE);

			if (StringUtils.isEmptyOrNull(entityTypeName)) {
				List<IfcEntityBase> entities = parseEntities(entityElement, entity.getLocalId());
				if (entities.size() == 1) {
					return entities.get(0);
				}

				throw new IfcParserException("Cannot parse entity element: " + entityElement);
			}

		}

		IfcEntityTypeInfo entityTypeInfo = schema.getEntityTypeInfo(entityTypeName);
		entity.setTypeInfo(entityTypeInfo);
		model.addEntity(entity);

		//
		// add attributes
		//
		boolean isNil = false;
		NamedNodeMap attributes = entityElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); ++i) {
			Attr attr = (Attr) attributes.item(i);

			String attributeName = attr.getName();

			switch (attributeName) {
			case XML_ATTRIBUTE_ID:
			case XML_ATTRIBUTE_TYPE:
			case XML_ATTRIBUTE_REF:
				break;

			case XML_ATTRIBUTE_NIL:
				isNil = Boolean.parseBoolean(attr.getValue());
				break;

			default:
				IfcAttributeInfo attributeInfo = entityTypeInfo.getAttributeInfo(attributeName);
				IfcValue value = parseLiteralValues(attributeInfo, attr.getValue());
				entity.addLiteralAttribute(
						new IfcLiteralAttribute(attributeInfo, attributeInfo.getAttributeIndex(), value));
			}
		}

		//
		// add links
		//
		if (!isNil) {
			childCount = 0;
			for (Node childNode = entityElement.getFirstChild(); childNode != null; childNode = childNode
					.getNextSibling()) {
				assert (!(childNode instanceof Attr));
				if (childNode instanceof Element) {
					String attributeName = ((Element) childNode).getTagName();

					IfcAttributeInfo attributeInfo = entityTypeInfo.getAttributeInfo(attributeName, true);

					if (attributeInfo instanceof IfcLinkInfo) {

						IfcLinkInfo linkInfo = (IfcLinkInfo) attributeInfo;
						IfcTypeInfo linkTypeInfo = linkInfo.getAttributeTypeInfo();
						if (linkTypeInfo instanceof IfcCollectionTypeInfo) {
							List<IfcEntityBase> linkedEntities = parseEntities((Element) childNode, entity.getLocalId());
							IfcEntityCollection linkedEntityCollection = new IfcEntityCollection(linkedEntities);
							IfcLink link = new IfcLink(linkInfo, linkInfo.getAttributeIndex(), entity,
									linkedEntityCollection);
							entity.addOutgoingLink(link);
						} else {
							IfcEntityBase linkedEntity = parseEntity((Element) childNode, null, entity.getLocalId(), childCount++);
							IfcLink link = new IfcLink(linkInfo, linkInfo.getAttributeIndex(),
									entity, linkedEntity);
							entity.addOutgoingLink(link);
						}

					} else if (attributeInfo instanceof IfcInverseLinkInfo) {
						
						IfcTypeInfo inverseLinkTypeInfo = ((IfcInverseLinkInfo) attributeInfo).getAttributeTypeInfo();

						List<IfcEntityBase> linkedEntities;
						if (inverseLinkTypeInfo instanceof IfcCollectionTypeInfo) {
							linkedEntities = parseEntities((Element) childNode, entity.getLocalId());							
						} else {
							linkedEntities = new ArrayList<>();
							IfcEntity linkedEntity = (IfcEntity) parseEntity((Element) childNode, null, entity.getLocalId(), childCount++);
							linkedEntities.add(linkedEntity);
						}
						
						IfcLinkInfo linkInfo = ((IfcInverseLinkInfo) attributeInfo).getOutgoingLinkInfo();
						IfcTypeInfo linkTypeInfo = linkInfo.getAttributeTypeInfo();
						for (IfcEntityBase linkedEntity : linkedEntities) {
							
							if (linkTypeInfo instanceof IfcCollectionTypeInfo) {
								
								IfcLink link = ((IfcEntity)linkedEntity).getOutgoingLink(linkInfo);
								if (link == null) {
									IfcEntityCollection linkedEntityCollection = new IfcEntityCollection();
									linkedEntityCollection.add(entity);
									link = new IfcLink(linkInfo, linkInfo.getAttributeIndex(), (IfcEntity)linkedEntity,
											linkedEntityCollection);
									((IfcEntity)linkedEntity).addOutgoingLink(link);
								} else {
									link.getDestinations().add(entity);
								}	
								
							} else {
								IfcLink link = new IfcLink(linkInfo, linkInfo.getAttributeIndex(), (IfcEntity)linkedEntity, entity);
								entity.addOutgoingLink(link);
							}
							
						}
						
					} else {

						IfcValue value = parseLiteralValues(attributeInfo, childNode.getNodeValue());
						entity.addLiteralAttribute(
								new IfcLiteralAttribute(attributeInfo, attributeInfo.getAttributeIndex(), value));

					}
				}

			}
		}

		assert(entity.getTypeInfo() != null);
		return entity;
	}

	private IfcValue parseLiteralValues(IfcAttributeInfo attributeInfo, String nodeValue)
			throws IfcParserException, IfcNotFoundException {

		IfcTypeInfo attributeTypeInfo = attributeInfo.getAttributeTypeInfo();
		assert (!(attributeTypeInfo instanceof IfcEntityTypeInfo));

		if (attributeTypeInfo instanceof IfcCollectionTypeInfo) {

			String[] valueStrings = nodeValue.split(" ");
			IfcLiteralValueCollection valueCollection = new IfcLiteralValueCollection();
			
			IfcTypeInfo valueTypeInfo = ((IfcCollectionTypeInfo) attributeTypeInfo).getItemTypeInfo();

			for (int i = 0; i < valueStrings.length; ++i) {
				valueCollection.add(parseSingleLiteralValue(valueTypeInfo, valueStrings[i]));
			}

			return valueCollection;

		} else {
			return parseSingleLiteralValue(attributeTypeInfo, nodeValue);
		}

	}

	private IfcLiteralValue parseSingleLiteralValue(IfcTypeInfo valueTypeInfo, String valueString)
			throws IfcParserException {

		assert (valueTypeInfo.getValueTypes().size() == 1);
		assert (valueString != null);

		IfcTypeEnum attributeValueType = valueTypeInfo.getValueTypes().iterator().next();

		Object value;
		if (attributeValueType.equals(IfcTypeEnum.STRING) || attributeValueType.equals(IfcTypeEnum.ENUM)) {
			value = valueString;
		} else if (attributeValueType.equals(IfcTypeEnum.REAL) || attributeValueType.equals(IfcTypeEnum.NUMBER)) {
			value = Double.parseDouble(valueString);
		} else if (attributeValueType.equals(IfcTypeEnum.INTEGER)) {
			value = Long.parseLong(valueString);
		} else if (attributeValueType.equals(IfcTypeEnum.LOGICAL)) {
			switch (valueString) {
			case "true":
				value = LogicalEnum.TRUE;
				break;
			case "false":
				value = LogicalEnum.FALSE;
				break;
			case "unknown":
			case "Unknown":
			case "UNKNOWN":
				value = LogicalEnum.UNKNOWN;
			default:
				throw new IfcParserException("Unknown logical value: " + valueString);
			}
		} else if (attributeValueType.equals(IfcTypeEnum.DATETIME)) {
			long timeStamp = Long.parseLong(valueString);
			value = Calendar.getInstance();
			((Calendar) value).setTimeInMillis(timeStamp * 1000);
		} else {
			throw new IfcParserException("Unsupported attribute value type: " + attributeValueType);
		}

		return new IfcLiteralValue(value, valueTypeInfo, attributeValueType);

	}

}
