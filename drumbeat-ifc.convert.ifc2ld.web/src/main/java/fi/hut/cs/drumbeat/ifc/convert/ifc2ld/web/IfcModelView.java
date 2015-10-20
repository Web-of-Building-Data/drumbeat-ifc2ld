package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.*;

import org.apache.jena.riot.RDFFormat;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import fi.hut.cs.drumbeat.common.file.FileManager;
import fi.hut.cs.drumbeat.rdf.RdfUtils;

@SuppressWarnings("serial")
public class IfcModelView extends FormLayout {

	public IfcModelView() {
		
		Upload upload = new Upload("IFC Data File", new Receiver() {
			
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					FileManager.createDirectory(IfcApplication.TEMP_UPLOADS_PATH);
					return new FileOutputStream(IfcApplication.TEMP_UPLOADS_PATH + "/" + filename, false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
					return null;
				}
			}
			
		});
		upload.setButtonCaption("Upload to Server");		
		upload.setWidth(MainUI.DEFAULT_WIDTH * 2, Unit.PIXELS);
//		upload.setRequired(true);
//		upload.setNullSelectionAllowed(false);
//		upload.setValue(OwlProfileEnum.OWL2_DL);
		addComponent(upload);
		
		
		Label lblFileName = new Label();
		lblFileName.setCaption("File Name");
		addComponent(lblFileName);
		
		
		ComboBox cbRdfFormats = new ComboBox("RDF Format");		
		RdfUtils.getRdfFormatMap().entrySet().forEach(x -> {
			cbRdfFormats.addItem(x.getValue());
			cbRdfFormats.setItemCaption(x.getValue(), x.getKey());
		}); 
		cbRdfFormats.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbRdfFormats.setRequired(true);
		cbRdfFormats.setNullSelectionAllowed(false);
		cbRdfFormats.setValue(RDFFormat.TURTLE_PRETTY);
//		cbRdfFormats.setComponentError(new UserError("Select an IFC schema!"));
//		cbRdfFormats.setValidationVisible(false);		
		addComponent(cbRdfFormats);
		
		
		CheckBox chbGzipFile = new CheckBox("gzip compression");
		addComponent(chbGzipFile);		
		
		
		
		Button btnConvert = new Button("Convert");
		btnConvert.setEnabled(false);
		addComponent(btnConvert);
		
		btnConvert.addClickListener(new ClickListener() {
			
		    @SuppressWarnings("deprecation")
			@Override
		    public void buttonClick(ClickEvent event) {
		    		String modelName = lblFileName.getValue();
		    		RDFFormat rdfFormat = (RDFFormat)cbRdfFormats.getValue();
		    		Boolean gzip = chbGzipFile.getValue();		    		
		    		try {
						File modelFile = IfcApplication.exportModel(modelName, rdfFormat, gzip);
						FileResource resource = new FileResource(modelFile);
//						FileDownloader downloader = new FileDownloader(resource);
//						downloader.extend(btnConvert);
						
						Page.getCurrent().open(resource, null, false);
						
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
					}
		        
		        
		    }
		    
		});
		
		
		upload.addSucceededListener(new SucceededListener() {
			
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				lblFileName.setValue(event.getFilename());
				btnConvert.setEnabled(true);				
			}
			
		});
		

		
		
		
	}
	

}
