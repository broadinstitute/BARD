package bard.db.dictionary

import bard.validation.ext.BardExternalOntologyFactory
import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import groovy.transform.TypeChecked
import org.hibernate.Query
import org.hibernate.Session
import org.springframework.util.Assert

import static BardExternalOntologyFactory.NCBI_EMAIL
import static BardExternalOntologyFactory.NCBI_TOOL

class OntologyDataAccessService {

    private static final int DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE = 20
    BardExternalOntologyFactory externalOntologyFactory

    private static final Properties externalOntologyProperites = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'test@test.com'])


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

    public List<Element> getElementsForAttributes(String term) {
        List<Element> results = []
        Element.withSession { Session session ->
            /**
             * selecting all the entries in Bard_tree that aren't entries in the dictionary_tree table
             * additionally, screens out the root node in the dictionary
             */
            Query query = session.createSQLQuery("""
                select *
                from element
                where exists (
                                select 1
                                from  bard_tree bt
                                where not EXISTS( select 1 from DICTIONARY_TREE dt where dt.ELEMENT_ID= bt.element_Id and dt.PARENT_NODE_ID != 0 )
                                and lower(bt.label) like :term
                                and bt.element_status != :elementStatus
                                and bt.ELEMENT_ID = element.ELEMENT_ID
                             )
                order by lower(element.label)
            """)
            query.addEntity(Element)
            query.setString('term', "%${term?.trim()?.toLowerCase()}%")
            query.setString('elementStatus', ElementStatus.Retired.name())
            query.setReadOnly(true)
            results = query.list()
        }
        return results
    }

    public List<Element> getElementsForValues(Long elementId, String term) {
        List<Element> results = []
        Element.withSession { Session session ->
            Query query = session.createSQLQuery("""
                select *
                from element
                where exists ( select 1
                               from bard_tree bt
                               join (select element_id, full_path
                                     from bard_tree
                                     where element_id = :ancestorElementId) ancestor on ancestor.full_path = substr(bt.full_path, 0, length(ancestor.full_path))
                               where lower(bt.label) like :term
                               and bt.element_status != :elementStatus
                               and bt.ELEMENT_ID = element.ELEMENT_ID
                             )
                order by lower(element.label)
            """)
            query.addEntity(Element)
            query.setLong("ancestorElementId", elementId)
            query.setString('term', "%${term?.trim()?.toLowerCase()}%")
            query.setString('elementStatus', ElementStatus.Retired.name())
            query.setReadOnly(true)
            results = query.list()
        }
        return results
    }

    /**
     * Given a externalUrl utilize the ExternalOntologyFactory and the underlying externalOntologyAPI implementations
     * to look for ExternalItems containing that term
     *
     * This uses a default page size of 20 to limit the number of matches returned
     *
     * @param externalUrl cannot be blank
     * @param term cannot be blank
     * @return a List<ExternalItem> empty if no matches
     */
    @TypeChecked
    List<ExternalItem> findExternalItemsByTerm(String externalUrl, String term) {
        findExternalItemsByTerm(externalUrl, term, DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE)
    }

    /**
     * Given a externalUrl utilize the ExternalOntologyFactory and the underlying externalOntologyAPI implementations
     * to look for ExternalItems containing that term
     *
     * @param externalUrl cannot be blank
     * @param term cannot be blank
     * @param limit
     * @return a List<ExternalItem> empty if no matches
     * @throws ExternalOntologyException
     */
    @TypeChecked
    List<ExternalItem> findExternalItemsByTerm(String externalUrl, String term, int limit) throws ExternalOntologyException {
        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(term, "term cannot be blank")
        final List<ExternalItem> externalItems = []
        try {
            ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl, externalOntologyProperites)
            if (externalOntology) {
                externalItems.addAll(externalOntology.findMatching(term, limit))
            }
        } catch (ExternalOntologyException e) {
            log.error("Exception when calling externalOntology.findMatching() with externalUrl: $externalUrl term: $term", e)
            throw e
        }
        externalItems
    }

    /**
     * Given a externalUrl utilize the ExternalOntologyFactory and the underlying externalOntologyAPI implementations
     * to look for an ExternalItem by it's id
     * @param externalUrl cannot be blank
     * @param id cannot be blank
     * @return an ExternalItem or null if no match is found
     * @throws ExternalOntologyException
     */
    @TypeChecked
    ExternalItem findExternalItemById(String externalUrl, String id) throws ExternalOntologyException {
        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(id, "id cannot be blank")
        ExternalItem externalItem

        try {
            ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl, externalOntologyProperites)
            if (externalOntology) {
                externalItem = externalOntology.findById(id)
            }
        } catch (ExternalOntologyException e) {
            log.error("Exception when calling externalOntology.findMatching() with externalUrl: $externalUrl term: $id", e)
            throw e
        }
        externalItem
    }

    public List<Element> getAllUnits() {
        List<Element> results = []
        Element.withSession { Session session ->
            Query query = session.createSQLQuery("""
                select e.*
				from unit_tree ut, element e
				where ut.unit_id = e.element_id
				order by lower(ut.parent_node_id)
            """)
            query.addEntity(Element)
            query.setReadOnly(true)
            results = query.list()
        }
        return results
    }

    public List<UnitTree> getBaseUnits(Long elementId, Long toUnitId) {
        List<Long> resultsOne = UnitConversion.executeQuery("select uc.fromUnit.id from UnitConversion uc where toUnit.id = ?", toUnitId)
        List<Long> resultsTwo = Element.executeQuery("select e.id from Element e where id = ?", elementId)
        resultsOne.addAll(resultsTwo)
        List<Long> unionAll = resultsOne
        String parametizedString = getInParametizedQueryString(unionAll);
        List<UnitTree> unitResults = UnitTree.executeQuery("from UnitTree ut where ut.element.id in (" + parametizedString + ")", unionAll)
        return unitResults
//        println "# of Unit Results: " + unitResults.size()
//        Map<Long, String> unitsMap = [:];
//        for (UnitTree u in unitResults) {
//            unitsMap.put(u.id, u.label)
//        }
//        return unitsMap
    }

    private String getInParametizedQueryString(List theList) {
        String parametizedString = "";
        int counter = 0;
        for (item in theList) {
            counter++
            parametizedString = parametizedString + "?"
            if (counter != theList.size())
                parametizedString = parametizedString + ", "
        }
        return parametizedString
    }

}
