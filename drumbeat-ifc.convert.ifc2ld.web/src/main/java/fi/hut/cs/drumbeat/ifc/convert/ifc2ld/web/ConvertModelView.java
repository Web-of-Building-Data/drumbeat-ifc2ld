package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.*;

import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import fi.hut.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.hut.cs.drumbeat.common.config.ConfigurationPool;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.common.file.FileManager;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.modelfactory.config.JenaModelFactoryPoolConfigurationSection;

@SuppressWarnings("serial")
public class ConvertModelView extends FormLayout {

	private static final Object CSV_FORMAT = new Object();
	
	private ComboBox cbOutputTypes;
	private ComboBox cbRdfFormats;
	private CheckBox chbGzipFile;
	private boolean outputToFile;
	
	private ConfigurationPool<ConfigurationItemEx> configurationPool;

	public ConvertModelView() {
		
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
		
		cbOutputTypes = new ComboBox("Output Type");
		try {
			configurationPool = JenaModelFactoryPoolConfigurationSection.getInstance().getConfigurationPool();
		} catch (ConfigurationParserException e1) {
			e1.printStackTrace();
			Notification.show(e1.getMessage(), Type.ERROR_MESSAGE);
			return;
		}
		cbOutputTypes.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbOutputTypes.setRequired(true);
		cbOutputTypes.setNullSelectionAllowed(false);
		for (ConfigurationItemEx configuration : configurationPool) {
			cbOutputTypes.addItem(configuration);
			cbOutputTypes.setItemCaption(configuration, configuration.getName());
			if (configuration.isDefault()) {
				cbOutputTypes.setValue(configuration);
			}
		}
		addComponent(cbOutputTypes);
		
		
		cbRdfFormats = new ComboBox("RDF Format");
		
		cbRdfFormats.addItem(CSV_FORMAT);
		cbRdfFormats.setItemCaption(CSV_FORMAT, "CSV Format (non-standard)");		
		
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
		
		
		chbGzipFile = new CheckBox("gzip compression");
		chbGzipFile.setVisible(false);
		addComponent(chbGzipFile);
		
		
		cbOutputTypes.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				onOutputTypeChanged();
			}
		});
		
		Button btnConvert = new Button("Convert");
		btnConvert.setEnabled(false);
		addComponent(btnConvert);
		
		btnConvert.addClickListener(new ClickListener() {
			
		    @SuppressWarnings("deprecation")
			@Override
		    public void buttonClick(ClickEvent event) {
	    		try {
		    		String fileName = lblFileName.getValue();
	    			
			    	if (outputToFile) {
			    		Object outputFormat = cbRdfFormats.getValue(); 
			    		boolean export2Neo4j = outputFormat.equals(CSV_FORMAT); 
			    		
			    		RDFFormat rdfFormat = export2Neo4j ? RDFFormat.NQUADS : (RDFFormat)outputFormat;
				    	Boolean gzipOutputFile = chbGzipFile.getValue();
				    	
				    	Model outputModel = IfcApplication.convertIfcModelToJenaModel(fileName, null);
				    	File outputFile = IfcApplication.exportJenaModelToFile(outputModel, fileName, rdfFormat, gzipOutputFile);
				    	
				    	if (!export2Neo4j) {

					    	FileResource resource = new FileResource(outputFile);			
							Page.getCurrent().open(resource, null, false);
							
				    	} else {
				    		IfcApplication.convertRdfFileToNeo4j(outputFile);
				    	}
				    	
			    	} else {
//			    		exportToJenaModel(fileName);			    		
			    	}
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
		
		
		onOutputTypeChanged();
		
	}
	
	
	private void onOutputTypeChanged() {
		outputToFile = cbOutputTypes.getValue().equals(configurationPool.get(0));
		cbRdfFormats.setVisible(outputToFile);
		cbRdfFormats.setRequired(outputToFile);
		chbGzipFile.setValidationVisible(outputToFile);
	}
	
	
	private void exportToJenaModel(String fileName) throws Exception {
		ConfigurationItemEx configuration = (ConfigurationItemEx)cbOutputTypes.getValue();

		// TODO:
//		JenaModelFactoryBase modelFactory = new VirtuosoJenaModelFactory1("Virtuoso", configuration.getProperties());
//		
////		JenaModelFactoryBase modelFactory = JenaModelFactoryBase.getFactory(configuration);
//		Model jenaModel = IfcApplication.convertIfcModelToJenaModel(fileName, modelFactory.createModel());
		
		
	}
	
	
	
	

}
