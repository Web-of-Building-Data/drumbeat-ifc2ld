package fi.aalto.cs.drumbeat.ifc.data.model;

import java.util.List;

public class IfcEntityCollection extends IfcCollectionValue<IfcEntityBase> {

	private static final long serialVersionUID = 1L;

	public IfcEntityCollection() {
	}

	public <T extends IfcEntityBase> IfcEntityCollection(List<T> values) {
		super(values);
	}

	@Override
	public Boolean isLiteralType() {
		return false;
	}

}
