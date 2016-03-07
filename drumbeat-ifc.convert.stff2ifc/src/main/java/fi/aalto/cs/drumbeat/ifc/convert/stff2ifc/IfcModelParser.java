package fi.aalto.cs.drumbeat.ifc.convert.stff2ifc;

import java.io.IOException;
import java.io.InputStream;

import fi.aalto.cs.drumbeat.common.file.FileManager;
import fi.aalto.cs.drumbeat.common.io.SerializedInputStream;
import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.model.IfcModel;

public class IfcModelParser {	
	
	public static IfcModel parse(String filePath) throws IfcParserException, IOException {
		return parse(new SerializedInputStream(filePath));
	}

	public static IfcModel parse(SerializedInputStream sis) throws IfcParserException, IOException {
		sis = sis.uncompress();
		
		InputStream in = sis.getInputStream();
		String filePath = sis.getSerializationInfo();
		
		if (FileManager.hasFileExtension(filePath, IfcVocabulary.SpfFormat.FILE_EXTENSION_IFC_ZIP)) {
			
			filePath = FileManager.replaceLastExtension(filePath, FileManager.FILE_EXTENSION_ZIP);
			return parse(SerializedInputStream.getUncompressedInputStream(in, filePath));
			
		} else if (FileManager.hasAnyFileExtension(
				filePath,
				IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_IFC_XML,
				IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_IFX,
				IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_XML))
		{
			
			IfcXmlModelParser xmlParser = new IfcXmlModelParser(in);
			return xmlParser.parseModel();
			
		} else if (FileManager.hasAnyFileExtension(
				filePath,
				IfcVocabulary.SpfFormat.FILE_EXTENSION_IFC,
				IfcVocabulary.SpfFormat.FILE_EXTENSION_STP))
		{
			
			IfcSpfModelParser spfParser = new IfcSpfModelParser(in);
			return spfParser.parseModel();
			
		} else {
			
			throw new IfcParserException("Unknown serialization format: " + filePath);
			
		}
		
		
	}



}
