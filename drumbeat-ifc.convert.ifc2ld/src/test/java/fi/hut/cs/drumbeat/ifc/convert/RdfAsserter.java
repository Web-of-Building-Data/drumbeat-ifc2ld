package fi.hut.cs.drumbeat.ifc.convert;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fi.hut.cs.drumbeat.rdf.data.RdfComparator;
import fi.hut.cs.drumbeat.rdf.data.RdfNodeTypeEnum;

public class RdfAsserter {
	
	public interface Asserter<T> {
		void assertEquals(T o1, T o2);
	}
	
//	public static final Asserter<Literal> LITERAL_COMPARATOR = new LiteralAsserter();
//	public static final Asserter<RDFNode> NODE_COMPARATOR_NOT_IGNORING_ = new RdfNodeAsserter(false);
//	public static final Asserter<RDFNode> NODE_COMPARATOR_BY_IDS_AND_CHILDREN = new RdfNodeAsserter(true);
	
	public static class LiteralAsserter implements Asserter<Literal> {

		@Override
		public void assertEquals(Literal o1, Literal o2) {			
			Assert.assertEquals(o1.getDatatypeURI(), o2.getDatatypeURI());			
			Assert.assertEquals(o1.getValue(), o2.getValue());
		}
		
	}
	
	public static class RdfNodeAsserter implements Asserter<RDFNode> {
		
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;
		
		public RdfNodeAsserter(boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public void assertEquals(RDFNode o1, RDFNode o2) {

			RdfNodeTypeEnum type1 = RdfNodeTypeEnum.getType(o1);
			RdfNodeTypeEnum type2 = RdfNodeTypeEnum.getType(o2);
			Assert.assertEquals(type1, type2);
			
			if (type1.equals(RdfNodeTypeEnum.Literal)) {
				final Asserter<Literal> literalAsserter = new LiteralAsserter();
				literalAsserter.assertEquals(o1.asLiteral(), o2.asLiteral());
			} else if (type1.equals(RdfNodeTypeEnum.Uri)) {
				Assert.assertEquals(o1.asResource().getURI(), o2.asResource().getURI());
			} else {
				assert(o1.isAnon());
				
				if (!ignoreAnonIds) {
					Assert.assertEquals(o1.asResource().getId().toString(), o2.asResource().getId().toString());
				}
				
				if (!ignoreAnonProperties) {
					final Asserter<StmtIterator> stmtIteratorAsserter = new StmtIteratorAsserter(true, ignoreAnonIds, ignoreAnonProperties);
					stmtIteratorAsserter.assertEquals(o1.asResource().listProperties(), o2.asResource().listProperties());
				}				
			}
		}
		
		public boolean ignoreAnonIds() {
			return ignoreAnonIds;
		}

		public boolean ignoreAnonProperties() {
			return ignoreAnonProperties;
		}
		
	}
	
	public static class StatementAsserter implements Asserter<Statement> {

		private final boolean ignoreStatementSubjects;
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;

		public StatementAsserter(boolean ignoreStatementSubjects, boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreStatementSubjects = ignoreAnonProperties;
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public void assertEquals(Statement o1, Statement o2) {
			
			final Asserter<RDFNode> nodeAsserter = new RdfNodeAsserter(ignoreAnonIds, ignoreAnonProperties);
			
			if (!ignoreStatementSubjects) {
				nodeAsserter.assertEquals(o1.getSubject(), o2.getSubject());
			}
			
			nodeAsserter.assertEquals(o1.getPredicate(), o2.getPredicate());
			
			nodeAsserter.assertEquals(o1.getObject(), o2.getObject());
		}
		
		public boolean ignoreStatementSubjects() {
			return ignoreStatementSubjects;
		}

		public boolean ignoreAnonIds() {
			return ignoreAnonIds;
		}

		public boolean ignoreAnonProperties() {
			return ignoreAnonProperties;
		}
		
	}
	
	
	public static class StatementListAsserter implements Asserter<List<Statement>> {

		private final boolean ignoreStatementSubjects;
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;

		public StatementListAsserter(boolean ignoreStatementSubjects, boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreStatementSubjects = ignoreAnonProperties;
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public void assertEquals(List<Statement> o1, List<Statement> o2) {
			
			final Asserter<Statement> statementAsserter = new StatementAsserter(ignoreStatementSubjects, ignoreAnonIds, ignoreAnonProperties);
			final Comparator<Statement> statementComparator = new RdfComparator.StatementComparator(false, true, false);
			
			//Assert.assertEquals(o1.size(), o2.size());
			
			o1.sort(statementComparator);
			o2.sort(statementComparator);
			
			Iterator<Statement> it1 = o1.iterator();
			Iterator<Statement> it2 = o2.iterator();
			
			while (it1.hasNext()) {
//				System.out.println(it1.next());
//				System.out.println(it2.next());
				Assert.assertTrue(it2.hasNext());
				Statement s1 = it1.next();
				Statement s2 = it2.next();
				statementAsserter.assertEquals(s1, s2);				
			}
			Assert.assertFalse(it2.hasNext());
		}
		
		public boolean ignoreStatementSubjects() {
			return ignoreStatementSubjects;
		}

		public boolean ignoreAnonIds() {
			return ignoreAnonIds;
		}

		public boolean ignoreAnonProperties() {
			return ignoreAnonProperties;
		}
		
	}
		

	
	public static class StmtIteratorAsserter implements Asserter<StmtIterator> {

		private final boolean ignoreStatementSubjects;
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;

		public StmtIteratorAsserter(boolean ignoreStatementSubjects, boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreStatementSubjects = ignoreAnonProperties;
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public void assertEquals(StmtIterator o1, StmtIterator o2) {
			
			final Asserter<List<Statement>> statementListAsserter = new StatementListAsserter(ignoreStatementSubjects, ignoreAnonIds, ignoreAnonProperties);
			
			List<Statement> l1 = o1.toList();
			List<Statement> l2 = o2.toList();
			
			statementListAsserter.assertEquals(l1, l2);
		}
		
		public boolean ignoreStatementSubjects() {
			return ignoreStatementSubjects;
		}

		public boolean ignoreAnonIds() {
			return ignoreAnonIds;
		}

		public boolean ignoreAnonProperties() {
			return ignoreAnonProperties;
		}
		
	}
	
	
	public static class FullResourceAsserter implements Asserter<Resource> {

		@Override
		public void assertEquals(Resource o1, Resource o2) {
			final Asserter<RDFNode> nodeAsserter = new RdfNodeAsserter(true, true);
			nodeAsserter.assertEquals(o1, o2);
			
			final Asserter<StmtIterator> stmtIteratorAsserter = new StmtIteratorAsserter(false, true, true);
			stmtIteratorAsserter.assertEquals(o1.listProperties(), o2.listProperties());
		}
		
	}
	
	
}
