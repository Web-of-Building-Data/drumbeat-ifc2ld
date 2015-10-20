package fi.hut.cs.drumbeat.ifc.convert;

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
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfSchemaExporter;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfVocabulary;
import fi.hut.cs.drumbeat.ifc.data.LogicalEnum;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;
import fi.hut.cs.drumbeat.rdf.OwlProfile;
import fi.hut.cs.drumbeat.rdf.OwlProfileEnum;
import fi.hut.cs.drumbeat.rdf.OwlProfileList;

public class Test_Ifc2RdfExporterBase_Exporting_DoubleTypes {
	
	private static final String TEST_SCHEMA_VERSION = "IfcTest"; 
	
	private IfcSchema ifcSchema;
	private Model jenaModel;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DrumbeatTestHelper.init();
	}
	

	@Before
	public void setUp() throws Exception {		
		ifcSchema = new IfcSchema(TEST_SCHEMA_VERSION);

		jenaModel = ModelFactory.createDefaultModel();
	}

	@Test
	public void test_Getting_XsdTypeOfDouble_As_XsdDouble() {
		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
		conversionContext.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_DOUBLE);
		
		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
		Resource xsdDataType = rdfSchemaExporter.getBaseTypeForLiterals(ifcSchema.REAL);
		assertEquals(XSD.xdouble, xsdDataType);
	}

	@Test
	public void test_Getting_XsdTypeOfDouble_As_XsdDecimal() {
		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
		conversionContext.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_DECIMAL);
		
		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
		Resource xsdDataType = rdfSchemaExporter.getBaseTypeForLiterals(ifcSchema.REAL);
		assertEquals(XSD.decimal, xsdDataType);
	}

	@Test
	public void test_Getting_XsdTypeOfDouble_As_XsdString() {
		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
		conversionContext.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_STRING);
		
		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
		Resource xsdDataType = rdfSchemaExporter.getBaseTypeForLiterals(ifcSchema.REAL);
		assertEquals(XSD.xstring, xsdDataType);
	}
	
//	@Test
//	public void test_Getting_XsdTypeOfDouble_As_AutoMostEfficient_OWL2RL() {
//		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
//		conversionContext.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
//				Ifc2RdfConversionParams.VALUE_AUTO_MOST_EFFICIENT);
//		OwlProfileList owlProfiles = new OwlProfileList();
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_RL));
//		conversionContext.setOwlProfiles(owlProfiles);
//		
//		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
//		Resource xsdDataType = rdfSchemaExporter.getBaseTypeForLiterals(ifcSchema.REAL);
//		assertEquals(XSD.xdouble, xsdDataType);
//	}	
//
//	@Test
//	public void test_Getting_XsdTypeOfDouble_As_AutoMostEfficient_OWL2EL() {
//		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
//		conversionContext.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
//				Ifc2RdfConversionParams.VALUE_AUTO_MOST_EFFICIENT);
//		OwlProfileList owlProfiles = new OwlProfileList();
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_RL));
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_EL));
//		conversionContext.setOwlProfiles(owlProfiles);
//		
//		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
//		Resource xsdDataType = rdfSchemaExporter.getBaseTypeForLiterals(ifcSchema.REAL);
//		assertEquals(XSD.decimal, xsdDataType);
//	}
//	
//	@Test
//	public void test_Getting_XsdTypeOfDouble_As_AutoMostSupported_OWL2RL() {
//		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
//		conversionContext.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
//				Ifc2RdfConversionParams.VALUE_AUTO_MOST_SUPPORTED);
//		OwlProfileList owlProfiles = new OwlProfileList();
//		owlProfiles.add(new OwlProfile(OwlProfileEnum.OWL2_RL));
//		conversionContext.setOwlProfiles(owlProfiles);
//		
//		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
//		Resource xsdDataType = rdfSchemaExporter.getBaseTypeForLiterals(ifcSchema.REAL);
//		assertEquals(XSD.decimal, xsdDataType);
//	}	

	@Test
	public void test_Exporting_Double_As_XsdDouble_True() {
		Ifc2RdfConversionContext conversionContext = new Ifc2RdfConversionContext();
		conversionContext.getConversionParams().setParamValue(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
				Ifc2RdfConversionParams.VALUE_XSD_DOUBLE);
		
		Ifc2RdfSchemaExporter rdfSchemaExporter = new Ifc2RdfSchemaExporter(ifcSchema, conversionContext, jenaModel);
		
		double d = new Random().nextDouble();
		
		IfcLiteralValue value = new IfcLiteralValue(d, ifcSchema.REAL, IfcTypeEnum.REAL);		
		RDFNode node = rdfSchemaExporter.convertLiteralToNode(value);		
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
