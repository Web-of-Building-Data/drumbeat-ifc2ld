package fi.aalto.cs.drumbeat.ifc.data.metamodel;

import java.util.List;

import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcEntity;

public class IfcStepFileDescription extends IfcStepEntity {
	
	public IfcStepFileDescription(IfcEntity entity) {
		super(entity);
	}
	
	public List<String> getDescriptions() {
		return getListValue(IfcVocabulary.SpfFormat.Header.FileDescription.DESCRIPTION);
	}
	
	public String getImplementationLevel() {
		return getSingleValue(IfcVocabulary.SpfFormat.Header.FileDescription.IMPLEMENTATION_LEVEL);
	}
	
	
}
