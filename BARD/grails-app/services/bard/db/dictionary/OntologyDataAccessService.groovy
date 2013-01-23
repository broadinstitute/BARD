package bard.db.dictionary

class OntologyDataAccessService {

    private static final String ASSAY_DESCRIPTOR = "assay protocol"

    private static final String BIOLOGY_DESCRIPTOR = "biology"

    private static final String INSTANCE_DESCRIPTOR = "project management"

    public static class ElementSummary {
        String label;
        Number elementId;
    }

    Map<String, Collection<ElementSummary>> cachedElements = null;

    public Map<String, Collection<ElementSummary>> computeTrees() {
        Map<String, Collection<ElementSummary>> trees = new HashMap()
        List<ElementHierarchy> relationships = ElementHierarchy.findAll();

        // let hibernate also load the elements for this round
        Element.findAll()

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

    public void ensureTreeCached() {
        if (cachedElements == null) {
            cachedElements = Collections.synchronizedMap(computeTrees())
        }
    }

    public List<ElementSummary> getElementsFromTree(String treeName, String label) {
        ensureTreeCached()

        if (label == null)
            label = ""

        Collection<ElementSummary> elements = cachedElements.get(treeName)

        return elements.findAll { it.label != null && it.label.toLowerCase().contains(label.toLowerCase()) }
    }

    /*
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
    */

    public List<Descriptor> getAttributeDescriptors(String path, String label) {
        def results
        if (path && path.startsWith(ASSAY_DESCRIPTOR)) {
            results = AssayDescriptor.findAllByFullPathLikeAndLabelIlike(path + "%", "%" + label + "%")
        } else if (path && path.startsWith(BIOLOGY_DESCRIPTOR)) {
            results = BiologyDescriptor.findAllByFullPathLikeAndLabelIlike(path + "%", "%" + label + "%")
        } else if (path && path.startsWith(INSTANCE_DESCRIPTOR)) {
            results = InstanceDescriptor.findAllByFullPathLikeAndLabelIlike(path + "%", "%" + label + "%")
        } else {
            results = BardDescriptor.findAllByLabelIlike("%${label}%")
        }
        return results
    }

    public List<Descriptor> getValueDescriptors(Long elementId, String path, String term) {
        def results
        if (path && path.startsWith(ASSAY_DESCRIPTOR)) {
            def query = AssayDescriptor.where { (element.id == elementId) }
            results = query.list()
            if (results) {
                List<Descriptor> allDescriptors = new ArrayList<Descriptor>()
                for (ad in results) {
                    query = AssayDescriptor.where { (fullPath.startsWith(ad.path)) && (leaf == true) && (label ==~ "%" + term + "%") }
                    def descriptors = query.list()
                    allDescriptors.addAll(descriptors)
                }
                results = allDescriptors
            }
        } else if (path && path.startsWith(BIOLOGY_DESCRIPTOR)) {
            def query = BiologyDescriptor.where { (element.id == elementId) }
            results = query.list()
            if (results) {
                List<Descriptor> allDescriptors = new ArrayList<Descriptor>()
                for (ad in results) {
                    query = BiologyDescriptor.where { (fullPath.startsWith(ad.path)) && (leaf == true) && (label ==~ "%" + term + "%") }
                    def descriptors = query.list()
                    allDescriptors.addAll(descriptors)
                }
                results = allDescriptors
            }
        } else if (path && path.startsWith(INSTANCE_DESCRIPTOR)) {
            def query = InstanceDescriptor.where { (element.id == elementId) }
            results = query.list()
            if (results) {
                List<Descriptor> allDescriptors = new ArrayList<Descriptor>()
                for (ad in results) {
                    query = InstanceDescriptor.where { (fullPath.startsWith(ad.path)) && (leaf == true) && (label ==~ "%" + term + "%") }
                    def descriptors = query.list()
                    allDescriptors.addAll(descriptors)
                }
                results = allDescriptors
            }
        } else {
            def query = BardDescriptor.where { (element.id == elementId) }
            results = query.list()
            if (results) {
                List<Descriptor> allDescriptors = new ArrayList<Descriptor>()
                for (ad in results) {
                    query = BardDescriptor.where { (fullPath.startsWith(ad.path)) && (leaf == true) && (label ==~ "%" + term + "%") }
                    def descriptors = query.list()
                    allDescriptors.addAll(descriptors)
                }
                results = allDescriptors
            }
        }
        println "Results - Descriptor list size:" + results?.size()
        return results
    }
}
