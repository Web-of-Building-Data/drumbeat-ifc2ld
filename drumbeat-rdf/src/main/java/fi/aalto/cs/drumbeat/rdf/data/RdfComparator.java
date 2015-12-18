package fi.aalto.cs.drumbeat.rdf.data;

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RdfComparator {
	
//	public static final Comparator<Literal> LITERAL_COMPARATOR = new LiteralComparator();
//	public static final Comparator<RDFNode> NODE_COMPARATOR_NOT_IGNORING_ = new RdfNodeComparator(false);
//	public static final Comparator<RDFNode> NODE_COMPARATOR_BY_IDS_AND_CHILDREN = new RdfNodeComparator(true);
	
	public static class LiteralComparator implements Comparator<Literal> {

		@Override
		public int compare(Literal o1, Literal o2) {
			
			int result;
			if ((result = o1.getDatatypeURI().compareTo(o2.getDatatypeURI())) != 0) {
				return result;
			}
			
			return o1.getValue().toString().compareTo(o2.getValue().toString());
		}
		
	}
	
	public static class RdfNodeComparator implements Comparator<RDFNode> {
		
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;
		
		public RdfNodeComparator(boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public int compare(RDFNode o1, RDFNode o2) {

			RdfNodeTypeEnum type1 = RdfNodeTypeEnum.getType(o1);
			RdfNodeTypeEnum type2 = RdfNodeTypeEnum.getType(o2);
			int result;
			if ((result = type1.compareTo(type2)) != 0) {
				return result;			
			}
			
			if (type1.equals(RdfNodeTypeEnum.Literal)) {
				final Comparator<Literal> literalComparator = new LiteralComparator();
				return literalComparator.compare(o1.asLiteral(), o2.asLiteral());
			} else if (type1.equals(RdfNodeTypeEnum.Uri)) {
				return o1.asResource().getURI().compareTo(o2.asResource().getURI());
			} else {
				assert(o1.isAnon());
				
				if (!ignoreAnonIds &&
					(result = o1.asResource().getId().toString().compareTo(o2.asResource().getId().toString())) != 0)
				{
					return result;
				}
				
				if (!ignoreAnonProperties) {
					final Comparator<StmtIterator> stmtIteratorComparator = new StmtIteratorComparator(true, ignoreAnonIds, ignoreAnonProperties);
					if ((result = stmtIteratorComparator.compare(o1.asResource().listProperties(), o2.asResource().listProperties())) != 0) {
						return result;
					}
				}
				
				return 0;				
			}
		}
		
		public boolean ignoreAnonIds() {
			return ignoreAnonIds;
		}

		public boolean ignoreAnonProperties() {
			return ignoreAnonProperties;
		}
		
	}
	
	public static class StatementComparator implements Comparator<Statement> {

		private final boolean ignoreStatementSubjects;
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;

		public StatementComparator(boolean ignoreStatementSubjects, boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreStatementSubjects = ignoreStatementSubjects;
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public int compare(Statement o1, Statement o2) {
			
			int result;
			final Comparator<RDFNode> nodeComparator = new RdfNodeComparator(ignoreAnonIds, ignoreAnonProperties);
			
			if (!ignoreStatementSubjects &&
				(result = nodeComparator.compare(o1.getSubject(), o2.getSubject())) != 0)
			{
				return result;
			}			
			
			if ((result = nodeComparator.compare(o1.getPredicate(), o2.getPredicate())) != 0) {
				return result;
			}
			
			return nodeComparator.compare(o1.getObject(), o2.getObject());
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
		

	
	public static class StmtIteratorComparator implements Comparator<StmtIterator> {

		private final boolean ignoreStatementSubjects;
		private final boolean ignoreAnonIds;
		private final boolean ignoreAnonProperties;

		public StmtIteratorComparator(boolean ignoreStatementSubjects, boolean ignoreAnonIds, boolean ignoreAnonProperties) {
			this.ignoreStatementSubjects = ignoreStatementSubjects;
			this.ignoreAnonIds = ignoreAnonIds;
			this.ignoreAnonProperties = ignoreAnonProperties;
		}
		
		@Override
		public int compare(StmtIterator o1, StmtIterator o2) {
			
			int result;
			final Comparator<Statement> statementComparator = new StatementComparator(ignoreStatementSubjects, ignoreAnonIds, ignoreAnonProperties);
			
			while (o1.hasNext()) {
				if (!o2.hasNext()) {
					return 1;
				}
				
				if ((result = statementComparator.compare(o1.nextStatement(), o2.nextStatement())) != 0) {
					return result;
				}
				
			}
			
			if (o2.hasNext()) {
				return -1;
			}
			
			return 0;
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
	
	
}
