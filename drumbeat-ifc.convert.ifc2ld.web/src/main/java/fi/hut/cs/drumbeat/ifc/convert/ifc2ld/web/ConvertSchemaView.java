package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.*;
import java.util.List;

import org.apache.jena.riot.RDFFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.rdf.OwlProfileEnum;
import fi.hut.cs.drumbeat.rdf.RdfUtils;

@SuppressWarnings("serial")
public class ConvertSchemaView extends FormLayout {

	public ConvertSchemaView() {
		
//		addComponent(
//			new Label("<h2>IFC-EXPRESS-to-OWL</h2>", ContentMode.HTML)
//		);
		
		ComboBox cbSchemaVersions = new ComboBox("IFC Schema Versions");		
		List<IfcSchema> ifcSchemas = IfcApplication.getSchemas();		
		if (ifcSchemas != null) {
			ifcSchemas.stream().forEach(x -> cbSchemaVersions.addItems(x.getVersion()));
		}		
		
		cbSchemaVersions.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbSchemaVersions.setRequired(true);
		cbSchemaVersions.setNullSelectionAllowed(false);
		cbSchemaVersions.setValue(OwlProfileEnum.OWL2_DL);
//		cbSchemaVersions.setComponentError(new UserError("Select an IFC schema!"));
//		cbSchemaVersions.setValidationVisible(false);		
		addComponent(cbSchemaVersions);
		
		ComboBox cbRdfFormats = new ComboBox("RDF Format");		
//		RdfUtils.getRdfFormatMap().entrySet().forEach(x -> {
//			cbRdfFormats.addItem(x.getValue());
//			cbRdfFormats.setItemCaption(x.getValue(), x.getKey());
//		}); 
		
		cbRdfFormats.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbRdfFormats.setRequired(true);
		cbRdfFormats.setNullSelectionAllowed(false);
//		cbRdfFormats.setValue(RDFFormat.TURTLE_PRETTY);
//		cbRdfFormats.setComponentError(new UserError("Select an IFC schema!"));
//		cbRdfFormats.setValidationVisible(false);		
		addComponent(cbRdfFormats);		
		
		CheckBox chbGzipFile = new CheckBox("gzip compression");
		addComponent(chbGzipFile);		
		
		
		Button btnConvert = new Button("Convert");
		btnConvert.setEnabled(false);
		addComponent(btnConvert);
		
		
		cbSchemaVersions.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				btnConvert.setEnabled(cbSchemaVersions.getValue() != null);				
			}
		});
		
		
		btnConvert.addClickListener(new ClickListener() {
		    @SuppressWarnings("deprecation")
			@Override
		    public void buttonClick(ClickEvent event) {
		    	
	    		String schemaName = (String)cbSchemaVersions.getValue();
	    		RDFFormat rdfFormat = (RDFFormat)cbRdfFormats.getValue();
	    		Boolean gzip = chbGzipFile.getValue();
	    		try {
					File schemaFile = IfcApplication.exportSchema(schemaName, rdfFormat, gzip);
					FileResource resource = new FileResource(schemaFile);
//						FileDownloader downloader = new FileDownloader(resource);
//						downloader.extend(btnConvert);
					
					Page.getCurrent().open(resource, null, false);
					
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
				}
		        
		    }
		    
		});
		
		
		
	}
	

}
