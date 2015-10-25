package fi.hut.cs.drumbeat.rdf.modelfactory.virtuoso;

import java.util.Properties;

import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	public final static String FACTORY_NAME = "drumbeat.virtuoso";
	
	public final static String SERVER_URL = "jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2";
	public final static String USER_NAME = "dba";
	public final static String USER_PASSWORD = "dba";
	public final static String NAMED_GRAPH_ID = "";
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
		Properties properties = new Properties();
		properties.put(JenaModelFactoryBase.ARGUMENT_SERVER_URL, SERVER_URL);
		properties.put(JenaModelFactoryBase.ARGUMENT_USER_NAME, USER_NAME);
		properties.put(JenaModelFactoryBase.ARGUMENT_PASSWORD, USER_PASSWORD);
		properties.put(JenaModelFactoryBase.ARGUMENT_MODEL_ID, NAMED_GRAPH_ID);
		VirtuosoJenaModelFactory1 modelFactory = new VirtuosoJenaModelFactory1(FACTORY_NAME, properties);
		assertNotNull(modelFactory);
    }
}
