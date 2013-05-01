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
        Element parentElement = Element.findById(termCommand.parentId)
        Element unit = termCommand.unitId ?: Element.findById(termCommand.unitId)
        Element element = new Element(label: termCommand.label,description: termCommand.description,
                abbreviation: termCommand.abbreviation,synonyms: termCommand.synonyms, unit: unit, comments: termCommand.comments)
        element.save(flush: true)
        addElementHierarchy(parentElement,element,termCommand.relationship)
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
                childElement: childElement, relationshipType: relationshipType, modifiedBy: springSecurityService.principal?.username, dateCreated: new Date())

        elementHierarchy.save(failOnError: true)

        parentElement.parentHierarchies.add(elementHierarchy)

        childElement.childHierarchies.add(elementHierarchy)
        return elementHierarchy

    }
}