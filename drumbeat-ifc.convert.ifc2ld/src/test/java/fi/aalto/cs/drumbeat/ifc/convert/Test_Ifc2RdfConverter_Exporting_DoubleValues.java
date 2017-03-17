package fi.aalto.cs.drumbeat.ifc.convert;

import java.math.BigDecimal;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.XSD;
import org.junit.Test;

import fi.aalto.cs.drumbeat.rdf.RdfVocabulary.OWL;

public class Test_Ifc2RdfConverter_Exporting_DoubleValues {
	
	@Test
	public void test_exportDouble() {
		
		double d = 2.24130072214;
		Model model = ModelFactory.createDefaultModel();
		
		Literal l1 = model.createTypedLiteral(d);
		System.out.println(l1);
		
		Literal l2 = model.createTypedLiteral(d, XSD.xdouble.getURI());
		System.out.println(l2);
		
		Literal l4 = model.createTypedLiteral(d, OWL.real.getURI());
		System.out.println(l4);
		
		Literal l5 = model.createTypedLiteral(d, XSD.xstring.getURI());
		System.out.println(l5);	
		
		Literal l6 = model.createTypedLiteral(d, XSD.decimal.getURI());
		System.out.println(l6);

		Literal l7 = model.createTypedLiteral(BigDecimal.valueOf(d), XSD.decimal.getURI());
		System.out.println(l7);
		
	}


}
