package fi.hut.cs.drumbeat.rdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class OwlProfileList extends ArrayList<OwlProfile> {
	
	private static final long serialVersionUID = 1L;
	
	public OwlProfileList() {		
	}

	public OwlProfileList(String[] owlProfileNames) {
		for (String owlProfileName : owlProfileNames) {
			OwlProfileEnum owlProfileEnum = OwlProfileEnum.valueOf(owlProfileName.trim());
			add(new OwlProfile(owlProfileEnum));
		}		
	}
	
	public List<OwlProfileEnum> getOwlProfileIds() {
		return this.stream().map(OwlProfile::getOwlProfileId).collect(Collectors.toList());
	}
	
	public boolean supportsStatement(Property property, RDFNode object) {
		for (OwlProfile owlProfile : this) {
			if (!owlProfile.supportsStatement(property, object)) {
				return false;
			}
		}
		return true;
	}
	
	
	
	public Resource getFirstSupportedDatatype(Collection<Resource> datatypes) {
		
		for (Resource type : datatypes) {
			boolean isSupported = true;
			for (OwlProfile profile : this) {
				if (!profile.supportDataType(type)) {
					isSupported = false;
				}
			}
			if (isSupported) {
				return type;
			}
		}
		
		return null;		
	}

}
