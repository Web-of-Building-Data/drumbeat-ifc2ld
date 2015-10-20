package fi.hut.cs.drumbeat.rdf;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OwlProfile {

	public static final float OWL_VERSION_1_0 = 1.0f;
	public static final float OWL_VERSION_2_0 = 2.0f;

//	public enum RdfTripleObjectTypeEnum {
//		Single, List, Data, Object, ClassIdentifier, NonClassIdentifier, ZeroOrOne, ZeroOrOneOrMany;
//
//		public static final EnumSet<RdfTripleObjectTypeEnum> ANY = EnumSet
//				.allOf(RdfTripleObjectTypeEnum.class);
//		public static final EnumSet<RdfTripleObjectTypeEnum> SingleObject = EnumSet
//				.of(RdfTripleObjectTypeEnum.Single,
//						RdfTripleObjectTypeEnum.Object);
//		public static final EnumSet<RdfTripleObjectTypeEnum> SingleData = EnumSet
//				.of(RdfTripleObjectTypeEnum.Single,
//						RdfTripleObjectTypeEnum.Data);
//		public static final EnumSet<RdfTripleObjectTypeEnum> DataList = EnumSet
//				.of(RdfTripleObjectTypeEnum.List, RdfTripleObjectTypeEnum.Data);
//		public static final EnumSet<RdfTripleObjectTypeEnum> ObjectList = EnumSet
//				.of(RdfTripleObjectTypeEnum.List,
//						RdfTripleObjectTypeEnum.Object);
//	}

	private OwlProfileEnum owlProfileId;
	private float owlVersion;
	private Set<Resource> supportedDatatypes;

	public OwlProfile(OwlProfileEnum owlProfileId) {
		this.owlProfileId = owlProfileId;
		this.owlVersion = owlProfileId.toVersion();
	}

	/**
	 * @return the owlProfile
	 */
	public OwlProfileEnum getOwlProfileId() {
		return owlProfileId;
	}

	/**
	 * @return the owlVersion
	 */
	public float getOwlVersion() {
		return owlVersion;
	}

	public boolean isOwl2() {
		return owlVersion == OWL_VERSION_2_0;
	}

	public boolean supportsStatement(Property property, RDFNode object) {

		if (isOwl2()) {

			if (owlProfileId == OwlProfileEnum.OWL2_EL
					|| owlProfileId == OwlProfileEnum.OWL2_QL) {

				if (property.equals(OWL2.allValuesFrom) || // ObjectAllValuesFrom,
															// DataAllValuesFrom
						property.equals(OWL2.disjointUnionOf) || // DisjointUnion
						property.equals(OWL.oneOf) || // ObjectOneOf, DataOneOf
						property.equals(OWL.unionOf) || // ObjectUnionOf,
														// DataUnionOf
						property.equals(OWL.maxCardinality) || // ObjectMaxCardinality,
																// DataMaxCardinality
						property.equals(OWL.minCardinality) || // ObjectMinCardinality,
																// DataMinCardinality
						property.equals(OWL.cardinality) || // ObjectExactCardinality,
															// DataExactCardinality
						property.equals(OWL2.hasKey)) // ObjectExactCardinality,
														// DataExactCardinality
				{
					//
					// XXXAllValuesFrom, XXXCardinality, XXXUnionOf,
					// DisjointUnion
					// are not supported by OWL 2 EL
					// See: http://www.w3.org/TR/owl2-profiles/#Feature_Overview
					//
					return false;
				} else if (property.equals(OWL.inverseOf)) { // InverseObjectProperties
					//
					// Many XXXObjectProperty are not supported by OWL 2 EL
					// See: http://www.w3.org/TR/owl2-profiles/#Feature_Overview
					//
					return false; // !tripleObjectType.contains(RdfTripleObjectTypeEnum.Object);
				} else if (property.equals(RDF.type) && object != null) {
					if (object instanceof RDFList || !object.isResource()) {
						throw new IllegalArgumentException(String.format("Invalid statement object: %s", object));
					}
					if (object.equals(OWL.Restriction) ||
						object.equals(OWL.FunctionalProperty) ||		// FunctionalObjectProperty,
						object.equals(OWL.InverseFunctionalProperty))	// InverseFunctionalObjectProperty 
					{							
						//
						// Many XXXObjectProperty are not supported by OWL 2 EL
						// See:
						// http://www.w3.org/TR/owl2-profiles/#Feature_Overview
						//
						return false; // !tripleObjectType.contains(RdfTripleObjectTypeEnum.Object);
					}
				}

			} else if (owlProfileId == OwlProfileEnum.OWL2_RL) {

				if (property.equals(OWL.oneOf) || // ObjectOneOf, DataOneOf
						property.equals(OWL.unionOf))  // ObjectUnionOf,
														// DataUnionOf
//						property.equals(OWL.disjointWith)) // DisjointClasses
				{
					//
					// Usage of owl:Thing is restricted by the grammar of OWL 2
					// RL
					// See: http://www.w3.org/TR/owl2-profiles/#Entities_3
					//
					return false;
				} else if (property.equals(RDF.type) && object != null) {
					if (object instanceof RDFList || !object.isResource()) {
						throw new IllegalArgumentException(String.format("Invalid statement object: %s", object));
					}
					if (object.equals(OWL.Thing)) {
						//
						// Usage of owl:Thing is restricted by the grammar of
						// OWL 2
						// RL
						// See: http://www.w3.org/TR/owl2-profiles/#Entities_3
						//
						return false;
					}
				}

			}
		} else { // OWL 1

			if (owlProfileId == OwlProfileEnum.OWL1_Lite) {

				if (property.equals(OWL.complementOf)
						|| property.equals(OWL.disjointWith)
						|| property.equals(OWL2.hasKey)
						|| property.equals(OWL.oneOf)
						|| property.equals(OWL.unionOf)) {
					//
					// OWL Lite forbids the use of owl:OneOf, owl:unionOf,
					// owl:complementOf, owl:hasValue, owl:disjointWith,
					// owl:DataRange
					// See: http://www.w3.org/TR/owl-ref/#OWLLite
					//
					return false;
				} else if (property.equals(OWL.cardinality)) {
					//
					// OWL Lite includes the use of all three types of
					// cardinality
					// constraints,
					// but only when used with the values "0" or "1".
					// See: http://www.w3.org/TR/owl-ref/#CardinalityRestriction
					//
					// assert(objects != null && objects.length == 1 &&
					// objects[0].isLiteral());
					if (object == null || !object.isLiteral()) {
						throw new IllegalArgumentException(String.format("Invalid statement object: %s", object));
					}
					int cardinality = object.asLiteral().getInt();
					return cardinality == 0 || cardinality == 1;
				} else if (property.equals(RDFS.domain)
						|| property.equals(RDFS.range)) {
					//
					// In OWL Lite the value of rdfs:domain and rdfs:range must
					// be a
					// class identifier.
					// See: http://www.w3.org/TR/owl-ref/#domain-def
					// See: http://www.w3.org/TR/owl-ref/#range-def
					//
					// assert(object != null && object.isResource());
					if (object == null || object instanceof RDFList || !object.isResource()) {
						throw new IllegalArgumentException(String.format("Invalid statement object: %s", object));
					}
					return object.asResource().isURIResource();
				} else if (property.equals(RDF.type)  && object != null) {
					if (object instanceof RDFList || !object.isResource()) {
						throw new IllegalArgumentException(String.format("Invalid statement object: %s", object));
					}
					if (object.equals(OWL.Restriction))
					{							
						return false;
					}				
				}

			}
		}

		if (property.equals(OWL.disjointWith)) {
			//
			// Range of owl:disjointWith in OWL 1 can be only a single object,
			// but in OWL 2 it can be a list.
			// See:
			// http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale#F2:_DisjointClasses.
			//

			if (object == null || !object.isResource()) {
				throw new IllegalArgumentException(String.format("Invalid statement object: %s", object));
			}
			
			return isOwl2() ||  !(object instanceof RDFList) ||  ((RDFList)object).size() == 1; 

		} else if (property.equals(OWL2.disjointUnionOf)
				|| property.equals(OWL2.withRestrictions)
				|| property.equals(RdfVocabulary.XSD.maxExclusive)
				|| property.equals(RdfVocabulary.XSD.minExclusive)) {
			//
			// owl:disjointUnionOf are new features of OWL 2, but it is not supported by OWL 2 EL, QL and RL
			// See: http://www.w3.org/2007/OWL/wiki/New_Features_and_Rationale.
			// www.w3.org/TR/owl2-profiles
			//
			return owlProfileId == OwlProfileEnum.OWL2_Full;
		}

		return true;

	}

	public boolean supportDataType(Resource type) {

		return getSupportedDataTypes().contains(type);

	}

	public Set<Resource> getSupportedDataTypes() {
		if (supportedDatatypes == null) {
			supportedDatatypes = new HashSet<>();

			if (isOwl2()) { // OWL 2

				if (owlProfileId == OwlProfileEnum.OWL2_EL
						|| owlProfileId == OwlProfileEnum.OWL2_QL) {

					// //
					// // The following datatypes must not be used in OWL 2 EL
					// and OWL 2 QL:
					// // xsd:boolean, xsd:double, xsd:float, xsd:XXXInteger
					// (exception xsd:integer and xsd:nonNegativeInteger),
					// // xsd:long, xsd:int, xsd:short, xsd:byte,
					// xsd:unsignedXXX, xsd:language
					// // See: http://www.w3.org/TR/owl2-profiles/#Entities
					// // See: http://www.w3.org/TR/owl2-profiles/#Entities2
					// //

					// supportedDatatypes.add(RDF.PlainLiteral);
					// supportedDatatypes.add(RDF.XMLLiteral);
					supportedDatatypes.add(RDFS.Literal);
					supportedDatatypes.add(RDFS.Literal);
					supportedDatatypes.add(RdfVocabulary.OWL.real);
					supportedDatatypes.add(RdfVocabulary.OWL.rational);
					supportedDatatypes.add(XSD.decimal);
					supportedDatatypes.add(XSD.integer);
					supportedDatatypes.add(XSD.nonNegativeInteger);
					supportedDatatypes.add(XSD.xstring);
					supportedDatatypes.add(XSD.normalizedString);
					supportedDatatypes.add(XSD.token);
					supportedDatatypes.add(XSD.Name);
					supportedDatatypes.add(XSD.NCName);
					supportedDatatypes.add(XSD.NMTOKEN);
					supportedDatatypes.add(XSD.hexBinary);
					supportedDatatypes.add(XSD.base64Binary);
					supportedDatatypes.add(XSD.anyURI);
					supportedDatatypes.add(XSD.dateTime);
					// supportedDatatypes.add(XSD.dateTimeStamp);

				} else {

					// owlProfileId == OwlProfileEnum.OWL2_RL

					// supportedDatatypes.add(RDF.PlainLiteral);
					// supportedDatatypes.add(RDF.XMLLiteral);
					supportedDatatypes.add(RDFS.Literal);
					supportedDatatypes.add(XSD.decimal);
					supportedDatatypes.add(XSD.integer);
					supportedDatatypes.add(XSD.nonNegativeInteger);
					supportedDatatypes.add(XSD.nonPositiveInteger);
					supportedDatatypes.add(XSD.positiveInteger);
					supportedDatatypes.add(XSD.negativeInteger);
					supportedDatatypes.add(XSD.xlong);
					supportedDatatypes.add(XSD.xint);
					supportedDatatypes.add(XSD.xshort);
					supportedDatatypes.add(XSD.xbyte);
					supportedDatatypes.add(XSD.unsignedLong);
					supportedDatatypes.add(XSD.unsignedInt);
					supportedDatatypes.add(XSD.unsignedShort);
					supportedDatatypes.add(XSD.unsignedByte);
					supportedDatatypes.add(XSD.xfloat);
					supportedDatatypes.add(XSD.xdouble);
					supportedDatatypes.add(XSD.xstring);
					supportedDatatypes.add(XSD.normalizedString);
					supportedDatatypes.add(XSD.token);
					supportedDatatypes.add(XSD.language);
					supportedDatatypes.add(XSD.Name);
					supportedDatatypes.add(XSD.NCName);
					supportedDatatypes.add(XSD.NMTOKEN);
					supportedDatatypes.add(XSD.xboolean);
					supportedDatatypes.add(XSD.hexBinary);
					supportedDatatypes.add(XSD.base64Binary);
					supportedDatatypes.add(XSD.anyURI);
					supportedDatatypes.add(XSD.dateTime);
					// supportedDatatypes.add(XSD.dateTimeStamp);

					if (owlProfileId != OwlProfileEnum.OWL2_RL) {
						supportedDatatypes.add(RdfVocabulary.OWL.real);
						supportedDatatypes.add(RdfVocabulary.OWL.rational);
					}

				}

			} else { // OWL 1

				//
				// OWL1 supports almost all XSD types:
				// See: http://www.w3.org/TR/owl-ref/#rdf-datatype
				//

				supportedDatatypes.add(XSD.xstring);
				supportedDatatypes.add(XSD.normalizedString);
				supportedDatatypes.add(XSD.token);
				supportedDatatypes.add(XSD.language);
				supportedDatatypes.add(XSD.NMTOKEN);
				supportedDatatypes.add(XSD.Name);
				supportedDatatypes.add(XSD.NCName);

				supportedDatatypes.add(XSD.xboolean);
				supportedDatatypes.add(XSD.decimal);
				supportedDatatypes.add(XSD.xfloat);
				supportedDatatypes.add(XSD.xdouble);
				supportedDatatypes.add(XSD.integer);
				supportedDatatypes.add(XSD.positiveInteger);
				supportedDatatypes.add(XSD.nonPositiveInteger);
				supportedDatatypes.add(XSD.negativeInteger);
				supportedDatatypes.add(XSD.nonNegativeInteger);
				supportedDatatypes.add(XSD.xlong);
				supportedDatatypes.add(XSD.xint);
				supportedDatatypes.add(XSD.xshort);
				supportedDatatypes.add(XSD.xbyte);
				supportedDatatypes.add(XSD.unsignedLong);
				supportedDatatypes.add(XSD.unsignedInt);
				supportedDatatypes.add(XSD.unsignedShort);
				supportedDatatypes.add(XSD.unsignedByte);

				supportedDatatypes.add(XSD.dateTime);
				supportedDatatypes.add(XSD.time);
				supportedDatatypes.add(XSD.date);
				supportedDatatypes.add(XSD.gYearMonth);
				supportedDatatypes.add(XSD.gYear);
				supportedDatatypes.add(XSD.gMonthDay);
				supportedDatatypes.add(XSD.gDay);
				supportedDatatypes.add(XSD.gMonth);

				supportedDatatypes.add(XSD.hexBinary);
				supportedDatatypes.add(XSD.base64Binary);
				supportedDatatypes.add(XSD.anyURI);

			}
		}

		return supportedDatatypes;

	}

}
