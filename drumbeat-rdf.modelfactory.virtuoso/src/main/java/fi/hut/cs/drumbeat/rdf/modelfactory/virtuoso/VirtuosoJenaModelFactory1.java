package fi.hut.cs.drumbeat.rdf.modelfactory.virtuoso;

import java.util.Properties;

import org.apache.log4j.Logger;

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;

public class VirtuosoJenaModelFactory1 extends JenaModelFactoryBase {
	
	private static final Logger logger = Logger.getLogger(VirtuosoJenaModelFactory1.class);
	
	private Model model;
	
	public VirtuosoJenaModelFactory1(String factoryName, Properties properties) {
		super(factoryName, properties);
	}
	
	@Override
	public Model createModel() throws Exception {
		Model model = getModel();
		
		if (!model.isEmpty()) {

			logger.info(String.format("[Virt] Clearing model '%s'", getModelId()));
			
			model.removeAll();
			
			logger.info(String.format("[Virt] Clearing model '%s' compeleted", getModelId()));
		}
		
		return model;
	}

	@Override
	public Model getModel() throws Exception {
		if (model == null) {
		
			if (getModelId() == null) {
				throw new IllegalArgumentException(String.format("Argument %s is undefined", ARGUMENT_MODEL_ID));
			}

			logger.info(String.format("[Virt] Getting model '%s'", getModelId()));
			
			model = VirtModel.openDatabaseModel(getModelId(), getServerUrl(), getUserName(), getPassword());
			
			logger.info(String.format("[Virt] Getting model '%s' completed", getModelId()));
			
		}
		
		return model;
	}

	@Override
	public void release() throws Exception {
		if (model != null) {
			model.close();
			model = null;
		}
	}

}
