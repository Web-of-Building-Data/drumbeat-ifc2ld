package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import fi.hut.cs.drumbeat.common.params.TypedParam;
import fi.hut.cs.drumbeat.ifc.convert.ifc2ld.Ifc2RdfConversionParams;
import fi.hut.cs.drumbeat.rdf.OwlProfileEnum;

@SuppressWarnings("serial")
public class IfcConfigView extends FormLayout {

	private Ifc2RdfConversionParams params;

	public IfcConfigView() {
		final boolean readOnly = true;
		
		params = new Ifc2RdfConversionParams();


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
		
		
		CheckBox chbIgnoreExpressSchema = new CheckBox(
				normalizeCaption(Ifc2RdfConversionParams.PARAM_IGNORE_EXPRESS_SCHEMA));
		chbIgnoreExpressSchema.setEnabled(false);
		loadControlValue(chbIgnoreExpressSchema,
				Ifc2RdfConversionParams.PARAM_IGNORE_EXPRESS_SCHEMA, false);

		// chbIgnoreExpressSchema.setValue(params.getParam(Ifc2RdfConversionParams.PARAM_IGNORE_EXPRESS_SCHEMA).getBooleanValue());
		addComponent(chbIgnoreExpressSchema);
		//
		
		
		TwinColSelect lstOwlProfiles = new TwinColSelect("OWL Profiles");
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
		
		

		// CheckBox chbIgnoreIfcSchema = new
		// CheckBox(Ifc2RdfConversionParams.PARAM_IGNORE_IFC_SCHEMA);
		// chbIgnoreIfcSchema.setValue(params.getParam(Ifc2RdfConversionParams.PARAM_IGNORE_IFC_SCHEMA).getBooleanValue());
		// addComponent(chbIgnoreIfcSchema);
		//
		// CheckBox chbExportDebugInfo = new
		// CheckBox(Ifc2RdfConversionParams.PARAM_EXPORT_DEBUG_INFO);
		// chbExportDebugInfo.setValue(params.getParam(Ifc2RdfConversionParams.PARAM_EXPORT_DEBUG_INFO).getBooleanValue());
		// addComponent(chbExportDebugInfo);

		ComboBox cbExportPropertyDomainAndRanges = new ComboBox(
				normalizeCaption(Ifc2RdfConversionParams.PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES));
		cbExportPropertyDomainAndRanges
				.addItems(params
						.getParam(
								Ifc2RdfConversionParams.PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES)
						.getPossibleValues());
		cbExportPropertyDomainAndRanges
				.setValue(params
						.getParam(
								Ifc2RdfConversionParams.PARAM_EXPORT_PROPERTY_DOMAIN_AND_RANGES)
						.getDefaultValue());
		cbExportPropertyDomainAndRanges.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbExportPropertyDomainAndRanges.setNullSelectionAllowed(false);
		cbExportPropertyDomainAndRanges.setRequired(true);
		cbExportPropertyDomainAndRanges.setReadOnly(readOnly);
		addComponent(cbExportPropertyDomainAndRanges);

		ComboBox cbConvertEnumsTo = new ComboBox(
				normalizeCaption(Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO));
		cbConvertEnumsTo.addItems(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO)
				.getPossibleValues());
		cbConvertEnumsTo.setValue(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_ENUMS_TO)
				.getDefaultValue());
		cbConvertEnumsTo.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbConvertEnumsTo.setNullSelectionAllowed(false);
		cbConvertEnumsTo.setRequired(true);
		cbConvertEnumsTo.setReadOnly(readOnly);
		addComponent(cbConvertEnumsTo);

		ComboBox cbConvertBooleansTo = new ComboBox(
				normalizeCaption(Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO));
		cbConvertBooleansTo.addItems(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO)
				.getPossibleValues());
		cbConvertBooleansTo.setValue(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_BOOLEANS_TO)
				.getDefaultValue());
		cbConvertBooleansTo.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbConvertBooleansTo.setNullSelectionAllowed(false);
		cbConvertBooleansTo.setRequired(true);
		cbConvertBooleansTo.setReadOnly(readOnly);
		addComponent(cbConvertBooleansTo);

		ComboBox cbConvertDoublesTo = new ComboBox(
				normalizeCaption(Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO));
		cbConvertDoublesTo.addItems(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO)
				.getPossibleValues());
		cbConvertDoublesTo.setValue(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_DOUBLES_TO)
				.getDefaultValue());
		cbConvertDoublesTo.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbConvertDoublesTo.setNullSelectionAllowed(false);
		cbConvertDoublesTo.setRequired(true);
		cbConvertDoublesTo.setReadOnly(readOnly);
		addComponent(cbConvertDoublesTo);

		ComboBox cbConvertCollectionsTo = new ComboBox(
				normalizeCaption(Ifc2RdfConversionParams.PARAM_CONVERT_COLLECTIONS_TO));
		cbConvertCollectionsTo.addItems(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_COLLECTIONS_TO)
				.getPossibleValues());
		cbConvertCollectionsTo.setValue(params.getParam(
				Ifc2RdfConversionParams.PARAM_CONVERT_COLLECTIONS_TO)
				.getDefaultValue());
		cbConvertCollectionsTo.setWidth(MainUI.DEFAULT_WIDTH, Unit.PIXELS);
		cbConvertCollectionsTo.setNullSelectionAllowed(false);
		cbConvertCollectionsTo.setRequired(true);
		cbConvertCollectionsTo.setReadOnly(readOnly);
		addComponent(cbConvertCollectionsTo);

//		CheckBox cbhUseLongAttributeName = new CheckBox(
//				normalizeCaption(Ifc2RdfConversionParams.PARAM_USE_LONG_ATTRIBUTE_NAME));
//		cbhUseLongAttributeName.setValue((Boolean) params.getParam(
//				Ifc2RdfConversionParams.PARAM_USE_LONG_ATTRIBUTE_NAME)
//				.getValue());
//		addComponent(cbhUseLongAttributeName);

//		Button btnConvert = new Button("Convert");
//		addComponent(btnConvert);
		
		
		HorizontalLayout layoutConfigButtons = new HorizontalLayout();

		Button btnLoadConfig = new Button("Load...");
		btnLoadConfig.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// addComponent(new Label("Thank you for clicking"));
			}
		});
		btnLoadConfig.setEnabled(false);
		layoutConfigButtons.addComponent(btnLoadConfig);

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
	
	//
	// splits Words ByUpper Case Letters
	// 
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

}
