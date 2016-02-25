package fi.aalto.cs.drumbeat.ifc.convert.stff2ifc;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.NotImplementedException;

import fi.aalto.cs.drumbeat.common.file.FileManager;
import fi.aalto.cs.drumbeat.common.string.StringUtils;
import fi.aalto.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.aalto.cs.drumbeat.ifc.data.IfcVocabulary;
import fi.aalto.cs.drumbeat.ifc.data.metamodel.IfcMetaModel;
import fi.aalto.cs.drumbeat.ifc.data.model.*;
import fi.aalto.cs.drumbeat.ifc.data.schema.*;


public class IfcModelParser  {
	
	IfcLineReader lineReader;

	protected IfcModelParser(InputStream input) {
		lineReader = new IfcLineReader(input);		
	}

	public static IfcModel parse(InputStream input) throws IfcParserException {
		IfcModelParser parser = new IfcModelParser(input);
		return parser.parseModel();
	}

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
					case IfcVocabulary.StepFormat.FILE_EXTENSION_IFC_ZIP:
						ZipInputStream zipInput = new ZipInputStream(input);
						ZipEntry zipEntry = zipInput.getNextEntry();
						return parse(zipInput, zipEntry.getName());
						
					case IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_IFC_XML:
					case IfcVocabulary.IfcXmlFormat.FILE_EXTENSION_IFX:
						throw new NotImplementedException("Parsing .ifcxml is not implemented yet");
	
					case IfcVocabulary.StepFormat.FILE_EXTENSION_IFC:
					case IfcVocabulary.StepFormat.FILE_EXTENSION_STP:
					default:
						IfcModelParser parser = new IfcModelParser(input);
						return parser.parseModel();
					
					}
				}
			
			} catch (IOException e) {
				throw new IfcParserException("Parsing error: " + e.getMessage());
			}
			
		}
		
		IfcModelParser parser = new IfcModelParser(input);
		return parser.parseModel();
	}

	private IfcModel parseModel() throws IfcParserException {

		try {
			
			//
			// detect the schema version info and get the schema from the schema pool
			//
			IfcMetaModel metaModel;
			
			String statement = lineReader.getNextStatement();
			statement = lineReader.getNextStatement();
			if (statement != null && statement.startsWith(IfcVocabulary.StepFormat.HEADER)) {
				StringBuilder headerStringBuilder = new StringBuilder(256);
				while ((statement = lineReader.getNextStatement()) != null && !statement.startsWith(IfcVocabulary.StepFormat.ENDSEC)) {
					headerStringBuilder.append(statement);
					headerStringBuilder.append(StringUtils.SEMICOLON_CHAR);
				}
				
				IfcLineReader headerReader = new IfcLineReader(new ByteArrayInputStream(headerStringBuilder.toString().getBytes()));
				IfcSchema stepSchema = IfcSchemaParser.getStepSchema();
				List<IfcEntity> entities = new IfcModelSectionParser().parseEntities(headerReader, stepSchema, true, true);
				
				metaModel = new IfcMetaModel(stepSchema);
				metaModel.addEntities(entities);
			} else {
				throw new IfcFormatException(lineReader.getCurrentLineNumber(), String.format("Expected '%s'", IfcVocabulary.StepFormat.HEADER));
			}
			
			IfcSchema schema = null;
			List<String> schemaVersions = metaModel.getFileSchema().getSchemas();
			for (String schemaVersion : schemaVersions) {
				schema = IfcSchemaPool.getSchema(schemaVersion);
				if (schema != null) {
					break;
				}
			}
			
			if (schema == null) {
				throw new IfcNotFoundException("None of schemas " + schemaVersions + " is found");
			}

			//
			// create a new model 
			//
			IfcModel model = new IfcModel(schema, metaModel);

			//
			// parse entities
			//
			statement = lineReader.getNextStatement();
			
			if (!statement.equalsIgnoreCase(IfcVocabulary.StepFormat.DATA)) {
				throw new IfcParserException("Expected statement: " + IfcVocabulary.StepFormat.DATA);
			}
			
			List<IfcEntity> entities = new IfcModelSectionParser().parseEntities(lineReader, schema, false, false);
			model.addEntities(entities);

			return model;

		} catch (IfcParserException e) {
			throw e;
		} catch (Exception e) {
			throw new IfcParserException(e);
		}
	}

		
	
//	private static IfcLiteralTypeInfo getLiteralTypeInfo(IfcAttributeInfo attributeInfo) {
//		IfcTypeInfo typeInfo = attributeInfo.getAttributeTypeInfo();				
//
//		if (typeInfo instanceof IfcCollectionTypeInfo) {
//			typeInfo = ((IfcCollectionTypeInfo)typeInfo).getItemTypeInfo();
//		}
//		
//		while (typeInfo instanceof IfcDefinedTypeInfo) {
//			typeInfo = ((IfcDefinedTypeInfo)typeInfo).getDefinedTypeInfo();
//		}
//		
//		if (typeInfo instanceof IfcLiteralTypeInfo) {
//			return (IfcLiteralTypeInfo)typeInfo;			
//		} else {
//			return null;
//		}
//	}
	
	
	
	/**
	 * Gets the next statement from the input stream   
	 * @return Tokens of the next statement in the file (without semicolon)
	 * @throws IOException
	 * @throws IfcFormatException
	 */
//	@Override
//	protected String getNextStatement() throws IOException, IfcFormatException {
//		String line;
//		while ((line = reader.readLine()) != null) {
//			if (line.endsWith(StringUtils.SEMICOLON)) {
//				return line.substring(0, line.length()-1);
//			} else if (!line.isEmpty()) {
//				throw new IfcFormatException(reader.getCurrentLineNumber(), String.format("Line is not ended with '%s'", StringUtils.SEMICOLON));
//			}
//		}
//		return line;		
//	}

}
