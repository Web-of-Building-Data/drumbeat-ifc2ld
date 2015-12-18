package fi.aalto.cs.drumbeat.ifc.convert;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConverter;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcTypeEnum;

public class Test_Ifc2RdfConverter_Exporting_DoubleTypes {
	
	private static final String TEST_SCHEMA_VERSION = "IfcTest"; 
	
	private IfcSchema ifcSchema;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DrumbeatTestHelper.init();
	}
	

	@Before
	public void setUp() throws Exception {
		ifcSchema = new IfcSchema(TEST_SCHEMA_VERSION);
	}

	@Test
	public void test_Getting_XsdTypeOfDouble_As_XsdDouble() {
		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
		context.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_DOUBLE);
		
		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
		Resource xsdDataType = converter.getBaseTypeForLiterals(ifcSchema.REAL);
		assertEquals(XSD.xdouble, xsdDataType);
	}

	@Test
	public void test_Getting_XsdTypeOfDouble_As_XsdDecimal() {
		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
		context.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_DECIMAL);
		
		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
		Resource xsdDataType = converter.getBaseTypeForLiterals(ifcSchema.REAL);
		assertEquals(XSD.decimal, xsdDataType);
	}

	@Test
	public void test_Getting_XsdTypeOfDouble_As_XsdString() {
		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
		context.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_STRING);
		
		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
		Resource xsdDataType = converter.getBaseTypeForLiterals(ifcSchema.REAL);
		assertEquals(XSD.xstring, xsdDataType);
	}
	
//	@Test
//	public void test_Getting_XsdTypeOfDouble_As_AutoMostEfficient_OWL2RL() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
//				Ifc2RdfConversionParams.VALUE_AUTO_MOST_EFFICIENT);
//		OwlProfileList owlProfiles = new OwlProfileList();
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_RL));
//		context.setOwlProfiles(owlProfiles);
//		
//		Ifc2RdfSchemaExporter converter = new Ifc2RdfSchemaExporter(ifcSchema, context, jenaModel);
//		Resource xsdDataType = converter.getBaseTypeForLiterals(ifcSchema.REAL);
//		assertEquals(XSD.xdouble, xsdDataType);
//	}	
//
//	@Test
//	public void test_Getting_XsdTypeOfDouble_As_AutoMostEfficient_OWL2EL() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
//				Ifc2RdfConversionParams.VALUE_AUTO_MOST_EFFICIENT);
//		OwlProfileList owlProfiles = new OwlProfileList();
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_RL));
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_EL));
//		context.setOwlProfiles(owlProfiles);
//		
//		Ifc2RdfSchemaExporter converter = new Ifc2RdfSchemaExporter(ifcSchema, context, jenaModel);
//		Resource xsdDataType = converter.getBaseTypeForLiterals(ifcSchema.REAL);
//		assertEquals(XSD.decimal, xsdDataType);
//	}
//	
//	@Test
//	public void test_Getting_XsdTypeOfDouble_As_AutoMostSupported_OWL2RL() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
//				Ifc2RdfConversionParams.VALUE_AUTO_MOST_SUPPORTED);
//		OwlProfileList owlProfiles = new OwlProfileList();
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_RL));
//		context.setOwlProfiles(owlProfiles);
//		
//		Ifc2RdfSchemaExporter converter = new Ifc2RdfSchemaExporter(ifcSchema, context, jenaModel);
//		Resource xsdDataType = converter.getBaseTypeForLiterals(ifcSchema.REAL);
//		assertEquals(XSD.decimal, xsdDataType);
//	}	

	@Test
	public void test_Exporting_Double_As_XsdDouble_True() {
		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
		context.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_DOUBLE);
		
		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
		
		double d = new Random().nextDouble();
		
		IfcLiteralValue value = new IfcLiteralValue(d, ifcSchema.REAL, IfcTypeEnum.REAL);
		Model jenaModel = ModelFactory.createDefaultModel();
		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
		assertNotNull(node);
		assertNotNull(node.isResource());		
		assertTrue(node.isAnon());
		
		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasReal);
		assertNotNull(valueStatement);
		RDFNode valueObject = valueStatement.getObject();
		
		assertNotNull(valueObject);
		assertTrue(valueObject.isLiteral());
		assertEquals(XSD.xdouble.getURI(), valueObject.asLiteral().getDatatypeURI());
		assertEquals(d, valueObject.asLiteral().getDouble(), DrumbeatTestHelper.DOUBLE_DELTA);
	}	

}
