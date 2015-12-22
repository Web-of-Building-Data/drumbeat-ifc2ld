package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFFormatVariant;
import org.apache.log4j.Logger;

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

import fi.aalto.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.aalto.cs.drumbeat.common.config.ConfigurationPool;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.aalto.cs.drumbeat.common.file.FileManager;
import fi.aalto.cs.drumbeat.rdf.RdfUtils;
import fi.aalto.cs.drumbeat.rdf.jena.provider.config.JenaProviderPoolConfigurationSection;

@SuppressWarnings("serial")
public class ConvertModelView extends FormLayout {

//	private static final Object CSV_FORMAT = new Object();
	
	
	private CheckBox chbSaveFile;
//	private ComboBox cbOutputTypes;
	private ComboBox cbRdfFormats;
	private CheckBox chbGzipFile;
//	private boolean outputToFile;
	
	private ConfigurationPool<ConfigurationItemEx> configurationPool;

	public ConvertModelView() {
		
		Upload upload = new Upload("IFC Data File", new Receiver() {
			
			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					FileManager.createDirectory(IfcApplication.TEMP_UPLOADS_DIR_PATH);
					return new FileOutputStream(IfcApplication.TEMP_UPLOADS_DIR_PATH + "/" + filename, false);
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
		
//		cbOutputTypes = new ComboBox("Output Type");
//		try {
//			configurationPool = JenaProviderPoolConfigurationSection.getInstance().getConfigurationPool();
//		} catch (ConfigurationParserException e1) {
//			e1.printStackTrace();
//			Notification.show(e1.getMessage(), Type.ERROR_MESSAGE);
//			return;
//		}
//		cbOutputTypes.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
//		cbOutputTypes.setRequired(true);
//		cbOutputTypes.setNullSelectionAllowed(false);
//		for (ConfigurationItemEx configuration : configurationPool) {
//			cbOutputTypes.addItem(configuration);
//			cbOutputTypes.setItemCaption(configuration, configuration.getName());
//			if (configuration.isDefault()) {
//				cbOutputTypes.setValue(configuration);
//			}
//		}
//		addComponent(cbOutputTypes);
		
		chbSaveFile = new CheckBox("Save file");
		addComponent(chbSaveFile);		
		
		cbRdfFormats = new ComboBox("RDF Format");
		fillComboBoxWithRdfFormats(cbRdfFormats);
	
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
		
		
		CheckBox chbExportToVirtuoso = new CheckBox("Export to Virtuoso");
		addComponent(chbExportToVirtuoso);
		
		CheckBox chbExportToNeo4j = new CheckBox("Export to Neo4j");
		addComponent(chbExportToNeo4j);
		
		
//		cbOutputTypes.addValueChangeListener(new ValueChangeListener() {
//			
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				onOutputTypeChanged();
//			}
//		});
		
		Button btnConvert = new Button("Convert");
		btnConvert.setEnabled(false);
		addComponent(btnConvert);
		
		btnConvert.addClickListener(new ClickListener() {
			
		    @SuppressWarnings("deprecation")
			@Override
		    public void buttonClick(ClickEvent event) {
	    		try {
		    		String fileName = lblFileName.getValue();
		    		
		    		File ntriplesOutputFile = null;

		    		if (chbSaveFile.getValue()) {
			    		RDFFormat rdfFormat = (RDFFormat)cbRdfFormats.getValue();
				    	Boolean gzipOutputFile = chbGzipFile.getValue();
				    	
				    	Model outputModel = IfcApplication.convertIfcModelToJenaModel(fileName, null);
				    	File outputFile = IfcApplication.exportJenaModelToFile(outputModel, fileName, rdfFormat, gzipOutputFile);
				    	
				    	if (rdfFormat.equals(RDFFormat.NTRIPLES)) {
				    		ntriplesOutputFile = outputFile;
				    	}
				    	
				    	FileResource resource = new FileResource(outputFile);			
						Page.getCurrent().open(resource, null, false);
			    	}
			    	
		    		if (chbExportToVirtuoso.getValue() || chbExportToNeo4j.getValue()) {
		    			if (ntriplesOutputFile == null) {
					    	Model outputModel = IfcApplication.convertIfcModelToJenaModel(fileName, null);
					    	ntriplesOutputFile = IfcApplication.exportJenaModelToFile(outputModel, fileName, RDFFormat.NTRIPLES, false);
		    			}		    			
			    	}
		    		
//		    		if (chbExportToVirtuoso.getValue()) {
//		    			String filePath = IfcApplication.getVirtuosoFolderPath() + "/" + ntriplesOutputFile.getName();
//		    			Logger.getRootLogger().info("Copy output file to " + filePath);
//		    			Files.copy(ntriplesOutputFile.toPath(), new File(filePath).toPath());
//		    		}
//		    		
//		    		if (chbExportToNeo4j.getValue()) {
//		    			String filePath = IfcApplication.getNeo4jFolderPath() + "/" + ntriplesOutputFile.getName();
//		    			Logger.getRootLogger().info("Copy output file to " + filePath);
//		    			Files.copy(ntriplesOutputFile.toPath(), new File(filePath).toPath());
//		    		}
//			    	
			    	
			    	
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
		
		chbSaveFile.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				onChkSaveToFileClicked();
			}
		});
		
		onChkSaveToFileClicked();
		
	}
	
	private void onChkSaveToFileClicked() {
		boolean saveToFile = chbSaveFile.getValue();
		cbRdfFormats.setRequired(saveToFile);
		cbRdfFormats.setNullSelectionAllowed(saveToFile);
		cbRdfFormats.setVisible(saveToFile);
	}
	
	
//	private void onOutputTypeChanged() {
//		outputToFile = cbOutputTypes.getValue().equals(configurationPool.get(0));
//		cbRdfFormats.setVisible(outputToFile);
//		cbRdfFormats.setRequired(outputToFile);
//		chbGzipFile.setValidationVisible(outputToFile);
//	}
	
	
//	private void exportToJenaModel(String fileName) throws Exception {
//		ConfigurationItemEx configuration = (ConfigurationItemEx)cbOutputTypes.getValue();
//
//		// TODO:
////		JenaProviderBase modelFactory = new VirtuosoJenaProvider1("Virtuoso", configuration.getProperties());
////		
//////		JenaProviderBase modelFactory = JenaProviderBase.getFactory(configuration);
////		Model jenaModel = IfcApplication.convertIfcModelToJenaModel(fileName, modelFactory.createModel());
//		
//		
//	}
	
	
	private void fillComboBoxWithRdfFormats(ComboBox comboBox) {
		Set<Entry<String, RDFFormat>> entrySet = RdfUtils.getRdfFormatMap().entrySet(); 
		entrySet.forEach(x -> {
			comboBox.addItem(x.getValue());
			comboBox.setItemCaption(x.getValue(), x.getKey());
		});
		
	}
	
	
	
	

}
