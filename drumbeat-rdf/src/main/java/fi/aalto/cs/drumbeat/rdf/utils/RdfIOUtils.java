package fi.aalto.cs.drumbeat.rdf.utils;

import java.io.IOException;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.rdf.model.Model;

import fi.aalto.cs.drumbeat.common.io.SerializedInputStream;

public class RdfIOUtils {
	
	
	public static RDFFormat getRdfFormatFromFilePath(String filePath) {
		filePath = filePath.toLowerCase();
		if (filePath.endsWith(".gzip")) {
			filePath = filePath.substring(0, filePath.length() - 5);
		} else if (filePath.endsWith(".gz")) {
			filePath = filePath.substring(0, filePath.length() - 3);			
		}
		
		if (filePath.endsWith("ttl")) {
			return RDFFormat.TURTLE;
		} else if (filePath.endsWith("nt")) {
			return RDFFormat.NTRIPLES;
		} else if (filePath.endsWith("nq")) {
			return RDFFormat.NQUADS;
		} else if (filePath.endsWith("jsonld")) {
			return RDFFormat.JSONLD;
		} else if (filePath.endsWith("trig")) {
			return RDFFormat.TRIG;
		} else {
			return RDFFormat.RDFXML;
		}
	}
	
	public static Lang getRdfLangFromFilePath(String filePath) {
		filePath = filePath.toLowerCase();
		if (filePath.endsWith(".gzip")) {
			filePath = filePath.substring(0, filePath.length() - 5);
		} else if (filePath.endsWith(".gz")) {
			filePath = filePath.substring(0, filePath.length() - 3);			
		}
		
		return RDFLanguages.filenameToLang(filePath);
	}
	
	
	public static void importRdfFileToJenaModel(Model model, String filePath) throws IOException {
		
		SerializedInputStream sis = SerializedInputStream.getUncompressedInputStream(filePath);
		RDFDataMgr.read(model, sis.getInputStream(), RDFLanguages.filenameToLang(sis.getSerializationInfo()));
		
	}
	
}
