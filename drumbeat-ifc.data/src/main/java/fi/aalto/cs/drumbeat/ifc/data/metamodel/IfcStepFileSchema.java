package fi.aalto.cs.drumbeat.ifc.data.metamodel;

import java.util.List;

import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcEntity;

public class IfcStepFileSchema extends IfcStepEntity {
	
	public IfcStepFileSchema(IfcEntity entity) {
		super(entity);
	}

	public List<String> getSchemas() {
		return getListValue(IfcVocabulary.SpfFormat.Header.FileSchema.SCHEMA_IDENTIFIERS);
	}	
}
