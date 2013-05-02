package bard.db.dictionary

import bard.hibernate.AuthenticatedUserRequired
import grails.plugins.springsecurity.SpringSecurityService

/**
 * used to modify Element's and ElementHierarchy's
 */
class ModifyElementAndHierarchyService {

    String relationshipType

    SpringSecurityService springSecurityService

    /**
     * @param relationshipType    relationshipType to be used when finding and modifying ElementHierarchy's
     */
    public ModifyElementAndHierarchyService(String relationshipType = "subClassOf") {
        this.relationshipType = relationshipType
    }

    /**
     * Attempt to modify the element and path to match the proposed changes contained in newElementAndPath
     * @param newElementAndPath
     * @throws NewElementLabelMatchesExistingElementLabelException
     * @throws AuthenticatedUserRequired
     * @throws ModifyElementAndHierarchyLoopInPathException
     */
    void modify(NewElementAndPath newElementAndPath) throws NewElementLabelMatchesExistingElementLabelException,
            AuthenticatedUserRequired, ModifyElementAndHierarchyLoopInPathException {

        if (newElementAndPath.element.label != newElementAndPath.newElementLabel) {
            renameElement(newElementAndPath.element, newElementAndPath.newElementLabel)
        }

        updateHierarchyIfNeeded(newElementAndPath)
    }

    /**
     * compare the element hierarchy from the database that represents the path of the element with the current elements
     * the user has entered to be the path.  If different, make changes so that element hierarchies in database reflect
     * user specified path.
     * @param elementHierarchyList
     * @param element
     * @param newPathElementList
     * @throws AuthenticatedUserRequired
     */
    void updateHierarchyIfNeeded(NewElementAndPath newElementAndPath) throws AuthenticatedUserRequired,
            ModifyElementAndHierarchyLoopInPathException {

        //represents the previous path as an element hierarchy list
        List<ElementHierarchy> previousPathAsElementHierarchyList = newElementAndPath.elementHierarchyList
        //the child in the parent-child relationship that is being updated
        Element childElement = newElementAndPath.element
        //the new path as represented by a list of elements
        List<Element> newPathAsElementList = newElementAndPath.newPathElementList

        if (previousPathAsElementHierarchyList.size() == 0 && newPathAsElementList.size() > 0) {
            Element newPathLastElement = newPathAsElementList.get(newPathAsElementList.size()-1)

            //test if the proposed path has any loops by searching recursively
            List<Element> pathWithLoop = checkPathForLoop(newPathAsElementList, childElement)
            if (null == pathWithLoop) {
                ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: newPathLastElement,
                        childElement: childElement, relationshipType: relationshipType, dateCreated: new Date(),
                        modifiedBy: getUsername())

                elementHierarchy.save(failOnError: true)

                newPathLastElement.parentHierarchies.add(elementHierarchy)

                childElement.childHierarchies.add(elementHierarchy)
            } else {
                throw new ModifyElementAndHierarchyLoopInPathException(pathWithLoop: pathWithLoop)
            }

        } else if (previousPathAsElementHierarchyList.size() > 0 && newPathAsElementList.size() == 0) {
            ElementHierarchy elementHierarchy = previousPathAsElementHierarchyList.get(previousPathAsElementHierarchyList.size()-1)
            Element originalParent = elementHierarchy.parentElement
            originalParent.parentHierarchies.remove(elementHierarchy)
            childElement.childHierarchies.remove(elementHierarchy)
            elementHierarchy.parentElement = null
            elementHierarchy.childElement = null
            elementHierarchy.delete()

        } else if (previousPathAsElementHierarchyList.size() > 0 && newPathAsElementList.size() > 0) {
            //last section of previous path represented as element hierarchy.  This represents what the parent-child
            //relationship was previously.
            ElementHierarchy previousPathLastElementHierarchy = previousPathAsElementHierarchyList.get(previousPathAsElementHierarchyList.size()-1)
            //last section of new path - parent, represented as element
            //this represents what the new parent should be in the parent-child relationship
            Element newPathLastElement = newPathAsElementList.get(newPathAsElementList.size()-1)

            //check if there is a change to be made - if the new parent is different from the old
            if (previousPathLastElementHierarchy.parentElement != newPathLastElement) {

                //test if the proposed path has any loops by searching recursively
                List<Element> pathWithLoop = checkPathForLoop(newPathAsElementList, childElement)
                if (null == pathWithLoop) {
                    Element oldParent = previousPathLastElementHierarchy.parentElement
                    oldParent.parentHierarchies.remove(previousPathLastElementHierarchy)
                    previousPathLastElementHierarchy.parentElement = newPathLastElement
                    newPathLastElement.parentHierarchies.add(previousPathLastElementHierarchy)
                } else {
                    throw new ModifyElementAndHierarchyLoopInPathException(pathWithLoop: pathWithLoop)
                }
            }
        } else {
            //both are empty and therefore the same; no change to be made
        }
    }

    /**
     * attempt to rename the provided element with the provided new label
     * @param element
     * @param newLabel
     * @throws NewElementLabelMatchesExistingElementLabelException
     */
    void renameElement(Element element, String newLabel) throws NewElementLabelMatchesExistingElementLabelException {
        Element duplicate = Element.findByLabel(newLabel)
        if (duplicate) {
            throw new NewElementLabelMatchesExistingElementLabelException(matchingElement: duplicate)
        } else {
            element.label = newLabel
            element.save()
        }
    }

    private String getUsername() {
        String username = springSecurityService.getPrincipal()?.username
        if (!username) {
            throw new AuthenticatedUserRequired('An authenticated user was expected this point');
        }
        return username
    }

    /**
     * check to see if changing the current set of ElementHierarchy's to the path indicated by the provided newPath will
     * cause loops to be present in the set of ElementHierarchy's
     * @param newPath
     * @param child
     * @return
     */
    public static List<Element> checkPathForLoop(List<Element> newPath, Element child) {
        assert newPath.size() > 0

        List<Element> testPath = new LinkedList<Element>(newPath)
        testPath.add(child)

        if (newPath.last() == child) {
            return testPath
        } else {
            return hasLoopInReachableDescendant(testPath)
        }
    }

    private static List<Element> hasLoopInReachableDescendant(List<Element> testPath) {
        Element lastElement = testPath.last()

        for (ElementHierarchy parentHierarchy : lastElement.parentHierarchies) {
            if (testPath.contains(parentHierarchy.childElement)) {
                //add the child so that the path contains the full loop
                testPath.add(parentHierarchy.childElement)
                return testPath
            } else {
                List<Element> newTestPath = new LinkedList<Element>(testPath)
                newTestPath.add(parentHierarchy.childElement)
                return hasLoopInReachableDescendant(newTestPath)
            }
        }

        return null
    }
}

class NewElementLabelMatchesExistingElementLabelException extends Exception {
    Element matchingElement
}

class ModifyElementAndHierarchyLoopInPathException extends Exception {
    List<Element> pathWithLoop
}