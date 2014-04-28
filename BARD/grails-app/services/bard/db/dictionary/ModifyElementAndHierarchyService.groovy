/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.dictionary

import bard.hibernate.AuthenticatedUserRequired
import bard.util.BardCacheUtilsService
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.lang3.BooleanUtils

/**
 * used to modify Element's and ElementHierarchy's
 */
class ModifyElementAndHierarchyService {

    String relationshipType

    SpringSecurityService springSecurityService
    BardCacheUtilsService bardCacheUtilsService

    /**
     * @param relationshipType relationshipType to be used when finding and modifying ElementHierarchy's
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
            bardCacheUtilsService.refreshDueToNonDictionaryEntry()
        }

        if (updateHierarchyIfNeeded(newElementAndPath)) {  //reload the cache if this returned true
            bardCacheUtilsService.refreshDueToNonDictionaryEntry()
        }
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
    boolean updateHierarchyIfNeeded(NewElementAndPath newElementAndPath) throws AuthenticatedUserRequired,
            ModifyElementAndHierarchyLoopInPathException {

        //represents the previous path as an element hierarchy list
        List<ElementHierarchy> previousPathAsElementHierarchyList = newElementAndPath.elementHierarchyList
        //the child in the parent-child relationship that is being updated
        Element childElement = newElementAndPath.element
        //the new path as represented by a list of elements
        List<Element> newPathAsElementList = newElementAndPath.newPathElementList

        if (previousPathAsElementHierarchyList.size() == 0 && newPathAsElementList.size() > 0) {
            Element newPathLastElement = newPathAsElementList.get(newPathAsElementList.size() - 1)

            //test if the proposed path has any loops by searching recursively
            List<Element> pathWithLoop = checkPathForLoop(newPathAsElementList, childElement)
            if (pathWithLoop.isEmpty()) {
                ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: newPathLastElement,
                        childElement: childElement, relationshipType: relationshipType, dateCreated: new Date(),
                        modifiedBy: getUsername())

                elementHierarchy.save(failOnError: true)

                newPathLastElement.parentHierarchies.add(elementHierarchy)

                childElement.childHierarchies.add(elementHierarchy)
                return true
            } else {
                throw new ModifyElementAndHierarchyLoopInPathException(pathWithLoop: pathWithLoop)
            }

        } else if (previousPathAsElementHierarchyList.size() > 0 && newPathAsElementList.size() == 0) {
            ElementHierarchy elementHierarchy = previousPathAsElementHierarchyList.get(previousPathAsElementHierarchyList.size() - 1)
            Element originalParent = elementHierarchy.parentElement
            originalParent.parentHierarchies.remove(elementHierarchy)
            childElement.childHierarchies.remove(elementHierarchy)
            elementHierarchy.parentElement = null
            elementHierarchy.childElement = null
            elementHierarchy.delete()
            return true

        } else if (previousPathAsElementHierarchyList.size() > 0 && newPathAsElementList.size() > 0) {
            //last section of previous path represented as element hierarchy.  This represents what the parent-child
            //relationship was previously.
            ElementHierarchy previousPathLastElementHierarchy = previousPathAsElementHierarchyList.get(previousPathAsElementHierarchyList.size() - 1)
            //last section of new path - parent, represented as element
            //this represents what the new parent should be in the parent-child relationship
            Element newPathLastElement = newPathAsElementList.get(newPathAsElementList.size() - 1)

            //check if there is a change to be made - if the new parent is different from the old
            if (previousPathLastElementHierarchy.parentElement != newPathLastElement) {

                //test if the proposed path has any loops by searching recursively
                List<Element> pathWithLoop = checkPathForLoop(newPathAsElementList, childElement)
                if (pathWithLoop.isEmpty()) {
                    Element oldParent = previousPathLastElementHierarchy.parentElement
                    oldParent.parentHierarchies.remove(previousPathLastElementHierarchy)
                    previousPathLastElementHierarchy.parentElement = newPathLastElement
                    newPathLastElement.parentHierarchies.add(previousPathLastElementHierarchy)
                    return true
                } else {
                    throw new ModifyElementAndHierarchyLoopInPathException(pathWithLoop: pathWithLoop)
                }
            }
        } else {
            //both are empty and therefore the same; no change to be made
        }
        return false
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

        if (newPath.contains(child)) {
            return testPath
        } else {
            return hasLoopInReachableDescendant(testPath)
        }
    }

    private static List<Element> hasLoopInReachableDescendant(List<Element> testPath) {
        final List<Element> loopPath = []
        final List<ElementHierarchy> lastElementChildren = testPath.last().parentHierarchies.toList()

        for (ElementHierarchy parentHierarchy : lastElementChildren) {
            printPath(testPath, "loop")
            if (testPath.contains(parentHierarchy.childElement)) {
                //add the child so that the path contains the full loop
                testPath.add(parentHierarchy.childElement)
                printPath(testPath, "if testPath return")
                loopPath.addAll(testPath)
                break
            } else {
                List<Element> newTestPath = new LinkedList<Element>(testPath)
                newTestPath.add(parentHierarchy.childElement)
                printPath(newTestPath, "before else call")
                List<Element> descendantLoop = hasLoopInReachableDescendant(newTestPath)
                if (BooleanUtils.isFalse(descendantLoop.isEmpty())) {
                    printPath(descendantLoop, "descendantLoop")
                    loopPath.addAll(descendantLoop)
                    break
                }
            }
        }
        return loopPath
    }


    static void printPath(List<Element> path, String msg = "") {
        println("${path*.label.join(',')}  ${msg}")
    }
}

class NewElementLabelMatchesExistingElementLabelException extends Exception {
    Element matchingElement
}

class ModifyElementAndHierarchyLoopInPathException extends Exception {
    List<Element> pathWithLoop
}
