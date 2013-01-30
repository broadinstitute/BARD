package bard.db.dictionary

import org.hibernate.Query
import org.hibernate.Session

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

    public List<Descriptor> getAttributeDescriptors(String term) {
        List<Descriptor> results = []
        BardDescriptor.withSession { Session session ->
            /**
             * selecting all the entries in Bard_tree that aren't entries in the dictionary_tree table
             * additionally, screens out the root node in the dictionary
             */
            Query query = session.createSQLQuery("""
                select *
                from  bard_tree bt
                where not EXISTS( select 1 from DICTIONARY_TREE dt where dt.ELEMENT_ID= bt.element_Id and dt.PARENT_NODE_ID != 0 )
                and lower(bt.label) like :term
                order by lower(bt.label)
            """)
            query.addEntity(BardDescriptor)
            query.setString('term', "%${term?.trim()?.toLowerCase()}%")
            query.setReadOnly(true)
            results = query.list()
        }
        return results
    }

    public List<Descriptor> getValueDescriptors(Long elementId, String path, String term) {
        List<Descriptor> results = []
        BardDescriptor.withSession { session ->
            Query query = session.createSQLQuery("""
                select *
                from bard_tree bt
                    join (select element_id, full_path
                          from bard_tree
                          where element_id = :elementId) ancestor on ancestor.full_path = substr(bt.full_path, 0, length(ancestor.full_path))
                where lower(bt.label) like :term
                and bt.is_leaf = 'Y'
                and bt.element_status != :elementStatus
                order by lower(bt.label)
            """)
            query.addEntity(BardDescriptor)
            query.setLong("elementId", elementId)
            query.setString('term', "%${term?.trim()?.toLowerCase()}%")
            query.setString('elementStatus', ElementStatus.Retired.name())
            query.setReadOnly(true)
            results = query.list()
        }
        return results
    }
	
	public Map<Long, String> getBaseUnits(Long elementId, Long toUnitId){
		List<Long> resultsOne = UnitConversion.executeQuery("select uc.fromUnit.id from UnitConversion uc where toUnit.id = ?", toUnitId)
		List<Long> resultsTwo = Element.executeQuery("select e.id from Element e where id = ?", elementId)
		resultsOne.addAll(resultsTwo)
		List<Long> unionAll = resultsOne
		String parametizedString = getInParametizedQueryString(unionAll);
		List<UnitTree> unitResults = UnitTree.executeQuery("from UnitTree ut where ut.element.id in (" + parametizedString + ")", unionAll)
		println "# of Unit Results: " + unitResults.size()
		Map<Long, String> unitsMap = [:];
		for(UnitTree u in unitResults){
			unitsMap.put(u.id, u.label)
		}
		return unitsMap
	}
	
	private String getInParametizedQueryString(List theList){
		String parametizedString = "";
		int counter = 0;
		for(item in theList){
			counter++
			parametizedString = parametizedString + "?"
			if(counter != theList.size())
				parametizedString = parametizedString + ", "
		}
		return parametizedString
	}
}
