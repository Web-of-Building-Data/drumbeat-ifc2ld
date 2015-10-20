package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import fi.hut.cs.drumbeat.rdf.OwlProfileList;


public class Ifc2RdfConversionContext {
	
	private String owlVersion; 
	private OwlProfileList owlProfileList;
	private String name;
	private String ontologyPrefix;
	private String ontologyNamespaceFormat;
	private String modelPrefix;
	private String modelNamespaceFormat;
	
	private Ifc2RdfConversionParams conversionParams;
	
	public Ifc2RdfConversionContext() {
		conversionParams = new Ifc2RdfConversionParams();
	}
	
	public String getOwlVersion() {
		return owlVersion;
	}

	public void setOwlVersion(String owlVersion) {
		this.owlVersion = owlVersion;
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
	
	/**
	 * @return the ontologyPrefix
	 */
	public String getOntologyPrefix() {
		return ontologyPrefix;
	}
	
	/**
	 * @param ontologyPrefix the ontologyPrefix to set
	 */
	public void setOntologyPrefix(String ontologyPrefix) {
		this.ontologyPrefix = ontologyPrefix;
	}
	
	/**
	 * @return the ontologyNamespaceFormat
	 */
	public String getOntologyNamespaceFormat() {
		return ontologyNamespaceFormat;
	}
	
	/**
	 * @param ontologyNamespaceFormat the ontologyNamespaceFormat to set
	 */
	public void setOntologyNamespaceFormat(String ontologyNamespaceFormat) {
		this.ontologyNamespaceFormat = ontologyNamespaceFormat;
	}
	
	/**
	 * @return the modelPrefix
	 */
	public String getModelPrefix() {
		return modelPrefix;	
	}
	
	/**
	 * @param modelPrefix the modelPrefix to set
	 */
	public void setModelPrefix(String modelPrefix) {
		this.modelPrefix = modelPrefix;
	}
	
	/**
	 * @return the modelNamespaceFormat
	 */
	public String getModelNamespaceFormat() {
		return modelNamespaceFormat;
	}
	
	/**
	 * @param modelNamespaceFormat the modelNamespaceFormat to set
	 */
	public void setModelNamespaceFormat(String modelNamespaceFormat) {
		this.modelNamespaceFormat = modelNamespaceFormat;
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
