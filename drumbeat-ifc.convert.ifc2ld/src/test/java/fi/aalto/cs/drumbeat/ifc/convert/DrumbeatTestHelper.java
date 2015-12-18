package fi.aalto.cs.drumbeat.ifc.convert;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.xml.DOMConfigurator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.aalto.cs.drumbeat.common.collections.Pair;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.aalto.cs.drumbeat.common.file.FileManager;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConverter;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfVocabulary;
import fi.aalto.cs.drumbeat.ifc.convert.stff2ifc.IfcParserException;
import fi.aalto.cs.drumbeat.ifc.convert.stff2ifc.util.IfcParserUtil;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.aalto.cs.drumbeat.ifc.data.schema.IfcSchemaPool;
import fi.aalto.cs.drumbeat.rdf.RdfVocabulary;

public class DrumbeatTestHelper {
	
	public static final String TEST_SCHEMA_VERSION = "IFC4_ADD1";
	
	public static final String TEST_RESOURCES_PATH ="src/test/resources/"; 
	
	public static final double DOUBLE_DELTA = 1e-15;
	
	public static final String CONFIG_FILE_PATH = "src/test/java/ifc2rdf-config.xml";
	public static final String LOGGER_CONFIG_FILE_PATH = "src/test/java/log4j.xml";
	
	public static final String TEST_SOURCE_RESOURCES_PATH = TEST_RESOURCES_PATH + "source/";

	public static final String TEST_IFC_SCHEMAS_FILE_PATH = TEST_SOURCE_RESOURCES_PATH + "schemas";
	public static final String TEST_IFC_MODEL_FILE_PATH = TEST_SOURCE_RESOURCES_PATH + "models/sample.ifc";
	
	public static final String TEST_TARGET_RESOURCES_PATH = TEST_RESOURCES_PATH + "target/";

	public static final String MODEL_NAMESPACE_PREFIX = "model";
	public static final String MODEL_NAMESPACE_URI_FORMAT = "http://example.org/domain/";
	
	private static boolean initialized = false;
	private static IfcModel testIfcModel;
	
	public static void init() throws Exception {
		if (!initialized) {
			initialized = true;
			DOMConfigurator.configure(LOGGER_CONFIG_FILE_PATH);			
			ConfigurationDocument.load(CONFIG_FILE_PATH);
			IfcParserUtil.parseSchemas(TEST_IFC_SCHEMAS_FILE_PATH);
		}
	}
	
	/**
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
	 * @return method name
	 */
	public static String getMethodName(final int methodCallShift)
	{
	  StackTraceElement[] ste = Thread.currentThread().getStackTrace();
	  return ste[methodCallShift + 2].getMethodName();
	}
	
	public static String getTestFilePath(Object object, int methodCallShift, boolean isExpected, String extension) {
		return String.format("%s%s/%s/%s.%s",
				TEST_TARGET_RESOURCES_PATH,
				isExpected ? "expected" : "actual",
				object.getClass().getSimpleName(),
				getMethodName(methodCallShift + 1),
				extension);
	}
	
	public static Model readModel(String filePath) throws IOException {
		FileInputStream in = new FileInputStream(filePath);
		Model model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(model, in, Lang.TURTLE);
		in.close();
		return model;
	}
	
	public static void writeModel(Model model, String filePath) throws IOException {
		FileOutputStream out = FileManager.createFileOutputStream(filePath);
		RDFDataMgr.write(out, model, RDFFormat.TURTLE_PRETTY);
		out.close();
	}	
	
	public static void setNsPrefixes(Model jenaModel, Ifc2RdfConverter converter) {
		// define owl:
		jenaModel.setNsPrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());

		// define rdf:
		jenaModel.setNsPrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());

		// define rdfs:
		jenaModel.setNsPrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());

		// define xsd:
		jenaModel.setNsPrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());

		// define expr:
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.EXPRESS.BASE_PREFIX,
				Ifc2RdfVocabulary.EXPRESS.getBaseUri());

		// define ifc:
		if (converter != null) {
			jenaModel.setNsPrefix(Ifc2RdfVocabulary.IFC.BASE_PREFIX,
					converter.getIfcOntologyNamespaceUri());
		}
	}

	public static IfcSchema getTestIfcSchema() {
		return IfcSchemaPool.getSchema(TEST_SCHEMA_VERSION);
	}
	
	public static IfcModel getTestIfcModel() throws IfcParserException, IOException {
		
		if (testIfcModel == null) {
			testIfcModel = IfcParserUtil.parseModel(TEST_IFC_MODEL_FILE_PATH);
		}
		return testIfcModel;
		
	}
	
	
	
	
//	public void assertEquals(Resource r1, Resource r2) {
//		
//		StmtIterator it1 = r1.listProperties();		
//		StmtIterator it2 = r2.listProperties();
//		
//		List<Pair<Property, RDFNode>> l1 = toPropertyList(it1);
//		List<Pair<Property, RDFNode>> l2 = toPropertyList(it2);
//		
//		assertEquals(l1.size(), l2.size());		
//	}
//	
//	public static List<Pair<Property, RDFNode>> toPropertyList(StmtIterator it) {
//		
//		ArrayList<Pair<Property, RDFNode>> list = new ArrayList<Pair<Property, RDFNode>>(); 
//
//		while (it.hasNext()) {
//			Statement s = it.nextStatement();
//			list.add(new Pair<Property, RDFNode>(s.getPredicate(), s.getObject()));
//		}
//		
//		list.sort(DrumbeatTestHelper::compare);
//		return list;
//	}
//	
//	public static int compare(Pair<Property, RDFNode> p1, Pair<Property, RDFNode> p2) {
//		int result;
//		if ((result = compare(p1.getKey(), p2.getKey())) != 0) {
//			return result;
//		}
//		
//		return compare(p1.getValue(), p2.getValue());
//	}
//	
//	public static int compare(RDFNode o1, RDFNode o2) {
//		int t1 = getRdfNodeType(o1);
//		int t2 = getRdfNodeType(o2);
//		
//		int result;
//		if ((result = Integer.compare(t1, t2)) != 0) {
//			return result;
//		}
//		
//		return o1.toString().compareTo(o2.toString());
//	}
//	
//	public static int getRdfNodeType(RDFNode o) {
//		if (o.isURIResource()) {
//			return 0;
//		} else if (o.isAnon()) {
//			return 1;
//		} else {
//			return 2;
//		}
//	}
}
