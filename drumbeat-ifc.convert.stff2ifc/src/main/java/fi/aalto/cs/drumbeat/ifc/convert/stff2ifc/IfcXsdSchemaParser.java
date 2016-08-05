//package fi.aalto.cs.drumbeat.ifc.convert.stff2ifc;
//
///**
// * This class is for parsing IFC schema from an input stream.
// * 
// * The IFC syntax format is based on this doc:
// * http://iaiweb.lbl.gov/Resources/IFC_Releases/IFC_Release_2.0/BETA_Docs_for_Review/IFC_R2_SpecDevGuide_Beta_d2.PDF
// * (page A-27).
// * 
// * This parser includes only minimum syntax checking and ignores many insignificant keywords
// * such as SUPERTYPE OF, DERIVE, WHERE, INVERSE, etc.
// * 
// *  
// * @author Nam Vu Hoang
// * 
// * History:
// * 20120217 - Created
// */
//
//import java.io.*;
//import java.util.*;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.xml.sax.SAXException;
//
//import fi.aalto.cs.drumbeat.common.string.RegexUtils;
//import fi.aalto.cs.drumbeat.common.string.StringUtils;
//import fi.aalto.cs.drumbeat.ifc.common.IfcException;
//import fi.aalto.cs.drumbeat.ifc.common.IfcHelper;
//import fi.aalto.cs.drumbeat.ifc.common.IfcNotFoundException;
//import fi.aalto.cs.drumbeat.ifc.data.Cardinality;
//import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
//import fi.aalto.cs.drumbeat.ifc.data.schema.*;
//
//
///**
// * 
// * Parser of IFC schema from input stream   
// * 
// * @author vuhoan1
// *
// */
//public class IfcXsdSchemaParser {
//	
//	/**
//	 * Cache for EXPRESS schema of STFF header section
//	 */
//	private static IfcSchema stffExpressSchema;
//	
//	private Element documentElement;
//	
//	/**
//	 * The output schema
//	 */
//	private IfcSchema schema;
//	
//	private List<IfcEntityTypeInfoText> entityTypeInfoTexts = new ArrayList<IfcEntityTypeInfoText>();
//	
//	/**
//	 * Creates a new parser. For internal use.
//	 * 
//	 * @param input
//	 */
//	protected IfcXsdSchemaParser(InputStream input) {
//		try {
//			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document document = documentBuilder.parse(input);
//			documentElement = document.getDocumentElement();
//		} catch (SAXException | IOException | ParserConfigurationException e) {
//		}
//	}
//
//	/**
//	 * Parses an IFC schema from an input stream. This is the main entry of the parser. 
//	 * 
//	 * @param input
//	 * @return {@link IfcSchema}
//	 * @throws IfcParserException
//	 */
//	public static IfcSchema parse(InputStream input) throws IfcParserException {		
//		IfcXsdSchemaParser parser = new IfcXsdSchemaParser(input);		
//		return parser.parseSchema();
//	}	
//	
//	/**
//	 * Parses schema
//	 * 
//	 * @return {@link IfcSchema}
//	 * @throws IfcParserException
//	 */
//	private IfcSchema parseSchema() throws IfcParserException {
//		
//		
//		
//		
//	}	
//	
//}
