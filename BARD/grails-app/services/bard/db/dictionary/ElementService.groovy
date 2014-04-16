package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import bard.util.BardCacheUtilsService
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
    BardCacheUtilsService bardCacheUtilsService

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
    List getChildNodes(long elementId, boolean doNotShowRetired, String expectedValueType) {
        ExpectedValueType expValType = ExpectedValueType.values().find { val -> val?.name()?.toLowerCase() == expectedValueType?.toLowerCase() }
        def childNodes = []
        final Element parentElement = Element.get(elementId)
        final List<ElementHierarchy> list = new ArrayList(parentElement.parentHierarchies)
        final List<ElementHierarchy> hierarchies = list.findAll { it.relationshipType == 'subClassOf' }
        Set<Element> seenSet = new HashSet<Element>()
        for (ElementHierarchy elementHierarchy : hierarchies) {
            final Element childElement = elementHierarchy.childElement
            if (doNotShowRetired && childElement.elementStatus.equals(ElementStatus.Retired)) {
                continue
            }
            //Check if this is the element type we are expecting
            if (expValType && childElement.expectedValueType != expValType) {
                continue
            }
            if (!seenSet.contains(childElement)) {
                seenSet.add(childElement)
                boolean isLazy = childElement.parentHierarchies ? true : false
                boolean isFolder = childElement.parentHierarchies ? true : false
                final AddChildMethod childMethod = childElement.addChildMethod
                String childElementLabel = childElement.label + (childElement.elementStatus.equals(ElementStatus.Retired) ? " (Retired)" : "")
                childNodes.add([elementId: childElement.id, key: childElement.id, childMethodDescription: childMethod.description, childMethod: childMethod.toString(), addClass: childMethod.label, title: childElementLabel, description: childElement.description, isFolder: isFolder, isLazy: isLazy])
            }
        }
        childNodes.sort { Map a, Map b -> a["title"].toLowerCase().compareTo(b["title"].toLowerCase()) }
        return childNodes
    }
    /**
     * @param full = will render the full tree otherwise it will lazily render it
     * @return list of hierarchy
     */
    public List createElementHierarchyTree(boolean doNotShowRetired, String treeRoot, String expectedValueType) {
        Element element = Element.findByLabel(treeRoot)//find the ROOT OF THE TREE ("BARD" or "BARD Dictionary")
        return getChildNodes(element.id, doNotShowRetired, expectedValueType)
    }

    /**
     * Converts a list of elements and their paths, represented by List<ElementAndFullPath>, to a data-structure
     *  that the Select2 widget is expecting.
     *
     * @param elementListWithPaths
     * @return
     */
    List convertPathsToSelectWidgetStructures(List<ElementAndFullPath> elementListWithPaths) {
        List<ElementAndFullPath> select2Elements = []

        for (ElementAndFullPath elementAndFullPath in elementListWithPaths) {
            select2Elements << [
                    id: elementAndFullPath.element.id,
                    text: elementAndFullPath.element.label,
                    description: elementAndFullPath.element.description,
                    fullPath: elementAndFullPath.toString(),
                    parentFullPath: elementAndFullPath.getParentFullPath()
            ]
        }

        return select2Elements
    }

    public static boolean isChildOf(Element element, Element parent) {
        assert parent != null

        Set<Element> seen = [] as Set
        Set<Element> toCheck = [element]

        while(toCheck.size() > 0) {
            Element e = toCheck.first()
            toCheck.remove(e)

            if(seen.contains(e)) {
                continue;
            }

            seen.add(e)
            if(e == parent)
                return true;

            e.childHierarchies.each {
                toCheck.add(it.parentElement)
            }
        }

        return false;
    }

}