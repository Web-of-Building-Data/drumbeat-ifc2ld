package fi.hut.cs.drumbeat.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.hut.cs.drumbeat.common.file.FileManager;

public class RdfUtils {
	
	private static final Logger logger = Logger.getRootLogger();
	private static Map<String, RDFFormat> rdfFormats;

	public static String getShortString(RDFNode node) {
		if (node.isLiteral()) {
			return node.toString();
		} else {
			return getShortString(node.asResource());
		}
	}

	public static String getShortStringWithType(RDFNode node) {
		if (node.isLiteral()) {
			return node.asLiteral().getLexicalForm();
		} else {
			return getShortStringWithType(node.asResource());
		}
	}


	public static String getShortString(Resource resource) {
		if (resource.isURIResource()) {
			return resource.getLocalName();
		} else {
			return resource.getId().toString();
		}
	}
	
	public static String getShortStringWithType(Resource resource) {
		Resource type = getType(resource);
		String typeLetter;
		if (resource.isURIResource()) {
			typeLetter = "U";
		} else {
			typeLetter = "B";
		}
		
		return String.format("%s/%s(%s)", getShortString(resource), type != null ? type.getLocalName() : "", typeLetter);		
	}
	

	public static String getShortString(Statement statement) {
		return String.format("[%s, %s, %s]",
				RdfUtils.getShortString(statement.getSubject()),
				RdfUtils.getShortString(statement.getPredicate()),
				RdfUtils.getShortString(statement.getObject()));
	}
	
	public static String getShortStringWithTypes(Statement statement) {
		return String.format("[%s, %s, %s]",
				RdfUtils.getShortStringWithType(statement.getSubject()),
				RdfUtils.getShortString(statement.getPredicate()),
				RdfUtils.getShortStringWithType(statement.getObject()));
	}
	
	public static Resource getType(Resource resource) {
		return resource.getPropertyResourceValue(RDF.type);
	}
	
	
	public static int getNumberOfIncomingLinks(Resource resource) {
		 StmtIterator stmtIterator = resource.getModel().listStatements(null, null, resource);
		 int count = 0;
		 for (; stmtIterator.hasNext(); ++count) {
			 stmtIterator.next();
		 }
		 return count;
	}
	
	public static String formatRdfFileName(String filePath, RDFFormat format, boolean gzip) {
		String fileExtension = RdfVocabulary.getRdfFileExtension(format);
		if (gzip) {
			fileExtension += ".gz";
		}
				
		String filePathWithExtension = FileManager.createFileNameWithExtension(filePath, fileExtension);
		return filePathWithExtension;
	}	
	
	public static String exportJenaModelToRdfFile(Model model, String filePath, RDFFormat format, boolean gzip) throws IOException {
		
		String filePathWithExtension = formatRdfFileName(filePath, format, gzip);		

		logger.info(String.format("Exporting graph to file '%s' with format '%s'", filePathWithExtension, format));
		File file = FileManager.createFile(filePathWithExtension);
		OutputStream out = new FileOutputStream(file);
		if (gzip) {
			out = new GZIPOutputStream(out);
		}
		try {
			RDFDataMgr.write(out, model, format);
		}
		finally {
			out.close();
		}
		logger.info(String.format("Exporting graph to file is completed, file size: %s", FileManager.getReadableFileSize(file.length())));
		
		return filePathWithExtension;
	}
	
	public static void importRdfFileToJenaModel(Model model, String filePath) throws IOException {		
		InputStream in = new FileInputStream(filePath);
		RDFDataMgr.read(model, in, RdfVocabulary.getRdfLang(filePath));
	}
	
	public static Map<String, RDFFormat> getRdfFormatMap() {
		
		if (rdfFormats == null) {
			rdfFormats = new TreeMap<>();
			Field[] declaredFields = RDFFormat.class.getDeclaredFields();
			for (Field field : declaredFields) {
			    if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(RDFFormat.class)) {			    	
			    	try {
						rdfFormats.put(field.getName(), (RDFFormat)field.get(null));
					} catch (Exception e) {
						throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
					}
			    }
			}			
		}
		
		return rdfFormats;
		
	}
	
	
}
