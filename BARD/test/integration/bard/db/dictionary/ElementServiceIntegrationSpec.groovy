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

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/28/13
 * Time: 3:05 PM
 */
@Unroll
class ElementServiceIntegrationSpec extends IntegrationSpec {

    ElementService elementService

    @Before
    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    }


    void "test List getChildNodes #description"() {
        given:

        final Element parentElement = Element.build(label: parentLabel, description: description)
        final Element childElement = Element.build(label: childLabel, description: description)
        final Element leafElement = Element.build(label: leafLabel, description: description, expectedValueType: ExpectedValueType.ELEMENT)
        ElementHierarchy eh0a = buildElementHierarchy(parentElement, childElement, "subClassOf")
        boolean doNotShowRetired = false
        buildElementHierarchy(eh0a.childElement, leafElement, "subClassOf")
        when:
        List hierarchies = elementService.getChildNodes(childElement.id, doNotShowRetired, expectedValueType)
        then:
        assert hierarchiesSize == hierarchies.size()
        if (hierarchies) {
            final Map map = (Map) hierarchies.get(0)
            assert leafLabel == map.title
            assert description == map.description
            assert false == map.isFolder
            assert false == map.isLazy
            assert AddChildMethod.NO.label == map.addClass
            assert AddChildMethod.NO.description == map.childMethodDescription
            assert AddChildMethod.NO.toString() == map.childMethod
        }

        where:
        description                       | parentLabel | childLabel | leafLabel | expectedValueType                | hierarchiesSize
        "Has 'BARD' as root"              | "BARD1"     | "child1"   | "leaf1"   | null                             | 1
        "with expectedValueType=ELEMENT"  | "BARD2"     | "child2"   | "leaf2"   | ExpectedValueType.ELEMENT.name() | 1
        "with expectedValueType!=ELEMENT" | "BARD3"     | "child3"   | "leaf3"   | ExpectedValueType.NONE.name()    | 0

    }

    void "test createElementHierarchyTree"() {
        given:

        final Element parentElement = Element.build(label: parentLabel, description: description)
        final Element childElement = Element.build(label: childLabel, description: description)
        final Element leafElement = Element.build(label: leafLabel, description: description)
        ElementHierarchy eh0a = buildElementHierarchy(parentElement, childElement, "subClassOf")
        boolean doNotShowRetired = false
        buildElementHierarchy(eh0a.childElement, leafElement, "subClassOf")
        when:
        List hierarchies = elementService.createElementHierarchyTree(doNotShowRetired, "BARD", null)
        then:
        assert hierarchies
        assert 1 == hierarchies.size()
        final Map map = (Map) hierarchies.get(0)
        assert childLabel == map.title
        assert description == map.description
        assert true == map.isFolder
        assert true == map.isLazy

        where:
        description          | parentLabel | childLabel | leafLabel
        "Has 'BARD' as root" | "BARD"      | "child"    | "leaf"
    }

    public static ElementHierarchy buildElementHierarchy(Element parent, Element child, String relationshipType) {
        ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parent, childElement: child,
                relationshipType: relationshipType, dateCreated: new Date())
        assert elementHierarchy.save()

        parent.parentHierarchies.add(elementHierarchy)
        child.childHierarchies.add(elementHierarchy)

        return elementHierarchy
    }

    void "test add Element Hierarchy"() {

        given:
        Element parentElement = Element.build(label: "parentLabel")
        Element childElement = Element.build(label: "childElement")

        when:
        ElementHierarchy elementHierarchy = elementService.addElementHierarchy(parentElement, childElement)

        then:
        assert elementHierarchy
        assert "subClassOf" == elementHierarchy.relationshipType
        assert elementHierarchy.childElement.label == childElement.label
        assert elementHierarchy.parentElement.label == parentElement.label
        assert childElement.childHierarchies
        assert parentElement.parentHierarchies
    }

    void "test add new Term"() {

        given:
        Element parentElement = Element.build(label: "parentLabel")
        String newElementLabel = "My New Label"
        String description = "Description"
        String abbreviation = "MNL"
        String synonyms = "Abc,efg"
        String curationNotes = "Adding for testing"
        TermCommand termCommand =
            new TermCommand(parentLabel: parentElement.label, label: newElementLabel, description: description,
                    abbreviation: abbreviation, synonyms: synonyms, curationNotes: curationNotes)
        when:
        Element element = elementService.addNewTerm(termCommand)

        then:
        assert element
        assert newElementLabel == element.label
        assert description == element.description
        assert abbreviation == element.abbreviation
        assert synonyms == element.synonyms
        assert curationNotes == element.curationNotes
        assert element.childHierarchies
    }


}
