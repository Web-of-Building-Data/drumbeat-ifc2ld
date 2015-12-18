package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld;

import fi.aalto.cs.drumbeat.rdf.OwlProfileEnum;
import fi.aalto.cs.drumbeat.rdf.OwlProfileList;


public class Ifc2RdfConversionContext {
	
	private String ifcOntologyVersion; 
	private OwlProfileList owlProfileList;
	private String name;
//	private String ontologyPrefix;
//	private String ontologyNamespaceUriFormat;
	private String modelNamespacePrefix;
	private String modelNamespaceUriFormat;
	
	private Ifc2RdfConversionParams conversionParams;
	
	public Ifc2RdfConversionContext() {
		conversionParams = new Ifc2RdfConversionParams();
		owlProfileList = new OwlProfileList(OwlProfileEnum.OWL2_Full);
//		ontologyPrefix = Ifc2RdfVocabulary.IFC.BASE_PREFIX;
//		ontologyNamespaceUriFormat = Ifc2RdfVocabulary.DEFAULT_IFC_ONTOLOGY_BASE_FORMAT;
	}
	
	public String getIfcOntologyVersion() {
		return ifcOntologyVersion;
	}

	public void setIfcOntologyVersion(String version) {
		this.ifcOntologyVersion = version;
	}

	public OwlProfileList getOwlProfileList() {
		return owlProfileList;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
//	/**
//	 * @return the ontologyPrefix
//	 */
//	public String getOntologyPrefix() {
//		return ontologyPrefix;
//	}
	
//	/**
//	 * @param ontologyPrefix the ontologyPrefix to set
//	 */
//	public void setOntologyPrefix(String ontologyPrefix) {
//		this.ontologyPrefix = ontologyPrefix;
//	}
//	
//	/**
//	 * @return the ontologyNamespaceUriFormat
//	 */
//	public String getOntologyNamespaceUriFormat() {
//		return ontologyNamespaceUriFormat;
//	}
//	
//	/**
//	 * @param ontologyNamespaceUriFormat the ontologyNamespaceUriFormat to set
//	 */
//	public void setOntologyNamespaceUriFormat(String ontologyNamespaceUriFormat) {
//		this.ontologyNamespaceUriFormat = ontologyNamespaceUriFormat;
//	}
	
	/**
	 * @return the modelPrefix
	 */
	public String getModelNamespacePrefix() {
		return modelNamespacePrefix;	
	}
	
	/**
	 * @param modelNamespacePrefix the modelPrefix to set
	 */
	public void setModelNamespacePrefix(String modelNamespacePrefix) {
		this.modelNamespacePrefix = modelNamespacePrefix;
	}
	
	/**
	 * @return the modelNamespaceUriFormat
	 */
	public String getModelNamespaceUriFormat() {
		return modelNamespaceUriFormat;
	}
	
	/**
	 * @param modelNamespaceUriFormat the modelNamespaceUriFormat to set
	 */
	public void setModelNamespaceUriFormat(String modelNamespaceUriFormat) {
		this.modelNamespaceUriFormat = modelNamespaceUriFormat;
	}
	
	/**
	 * @return the conversionParams
	 */
	public Ifc2RdfConversionParams getConversionParams() {
		return conversionParams;
	}

	/**
	 * @param conversionParams the conversionParams to set
	 */
	public void setConversionParams(Ifc2RdfConversionParams conversionParams) {
		this.conversionParams = conversionParams;
	}

	public void setOwlProfiles(OwlProfileList owlProfileList) {
		this.owlProfileList = owlProfileList;
	}	
	
//	public boolean isEnabledOption(Ifc2RdfConversionParams conversionParam) {
//		return conversionParams.contains(conversionParam);
//	}
//	
}
