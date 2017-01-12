package fi.hut.cs.drumbeat.rdf;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.common.string.StringUtils;

public class RdfVocabulary {
	
	public static final Model DEFAULT_MODEL = ModelFactory.createDefaultModel();
	
	public final static Resource DUMP_URI_1 = DEFAULT_MODEL.createResource("http://example.org/uri1");
	public final static Resource DUMP_URI_2 = DEFAULT_MODEL.createResource("http://example.org/uri2");
	public final static Resource DUMP_URI_3 = DEFAULT_MODEL.createResource("http://example.org/uri3");
	public final static RDFList DUMP_URI_LIST = DEFAULT_MODEL.createList(new RDFNode[]{DUMP_URI_1, DUMP_URI_2, DUMP_URI_3});
	
	
	public static class OWL {
		public static final String BASE_PREFIX = "owl";
		public static final String BASE_URI = "http://www.w3.org/2002/07/owl#";
		
		public static final Property FunctionalDataProperty = DEFAULT_MODEL.createProperty(BASE_URI + "FunctionalDataProperty");

		public static final Resource rational = DEFAULT_MODEL.createResource(BASE_URI + "rational");
		public static final Resource real = DEFAULT_MODEL.createResource(BASE_URI + "real");
	}
	
	public static class RDF {
		public static final String BASE_PREFIX = "rdf";
	}

	public static class RDFS {
		public static final String BASE_PREFIX = "rdfs";
	}

	public static class XSD {
		public static final String BASE_PREFIX = "xsd";
		public static final Property maxExclusive = DEFAULT_MODEL.createProperty(com.hp.hpl.jena.vocabulary.XSD.getURI() + "maxExclusive");
		public static final Property minExclusive = DEFAULT_MODEL.createProperty(com.hp.hpl.jena.vocabulary.XSD.getURI() + "minExclusive");		
	}	
	
//	public static class OLO {
//		public static final String BASE_PREFIX = "olo";
//		public static final String BASE_URI = "http://purl.org/ontology/olo/core#";
//		public static final String IMPORT_URI = "http://purl.org/ontology/olo/orderedlistontology.owl"; 
//
//		public static final Resource OrderedList = DEFAULT_MODEL.createResource(BASE_URI + "OrderedList");
//		public static final Resource Slot = DEFAULT_MODEL.createResource(BASE_URI +  "Slot");
//		
//		public static final Property index = DEFAULT_MODEL.createProperty(BASE_URI +  "index");
//		public static final Property item = DEFAULT_MODEL.createProperty(BASE_URI +  "item");
//		public static final Property length = DEFAULT_MODEL.createProperty(BASE_URI +  "length");
//		public static final Property slot = DEFAULT_MODEL.createProperty(BASE_URI +  "slot");		
//	}
	
	public static class VOID {
		public static final String BASE_PREFIX = "void";
		public static final String BASE_URI = "http://rdfs.org/ns/void#";
		
		public static final Resource DataSet = DEFAULT_MODEL.createResource(BASE_URI + "DataSet");
	}
	
	public static class DCTERMS {
		public static final String BASE_PREFIX = "dcterms";
		public static final String BASE_URI = "http://purl.org/dc/terms/";
		
		public static final Property contributor  = DEFAULT_MODEL.createProperty(BASE_URI + "contributor");
		public static final Property created  = DEFAULT_MODEL.createProperty(BASE_URI + "created");
		public static final Property creator  = DEFAULT_MODEL.createProperty(BASE_URI + "creator");
		public static final Property date  = DEFAULT_MODEL.createProperty(BASE_URI + "date");
		public static final Property hasVersion  = DEFAULT_MODEL.createProperty(BASE_URI + "hasVersion");
		public static final Property issued  = DEFAULT_MODEL.createProperty(BASE_URI + "issued");
		public static final Property modified  = DEFAULT_MODEL.createProperty(BASE_URI + "modified");
		public static final Property publisher  = DEFAULT_MODEL.createProperty(BASE_URI + "publisher");
		public static final Property subject  = DEFAULT_MODEL.createProperty(BASE_URI + "subject");
		public static final Property source  = DEFAULT_MODEL.createProperty(BASE_URI + "source");
		public static final Property title  = DEFAULT_MODEL.createProperty(BASE_URI + "title");		
		public static final Property description  = DEFAULT_MODEL.createProperty(BASE_URI + "description");
		
		
		
	}
	
	public static String getRdfFileExtension(RDFFormat format) {
		// See: http://jena.apache.org/documentation/io/
		String formatString = format.toString().toUpperCase();		
		
		if (formatString.startsWith("RDF")) {
			if (formatString.contains("XML")) {
				// RDF/XML --> .rdf
				return "rdf";
			} else if (formatString.contains("JSON")) {
				// RDF/JSON --> .rj
				return "rj";
			} else if (formatString.contains("THRIFT")) {
				return "rt";
			}
		} else if (formatString.startsWith("TURTLE") || formatString.startsWith("TTL")) {
			return "ttl"; // similar to .n3
		} else if (formatString.startsWith("N-TRIPLES") || formatString.startsWith("NT")) {
			return "nt";
		} else if (formatString.startsWith("N-QUADS") || formatString.startsWith("NQ"))  {
			return "nq";
		} else if (formatString.startsWith("JSON")) {
			return "jsonld";
		} else if (formatString.startsWith("TRIG")) {
			return "trig";
		}
		return "rdf";
	}
	
	public static RDFFormat getRdfFormat(String filePath) {
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
	
	public static Lang getRdfLang(String filePath) {
		filePath = filePath.toLowerCase();
		if (filePath.endsWith(".gzip")) {
			filePath = filePath.substring(0, filePath.length() - 5);
		} else if (filePath.endsWith(".gz")) {
			filePath = filePath.substring(0, filePath.length() - 3);			
		}
		
		if (filePath.endsWith("ttl")) {
			return Lang.TURTLE;
		} else if (filePath.endsWith("nt")) {
			return Lang.NTRIPLES;
		} else if (filePath.endsWith("nq")) {
			return Lang.NQUADS;
		} else if (filePath.endsWith("jsonld")) {
			return Lang.JSONLD;
		} else if (filePath.endsWith("trig")) {
			return Lang.TRIG;
		} else if (filePath.endsWith("csv")) {
			return Lang.CSV;
		} else {
			return Lang.RDFXML;
		}
	}
	
}
