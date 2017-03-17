package fi.aalto.cs.drumbeat.ifc.processing.grounding;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import fi.aalto.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.aalto.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.aalto.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.aalto.cs.drumbeat.common.string.StringUtils;
import fi.aalto.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcEntityBase;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcLink;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcShortEntity;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;


/**
 * Processor that splits entities to different namespaces
 * 
 *  Sample syntax:
 *  
 *		<processor name="DevideEntitiesToSubModels" enabled="true">
 *			<class>fi.aalto.cs.drumbeat.ifc.processing.grounding.DevideEntitiesToSubModels</class>
 *			<params>
 *				<param name="propertySets.entityTypeNames" value="type1, type2, ...." />
 *				<param name="propertySets.namespaceSuffix" value="namespaceSuffix" />
 *			</params>
 *		</processor>
 *  
 * @author vuhoan1
 *
 */
public class DevideEntitiesToSubModels extends IfcGroundingProcessor {
	
	private static final Logger logger = Logger.getLogger(DevideEntitiesToSubModels.class);	

	private static final String PARAM_SUFFIX_ENTITY_TYPE_NAMES = ".entityTypeNames";
	private static final String PARAM_SUFFIX_NAMESPACE_SUFFIX = ".namespaceSuffix";
	private HashMap<IfcEntityTypeInfo, String> namespaceSuffixMap;
	private IfcSchema schema;
	private long entityCount;
	
	public DevideEntitiesToSubModels(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {
		
		schema = getMainProcessor().getAnalyzer().getSchema();
		namespaceSuffixMap = new HashMap<>();
		Properties properties = getProperties();
		entityCount = 0L;
		
		for (Entry<Object, Object> entry : properties.entrySet()) {
			if (((String)entry.getKey()).endsWith(PARAM_SUFFIX_NAMESPACE_SUFFIX)) {
				
				String paramName = (String)entry.getKey();
				String entitySetName = paramName.substring(0, paramName.length() - PARAM_SUFFIX_NAMESPACE_SUFFIX.length());
				String namespaceSuffix = (String)entry.getValue();				
				String entityTypeNamesString = properties.getProperty(entitySetName + PARAM_SUFFIX_ENTITY_TYPE_NAMES);
				
				if (entityTypeNamesString != null) {
					String[] tokens = entityTypeNamesString.split(StringUtils.COMMA);
					try {
						for (String token : tokens) {
							String entityTypeName = token.trim();
							IfcEntityTypeInfo entityType = schema.getEntityTypeInfo(entityTypeName); 
							namespaceSuffixMap.put(entityType, namespaceSuffix);
						}
					} catch (IfcNotFoundException e) {
						throw new IfcAnalyserException(e.getMessage(), e);
					}
				} else {
					throw new IfcAnalyserException(String.format("Parameter '%s' was found but parameter '%s' is undefined",
							paramName,
							entitySetName + PARAM_SUFFIX_ENTITY_TYPE_NAMES));
				}				
			}
		}

	}

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.UngroundedEntity;
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		
		for (IfcEntityTypeInfo entityTypeInfo : namespaceSuffixMap.keySet()) {
			if (entity.isInstanceOf(entityTypeInfo)) {
				setNamespaceSuffix(entity, namespaceSuffixMap.get(entityTypeInfo));
				return true;
			}
		}
		return false;
	}
	
	private void setNamespaceSuffix(IfcEntityBase entity, String namespaceSuffix) throws IfcAnalyserException {
		
		String currentNamespaceSuffix = entity.getNamespaceSuffix();
		if (currentNamespaceSuffix != null && !currentNamespaceSuffix.equals(namespaceSuffix)) {
			throw new IfcAnalyserException("Entity " + entity + " already has namespace suffix: " + currentNamespaceSuffix);
		}		
		
		entity.setNamespaceSuffix(namespaceSuffix);
		++entityCount;
		
		if (entity instanceof IfcEntity) {
		
			for (IfcLink link : ((IfcEntity)entity).getOutgoingLinks()) {
				
				for (IfcEntityBase destination : link.getDestinations()) {
					
					if (destination instanceof IfcShortEntity || !((IfcEntity)destination).isInstanceOf(schema.IFC_ROOT))  {
						// skip root entities (GUID-entities) 
						setNamespaceSuffix(destination, namespaceSuffix);
					}				
				}
				
			}
			
		}
		
	}
	
//	private void removeOutgoingLinks(IfcEntity entity) {
//		for (IfcLink link : entity.getOutgoingLinks()) {
//			for (IfcEntityBase linkedEntity : link.getDestinations()) {
//				if (linkedEntity instanceof IfcEntity) {
//					entitiesToRemove.add((IfcEntity)entity);
//					removeOutgoingLinks(entity);
//				}
//			}
//		}
//	}
	
//	private void removeIncomingLinks(IfcEntity entity) {
//		for (IfcLink link : entity.getIncomingLinks()) {
//			link.getDestinations().remove(entity);
//			if (link.get)
//		}
//	}

	@Override
	boolean checkNameDuplication() {
		return false;
	}

	@Override
	boolean allowNameDuplication() {
		return false;
	}
	
	public void preProcess() {
		super.preProcess();
	}

	@Override
	public void postProcess() {
		logger.info(String.format("\tTotal number of effective entities: %,d", entityCount));
		super.postProcess();
		
	}	

}
