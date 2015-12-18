package fi.aalto.cs.drumbeat.ifc.data.model;

public abstract class IfcSingleValue extends IfcValue {

	private static final long serialVersionUID = 1L;

	public boolean isNullOrAny() {
		return false;
	}
	
}
