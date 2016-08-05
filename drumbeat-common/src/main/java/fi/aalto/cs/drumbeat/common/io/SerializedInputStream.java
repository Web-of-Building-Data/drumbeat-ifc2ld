package fi.aalto.cs.drumbeat.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import fi.aalto.cs.drumbeat.common.file.FileManager;

public class SerializedInputStream {
	
	private InputStream in;
	private String serializationInfo;
	
	public SerializedInputStream(String filePath) throws FileNotFoundException {
		this.in = new FileInputStream(filePath);
		this.serializationInfo = filePath;		
	}
	
	public SerializedInputStream(File file) throws FileNotFoundException {
		this.in = new FileInputStream(file);
		this.serializationInfo = file.getName();		
	}

	public SerializedInputStream(InputStream in, String serializationInfo) {
		this.in = in;
		this.serializationInfo = serializationInfo;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public String getSerializationInfo() {
		return serializationInfo;
	}
	
	public void setSerializationFormat(String serializationInfo) {
		this.serializationInfo = serializationInfo;
	}
	
	public SerializedInputStream uncompress() throws IOException {
		return getUncompressedInputStream(in, serializationInfo);
	}
	

	
	public static SerializedInputStream getUncompressedInputStream(String filePath) throws IOException {
		return getUncompressedInputStream(new FileInputStream(filePath), new File(filePath).getName());
	}
	
	
	public static SerializedInputStream getUncompressedInputStream(InputStream in, String serializationInfo) throws IOException {
		
		if (serializationInfo == null) {
			throw new IllegalArgumentException("Serialization format is null");
		}
		
		if (FileManager.hasFileExtension(serializationInfo, FileManager.FILE_EXTENSION_GZIP)) {
			
			return getUncompressedInputStream(
					new GZIPInputStream(in),
					FileManager.removeLastExtension(serializationInfo));
			
		} else if (FileManager.hasFileExtension(serializationInfo, FileManager.FILE_EXTENSION_ZIP)) {
			
			ZipInputStream zipInput = new ZipInputStream(in);
			return getUncompressedInputStream(
					zipInput,
					zipInput.getNextEntry().getName());
			
		} else {
			
			return new SerializedInputStream(in, serializationInfo);
			
		}		
		
		
	}
	

}
