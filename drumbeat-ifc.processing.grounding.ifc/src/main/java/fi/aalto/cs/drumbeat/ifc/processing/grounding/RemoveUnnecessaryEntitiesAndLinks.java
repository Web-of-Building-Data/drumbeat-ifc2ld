package fi.aalto.cs.drumbeat.ifc.processing.grounding;

import java.util.*;

import org.apache.log4j.Logger;

import fi.aalto.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.aalto.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.aalto.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.aalto.cs.drumbeat.common.string.StringUtils;
import fi.aalto.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcEntityBase;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcLink;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcLinkInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;


/**
 * Processor that removes all unnecessary entities and links.
 * 
 *  Sample syntax:
 *  
 *		<processor name="RemoveUnnecessaryEntitiesAndLinks" enabled="true">
 *			<class>fi.aalto.cs.drumbeat.ifc.processing.grounding.RemoveUnnecessaryEntitiesAndLinks</class>
 *			<params>
 *				<param name="entityTypeNames" value="type1, type2, ..." />
 *			</params>
 *		</processor>
 *  
 * @author vuhoan1
 *
 */
public class RemoveUnnecessaryEntitiesAndLinks extends IfcGroundingProcessor {
	
	private static final Logger logger = Logger.getLogger(RemoveUnnecessaryEntitiesAndLinks.class);	

	private static final String PARAM_ENTITY_TYPE_NAMES = "entityTypeNames";
//	private static final String PARAM_RECURSIVE = "recursive";
	
	private List<IfcEntityTypeInfo> entityTypeInfos;
	private List<IfcEntity> entitiesToRemove;
	private int numberOfRemovedEntities;
	private int numberOfRemovedLinks;
//	private boolean recursive;

	public RemoveUnnecessaryEntitiesAndLinks(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	void initialize() throws IfcAnalyserException {
		IfcSchema schema = getMainProcessor().getAnalyzer().getSchema();
		entityTypeInfos = new ArrayList<>();
		String entityTypeNamesString = getProperties().getProperty(PARAM_ENTITY_TYPE_NAMES);
		if (entityTypeNamesString != null) {
			String[] tokens = entityTypeNamesString.split(StringUtils.COMMA);
			try {
				for (String token : tokens) {
					String entityTypeName = token.trim();
					logger.trace("Removing entity type: '" + entityTypeName + "'");
					entityTypeInfos.add(schema.getEntityTypeInfo(entityTypeName));
				}
			} catch (IfcNotFoundException e) {
				throw new IfcAnalyserException(e.getMessage(), e);
			}
		} else {
			throw new IfcAnalyserException(String.format("Parameter %s is undefined", PARAM_ENTITY_TYPE_NAMES));
		}
		
//		String recursiveString = getProperties().getProperty(PARAM_RECURSIVE);
//		BooleanParam recursiveParam = new BooleanParam();
//		recursiveParam.setStringValue(recursiveString);
//		recursive = recursiveParam.getValue();
	}

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.UngroundedEntity;
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		for (IfcEntityTypeInfo entityTypeInfo : entityTypeInfos) {
			if (entity.isInstanceOf(entityTypeInfo)) {
				logger.trace("Removing entity " + entity);
				entitiesToRemove.add(entity);				
//				if (recursive) {
//					removeOutgoingLinks(entity);
//				}
				return true;
			}
		}
		return false;
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
		entitiesToRemove = new ArrayList<>();
		super.preProcess();
	}

	@Override
	public void postProcess() {
		numberOfRemovedEntities = 0;
		numberOfRemovedLinks = 0;

		IfcModel model = getMainProcessor().getAnalyzer().getModel();
		
		for (IfcEntity entity : entitiesToRemove) {
			removeEntity(model, entity, true);
		}
		
		logger.debug(String.format("\tTotal number of removed entities: %,d", numberOfRemovedEntities));
		logger.debug(String.format("\tTotal number of removed links: %,d", numberOfRemovedLinks));
		
		super.postProcess();
		
	}
	
	private void removeEntity(IfcModel model, IfcEntity entity, boolean forceRemovingIncomingLinks) {
//		logger.trace("Removing entity " + entity);
		
		if (forceRemovingIncomingLinks) {
			
			for (IfcLink incomingLink : entity.getIncomingLinks()) {
				
				IfcEntity source = incomingLink.getSource();
				IfcLinkInfo linkInfo = incomingLink.getLinkInfo();				
				source.getOutgoingLinks().remove(linkInfo, entity);
				
				++numberOfRemovedLinks;
				
			}

		} else {
			assert (entity.getIncomingLinks().isEmpty()) : "Expected: entity.getIncomingLinks().isEmpty()";
		}		
	
		for (IfcLink outgoingLink : entity.getOutgoingLinks()) {
			
			for (IfcEntityBase d : outgoingLink.getDestinations()) {				
				if (d instanceof IfcEntity) {
					IfcEntity destination = (IfcEntity)d;
					List<IfcLink> incomingLinks = destination.getIncomingLinks();
					for (int i = 0; i < incomingLinks.size(); ++i) {
						IfcLink incomingLink = incomingLinks.get(i);
						if (incomingLink.getSource().equals(entity) && incomingLink.getLinkInfo().equals(outgoingLink.getLinkInfo())) {
							incomingLinks.remove(i);
							++numberOfRemovedLinks;
							break;
						}
					}
					
					if (incomingLinks.isEmpty()) {
						removeEntity(model, destination, false);
					}
				}
			}
			
		}			
		
		model.removeEntity(entity);
		
		++numberOfRemovedEntities;
		
		addEffectedEntityInfoForDebugging(entity);		
	}

}
