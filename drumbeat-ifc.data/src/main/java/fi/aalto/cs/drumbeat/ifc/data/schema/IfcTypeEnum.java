package fi.aalto.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;

public enum IfcTypeEnum {	
	
	ENTITY,
	BINARY,
	GUID,
	ENUM,
	INTEGER,
	REAL,
	NUMBER,
	STRING,
	LOGICAL,
	DATETIME;
	
	public static final EnumSet<IfcTypeEnum> LITERAL	= EnumSet.of(GUID, ENUM, INTEGER, REAL, NUMBER, STRING, LOGICAL, DATETIME);
	public static final EnumSet<IfcTypeEnum> LINK		= EnumSet.of(ENTITY);
	public static final EnumSet<IfcTypeEnum> NUMBERIRC	= EnumSet.of(INTEGER, REAL, NUMBER);
	
}
