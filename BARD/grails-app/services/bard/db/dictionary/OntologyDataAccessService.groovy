package bard.db.dictionary

import bard.db.dictionary.*
import bard.db.registration.*;

class OntologyDataAccessService {

    private static final String ASSAY_DESCRIPTOR = "assay protocol"

    private static final String BIOLOGY_DESCRIPTOR = "biology"

    private static final String INSTANCE_DESCRIPTOR = "project management"

    public static class ElementSummary {
        String label;
        Number elementId;
    }

    Map<String, Collection<ElementSummary>> cachedElements = null;

    protected Map<String, Collection<ElementSummary>> computeTrees() {
        Map<String, Collection<ElementSummary>> trees= new HashMap()
        List<ElementHierarchy> relationships = ElementHierarchy.findAll();
        // let hibernate also load the elements for this round
        def els = Element.findAll()

        // create a mapping of parent -> children.  Again, the domain could be changed to accommodate, but want to
        // make sure this works before invasive changes
        def parentToChildren = relationships.groupBy { it.parentElement }

        def addChildren;
        addChildren = { Element element, Collection results, Set seen ->
            if (!seen.contains(element)) {
                seen.add(element)
                results.add(new ElementSummary(label: element.label, elementId: element.id))
                if (element.elementStatus != ElementStatus.Retired) {
                    for (relationship in parentToChildren.get(element)) {
                        addChildren(relationship.childElement, results, seen)
                    }
                }
            }
        }

        for (root in TreeRoot.findAll()) {
            List<Element> results = []
            addChildren(root.element, results, new HashSet())

            trees.put(root.treeName, results.sort { it.label })
        }

        return trees
    }

    public List<ElementSummary> getElementsFromTree(String treeName, String label) {
        if (label == null)
            label = ""

        if (cachedElements == null) {
            cachedElements = Collections.synchronizedMap(computeTrees())
        }

        Collection<ElementSummary> elements = cachedElements.get(treeName)

        return elements.findAll { it.label != null && it.label.toLowerCase().contains(label.toLowerCase()) }
    }

    public List<Descriptor> getAttributeDescriptors(String path, String label) {
        def results = BardDescriptor.findAllByFullPathLikeAndLabelIlike(BardDescriptor.ROOT_PREFIX + path + "%", "%" + label + "%")
        return results
    }

    List<Descriptor> getLeaves(BardDescriptor start, String labelExpr) {
        def parents = [start]
        def leaves = []
        while (!parents.isEmpty()) {
            def nextParents = []
            def query = AssayDescriptor.where { (parent in parents) && ((leaf != Boolean.TRUE) || (label ==~ labelExpr)) }
            query.list().each {
                if (it.leaf) {
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
    public List<Descriptor> getValueDescriptors(Long elementId, String path, String term) {
        Element element = Element.get(elementId)
        BardDescriptor root = BardDescriptor.findByElement(element)
        return getLeaves(root, BardDescriptor.ROOT_PREFIX + path + "%")
    }
}
