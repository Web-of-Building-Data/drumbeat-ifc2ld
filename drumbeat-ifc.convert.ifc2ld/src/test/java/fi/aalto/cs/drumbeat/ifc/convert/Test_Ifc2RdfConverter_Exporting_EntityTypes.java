package fi.aalto.cs.drumbeat.ifc.convert;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.cs.drumbeat.ifc.common.IfcException;
import fi.aalto.cs.drumbeat.ifc.convert.RdfAsserter.FullResourceAsserter;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConverter;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;

public class Test_Ifc2RdfConverter_Exporting_EntityTypes {
	
	private static IfcSchema ifcSchema;
	private Model jenaModel;
	private Ifc2RdfConversionContext context;
	private Ifc2RdfConverter converter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DrumbeatTestHelper.init();
		ifcSchema = DrumbeatTestHelper.getTestIfcSchema();
	}
	

	@Before
	public void setUp() throws Exception {		
		context = new Ifc2RdfConversionContext();		
		converter = new Ifc2RdfConverter(context, ifcSchema);

		jenaModel = ModelFactory.createDefaultModel();		
		DrumbeatTestHelper.setNsPrefixes(jenaModel, converter);
	}
	
	private void compareWithExpectedResult(Resource actualTypeResource) throws IOException {
		
		boolean readExpectedModel = true;
		if (readExpectedModel) {
			String testFilePath = DrumbeatTestHelper.getTestFilePath(this, 2, true, "txt");
			Model expectedModel = DrumbeatTestHelper.readModel(testFilePath);
			
			Resource expectedTypeResource = expectedModel.getResource(actualTypeResource.getURI());
			assertNotNull(expectedTypeResource);
			
			FullResourceAsserter asserter = new FullResourceAsserter();
			asserter.assertEquals(expectedTypeResource, actualTypeResource);
		} else {
			String actualFilePath = DrumbeatTestHelper.getTestFilePath(this, 2, false, "txt");
			DrumbeatTestHelper.writeModel(jenaModel, actualFilePath);			
			throw new NotImplementedException("TODO: Compare with expected result");
		}
	}
	
	
	private void test_convert_IfcEntityTypeInfo(String typeName) throws IOException, IfcException {
		
		IfcEntityTypeInfo typeInfo = ifcSchema.getEntityTypeInfo(typeName);
		
		Resource typeResource = converter.convertEntityTypeInfo((IfcEntityTypeInfo)typeInfo, jenaModel);
		
		assertNotNull(typeResource);
		assertTrue(typeResource.isURIResource());
		assertEquals(typeResource.getLocalName(), typeInfo.getName());
		

		compareWithExpectedResult(typeResource);
	}
	


	@Test
	public void test_convert_IfcEntityTypeInfo_IfcObject() throws IOException, IfcException {
		test_convert_IfcEntityTypeInfo("IfcObject");		
	}	
	
	@Test
	public void test_convert_IfcEntityTypeInfo_IfcPolyline() throws IOException, IfcException {
		test_convert_IfcEntityTypeInfo("IfcPolyline");		
	}
	
	@Test
	public void test_convert_IfcEntityTypeInfo_IfcCompositeProfileDef() throws IOException, IfcException {
		test_convert_IfcEntityTypeInfo("IfcCompositeProfileDef");		
	}

	

}
