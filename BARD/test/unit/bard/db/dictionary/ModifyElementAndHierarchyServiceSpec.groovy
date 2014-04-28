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

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ModifyElementAndHierarchyService)
@Build([Element, ElementHierarchy])
class ModifyElementAndHierarchyServiceSpec extends Specification {
    private static final String relationshipType = "subClassOf"

    ModifyElementAndHierarchyService service

    def setup() {
        service = new ModifyElementAndHierarchyService(relationshipType: relationshipType)

        service.springSecurityService = Mock(SpringSecurityService)
        service.springSecurityService.getPrincipal() >> ['username':'dlahr']
    }

    void "test renameElement same name"() {
        setup:
        Element element = Element.build()

        when:
        service.renameElement(element, element.label)

        then:
        thrown(NewElementLabelMatchesExistingElementLabelException)
    }

    void "test renameElement"() {
        setup:
        Element element = Element.build()
        String newLabel = "a new name for this element - dlahr"

        when:
        service.renameElement(element, newLabel)

        then:
        element.label == newLabel
    }

    void "test updateHierarchyIfNeeded both empty"() {
        setup:
        Element element = Element.build()
        List<Element> elementList = new LinkedList<Element>()
        List<ElementHierarchy> elementHierarchyList = new LinkedList<ElementHierarchy>()
        NewElementAndPath newElementAndPath = new NewElementAndPath(element: element, newPathElementList: elementList,
                elementHierarchyList: elementHierarchyList)

        when:
        service.updateHierarchyIfNeeded(newElementAndPath)
        List<Element> allElementList = Element.findAll()

        then:
        ElementHierarchy.findAll().size() == 0
        allElementList.size() == 1
        allElementList.get(0) == element
    }

    void "test updateHierarchyIfNeeded both not empty"() {
        setup:
        Element element = Element.build()

        ElementHierarchy eh = ElementHierarchy.build()
        eh.childElement = element

        element.childHierarchies.add(eh)

        Element originalParent = eh.parentElement

        List<ElementHierarchy> previousPath = [eh]
        List<Element> newPath = [Element.build()]
        assert previousPath.get(0).parentElement != newPath.get(0)

        NewElementAndPath newElementAndPath = new NewElementAndPath(element: element, elementHierarchyList: previousPath,
                newPathElementList: newPath)


        when:
        service.updateHierarchyIfNeeded(newElementAndPath)
        List<ElementHierarchy> allElementHierarchy = ElementHierarchy.findAll()
        Set<Element> allElementSet = new HashSet<Element>(Element.findAll())


        then:
        allElementHierarchy.size() == 1
        ElementHierarchy elementHierarchy = allElementHierarchy.get(0)

        elementHierarchy.childElement == element
        elementHierarchy.parentElement == newPath.get(0)

        allElementSet.size() == 3
        allElementSet.contains(element)
        allElementSet.contains(newPath.get(0))
        allElementSet.contains(originalParent)
    }

    void "test updateHierarchyIfNeeded new path empty"() {
        setup:
        Element element = Element.build()

        ElementHierarchy eh = ElementHierarchy.build()
        eh.childElement = element
        element.childHierarchies.add(eh)
        assert Element.findAll().size() == 2

        Element originalParent = eh.parentElement

        List<ElementHierarchy> previousPath = [eh]
        List<Element> newPath = new LinkedList<Element>()

        NewElementAndPath newElementAndPath = new NewElementAndPath(element: element, elementHierarchyList: previousPath,
                newPathElementList: newPath)


        when:
        service.updateHierarchyIfNeeded(newElementAndPath)
        List<ElementHierarchy> allElementHierarchy = ElementHierarchy.findAll()
        Set<Element> allElementSet = new HashSet<Element>(Element.findAll())


        then:
        allElementHierarchy.size() == 0

        originalParent.parentHierarchies.size() == 0

        element.childHierarchies.size() == 0

        allElementSet.size() == 2
        allElementSet.contains(element)
        allElementSet.contains(originalParent)
    }

    void "test update hierarchy old path empty"() {
        setup:
        Element element = Element.build()
        List<ElementHierarchy> previousPath = new LinkedList<ElementHierarchy>()
        List<Element> newPath = [Element.build()]
        NewElementAndPath newElementAndPath = new NewElementAndPath(element: element, elementHierarchyList: previousPath,
                newPathElementList: newPath)


        when:
        service.updateHierarchyIfNeeded(newElementAndPath)
        List<ElementHierarchy> allElementHierarchy = ElementHierarchy.findAll()
        Set<Element> allElementSet = new HashSet<Element>(Element.findAll())


        then:
        allElementHierarchy.size() == 1
        ElementHierarchy elementHierarchy = allElementHierarchy.get(0)
        elementHierarchy.childElement == element
        elementHierarchy.parentElement == newPath.get(0)

        allElementSet.size() == 2
        allElementSet.contains(element)
        allElementSet.contains(newPath.get(0))
    }

    void "test exception thrown when loop in path input both not empty"() {
        setup:
        //will be leaf and root
        Element element = Element.build()

        //establish original root and leaf
        ElementHierarchy eh = BuildElementPathsServiceSpec.buildElementHierarchy(Element.build(), element,
                "relationShapes - fake")

        List<ElementHierarchy> previousPath = [eh]

        //establish new root (which is leaf)
        List<Element> newPath = [element]

        NewElementAndPath newElementAndPath = new NewElementAndPath(element: element, elementHierarchyList: previousPath,
                newPathElementList: newPath)

        ModifyElementAndHierarchyLoopInPathException exception = null

        when:
        try {
            service.updateHierarchyIfNeeded(newElementAndPath)
        } catch (ModifyElementAndHierarchyLoopInPathException e) {
            exception = e
        }

        then:
        exception != null
        exception.pathWithLoop.size() == 2
        exception.pathWithLoop.get(0) == element
        exception.pathWithLoop.get(1) == element
    }

    void "test exception thrown when loop in path loop is because lower level leaf matches new parent"() {
        setup:
        final String relationship = "relationShapes - fake"
        //will be leaf and root
        Element element = Element.build()

        //establish original root
        ElementHierarchy eh0 = BuildElementPathsServiceSpec.buildElementHierarchy(Element.build(), Element.build(),
                relationship)
        //establish leaf
        ElementHierarchy eh1 = BuildElementPathsServiceSpec.buildElementHierarchy(eh0.childElement, element,
                relationship)

        List<ElementHierarchy> previousPath = [eh0]

        //establish new root (which is leaf)
        List<Element> newPath = [element]

        NewElementAndPath newElementAndPath = new NewElementAndPath(element: eh0.childElement, elementHierarchyList: previousPath,
                newPathElementList: newPath)

        ModifyElementAndHierarchyLoopInPathException exception = null

        when:
        try {
            service.updateHierarchyIfNeeded(newElementAndPath)
        } catch (ModifyElementAndHierarchyLoopInPathException e) {
            exception = e
        }

        then:
        exception != null
        exception.pathWithLoop.size() == 3
        exception.pathWithLoop.get(0) == element
        exception.pathWithLoop.get(1) == eh0.childElement
        exception.pathWithLoop.get(2) == element
    }
}
