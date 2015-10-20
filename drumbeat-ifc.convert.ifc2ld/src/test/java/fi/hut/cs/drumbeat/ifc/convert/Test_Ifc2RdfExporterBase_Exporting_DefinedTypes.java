package fi.hut.cs.drumbeat.ifc.convert;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL2;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfSchemaExporter;
import fi.hut.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;

public class Test_Ifc2RdfExporterBase_Exporting_DefinedTypes {
	
	private static final String TEST_SCHEMA_VERSION = "IfcTest";
	
	private IfcSchema ifcSchema;
	private Model jenaModel;
	private Ifc2RdfConversionContext conversionContext;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DrumbeatTestHelper.init();
	}
	

	@Before
	public void setUp() throws Exception {		
		ifcSchema = new IfcSchema(TEST_SCHEMA_VERSION);

		jenaModel = ModelFactory.createDefaultModel();
		
		conversionContext = new Ifc2RdfConversionContext();
	}
	
	@Test
	public void test_convertDefinedType() throws IfcNotFoundException {
		IfcTypeInfo typeInfo = ifcSchema.getNonEntityTypeInfo(IfcVocabulary.TypeNames.IFC_LOGICAL);
		
		assertTrue(typeInfo instanceof IfcDefinedTypeInfo);
		
		Ifc2RdfSchemaExporterBridge rdfSchemaExporter = new Ifc2RdfSchemaExporterBridge(ifcSchema, conversionContext, jenaModel);
		rdfSchemaExporter.exportDefinedTypeInfo((IfcDefinedTypeInfo)typeInfo);
//		assertEquals(OWL2.NamedIndividual, xsdDataType);
	}	

}
