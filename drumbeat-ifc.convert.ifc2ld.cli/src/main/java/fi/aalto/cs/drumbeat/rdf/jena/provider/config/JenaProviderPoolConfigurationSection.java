package fi.aalto.cs.drumbeat.rdf.jena.provider.config;

import org.w3c.dom.Element;

import fi.aalto.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.aalto.cs.drumbeat.common.config.ConfigurationPool;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.aalto.cs.drumbeat.common.config.document.ConfigurationSection;


public class JenaProviderPoolConfigurationSection extends ConfigurationSection {
	
	public static final String TAG_NAME = "jenaModelPool";
	
	public static JenaProviderPoolConfigurationSection getInstance() throws ConfigurationParserException {
		return new JenaProviderPoolConfigurationSection(
					ConfigurationDocument.getInstance().getDocument().getDocumentElement(),
					null,
					true,
					true);
	}
	
	
	private ConfigurationPool<ConfigurationItemEx> configurationPool;

	public JenaProviderPoolConfigurationSection(Element parentElement, String filter,
			boolean autoParse, boolean isMandatory)
			throws ConfigurationParserException {
		super(parentElement, filter, autoParse, isMandatory);
	}

	@Override
	protected String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected boolean initialize(Element element, String filter)
			throws ConfigurationParserException {
		String jenaModelTypeName = element.getAttribute(ConfigurationDocument.ATTRIBUTE_TYPE);	
		if (filter != null && !filter.equals(jenaModelTypeName)) {
			return false;
		}
		
		JenaProviderConfigurationSection childConfigurationSection = new JenaProviderConfigurationSection(element, null, false);
		if (childConfigurationSection.getInitialized() > 0) {
			configurationPool = new ConfigurationPool<ConfigurationItemEx>();
			configurationPool.addAll(childConfigurationSection.getConfigurations());
			return true;
		}
		
		return false;
	}

	public ConfigurationPool<ConfigurationItemEx> getConfigurationPool() {
		return configurationPool;
	}
}
