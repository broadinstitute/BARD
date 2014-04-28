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

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Build([Element, ElementHierarchy])
@TestFor(BuildElementPathsService)
@TestMixin(DomainClassUnitTestMixin)
class BuildElementPathsServiceSpec extends Specification {

    BuildElementPathsService service

    def setup() {
        service = new BuildElementPathsService()
    }

    void "buildElement with 0 parents"() {
        setup:
        Element element = Element.build()

        when:
        Set<ElementAndFullPath> elementAndFullPathSet = service.build(element)

        then:
        elementAndFullPathSet.size() == 1
        ElementAndFullPath elementAndFullPath = elementAndFullPathSet.iterator().next()
        elementAndFullPath.element == element
        elementAndFullPath.path.size() == 0
    }

    void "constructor"() {
        when:
        BuildElementPathsService buildElementPathsService = new BuildElementPathsService()

        then:
        assert buildElementPathsService.relationshipType != null
    }

    public static ElementHierarchy buildElementHierarchy(Element parent, Element child, String relationshipType) {
        ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parent, childElement: child,
                relationshipType: relationshipType, dateCreated: new Date())
        assert elementHierarchy.save()

        parent.parentHierarchies.add(elementHierarchy)
        child.childHierarchies.add(elementHierarchy)

        return elementHierarchy
    }

    void "build simplest"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)

        ElementHierarchy eh1 = buildElementHierarchy(eh0.childElement, Element.build(), service.relationshipType)

        ElementHierarchy eh2 = buildElementHierarchy(eh1.childElement, Element.build(), service.relationshipType)
        assert eh2.childElement
        assert eh2.relationshipType
        assert ElementHierarchy.findAll().size() == 3

        when:
        Set<ElementAndFullPath> result = service.build(eh2.childElement)

        then:
        result.size() == 1
        ElementAndFullPath elementAndFullPath = result.iterator().next()
        elementAndFullPath.element == eh2.childElement
        elementAndFullPath.path.size() == 3
        elementAndFullPath.path.get(0) == eh0
        elementAndFullPath.path.get(1) == eh1
        elementAndFullPath.path.get(2) == eh2
    }

    void "build forked in middle two ends"() {
        setup:
        ElementHierarchy eh0a = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        ElementHierarchy eh0b = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)

        ElementHierarchy eh1a = buildElementHierarchy(eh0a.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh1b = buildElementHierarchy(eh0b.childElement, Element.build(), service.relationshipType)

        Element intersection = Element.build()
        ElementHierarchy eh2a = buildElementHierarchy(eh1a.childElement, intersection, service.relationshipType)
        ElementHierarchy eh2b = buildElementHierarchy(eh1b.childElement, intersection, service.relationshipType)

        ElementHierarchy eh3 = buildElementHierarchy(intersection, Element.build(), service.relationshipType)
        ElementHierarchy eh4 = buildElementHierarchy(eh3.childElement, Element.build(), service.relationshipType)

        List<ElementHierarchy> pathA = [eh0a, eh1a, eh2a, eh3, eh4] as List<ElementHierarchy>
        List<ElementHierarchy> pathB = [eh0b, eh1b, eh2b, eh3, eh4] as List<ElementHierarchy>

        when:
        Set<ElementAndFullPath> result = service.build(eh4.childElement)

        then:
        result.size() == 2
        List<ElementAndFullPath> list = new ArrayList<ElementAndFullPath>(result)
        assert list.get(0).path.get(0) == eh0a || list.get(0).path.get(0) == eh0b
        assert list.get(1).path.get(0) == eh0a || list.get(1).path.get(0) == eh0b
        assert list.get(0).path.get(0) != list.get(1).path.get(0)

        ElementAndFullPath a, b
        if (list.get(0).path.get(0) == eh0a) {
            a = list.get(0)
            b = list.get(1)
        } else {
            a = list.get(1)
            b = list.get(0)
        }

        assert a.element == eh4.childElement
        assert b.element == eh4.childElement

        assert a.path.size() == pathA.size()
        assert b.path.size() == pathB.size()

        for (int i = 0; i < pathA.size(); i++) {
            assert a.path.get(i) == pathA.get(i)
            assert b.path.get(i) == pathB.get(i)
        }
    }


    void "build forked in middle two starts"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)

        ElementHierarchy eh1 = buildElementHierarchy(eh0.childElement, Element.build(), service.relationshipType)

        ElementHierarchy eh2a = buildElementHierarchy(eh1.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh2b = buildElementHierarchy(eh1.childElement, Element.build(), service.relationshipType)

        ElementHierarchy eh3a = buildElementHierarchy(eh2a.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh3b = buildElementHierarchy(eh2b.childElement, Element.build(), service.relationshipType)

        List<ElementHierarchy> pathA = [eh0, eh1, eh2a, eh3a] as List<ElementHierarchy>
        List<ElementHierarchy> pathB = [eh0, eh1, eh2b, eh3b] as List<ElementHierarchy>

        when:
        Set<ElementAndFullPath> resultA = service.build(eh3a.childElement)
        Set<ElementAndFullPath> resultB = service.build(eh3b.childElement)

        then:
        resultA.size() == 1
        resultB.size() == 1

        ElementAndFullPath a = resultA.iterator().next()
        ElementAndFullPath b = resultB.iterator().next()

        assert a.element == eh3a.childElement
        assert b.element == eh3b.childElement

        assert a.path.size() == pathA.size()
        assert b.path.size() == pathB.size()

        for (int i = 0; i < pathA.size(); i++) {
            assert a.path.get(i) == pathA.get(i)
            assert b.path.get(i) == pathB.get(i)
        }
    }


    void "build single path"() {
        given:
        final Element parentElement = Element.build(label: parentLabel)
        final Element childElement = Element.build(label: childLabel)
        final Element leafElement = Element.build(label: leafLabel)
        ElementHierarchy eh0a = buildElementHierarchy(parentElement, childElement, service.relationshipType)
        buildElementHierarchy(eh0a.childElement, leafElement, service.relationshipType)
        when:
        String path = service.buildSinglePath(leafElement)
        then:
        assert expectedPath == path
        where:

        description           | parentLabel | childLabel | leafLabel | expectedPath
        "Has 'BARD' as root"  | "BARD"      | "child"    | "leaf"    | "/child/leaf/"
        "Has 'BARD2' as root" | "BARD2"     | "child"    | "leaf"    | ""


    }

    void "build all"() {
        setup:
        ElementHierarchy eh0a = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        ElementHierarchy eh1a = buildElementHierarchy(eh0a.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh2a = buildElementHierarchy(eh1a.childElement, Element.build(), service.relationshipType)
        List<ElementHierarchy> pathA = [eh0a, eh1a, eh2a] as List<ElementHierarchy>

        ElementHierarchy eh0b = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        ElementHierarchy eh1b = buildElementHierarchy(eh0b.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh2b = buildElementHierarchy(eh1b.childElement, Element.build(), service.relationshipType)
        List<ElementHierarchy> pathB = [eh0b, eh1b, eh2b] as List<ElementHierarchy>

        when:
        Set<ElementAndFullPath> all = service.buildAll()

        then:
        all.size() == 8

        ElementAndFullPath a = null
        ElementAndFullPath b = null
        for (ElementAndFullPath elementAndFullPath : all) {
            if (elementAndFullPath.path.size() > 0) {
                if (elementAndFullPath.path.last() == eh2a) {
                    a = elementAndFullPath
                } else if (elementAndFullPath.path.last() == eh2b) {
                    b = elementAndFullPath
                }
            }
        }
        assert a
        assert b

        assert a.element == eh2a.childElement
        assert b.element == eh2b.childElement

        assert a.path.size() == pathA.size()
        assert b.path.size() == pathB.size()

        for (int i = 0; i < pathA.size(); i++) {
            assert a.path.get(i) == pathA.get(i)
            assert b.path.get(i) == pathB.get(i)
        }
    }


    void "create list sorted by String"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        //set the label of the root element of this path to a string that will be sorted into the first position
        eh0.parentElement.label = "aaaa"
        ElementHierarchy eh1 = buildElementHierarchy(eh0.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh2 = buildElementHierarchy(eh1.childElement, Element.build(), service.relationshipType)

        ElementHierarchy eh3 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        //set the label of the root element of this path to a string that will be sorted into the second position
        eh3.parentElement.label = "bbbb"
        ElementHierarchy eh4 = buildElementHierarchy(eh3.childElement, Element.build(), service.relationshipType)

        Set<ElementAndFullPath> allSet = service.buildAll()

        when:
        ElementAndFullPathListAndMaxPathLength result = service.createListSortedByString(allSet)
        List<ElementAndFullPath> sortedList = result.elementAndFullPathList


        then:
        sortedList.size() == 7

        sortedList.get(0).path.size() == 0
        sortedList.get(0).element == eh0.parentElement

        sortedList.get(1).path.size() == 1
        sortedList.get(1).path.get(0) == eh0
        sortedList.get(1).element == eh0.childElement

        sortedList.get(2).path.size() == 2
        sortedList.get(2).path.get(0) == eh0
        sortedList.get(2).path.get(1) == eh1
        sortedList.get(2).element == eh1.childElement

        sortedList.get(3).path.size() == 3
        sortedList.get(3).path.get(0) == eh0
        sortedList.get(3).path.get(1) == eh1
        sortedList.get(3).path.get(2) == eh2
        sortedList.get(3).element == eh2.childElement
        result.maxPathLength == sortedList.get(3).toString().length()

        sortedList.get(4).path.size() == 0
        sortedList.get(4).element == eh3.parentElement

        sortedList.get(5).path.size() == 1
        sortedList.get(5).path.get(0) == eh3
        sortedList.get(5).element == eh3.childElement

        sortedList.get(6).path.size() == 2
        sortedList.get(6).path.get(0) == eh3
        sortedList.get(6).path.get(1) == eh4
        sortedList.get(6).element == eh4.childElement

        println(sortedList)
    }

    void "test loop detection - exception thrown"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        ElementHierarchy eh1 = buildElementHierarchy(eh0.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh2 = buildElementHierarchy(eh1.childElement, eh0.parentElement, service.relationshipType)
        assert eh0.parentElement == eh2.childElement

        ElementAndFullPath elementAndFullPath = new ElementAndFullPath(eh2.childElement)
        elementAndFullPath.path.add(eh2)


        when:
        service.recursiveBuild(elementAndFullPath)


        then:
        thrown(BuildElementPathsServiceLoopInPathException)
    }

    void "test loop detection - contents of exception"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        ElementHierarchy eh1 = buildElementHierarchy(eh0.childElement, Element.build(), service.relationshipType)
        ElementHierarchy eh2 = buildElementHierarchy(eh1.childElement, eh0.parentElement, service.relationshipType)
        assert eh0.parentElement == eh2.childElement

        ElementAndFullPath elementAndFullPath = new ElementAndFullPath(eh2.childElement)
        elementAndFullPath.path.add(eh2)


        when:
        BuildElementPathsServiceLoopInPathException exception = null
        try {
            service.recursiveBuild(elementAndFullPath)
        } catch (BuildElementPathsServiceLoopInPathException e) {
            exception = e
        }

        then:
        exception != null
        exception.elementAndFullPath.path.size() == 2
        exception.elementAndFullPath.path.get(0) == eh1
        exception.elementAndFullPath.path.get(1) == eh2
        exception.nextTopElementHierarchy == eh0
    }

    void "test does not contained retired element"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        eh0.childElement.elementStatus = ElementStatus.Retired

        when:
        Set<ElementAndFullPath> result = service.buildAll()

        then:
        result != null
        result.size() == 1
        result.iterator().next().element.equals(eh0.parentElement)
    }

    void "test contains retired element()"() {
        setup:
        ElementHierarchy eh0 = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)
        eh0.childElement.elementStatus = ElementStatus.Retired
        BuildElementPathsService retiredService = new BuildElementPathsService("subClassOf", "/", true)

        when:
        Set<ElementAndFullPath> result = retiredService.buildAll()
        assert result != null
        assert result.size() == 2

        ElementAndFullPath parentPath = null
        ElementAndFullPath childPath = null
        for (ElementAndFullPath eafp : result) {
            if (eafp.element.equals(eh0.parentElement)) {
                parentPath = eafp
            } else if (eafp.element.equals(eh0.childElement)) {
                childPath = eafp
            }
        }


        then:
        parentPath != null
        parentPath.path.size() == 0
        childPath != null
        childPath.path.size() == 1
    }




    void "test removeInvalidDictionaryPaths"() {
        Element dict = Element.build(id: BuildElementPathsService.bardDictionaryElementId);
        dict.label = "Dictionaries"
        dict.save()

        ElementHierarchy eh0 = buildElementHierarchy(dict, Element.build(label: "example dictionary"), service.relationshipType)

        ElementHierarchy dictEntryEh =
            buildElementHierarchy(eh0.childElement, Element.build(label: "example dictionary element"), service.relationshipType)

        ElementHierarchy coreEh = buildElementHierarchy(Element.build(label: "core"), eh0.childElement, service.relationshipType)

        Set<ElementAndFullPath> eafpSet = new HashSet<ElementAndFullPath>()

        ElementAndFullPath keeper = new ElementAndFullPath(dictEntryEh.childElement)
        keeper.path.add(eh0)
        keeper.path.add(dictEntryEh)
        println(keeper)
        eafpSet.add(keeper)

        ElementAndFullPath toBeRemoved = new ElementAndFullPath(dictEntryEh.childElement)
        toBeRemoved.path.add(coreEh)
        toBeRemoved.path.add(dictEntryEh)
        println(toBeRemoved)
        eafpSet.add(toBeRemoved)

        when:
        service.removeUnwantedDictionaryPaths(eafpSet)

        then:
        assert eafpSet.size() == 1
        eafpSet.iterator().next().equals(keeper)
    }

    void "test buildAll with excluded dictionary paths"() {
        setup:
        Element dict = Element.build(id: BuildElementPathsService.bardDictionaryElementId)
        dict.label = "Dictionaries"
        dict.save()

        ElementHierarchy eh0 = buildElementHierarchy(dict, Element.build(label: "example dictionary"), service.relationshipType)

        ElementHierarchy dictEntryEh =
            buildElementHierarchy(eh0.childElement, Element.build(label: "example dictionary element"), service.relationshipType)

        buildElementHierarchy(Element.build(label: "core"), eh0.childElement, service.relationshipType)

        when:
        Set<ElementAndFullPath> result = service.buildAll()

        then:
        int dictEntryCount = 0
        for (ElementAndFullPath eafp : result) {
            if (eafp.element.equals(dictEntryEh.childElement)) {
                dictEntryCount++
            }

            println(eafp)
        }
        assert 1 == dictEntryCount

    }

    void "test isDictionaryChild empty path / root element"() {
        setup:
        ElementAndFullPath eafp = new ElementAndFullPath(Element.build())

        Set<ElementAndFullPath> pathSet = new HashSet<ElementAndFullPath>()
        pathSet.add(eafp)

        when:
        boolean result = BuildElementPathsService.isDictionaryChild(pathSet)

        then:
        assert ! result
    }

    void "test isDictionaryChild true"() {
        setup:
        Set<ElementAndFullPath> pathWith2Set = new HashSet<ElementAndFullPath>()
        Set<ElementAndFullPath> pathWith1Set = new HashSet<ElementAndFullPath>()

        Element dict = Element.build(id: BuildElementPathsService.bardDictionaryElementId)

        ElementHierarchy eh = buildElementHierarchy(dict, Element.build(), service.relationshipType)

        ElementAndFullPath eafp = new ElementAndFullPath(eh.childElement)
        eafp.path.add(eh)
        pathWith2Set.add(eafp)
        pathWith1Set.add(eafp)

        eh = buildElementHierarchy(Element.build(), eh.childElement, service.relationshipType)
        eafp = new ElementAndFullPath(eh.childElement)
        eafp.path.add(eh)
        pathWith2Set.add(eafp)

        when:
        boolean resultWith2 = BuildElementPathsService.isDictionaryChild(pathWith2Set)
        boolean resultWith1 = BuildElementPathsService.isDictionaryChild(pathWith1Set)

        then:
        assert resultWith2
        assert resultWith1
    }

    void "test isDictionaryChild false"() {
        setup:
        Set<ElementAndFullPath> pathWith2Set = new HashSet<ElementAndFullPath>()
        Set<ElementAndFullPath> pathWith1Set = new HashSet<ElementAndFullPath>()

        ElementHierarchy eh = buildElementHierarchy(Element.build(), Element.build(), service.relationshipType)

        ElementAndFullPath eafp = new ElementAndFullPath(eh.childElement)
        eafp.path.add(eh)
        pathWith2Set.add(eafp)
        pathWith1Set.add(eafp)

        eh = buildElementHierarchy(Element.build(), eh.childElement, service.relationshipType)
        eafp = new ElementAndFullPath(eh.childElement)
        eafp.path.add(eh)
        pathWith2Set.add(eafp)

        when:
        boolean resultWith2 = BuildElementPathsService.isDictionaryChild(pathWith2Set)
        boolean resultWith1 = BuildElementPathsService.isDictionaryChild(pathWith1Set)

        then:
        assert ! resultWith2
        assert ! resultWith1
    }

    void "test removeRetiredElementsAndAnyPathsTheyAreIn retired element"() {
        setup:
        Set<ElementAndFullPath> pathSet = new HashSet<ElementAndFullPath>()

        Element retired = Element.build()
        retired.elementStatus = ElementStatus.Retired

        //this should be removed because the element itself is retired
        pathSet.add(new ElementAndFullPath(retired))

        //these should be kept
        Set<ElementAndFullPath> keepSet = new HashSet<ElementAndFullPath>()
        ElementAndFullPath eafp = new ElementAndFullPath(Element.build())
        pathSet.add(eafp)
        keepSet.add(eafp)
        eafp = new ElementAndFullPath(Element.build())
        pathSet.add(eafp)
        keepSet.add(eafp)

        when:
        BuildElementPathsService.removeRetiredElementsAndAnyPathsTheyAreIn(pathSet)

        then:
        assert pathSet.size() == keepSet.size()
        assert pathSet.containsAll(keepSet)
    }

    void "test removeRetiredElementsAndAnyPathsTheyAreIn retired element in path"() {
        setup:
        Set<ElementAndFullPath> pathSet = new HashSet<ElementAndFullPath>()

        Element retired = Element.build()
        retired.elementStatus = ElementStatus.Retired

        //this one should be removed because there is a retired element in the path (at the root)
        ElementAndFullPath eafp = new ElementAndFullPath(Element.build())
        ElementHierarchy retiredParentHierarchy = buildElementHierarchy(retired, eafp.element, service.relationshipType)
        eafp.path.add(retiredParentHierarchy)
        pathSet.add(eafp)

        //this one should also be removed because there is a retired element in the path (middle)
        eafp = new ElementAndFullPath(retiredParentHierarchy.childElement)
        eafp.path.add(buildElementHierarchy(Element.build(), retired, service.relationshipType))
        eafp.path.add(retiredParentHierarchy)
        pathSet.add(eafp)

        //these should be kept
        Set<ElementAndFullPath> keepSet = new HashSet<ElementAndFullPath>()
        eafp = new ElementAndFullPath(Element.build())
        pathSet.add(eafp)
        keepSet.add(eafp)
        eafp = new ElementAndFullPath(Element.build())
        pathSet.add(eafp)
        keepSet.add(eafp)

        when:
        BuildElementPathsService.removeRetiredElementsAndAnyPathsTheyAreIn(pathSet)

        then:
        assert pathSet.size() == keepSet.size()
        assert pathSet.containsAll(keepSet)
    }
}
