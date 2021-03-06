package fi.aalto.cs.drumbeat.ifc.convert.stff2ifc;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.text.StrMatcher;
import org.apache.log4j.Logger;

import fi.aalto.cs.drumbeat.common.string.RegexUtils;
import fi.aalto.cs.drumbeat.common.string.StrBuilderWrapper;
import fi.aalto.cs.drumbeat.common.string.StringUtils;
import fi.aalto.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.LogicalEnum;
import fi.aalto.cs.drumbeat.ifc.data.model.*;
import fi.aalto.cs.drumbeat.ifc.data.schema.*;

class IfcSpfModelSectionParser {

	private class IfcTemporaryCollectionValueWrapper extends IfcValue {
		
		private static final long serialVersionUID = 1L;

		private List<IfcValue> values;

		public IfcTemporaryCollectionValueWrapper(List<IfcValue> values) {
			this.values = values;
		}
		
		public List<IfcValue> getValues() {
			return values;
		}

		@Override
		public String toString() {
			return null;
		}
		
		@Override
		public Boolean isLiteralType() {
			Boolean isLiteralValue = null;
			for (IfcValue value : values) {
				Boolean b = value.isLiteralType(); 
				if (b != isLiteralValue && b != null) {
					if (isLiteralValue != null) {
//						assert isLiteralValue == null : "Mixed literal and link type: " + StringUtils.collectionToString(values, null, null, null, ",");
						logger.warn("Mixed literal and link type: " + StringUtils.collectionToString(values, null, null, null, ","));
						return Boolean.FALSE;
					}
					isLiteralValue = b;
				}
			}
			return isLiteralValue;
		}

		@Override
		public boolean isNullOrAny() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object other) {
			return false;
		}

	}
	
	
	
	private static final Logger logger = Logger.getLogger(IfcSpfModelParser.class);	

	private IfcSchema schema;
	private IfcLineReader reader;

	private Map<String, IfcEntity> entityMap = new HashMap<>(); 	// map of entities indexed by line numbers
	
	
	/**
	 * Reads line by line and creates new entities
	 * @throws IOException
	 * @throws IfcNotFoundException
	 * @throws IfcParserException
	 */
	public List<IfcEntity> parseEntities(IfcLineReader reader, IfcSchema schema, boolean isHeaderSection, boolean ignoreUnknownTypes) throws IOException, IfcNotFoundException, IfcParserException {		
		
		this.schema = schema;
		this.reader = reader;
		
		List<IfcEntity> entities = new ArrayList<>();
		
		
		String statement;
		String[] tokens;
		//
		// getting entity headers
		//
		while ((statement = reader.getNextStatement()) != null) {
			
			IfcEntity entity;
			String entityAttributesString;
			
			if (!isHeaderSection) {
				tokens = RegexUtils.split2(statement, IfcVocabulary.SpfFormat.LINE_NUMBER);
				if (tokens.length != 2) {
					if (tokens.length == 1 && tokens[0].equalsIgnoreCase(IfcVocabulary.SpfFormat.ENDSEC)) {
						break;
					}
					throw new IfcParserException("Invalid statement: '" + statement + "'");
//					continue;
				}
			
				tokens = RegexUtils.split2(tokens[1], IfcVocabulary.SpfFormat.EQUAL);
			
				//
				// create entity
				//
				//long lineNumber = Long.parseLong(tokens[0].trim());
				String lineNumber = tokens[0].trim();
				entity = getEntity(lineNumber);
				entityAttributesString = tokens[1].trim();
				
			} else {
				// header entities have no line numbers, they have different types 
				entity = new IfcEntity("");
				entityAttributesString = statement;
			}
		
			//
			// set entity type
			//
			int indexOfOpeningBracket = entityAttributesString.indexOf(StringUtils.OPENING_ROUND_BRACKET);		
			String entityTypeInfoName = entityAttributesString.substring(0, indexOfOpeningBracket).trim();
			
			IfcEntityTypeInfo entityTypeInfo;
			
			try {			
				entityTypeInfo = schema.getEntityTypeInfo(entityTypeInfoName);
			} catch (IfcNotFoundException e) {
				if (ignoreUnknownTypes) {
					continue;
				} else {
					throw e;
				}
			}
			entity.setTypeInfo(entityTypeInfo);
			
			entityAttributesString = entityAttributesString.substring(indexOfOpeningBracket + 1,
					entityAttributesString.length() - 1);
			
			List<IfcAttributeInfo> attributeInfos = entityTypeInfo.getInheritedAttributeInfos();

			//
			// parse attribute string to get attribute values
			//
			List<IfcValue> attributeValues = parseAttributeValues(new StrBuilderWrapper(entityAttributesString), entity, attributeInfos, null, null);

			setEntityAttributeValues(entity, attributeInfos, attributeValues);
			
			//
			// add entity to the model
			//
			entities.add(entity);
			
						
		}
		
		if (!isHeaderSection) {
			for (IfcEntity entity : entities) {
				assert(entity.getTypeInfo() != null);
				entity.bindInverseLinks();
			}
		}
		
		return entities;
	}
	
	/**
	 * Gets an entity from the map by its line number, or creates a new entity if it doesn't exist 
	 * @param lineNumber
	 * @return
	 */
	private IfcEntity getEntity(String lineNumber) {
		IfcEntity entity = entityMap.get(lineNumber);
		if (entity == null) {
			entity = new IfcEntity(null, lineNumber);
			entityMap.put(lineNumber, entity);
		}
		return entity;
	}

	/**
	 * Parses an entity's attribute string to get attribute values
	 * 
	 * @param attributeStringBuilder
	 * @param attributeValueType
	 * @return a single attribute value or list of attribute values
	 * @throws IfcFormatException
	 * @throws IfcNotFoundException
	 * @throws IfcValueTypeConflictException 
	 */
	private List<IfcValue> parseAttributeValues(StrBuilderWrapper attributeStrBuilderWrapper, IfcEntity entity,
			List<IfcAttributeInfo> entityAttributeInfos, IfcTypeInfo commonAttributeTypeInfo, EnumSet<IfcTypeEnum> commonValueTypes) throws IfcFormatException, IfcNotFoundException {

		logger.debug(String.format("Parsing entity '%s'", entity));			

		List<IfcValue> attributeValues = new ArrayList<>();
		
		for (int attributeIndex = 0; !attributeStrBuilderWrapper.trimLeft().isEmpty(); ++attributeIndex) {

			EnumSet<IfcTypeEnum> attributeValueTypes;
			IfcAttributeInfo attributeInfo;
			IfcTypeInfo attributeTypeInfo;
			if (commonValueTypes == null) {
				assert(attributeIndex < entityAttributeInfos.size()) :
					String.format("attributeIndex=%d, entityAttributeInfos.size=%s, attributeStrBuilderWrapper='%s'",
							attributeIndex,
							entityAttributeInfos,
							attributeStrBuilderWrapper);
				attributeInfo = entityAttributeInfos.get(attributeIndex);
				attributeTypeInfo = attributeInfo.getAttributeTypeInfo();
				attributeValueTypes = attributeTypeInfo.getValueTypes();				
			} else {
				assert(commonAttributeTypeInfo != null);
				attributeInfo = entityAttributeInfos.get(0);
				attributeTypeInfo = commonAttributeTypeInfo;
				attributeValueTypes = commonValueTypes;
			}
			
			if (attributeTypeInfo instanceof IfcCollectionTypeInfo) {
				attributeTypeInfo = ((IfcCollectionTypeInfo)attributeTypeInfo).getItemTypeInfo();
			}

			switch (attributeStrBuilderWrapper.charAt(0)) {

			case IfcVocabulary.SpfFormat.LINE_NUMBER_SYMBOL: // Entity
				attributeStrBuilderWrapper.skip(1);
				//Long remoteLineNumber = attributeStrBuilderWrapper.getLong();
				String remoteLineNumber = Long.toString(attributeStrBuilderWrapper.getLong());				
				IfcEntity remoteEntity = getEntity(remoteLineNumber);
				if (remoteEntity != null) {
					attributeValues.add(remoteEntity);
				} else {
					throw new IfcNotFoundException("Entity not found: #" + remoteLineNumber);
				}
				break;

			case IfcVocabulary.SpfFormat.STRING_VALUE_SYMBOL:
				String s = attributeStrBuilderWrapper.getStringBetweenSingleQuotes();
				assert attributeValueTypes.size() == 1 : "Expect attributeValueTypes.size() == 1"; 
//				if (!attributeValueTypes.contains(IfcTypeEnum.GUID)) {
					attributeValues.add(new IfcLiteralValue(s, attributeTypeInfo, IfcTypeEnum.STRING));
//					break;
//				} else {
//					attributeValues.add(new IfcGuidValue(s));
//					break;
//				}
				break;

			case IfcVocabulary.SpfFormat.ENUMERATION_VALUE_SYMBOL:

				s = attributeStrBuilderWrapper.getStringBetweenSimilarCharacters(IfcVocabulary.SpfFormat.ENUMERATION_VALUE_SYMBOL);

				assert attributeValueTypes.size() == 1 : "Expect attributeValueTypes.size() == 1"; 
				if (!attributeValueTypes.contains(IfcTypeEnum.LOGICAL)) {
					attributeValues.add(new IfcLiteralValue(s, attributeTypeInfo, IfcTypeEnum.ENUM));
				} else {
					switch (s) {
					case "T":
					case "TRUE":
						attributeValues.add(new IfcLiteralValue(LogicalEnum.TRUE, attributeTypeInfo, IfcTypeEnum.LOGICAL));
						break;
					case "F":
					case "FALSE":
						attributeValues.add(new IfcLiteralValue(LogicalEnum.FALSE, attributeTypeInfo, IfcTypeEnum.LOGICAL));
						break;
					default:
						attributeValues.add(new IfcLiteralValue(LogicalEnum.UNKNOWN, attributeTypeInfo, IfcTypeEnum.LOGICAL));
						break;

					}
				}
				break;

			case IfcVocabulary.SpfFormat.NULL_SYMBOL: // $
				attributeValues.add(IfcValue.NULL);
				attributeStrBuilderWrapper.skip(1);
				break;

			case IfcVocabulary.SpfFormat.ANY_SYMBOL: // *
				attributeValues.add(IfcValue.ANY);
				attributeStrBuilderWrapper.skip(1);
				break;

			case StringUtils.OPENING_ROUND_BRACKET_CHAR: // List or Set

				String stringBetweenBrackets = attributeStrBuilderWrapper.getStringBetweenRoundBrackets();

				StrBuilderWrapper sbWrapper = new StrBuilderWrapper(stringBetweenBrackets);
				
				List<IfcAttributeInfo> attributeInfos = new ArrayList<>(1);
				attributeInfos.add(attributeInfo);

				List<IfcValue> values = parseAttributeValues(sbWrapper, null, attributeInfos, attributeTypeInfo, attributeValueTypes);
				attributeValues.add(new IfcTemporaryCollectionValueWrapper(values));
				break;

			default:

				if (Character.isAlphabetic(attributeStrBuilderWrapper.charAt(0))) {

					// 
					// parsing sub entity
					//
					String subEntityTypeInfoName = attributeStrBuilderWrapper.getIdentifierName();
					IfcNonEntityTypeInfo subNonEntityTypeInfo = schema.getNonEntityTypeInfo(subEntityTypeInfoName);
					attributeValueTypes = subNonEntityTypeInfo.getValueTypes();
					s = attributeStrBuilderWrapper.getStringBetweenRoundBrackets();
					
					assert (s != null);

					attributeInfos = new ArrayList<>(1);
					attributeInfos.add(attributeInfo);

					values = parseAttributeValues(new StrBuilderWrapper(s), null, attributeInfos, subNonEntityTypeInfo, attributeValueTypes);
					assert values.size() == 1 : "Expect only 1 argument: " + entity + ":" + values.toString();
					attributeValues.add(new IfcShortEntity(subNonEntityTypeInfo, (IfcLiteralValue)values.get(0)));
//					attributeValues.add((IfcLiteralValue)values.get(0));
				} else {
					
					//
					// parsing number or datetime
					//
					assert attributeValueTypes.size() == 1 : "Expect attributeValueTypes.size() == 1";
					IfcTypeEnum attributeValueType = (IfcTypeEnum)attributeValueTypes.iterator().next();
					Object value;
					if (attributeValueType == IfcTypeEnum.INTEGER) {
						value = attributeStrBuilderWrapper.getLong();
					} else if (attributeValueType == IfcTypeEnum.REAL || attributeValueType == IfcTypeEnum.NUMBER) {
						value = attributeStrBuilderWrapper.getDouble();
					} else if (attributeValueType == IfcTypeEnum.DATETIME) {
						long timeStamp = attributeStrBuilderWrapper.getLong();
						value = Calendar.getInstance();
						((Calendar)value).setTimeInMillis(timeStamp * 1000);
					} else {
						throw new IfcFormatException(reader.getCurrentLineNumber(), "Invalid attributeValueType: " + attributeValueType);
					}
					
					attributeValues.add(new IfcLiteralValue(value, (IfcNonEntityTypeInfo)attributeTypeInfo, attributeValueType));						
				}

				break;
			}
			
			attributeStrBuilderWrapper.trimLeft();
			attributeStrBuilderWrapper.getFirstMatch(StrMatcher.commaMatcher());
		}

		return attributeValues;
	}
	
	/**
	 * Set entity attribute values
	 * @param entity
	 * @param attributeInfos
	 * @param attributeValues
	 * @throws IfcParserException
	 */
	private void setEntityAttributeValues(IfcEntity entity, List<IfcAttributeInfo> attributeInfos, List<IfcValue> attributeValues) throws IfcParserException {
		try {
			
			if (attributeValues.size() == attributeInfos.size()) {
				boolean isLiteralValueContainer = true;
				for (int attributeIndex = 0; attributeIndex < attributeValues.size(); ++attributeIndex) {
					
					IfcAttributeInfo attributeInfo = attributeInfos.get(attributeIndex);
					IfcValue attributeValue = attributeValues.get(attributeIndex);
					
					Boolean isLiteralValue = attributeValue.isLiteralType();
					
					if (isLiteralValue != null) {
						isLiteralValueContainer = isLiteralValueContainer && (isLiteralValue == Boolean.TRUE);							
						if (isLiteralValue) {
						
							if (attributeValue instanceof IfcTemporaryCollectionValueWrapper) {
								
								if (attributeInfo.isCollection()) {
									
									IfcCollectionTypeInfo attributeTypeInfo = (IfcCollectionTypeInfo)attributeInfo.getAttributeTypeInfo();
									assert(attributeTypeInfo instanceof IfcCollectionTypeInfo) : "attributeTypeInfo instanceof IfcCollectionTypeInfo";
									
									if (attributeTypeInfo.getItemTypeInfo() instanceof IfcCollectionTypeInfo) {
										
										@SuppressWarnings("serial")
										IfcCollectionValue<IfcValue> values = new IfcCollectionValue<IfcValue>() {
											@Override
											public Boolean isLiteralType() {
												return null;
											}											
										};
										for (IfcValue value : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
											IfcLiteralValueCollection internalValue = new IfcLiteralValueCollection();
											for (IfcValue value2 : ((IfcTemporaryCollectionValueWrapper)value).getValues()) {
												internalValue.add((IfcLiteralValue)value2);
											}
											values.add(internalValue);	
										}
										entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, values));
										
									} else {

										IfcLiteralValueCollection values = new IfcLiteralValueCollection();										
										for (IfcValue value : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
											values.add((IfcLiteralValue)value);
										}
										entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, values));										
										
									}									
									
								} else {										
									
									for (IfcValue value : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, value));
									}										
									
								}
								
							} else {
								
								assert(attributeValue instanceof IfcLiteralValue) :
									String.format("Object is not a literal value, line number: %s, attributeInfo: %s, attribute value: %s, value type: %s",
											entity.getLocalId(), attributeInfo.getName(), attributeValue, attributeInfo.getAttributeTypeInfo().getValueTypes()); 
								entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, attributeValue));
								
							}
							
						} else { // attributeInfo instanceof IfcLinkInfo								
							
							if (attributeValue instanceof IfcTemporaryCollectionValueWrapper) {
								
								if (attributeInfo.isCollection()) {
									
									IfcEntityCollection destinations = new IfcEntityCollection();										
									for (IfcValue destination : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										if (!(destination instanceof IfcEntityBase)) {
											throw new IfcParserException(
													String.format("Entity attribute %s.%s has non-entity value: %s (%s)", 
															entity,
															attributeInfo,
															destination.getClass(),
															destination));
										}
										destinations.add((IfcEntityBase)destination);
									}
									
									entity.addOutgoingLink(new IfcLink((IfcLinkInfo)attributeInfo, attributeIndex, entity, destinations));
									
								} else {										
									
									for (IfcValue destination : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										IfcLink link = new IfcLink((IfcLinkInfo)attributeInfo, attributeIndex, entity, (IfcEntityBase)destination);
										entity.addOutgoingLink(link);
									}										
									
								}
								
							} else {
								
								assert (attributeValue instanceof IfcEntityBase) :
									String.format("Object is not an entity, line number: %s, attributeInfo: %s, attribute value: %s, value type: %s",
											entity.getLocalId(), attributeInfo.getName(), attributeValue, attributeInfo.getAttributeTypeInfo().getValueTypes());
								IfcLink link = new IfcLink((IfcLinkInfo)attributeInfo, attributeIndex, entity, (IfcEntityBase)attributeValue);
								entity.addOutgoingLink(link);
								
							}
						}
					}
					
				}						
				entity.setLiteralValueContainer(isLiteralValueContainer);
				
			} else {
				throw new IfcParserException(String.format("Type %s: Expected %d attributes, but %d were found: %s, %s",
						entity.getTypeInfo().getName(), attributeInfos.size(), attributeValues.size(), attributeInfos, attributeValues));						
			}

		} catch (Exception e) {
			throw new IfcParserException(String.format("Error parsing entity %s (line %s): %s", entity.toString(), entity.getLocalId(),
					e.getMessage()), e);
		}
	}	

}
