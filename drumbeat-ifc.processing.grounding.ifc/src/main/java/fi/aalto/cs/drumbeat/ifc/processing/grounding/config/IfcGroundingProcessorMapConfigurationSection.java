package fi.aalto.cs.drumbeat.ifc.processing.grounding.config;

import org.w3c.dom.Element;

import fi.aalto.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.aalto.cs.drumbeat.common.config.document.ProcessorMapConfigurationSection;


public class IfcGroundingProcessorMapConfigurationSection extends ProcessorMapConfigurationSection  {
	
	public static final String PROCESSOR_TYPE = "grounding";
	private static IfcGroundingProcessorMapConfigurationSection instance;

	public IfcGroundingProcessorMapConfigurationSection(Element parentElement) throws ConfigurationParserException {
		super(parentElement, PROCESSOR_TYPE, true);
	}
	
	public static IfcGroundingProcessorMapConfigurationSection getInstance() throws ConfigurationParserException {
		if (instance == null) {
			instance = new IfcGroundingProcessorMapConfigurationSection(
					ConfigurationDocument.getDefault().getDocument().getDocumentElement());
		}
		return instance;
	}
	
}
