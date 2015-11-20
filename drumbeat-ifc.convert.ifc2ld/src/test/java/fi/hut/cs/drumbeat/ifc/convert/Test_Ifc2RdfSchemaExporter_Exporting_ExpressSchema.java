package fi.hut.cs.drumbeat.ifc.convert;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.convert.RdfAsserter.ModelAsserter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConverter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfSchemaExporter;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;

public class Test_Ifc2RdfSchemaExporter_Exporting_ExpressSchema {
	
	private static IfcSchema ifcSchema;
	private Model jenaModel;
	private Ifc2RdfConversionContext context;
	private Ifc2RdfConverter converter;
	private Ifc2RdfSchemaExporter schemaExporter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DrumbeatTestHelper.init();
		ifcSchema = DrumbeatTestHelper.getTestIfcSchema();
	}
	

	@Before
	public void setUp() throws Exception {		
		context = new Ifc2RdfConversionContext();
		context.getConversionParams().setParamValue(Ifc2RdfConversionParams.PARAM_IGNORE_IFC_SCHEMA, true);
		converter = new Ifc2RdfConverter(context, ifcSchema);
		
		jenaModel = ModelFactory.createDefaultModel();		
		DrumbeatTestHelper.setNsPrefixes(jenaModel, converter);

		schemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, context, jenaModel);
	}
	
	private void compareWithExpectedResult(Model actualModel) throws IOException {
		
		boolean readExpectedModel = true;
		if (readExpectedModel) {
			String testFilePath = DrumbeatTestHelper.getTestFilePath(this, 1, true, "txt");
			Model expectedModel = DrumbeatTestHelper.readModel(testFilePath);

			ModelAsserter asserter = new ModelAsserter();
			asserter.assertEquals(expectedModel, actualModel);
		} else {
			String actualFilePath = DrumbeatTestHelper.getTestFilePath(this, 1, false, "txt");
			DrumbeatTestHelper.writeModel(jenaModel, actualFilePath);			
			throw new NotImplementedException("TODO: Compare with expected result");
		}
	}
	
	

	@Test
	public void test_export_ExpressSchema() throws IOException, IfcException {
		
		Model model = schemaExporter.export();
		compareWithExpectedResult(model);		
		
	}	
	
	

}
