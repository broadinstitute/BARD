package bard.db.dictionary

import grails.plugins.springsecurity.SpringSecurityService

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class ElementService {
    SpringSecurityService springSecurityService

    /**
     *
     * @param termCommand
     * @return the new created {@link Element}
     */
    Element addNewTerm(TermCommand termCommand) {
        Element parentElement = Element.findByLabel(termCommand.parentLabel)
        Element unit = termCommand.unitId ?: Element.findById(termCommand.unitId)
        Element element = new Element(label: termCommand.label, description: termCommand.description,
                abbreviation: termCommand.abbreviation, synonyms: termCommand.synonyms, unit: unit, comments: termCommand.comments)
        element.save(flush: true)
        addElementHierarchy(parentElement, element, termCommand.relationship)
        return element
    }
    /**
     *
     * @param parentElement
     * @param childElement
     * @param relationshipType
     * @return the newly created {@link ElementHierarchy}
     *
     */
    protected ElementHierarchy addElementHierarchy(Element parentElement, Element childElement, String relationshipType = "subClassOf") {

        ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parentElement,
                childElement: childElement, relationshipType: relationshipType,
                modifiedBy: springSecurityService.principal?.username, dateCreated: new Date())

        elementHierarchy.save(failOnError: true)

        parentElement.parentHierarchies.add(elementHierarchy)

        childElement.childHierarchies.add(elementHierarchy)
        return elementHierarchy

    }

    protected void addChildren(Element element, Collection results, Map<Element, List<ElementHierarchy>> parentToChildren, Set seen, boolean expand) {
        if (!seen.contains(element)) {
            seen.add(element)

            if (element.elementStatus != ElementStatus.Retired) {
                final long key = element.id;
                final String title = element.label
                final String relationshipType = "subClassOf"
                final String description = element.description
                def children = []
                final List<ElementHierarchy> elementHierarchies = parentToChildren.get(element)
                for (ElementHierarchy elementHierarchy : elementHierarchies) {
                    addChildren(elementHierarchy.childElement, children, parentToChildren, seen, false)
                }
                results.add([key: key, title: title, description: description, children: children, expand: expand, relationship: relationshipType])
            }

        }
    }
    /**
     *
     * @return list of hierarchy
     */
    public List createElementHierarchyTree() {
        def rootTrees = []

        //we need to get all of the parent element that are not child elements. Find the root of the trees in the forest
        List<ElementHierarchy> rootHierarchies =
            ElementHierarchy.findAll("from ElementHierarchy parentHierarchy WHERE parentHierarchy.parentElement not in (Select childHierarchy.childElement from ElementHierarchy childHierarchy)")
        Map<Element, List<ElementHierarchy>> rootParentToChildren = rootHierarchies.groupBy { it.parentElement }

        List<ElementHierarchy> elementHierarchies = ElementHierarchy.findAll();
        // let hibernate also load the elements for this round
        Element.findAll()

        // create a mapping of parent -> children.  Again, the domain could be changed to accommodate, but want to
        // make sure this works before invasive changes
        Map<Element, List<ElementHierarchy>> parentToChildrenMap = elementHierarchies.groupBy { it.parentElement }



        for (Element rootElement in rootParentToChildren.keySet()) {
            addChildren(rootElement, rootTrees, parentToChildrenMap, new HashSet(), true)
        }

        sortByKey(rootTrees)
        return rootTrees
    }


    void sortByKey(List<Map> children) {
        for (c in children) {
            if (c.containsKey("children"))
                sortByKey(c["children"])
        }

        children.sort { Map a, Map b -> a["title"].toLowerCase().compareTo(b["title"].toLowerCase()) }
    }

}