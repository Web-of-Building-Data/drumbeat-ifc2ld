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

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.convert.RdfAsserter.FullResourceAsserter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConverter;
import fi.hut.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLiteralTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLogicalTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeInfo;

public class Test_Ifc2RdfExporterBase_Exporting_LiteralTypes {
	
	private static IfcSchema ifcSchema;
	private Model jenaModel;
	private Ifc2RdfConversionContext context;
	private Ifc2RdfConverter converter;	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DrumbeatTestHelper.init();
		ifcSchema = DrumbeatTestHelper.getTestSchema();
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
	
	
	private void test_convert_IfcLiteralTypeInfo(String typeName) throws IfcNotFoundException, IOException {
		
		IfcTypeInfo typeInfo = ifcSchema.getNonEntityTypeInfo(typeName);
		
		assertEquals(IfcLiteralTypeInfo.class, typeInfo.getClass());
		
		Resource typeResource = converter.convertLiteralTypeInfo((IfcLiteralTypeInfo)typeInfo, jenaModel);
		
		assertNotNull(typeResource);
		assertTrue(typeResource.isURIResource());
		assertEquals(typeResource.getLocalName(), typeInfo.getName());
		

		compareWithExpectedResult(typeResource);
	}
	
	
	private void test_convert_IfcLogicalTypeInfo(String typeName) throws IfcNotFoundException, IOException {		
		
		IfcTypeInfo typeInfo = ifcSchema.getNonEntityTypeInfo(typeName);
		
		assertEquals(IfcLogicalTypeInfo.class, typeInfo.getClass());
		
		Resource typeResource = converter.convertLogicalTypeInfo((IfcLogicalTypeInfo)typeInfo, jenaModel);
		
		assertNotNull(typeResource);
		assertTrue(typeResource.isURIResource());
		assertEquals(typeResource.getLocalName(), typeInfo.getName());		

		compareWithExpectedResult(typeResource);
	}


	@Test
	public void test_convert_IfcLiteralTypeInfo_INTEGER() throws IfcNotFoundException, IOException {		
		test_convert_IfcLiteralTypeInfo(IfcVocabulary.TypeNames.INTEGER);		
	}	


	@Test
	public void test_convert_IfcLiteralTypeInfo_REAL() throws IfcNotFoundException, IOException {		
		test_convert_IfcLiteralTypeInfo(IfcVocabulary.TypeNames.REAL);		
	}	

	@Test
	public void test_convert_IfcLiteralTypeInfo_STRING() throws IfcNotFoundException, IOException {		
		test_convert_IfcLiteralTypeInfo(IfcVocabulary.TypeNames.STRING);		
	}	
	
	@Test
	public void test_convert_IfcLiteralTypeInfo_BINARY() throws IfcNotFoundException, IOException {		
		test_convert_IfcLiteralTypeInfo(IfcVocabulary.TypeNames.BINARY);		
	}	

	@Test
	public void test_convert_IfcLiteralTypeInfo_DATETIME() throws IfcNotFoundException, IOException {		
		test_convert_IfcLiteralTypeInfo(IfcVocabulary.TypeNames.DATETIME);		
	}	

	@Test
	public void test_convert_IfcLogicalTypeInfo_BOOLEAN() throws IfcNotFoundException, IOException {
		test_convert_IfcLogicalTypeInfo(IfcVocabulary.TypeNames.BOOLEAN);
	}	

	@Test
	public void test_convert_IfcLogicalTypeInfo_LOGICAL() throws IfcNotFoundException, IOException {
		test_convert_IfcLogicalTypeInfo(IfcVocabulary.TypeNames.LOGICAL);
	}	

}
