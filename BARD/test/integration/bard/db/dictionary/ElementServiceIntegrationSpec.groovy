package bard.db.dictionary

import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/28/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ElementServiceIntegrationSpec extends IntegrationSpec {

    ElementService elementService

    @Before
    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
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
        Long parentElementId = parentElement.id
        String newElementLabel="My New Label"
        String description= "Description"
        String abbreviation="MNL"
        String synonyms="Abc,efg"
        String comments="Adding for testing"
        Long unitId = null
        TermCommand termCommand =
            new TermCommand(parentId: parentElementId,label: newElementLabel,description: description,
                    abbreviation:abbreviation,synonyms: synonyms,comments: comments,unitId: unitId)
        when:
        Element element = elementService.addNewTerm(termCommand)

        then:
        assert element
        assert newElementLabel == element.label
        assert description == element.description
        assert abbreviation == element.abbreviation
        assert synonyms == element.synonyms
        assert comments == element.comments
        assert element.childHierarchies
    }


}