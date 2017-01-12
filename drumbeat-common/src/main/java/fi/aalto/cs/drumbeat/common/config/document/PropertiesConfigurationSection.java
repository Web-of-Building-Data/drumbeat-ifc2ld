package fi.aalto.cs.drumbeat.common.config.document;

import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PropertiesConfigurationSection extends ConfigurationSection {
	
	private Properties properties;
	
	public PropertiesConfigurationSection(Element parentElement, boolean isMandatory) throws ConfigurationParserException {
		super(parentElement, null, true, isMandatory);
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	@Override
	protected boolean initialize(Element element, String filter) throws ConfigurationParserException {

		properties = new Properties();

		NodeList propertyElements = element.getElementsByTagName(ConfigurationDocument.TAG_ANY);

		for (int j = 0; j < propertyElements.getLength(); ++j) {

			Element propertyElement = (Element) propertyElements.item(j);

			String propertyName = propertyElement.getNodeName();
			String paramValue = propertyElement.getTextContent();

			if (propertyName == null || paramValue == null) {
				throw new ConfigurationParserException(String.format("Invalid property '<%s>%s</%s>'",
						propertyName, paramValue, propertyName));
			}

			properties.put(propertyName, paramValue);
		}
		
		return true;
	}
	

	@Override
	protected String getTagName() {
		return ConfigurationDocument.TAG_PROPERTIES;
	}

}
