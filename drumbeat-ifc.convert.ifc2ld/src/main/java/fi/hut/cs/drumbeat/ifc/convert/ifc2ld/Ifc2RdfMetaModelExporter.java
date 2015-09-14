package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcMetaModel;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcStepFileDescription;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcStepFileName;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.rdf.OwlProfileList;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;


public class Ifc2RdfMetaModelExporter extends Ifc2RdfExporterBase {
	
	private String metaDataSetUri;
	private IfcSchema ifcSchema;
//	private IfcModel ifcModel;
	private IfcMetaModel metaModel;
	
	private Ifc2RdfConversionContext context;
	private OwlProfileList owlProfileList;
	
	public Ifc2RdfMetaModelExporter(String metaDataSetUri, IfcModel ifcModel, Ifc2RdfConversionContext context, Model jenaModel) {
		super(context, jenaModel);
		
		this.metaDataSetUri = metaDataSetUri;
		this.metaModel = ifcModel.getMetaModel();
		this.ifcSchema = ifcModel.getSchema();
		this.context = context;
		this.owlProfileList = context.getOwlProfileList();
		

		String modelNamespacePrefix = context.getModelPrefix();
		String modelNamespaceUri = String.format(context.getModelNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		
		String ontologyNamespaceUri = String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		super.setOntologyNamespaceUri(ontologyNamespaceUri);
		super.setModelNamespacePrefix(modelNamespacePrefix);
		super.setModelNamespaceUri(modelNamespaceUri);		
	}
	                                                                                                                    
	public Model export() throws IfcException {
		
		//
		// write header and prefixes
		//
		//adapter.startExport();		
		
		jenaModel.setNsPrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());
		jenaModel.setNsPrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());
		jenaModel.setNsPrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());
		jenaModel.setNsPrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());	
		jenaModel.setNsPrefix(RdfVocabulary.VOID.BASE_PREFIX, RdfVocabulary.VOID.BASE_URI);
		jenaModel.setNsPrefix(RdfVocabulary.DCTERMS.BASE_PREFIX, RdfVocabulary.DCTERMS.BASE_URI);
		
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.EXPRESS.BASE_PREFIX, Ifc2RdfVocabulary.EXPRESS.getBaseUri());		
		jenaModel.setNsPrefix(Ifc2RdfVocabulary.STEP.BASE_PREFIX,Ifc2RdfVocabulary.STEP.getBaseUri());

		
		//adapter.exportEmptyLine();
		
		Resource dataSetResource = super.createUriResource(metaDataSetUri);

		jenaModel.setNsPrefix(getModelNamespacePrefix(), getModelNamespaceUri());
		//adapter.exportEmptyLine();

		jenaModel.add(dataSetResource, RDF.type, RdfVocabulary.VOID.DataSet);

		String conversionParamsString = context.getConversionParams().toString();
//				.replaceFirst("\\[", "[\r\n\t\t\t ")
//				.replaceFirst("\\]", "\r\n\t\t]")
//				.replaceAll(",", "\r\n\t\t\t");		
		conversionParamsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				owlProfileList.getOwlProfileIds(),
				conversionParamsString); 
		jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.description, jenaModel.createTypedLiteral(conversionParamsString));		

		IfcStepFileDescription stepFileDescription = metaModel.getFileDescription();
		stepFileDescription.getDescriptions().forEach(x ->
			jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.description, jenaModel.createTypedLiteral(x))
		);
		
		IfcStepFileName stepFileName = metaModel.getFileName();		
		jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.title, jenaModel.createTypedLiteral(stepFileName.getName()));		

		stepFileName.getAuthors().forEach(x ->
			jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.creator, jenaModel.createTypedLiteral(x))
		);
		
		stepFileName.getOrganizations().forEach(x ->
			jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.publisher, jenaModel.createTypedLiteral(x))
		);
		
		jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.created, jenaModel.createTypedLiteral(stepFileName.getTimeStamp()));	
		
		
		jenaModel.add(dataSetResource, RdfVocabulary.DCTERMS.hasVersion, jenaModel.createTypedLiteral(stepFileName.getPreprocessorVersion(), XSD.date.toString()));
		
		Resource fileDescriptionResource = jenaModel.createResource();
		Resource fileNameResource = jenaModel.createResource();
		Resource fileSchemaResource = jenaModel.createResource();
		
		jenaModel.add(dataSetResource, Ifc2RdfVocabulary.STEP.fileDescription, fileDescriptionResource);
		jenaModel.add(dataSetResource, Ifc2RdfVocabulary.STEP.fileName, fileNameResource);
		jenaModel.add(dataSetResource, Ifc2RdfVocabulary.STEP.fileSchema, fileSchemaResource);
		
		stepFileDescription.getDescriptions().forEach(x ->
			jenaModel.add(fileDescriptionResource, Ifc2RdfVocabulary.STEP.FileDescription.description, jenaModel.createTypedLiteral(x))
		);
		jenaModel.add(fileDescriptionResource, Ifc2RdfVocabulary.STEP.FileDescription.implementation_level, jenaModel.createTypedLiteral(stepFileDescription.getImplementationLevel()));
		
		jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.name, jenaModel.createTypedLiteral(stepFileName.getName()));
		jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.time_stamp, jenaModel.createTypedLiteral(stepFileName.getTimeStamp()));
		stepFileName.getAuthors().forEach(x ->
			jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.author, jenaModel.createTypedLiteral(x))
		);

		stepFileName.getOrganizations().forEach(x ->
			jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.organization, jenaModel.createTypedLiteral(x))
		);
		
		jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.preprocessor_version, jenaModel.createTypedLiteral(stepFileName.getPreprocessorVersion()));
		jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.originating_system, jenaModel.createTypedLiteral(stepFileName.getOriginatingSystem()));
		jenaModel.add(fileNameResource, Ifc2RdfVocabulary.STEP.FileName.authorization, jenaModel.createTypedLiteral(stepFileName.getAuthorization()));
		
		metaModel.getFileSchema().getSchemas().forEach(x ->
			jenaModel.add(fileSchemaResource, Ifc2RdfVocabulary.STEP.FileSchema.schema_identifiers, jenaModel.createTypedLiteral(x))
		);
		
		//		IfcEntity ownerHistory = ifcModel.getFirstEntityByType(IfcVocabulary.TypeNames.IFC_OWNER_HISTORY);
//		if (ownerHistory != null) {
//			
//		}
		
		//adapter.endExport();
		
		return jenaModel;
	}
	
}
