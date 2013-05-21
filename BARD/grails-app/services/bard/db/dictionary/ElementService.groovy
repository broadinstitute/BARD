package bard.db.dictionary

import bard.db.enums.AddChildMethod
import grails.plugins.springsecurity.SpringSecurityService

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class ElementService {
    SpringSecurityService springSecurityService
    OntologyDataAccessService ontologyDataAccessService
    final ExecutorService executorService = Executors.newCachedThreadPool()

    /**
     *
     * @param termCommand
     * @return the new created {@link Element}
     */
    Element addNewTerm(TermCommand termCommand) {
        Element parentElement = Element.findByLabel(termCommand.parentLabel)
        Element element = new Element(label: termCommand.label, description: termCommand.description,
                abbreviation: termCommand.abbreviation, synonyms: termCommand.synonyms, curationNotes: termCommand.curationNotes)
        element.save(flush: true)
        addElementHierarchy(parentElement, element, termCommand.relationship)

        //execute async
        this.executorService.execute(new Runnable() {
            public void run() {
                ontologyDataAccessService.reloadCache()
            }
        });

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
    /**
     * Return all the immediate child nodes for a given element
     * @param elementId
     * @return List
     */
    List getChildNodes(long elementId) {
        def childNodes = []
        final Element parentElement = Element.get(elementId)
        final List<ElementHierarchy> list = new ArrayList(parentElement.parentHierarchies)
        final List<ElementHierarchy> hierarchies = list.findAll { it.relationshipType == 'subClassOf' }
        Set<Element> seenSet = new HashSet<Element>()
        for (ElementHierarchy elementHierarchy : hierarchies) {
            final Element childElement = elementHierarchy.childElement
            if (!seenSet.contains(childElement)) {
                seenSet.add(childElement)
                boolean isLazy = childElement.parentHierarchies ? true : false
                boolean isFolder = childElement.parentHierarchies ? true : false
                final AddChildMethod childMethod = childElement.addChildMethod
                childNodes.add([elementId: childElement.id, childMethodDescription:childMethod.description,childMethod: childMethod.toString(), addClass: childMethod.label, title: childElement.label, description: childElement.description, isFolder: isFolder, isLazy: isLazy])
            }
        }
        childNodes.sort { Map a, Map b -> a["title"].toLowerCase().compareTo(b["title"].toLowerCase()) }
        return childNodes
    }
    /**
     * @param full = will render the full tree otherwise it will lazily render it
     * @return list of hierarchy
     */
    public List createElementHierarchyTree() {
        Element element = Element.findByLabel("BARD")//find the ROOT OF THE BARD TREE
        return getChildNodes(element.id)
    }
}