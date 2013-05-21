package bard.db.dictionary

import bard.db.enums.AddChildMethod
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


    void "test List getChildNodes"() {
        given:

        final Element parentElement = Element.build(label: parentLabel, description: description)
        final Element childElement = Element.build(label: childLabel, description: description)
        final Element leafElement = Element.build(label: leafLabel, description: description)
        ElementHierarchy eh0a = buildElementHierarchy(parentElement, childElement, "subClassOf")
        buildElementHierarchy(eh0a.childElement, leafElement, "subClassOf")
        when:
        List hierarchies = elementService.getChildNodes(childElement.id)
        then:
        assert hierarchies
        assert 1 == hierarchies.size()
        final Map map = (Map) hierarchies.get(0)
        assert leafLabel == map.title
        assert description == map.description
        assert false == map.isFolder
        assert false == map.isLazy
        assert AddChildMethod.NO.label == map.addClass
        assert AddChildMethod.NO.description == map.childMethodDescription
        assert AddChildMethod.NO.toString() == map.childMethod

        where:
        description          | parentLabel | childLabel | leafLabel
        "Has 'BARD' as root" | "BARD"      | "child"    | "leaf"

    }

    void "test createElementHierarchyTree"() {
        given:

        final Element parentElement = Element.build(label: parentLabel, description: description)
        final Element childElement = Element.build(label: childLabel, description: description)
        final Element leafElement = Element.build(label: leafLabel, description: description)
        ElementHierarchy eh0a = buildElementHierarchy(parentElement, childElement, "subClassOf")
        buildElementHierarchy(eh0a.childElement, leafElement, "subClassOf")
        when:
        List hierarchies = elementService.createElementHierarchyTree()
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