//package fi.aalto.cs.drumbeat.ifc.convert;
//
//import static org.junit.Assert.*;
//
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.rdf.model.Statement;
//import com.hp.hpl.jena.vocabulary.OWL2;
//import com.hp.hpl.jena.vocabulary.XSD;
//
//import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
//import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
//import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConverter;
//import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfVocabulary;
//import fi.aalto.cs.drumbeat.ifc.data.LogicalEnum;
//import fi.aalto.cs.drumbeat.ifc.data.model.IfcLiteralValue;
//import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;
//import fi.aalto.cs.drumbeat.ifc.data.schema.IfcTypeEnum;
//
//public class Test_Ifc2RdfConverter_Exporting_BooleanTypes {
//	
//	private static final String TEST_SCHEMA_VERSION = "IfcTest"; 
//	
//	private IfcSchema ifcSchema;
//	private Model jenaModel;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		DrumbeatTestHelper.init();
//	}
//	
//
//	@Before
//	public void setUp() throws Exception {		
//		ifcSchema = new IfcSchema(TEST_SCHEMA_VERSION);
//		
//
//		jenaModel = ModelFactory.createDefaultModel();
//	}
//
//	@Test
//	public void test_Getting_XsdTypeOfBoolean_As_NamedIndividual() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_NAMED_INDIVIDUAL);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		Resource xsdDataType = converter.getBaseTypeForBooleans();
//		assertEquals(OWL2.NamedIndividual, xsdDataType);
//	}
//
//	@Test
//	public void test_Getting_XsdTypeOfBoolean_As_XsdBoolean() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_BOOLEAN);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		Resource xsdDataType = converter.getBaseTypeForBooleans();
//		assertEquals(XSD.xboolean, xsdDataType);
//	}
//
//	@Test
//	public void test_Getting_XsdTypeOfBoolean_As_XsdString() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_STRING);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		Resource xsdDataType = converter.getBaseTypeForBooleans();
//		assertEquals(XSD.xstring, xsdDataType);
//	}
//
//	@Test
//	public void test_Exporting_Boolean_As_NamedIndividual() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_NAMED_INDIVIDUAL);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.TRUE, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isURIResource());
//		assertEquals(Ifc2RdfVocabulary.EXPRESS.getBaseUri() + "true", node.asResource().getURI());
//	}
//	
//	@Test
//	public void test_Exporting_Boolean_As_XsdBoolean_True() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_BOOLEAN);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.TRUE, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isAnon());
//		
//		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasLogical);
//		assertNotNull(valueStatement);
//		RDFNode valueObject = valueStatement.getObject();
//		
//		assertNotNull(valueObject);
//		assertTrue(valueObject.isLiteral());
//		assertEquals(true, valueObject.asLiteral().getBoolean());
//		assertEquals(XSD.xboolean.getURI(), valueObject.asLiteral().getDatatypeURI());
//	}	
//
//	@Test
//	public void test_Exporting_Boolean_As_XsdBoolean_False() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_BOOLEAN);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.FALSE, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isAnon());
//		
//		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasLogical);
//		assertNotNull(valueStatement);
//		RDFNode valueObject = valueStatement.getObject();
//		
//		assertNotNull(valueObject);
//		assertTrue(valueObject.isLiteral());
//		assertEquals(false, valueObject.asLiteral().getBoolean());
//		assertEquals(XSD.xboolean.getURI(), valueObject.asLiteral().getDatatypeURI());
//	}	
//
//	@Test
//	public void test_Exporting_Boolean_As_XsdBoolean_Unknown() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_BOOLEAN);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.UNKNOWN, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isAnon());
//		
//		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasLogical);
//		assertNotNull(valueStatement);
//		RDFNode valueObject = valueStatement.getObject();
//		
//		assertNotNull(valueObject);
//		assertTrue(valueObject.isLiteral());
//		assertEquals("unknown", valueObject.asLiteral().getString());
//		assertEquals(XSD.xstring.getURI(), valueObject.asLiteral().getDatatypeURI());
//	}
//	
//	@Test
//	public void test_Exporting_Boolean_As_XsdString_True() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_STRING);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.TRUE, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isAnon());
//		
//		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasLogical);
//		assertNotNull(valueStatement);
//		RDFNode valueObject = valueStatement.getObject();
//		
//		assertNotNull(valueObject);
//		assertTrue(valueObject.isLiteral());
//		assertEquals("true", valueObject.asLiteral().getString());
//		assertEquals(XSD.xstring.getURI(), valueObject.asLiteral().getDatatypeURI());
//	}	
//
//	@Test
//	public void test_Exporting_Boolean_As_XsdString_False() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_STRING);
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//		
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.FALSE, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isAnon());
//		
//		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasLogical);
//		assertNotNull(valueStatement);
//		RDFNode valueObject = valueStatement.getObject();
//		
//		assertNotNull(valueObject);
//		assertTrue(valueObject.isLiteral());
//		assertEquals("false", valueObject.asLiteral().getString());
//		assertEquals(XSD.xstring.getURI(), valueObject.asLiteral().getDatatypeURI());
//	}	
//
//	@Test
//	public void test_Exporting_Boolean_As_XsdString_Unknown() {
//		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext();
//		context.getConversionParams().setParamValue(
//				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
//				Ifc2RdfConversionParams.VALUE_XSD_STRING);		
//		
//		Ifc2RdfConverter converter = new Ifc2RdfConverter(context, ifcSchema);
//
//		IfcLiteralValue value = new IfcLiteralValue(LogicalEnum.UNKNOWN, ifcSchema.BOOLEAN, IfcTypeEnum.LOGICAL);		
//		RDFNode node = converter.convertLiteralValue(value, jenaModel);		
//		assertNotNull(node);
//		assertTrue(node.isAnon());
//		
//		Statement valueStatement = node.asResource().getProperty(Ifc2RdfVocabulary.EXPRESS.hasLogical);
//		assertNotNull(valueStatement);
//		RDFNode valueObject = valueStatement.getObject();
//		
//		assertNotNull(valueObject);
//		assertTrue(valueObject.isLiteral());
//		assertEquals("unknown", valueObject.asLiteral().getString());
//		assertEquals(XSD.xstring.getURI(), valueObject.asLiteral().getDatatypeURI());
//	}	
//	
//}
