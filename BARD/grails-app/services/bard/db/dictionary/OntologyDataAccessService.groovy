package bard.db.dictionary

import bard.db.dictionary.*
import bard.db.registration.*;

class OntologyDataAccessService {

    public List<Element> getElementsInTree(Long assayContextId, String group) {		
		List<AssayContextItem> assayContextItems = AssayContextItem.where {assayContext {id == assayContextId}}
		return assayContextItems
    }
}
