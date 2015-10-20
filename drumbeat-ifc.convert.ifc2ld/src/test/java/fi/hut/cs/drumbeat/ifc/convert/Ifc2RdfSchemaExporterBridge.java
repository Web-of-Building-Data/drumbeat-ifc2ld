package fi.hut.cs.drumbeat.ifc.convert;

import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfSchemaExporter;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;

public class Ifc2RdfSchemaExporterBridge extends Ifc2RdfSchemaExporter {

	public Ifc2RdfSchemaExporterBridge(IfcSchema ifcSchema,
			Ifc2RdfConversionContext context, Model jenaModel) {
		super(ifcSchema, context, jenaModel);
	}
	
	
	@Override
	public void exportDefinedTypeInfo(IfcDefinedTypeInfo typeInfo) {
		super.exportDefinedTypeInfo(typeInfo);
	}

}
