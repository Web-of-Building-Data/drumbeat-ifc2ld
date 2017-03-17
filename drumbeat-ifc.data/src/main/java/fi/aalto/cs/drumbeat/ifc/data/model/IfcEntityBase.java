package fi.aalto.cs.drumbeat.ifc.data.model;

import fi.aalto.cs.drumbeat.ifc.data.schema.IfcTypeInfo;

public abstract class IfcEntityBase extends IfcSingleValue {

	private static final long serialVersionUID = 1L;
	private String namespaceSuffix;
	
	@Override
	public Boolean isLiteralType() {
		return Boolean.FALSE;
	}
	
	public abstract IfcTypeInfo getTypeInfo();	
	
	public abstract boolean equals(Object o);
	
	public abstract int hashCode();

	public abstract String toString();
	
	public abstract boolean isIdenticalTo(IfcEntityBase other);

	public String getNamespaceSuffix() {
		return namespaceSuffix;
	}
	
	public void setNamespaceSuffix(String namespaceSuffix) {
		this.namespaceSuffix = namespaceSuffix;
	}

}
