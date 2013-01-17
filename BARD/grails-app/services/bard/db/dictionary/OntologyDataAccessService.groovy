package bard.db.dictionary

import bard.db.dictionary.*
import bard.db.registration.*;

class OntologyDataAccessService {
	
	private static final String ASSAY_DESCRIPTOR = "assay protocol"
	
	private static final String BIOLOGY_DESCRIPTOR = "biology"
	
	private static final String INSTANCE_DESCRIPTOR = "project management"

	
	public List<Descriptor> getAttributeDescriptors(String path, String label){
		def results = BardDescriptor.findAllByFullPathLikeAndLabelIlike(BardDescriptor.ROOT_PREFIX + path + "%", "%" + label + "%")
		return results
	}

    List<Descriptor> getLeaves(BardDescriptor start, String labelExpr) {
        def parents = [start]
        def leaves = []
        while(!parents.isEmpty()) {
            def nextParents = []
            def query = AssayDescriptor.where {(parent in parents) && ((leaf != Boolean.TRUE) || (label ==~ labelExpr) ) }
            query.list().each {
                if(it.leaf) {
                    nextParents << it
                } else {
                    leaves << it
                }
            }
            parents = nextParents
        }

        return leaves
    }

    /**
     * @return a list of descriptors which are the child of the given element
     */
	public List<Descriptor> getValueDescriptors(Long elementId, String path, String term){
        Element element = Element.get(elementId)
        BardDescriptor root = BardDescriptor.findByElement(element)
        return getLeaves(root, BardDescriptor.ROOT_PREFIX+path+"%")
	}
}
