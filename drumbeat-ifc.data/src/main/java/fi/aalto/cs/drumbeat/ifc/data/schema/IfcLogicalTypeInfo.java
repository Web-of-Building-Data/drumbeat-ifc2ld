package fi.aalto.cs.drumbeat.ifc.data.schema;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.LogicalEnum;


public class IfcLogicalTypeInfo extends IfcNonEntityTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private List<LogicalEnum> values = new ArrayList<LogicalEnum>();
	
	public IfcLogicalTypeInfo(IfcSchema schema, String name, List<LogicalEnum> values) {
		super(schema, name);
		this.values = values; // .stream().map(value -> value.toString()).collect(Collectors.toList());
	}

	/**
	 * @return the values
	 */
	public List<LogicalEnum> getValues() {
		return values;
	}

	@Override
	public boolean isCollectionType() {
		return false;
	}

//	@Override
//	public boolean isEntityOrSelectType() {
//		return false;
//	}
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return IfcTypeEnum.STRING
	 */
	@Override
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return EnumSet.of(IfcTypeEnum.LOGICAL);
	}	
	
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return IfcVocabulary.ExpressFormat.ENUMERATION;
	}

	@Override
	public boolean isLiteralContainerType() {
		return true;
	}

}
