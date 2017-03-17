package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang3.NotImplementedException;

import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import fi.aalto.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.aalto.cs.drumbeat.common.params.BooleanParam;
import fi.aalto.cs.drumbeat.common.params.StringParam;
import fi.aalto.cs.drumbeat.common.params.TypedParam;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionContext;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
import fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.config.Ifc2RdfConversionContextLoader;
import fi.aalto.cs.drumbeat.rdf.OwlProfileEnum;
import fi.aalto.cs.drumbeat.rdf.OwlProfileList;

@SuppressWarnings("serial")
public class ConfigView extends FormLayout {

	private Ifc2RdfConversionParams params;
	private Map<String, Component> paramConponentMap;
	
	private List<Ifc2RdfConversionContext> contexts;
	private ComboBox cbConversionContext;
	private TwinColSelect lstOwlProfiles;

	public ConfigView() throws ConfigurationParserException {
		final boolean readOnly = true;
		
		params = new Ifc2RdfConversionParams();
		paramConponentMap = new HashMap<>();
		
		ConfigurationDocument configurationDocument = ConfigurationDocument.getDefault();
		contexts = Ifc2RdfConversionContextLoader.loadAllFromConfigurationDocument(configurationDocument);
		
		Ifc2RdfConversionContext defaultContext = new Ifc2RdfConversionContext();
		defaultContext.setName("(default)");
		contexts.add(0, defaultContext);
		
		cbConversionContext = new ComboBox("Basic context");
		contexts
			.stream()
			.forEach(context -> cbConversionContext.addItem(context));
		cbConversionContext.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbConversionContext.setRequired(true);
		cbConversionContext.setNullSelectionAllowed(false);
		cbConversionContext.setInvalidAllowed(false);
		cbConversionContext.select(defaultContext.getName());
		addComponent(cbConversionContext);
		
		
		addSectionSeparator();
		

//		ListSelect lstOwlProfiles = new ListSelect("OWL profiles");
//		lstOwlProfiles.setMultiSelect(true);
//
//		OwlProfileEnum[] owlProfiles = OwlProfileEnum.values();
//		for (int i = owlProfiles.length - 1; i >= 0; --i) {
//			lstOwlProfiles.addItem(owlProfiles[i]);
//			lstOwlProfiles.setItemCaption(owlProfiles[i], owlProfiles[i]
//					.toString().replace('_', ' '));
//		}
//		lstOwlProfiles.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
//		lstOwlProfiles.setRequired(true);
//		lstOwlProfiles.setNullSelectionAllowed(false);
//		lstOwlProfiles.setValue(OwlProfileEnum.OWL2_DL);
//		addComponent(lstOwlProfiles);
		
//		addParamComponent(params.getParamEx(Ifc2RdfConversionParams.PARAM_IGNORE_EXPRESS_SCHEMA, BooleanParam.class, true));
//		addParamComponent(params.getParamEx(Ifc2RdfConversionParams.PARAM_IGNORE_IFC_SCHEMA, BooleanParam.class, true));
		
		
		lstOwlProfiles = new TwinColSelect("OWL Profiles");
		lstOwlProfiles.setLeftColumnCaption("Not supported by");
		lstOwlProfiles.setRightColumnCaption("Supported by");
		lstOwlProfiles.setMultiSelect(true);

		OwlProfileEnum[] owlProfiles = OwlProfileEnum.values();		
		for (int i = owlProfiles.length - 1; i >= 0; --i) {
			lstOwlProfiles.addItem(owlProfiles[i]);
			lstOwlProfiles.setItemCaption(owlProfiles[i], owlProfiles[i]
					.toString().replace('_', ' '));
		}
		lstOwlProfiles.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		lstOwlProfiles.setRows(owlProfiles.length);
		lstOwlProfiles.setRequired(true);
		lstOwlProfiles.setNullSelectionAllowed(false);
		lstOwlProfiles.setValue(new HashSet<>(Arrays.asList(OwlProfileEnum.OWL2_DL, OwlProfileEnum.OWL2_Full)));
		lstOwlProfiles.setReadOnly(readOnly);
		addComponent(lstOwlProfiles);
		
		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES_TO,
						StringParam.class,
						true));
		
		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_USE_LONG_ATTRIBUTE_NAME,
						BooleanParam.class,
						true));		

		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO,
						StringParam.class,
						true));


		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_CONVERT_COLLECTIONS_TO,
						StringParam.class,
						true));

		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO,
						StringParam.class,
						true));

		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO,
						StringParam.class,
						true));

		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_NAME_ALL_BLANK_NODES,
						BooleanParam.class,
						true));
		
		addParamComponent(
				params.getParamEx(
						Ifc2RdfConversionParams.PARAM_EXPORT_DEBUG_INFO,
						BooleanParam.class,
						true));
		

		
		HorizontalLayout layoutConfigButtons = new HorizontalLayout();

		Button btnResetConfig = new Button("Reset"); 
				//new Button("Reset", null, "discard");
		btnResetConfig.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				resetParamValues();
			}
		});
		layoutConfigButtons.addComponent(btnResetConfig);

		Button btnSaveConfig = new Button("Save...");
		btnSaveConfig.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// addComponent(new Label("Thank you for clicking"));
			}
		});
		btnSaveConfig.setEnabled(false);
		layoutConfigButtons.addComponent(btnSaveConfig);

		addComponent(layoutConfigButtons);
		
	}

	private <T> void loadControlValue(AbstractField<T> control,
			String paramName, boolean loadPossibleValues) {
		TypedParam<T> param = params.getParamEx(paramName);

		if (loadPossibleValues && control instanceof AbstractSelect) {
			List<T> possibleValues = param.getPossibleValues();
			((AbstractSelect) control).addItems(possibleValues);
		}

		T value = param.getValue();
		control.setValue(value);
	}
	
	private Ifc2RdfConversionContext getSelectedConversionContext() {
		return (Ifc2RdfConversionContext)cbConversionContext.getConvertedValue();
	}
	
	private void resetParamValues() {
		
		Ifc2RdfConversionContext context = getSelectedConversionContext();
		
		if (context != null) {
		
			OwlProfileList owlProfileList = context.getOwlProfileList();
			
			lstOwlProfiles.setValue(new TreeSet<>(owlProfileList));
			
		}
		
	}

//	private <T> T saveControlValue(AbstractField<T> control, String paramName) {
//		TypedParam<T> param = params.getParamEx(paramName);
//		T value = control.getValue();
//		param.setValue(value);
//		return value;
//	}

	// private <T> AbstractField<T> addParamControl(AbstractField<T> control,
	// String caption, String paramName) {
	// control.setCaption(caption);
	// control.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
	//
	// TypedParam param = params.getParam(paramName);
	// control.addItems(param.getPossibleValues());1
	// control.setNullSelectionAllowed(false);
	// control.setValue(param.getDefaultValue());
	// control.setRequired(true);
	// return control;
	// }
	
	/*
	 * splits Words ByUpper Case Letters
	 */
	private static String normalizeCaption(String s) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < s.length(); ++i) {
			Character c = s.charAt(i);
			if (i > 1 && Character.isUpperCase(c)) {
				sb.append(' ');
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	
	private <T> void addParamComponent(TypedParam<T> param) {
		
		AbstractComponent component;
		
		String paramName = param.getName();
		String caption = normalizeCaption(paramName);
		
		if (param instanceof BooleanParam) {
		
			CheckBox checkBox = new CheckBox(caption);
			checkBox.setEnabled(true);
			checkBox.setValue(((BooleanParam)param).getValue());
			
			component = checkBox;
			
		} else if (param instanceof StringParam) {
			
			List<String> possibleValues = ((StringParam)param).getPossibleValues();
			if (possibleValues != null && !possibleValues.isEmpty()) {
				
				ComboBox comboBox = new ComboBox(caption);
				comboBox.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
				comboBox.addItems(possibleValues);
				comboBox.setRequired(true);
				comboBox.setNullSelectionAllowed(false);
				comboBox.setValue(param.getDefaultValue());
				
				component = comboBox;
				
			} else {
				
				TextField textField = new TextField(caption);
				textField.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
				textField.setValue(((StringParam)param).getDefaultValue());
				
				component = textField;
			}
			
			
		} else {
			
			throw new NotImplementedException("Typed param class: " + param.getClass());
			
		}
		
		component.setDescription(param.getDescription());			

		paramConponentMap.put(paramName, component);
		addComponent(component);
		
	}
	
	private void addSectionSeparator() {
		
		Label lblSeparator = new Label("<hr/>", ContentMode.HTML);
		addComponent(lblSeparator);
		
	}
	
	
	
	
	

}
