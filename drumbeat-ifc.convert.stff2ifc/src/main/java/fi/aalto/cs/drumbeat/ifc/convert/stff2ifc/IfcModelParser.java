package fi.aalto.cs.drumbeat.ifc.convert.stff2ifc;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fi.aalto.cs.drumbeat.common.file.FileManager;
import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;

public class IfcModelParser {	
	
//	public static IfcModel parse(InputStream input) throws IfcParserException {
//		return parse(input, null);
//	}

	public static IfcModel parse(InputStream input, String fileNameOrExtension) throws IfcParserException {
		
		if (fileNameOrExtension != null) {
			
			try {
				String[] tokens = fileNameOrExtension.split("\\.");
				
				for (int i = tokens.length - 1; i >= 0; --i) {
					switch (tokens[i]) {
					
					case FileManager.FILE_EXTENSION_GZ:
					case FileManager.FILE_EXTENSION_GZIP:
							input = new GZIPInputStream(input);
							break;
					case FileManager.FILE_EXTENSION_ZIP:
					case IfcVocabulary.SpfFormat.FILE_EXTENSION_IFC_ZIP:
						ZipInputStream zipInput = new ZipInputStream(input);
						ZipEntry zipEntry = zipInput.getNextEntry();
						return parse(zipInput, zipEntry.getName());
						
					case IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_IFC_XML:
					case IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_IFX:
					case IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_XML:
						IfcXmlModelParser xmlParser = new IfcXmlModelParser(input);
						return xmlParser.parseModel();
	
					case IfcVocabulary.SpfFormat.FILE_EXTENSION_IFC:
					case IfcVocabulary.SpfFormat.FILE_EXTENSION_STP:
						IfcSpfModelParser spfParser = new IfcSpfModelParser(input);
						return spfParser.parseModel();
						
					default:
						throw new IfcParserException("Unknown file type: " + fileNameOrExtension);
					
					}
				}
			
			} catch (IOException e) {
				throw new IfcParserException("Parsing error: " + e.getMessage());
			}
			
		}
		
		IfcSpfModelParser parser = new IfcSpfModelParser(input);
		return parser.parseModel();
	}

	

}
