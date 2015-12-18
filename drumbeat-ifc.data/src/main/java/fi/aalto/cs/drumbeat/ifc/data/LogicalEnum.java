package fi.aalto.cs.drumbeat.ifc.data;

public enum LogicalEnum {	
	TRUE,
	FALSE,
	UNKNOWN;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
