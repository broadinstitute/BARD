package bard.db.dictionary

import bard.db.dictionary.ElementStatus as ES
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.Session
import spock.lang.IgnoreRest
import spock.lang.Unroll

import static bard.db.dictionary.ElementStatus.*

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/28/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class OntologyDataAccessServiceIntegrationSpec extends IntegrationSpec {

    BardDescriptor grandParent
    BardDescriptor parent
    BardDictionaryDescriptor dictionaryParent

    OntologyDataAccessService ontologyDataAccessService

    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        grandParent = BardDescriptor.build(fullPath: "grandParent", leaf: false, label: 'grandParent')
        String parentFullPath = "${grandParent.fullPath}> parent"
        parent = BardDescriptor.build(fullPath: parentFullPath, leaf: false, parent: grandParent, label: 'parent')
        dictionaryParent = BardDictionaryDescriptor.build()
        parent.save(flush: true)
    }

    void "test getValueDescriptors with direct Children desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<Element> results = ontologyDataAccessService.getElementsForValues(parent.element.id, searchTerm)

        then:

        expectedLabels == results*.label

        where:
        desc                                           | expectedLabels                    | searchTerm | childProps
        "0 children"                                   | []                                | 'child'    | []
        "0 children due to search term no match"       | []                                | 'foo'      | [[elementStatus: Published, label: 'child1']]
        "0 children due to null search term"           | []                                | null       | [[elementStatus: Published, label: 'child1']]
        "0 children due to retired"                    | []                                | 'child'    | [[elementStatus: Retired, label: 'child1']]
        "1 child with status Published"                | ['child1']                        | 'child'    | [[elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                  | ['child1']                        | 'child'    | [[elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"               | ['child1']                        | 'child'    | [[elementStatus: ES.Deprecated, label: 'child1']]
        "1 child ensuring searchTerm trimmed"          | ['child1']                        | ' child'   | [[elementStatus: Published, label: 'child1']]
        "1 child with case insensitive contains match" | ['child1']                        | 'CHILD'    | [[elementStatus: Published, label: 'child1']]
        "1 child of 2 due to Retire"                   | ['child2']                        | 'child'    | [[elementStatus: Retired, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "1 child of 2 due to search term"              | ['child2']                        | 'child2'   | [[elementStatus: Published, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | [[elementStatus: Published, label: 'c_child'], [elementStatus: Published, label: 'B_child'], [elementStatus: Published, label: 'a_child']]
    }

    void "test getValueDescriptors with grandChildren desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getElementsForValues(grandParent.element.id, searchTerm)

        then:

        expectedLabels == results*.label

        where:
        desc                                           | expectedLabels                    | searchTerm | childProps
        "0 children"                                   | []                                | 'child'    | []
        "0 children due to search term no match"       | []                                | 'foo'      | [[elementStatus: Published, label: 'child1']]
        "0 children due to null search term"           | []                                | null       | [[elementStatus: Published, label: 'child1']]
        "0 children due to retired"                    | []                                | 'child'    | [[elementStatus: Retired, label: 'child1']]
        "1 child with status Published"                | ['child1']                        | 'child'    | [[elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                  | ['child1']                        | 'child'    | [[elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"               | ['child1']                        | 'child'    | [[elementStatus: ES.Deprecated, label: 'child1']]
        "1 child ensuring searchTerm trimmed"          | ['child1']                        | ' child'   | [[elementStatus: Published, label: 'child1']]
        "1 child with case insensitive contains match" | ['child1']                        | 'CHILD'    | [[elementStatus: Published, label: 'child1']]
        "1 child of 2 due to Retire"                   | ['child2']                        | 'child'    | [[elementStatus: Retired, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "1 child of 2 due to search term"              | ['child2']                        | 'child2'   | [[elementStatus: Published, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | [[elementStatus: Published, label: 'c_child'], [elementStatus: Published, label: 'B_child'], [elementStatus: Published, label: 'a_child']]
    }

    void "test getAttributeDescriptors desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        List<BardDescriptor> bardDescriptors = createDescendants(parent, childProps)

        for (BardDescriptor bardDescriptor : bardDescriptors) {
            if (bardDescriptor.label in dictionaryEntryLabels) {
                BardDictionaryDescriptor.build(element: bardDescriptor.element, parent: dictionaryParent)
            }
        }
        BardDescriptor.withSession {Session session -> session.flush()}

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getElementsForAttributes(searchTerm)


        then:
        expectedLabels == results*.label

        where:
        desc                                           | expectedLabels                    | searchTerm | dictionaryEntryLabels | childProps
        "0 children"                                   | []                                | 'child'    | []                    | []
        "0 children due to search term no match"       | []                                | 'notFound' | []                    | [[elementStatus: Published, label: 'child1']]
        "0 children due to null search term"           | []                                | null       | []                    | [[elementStatus: Published, label: 'child1']]
        "0 children due to retired"                    | []                                | 'child'    | []                    | [[elementStatus: Retired, label: 'child1']]
        "1 child with status Published"                | ['child1']                        | 'child'    | []                    | [[elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                  | ['child1']                        | 'child'    | []                    | [[elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"               | ['child1']                        | 'child'    | []                    | [[elementStatus: ES.Deprecated, label: 'child1']]
        "1 child ensuring searchTerm trimmed"          | ['child1']                        | ' child'   | []                    | [[elementStatus: Published, label: 'child1']]
        "1 child with case insensitive contains match" | ['child1']                        | 'HILD'     | []                    | [[elementStatus: Published, label: 'child1']]
        "2 children"                                   | ['child1', 'child2']              | 'child'    | []                    | [[elementStatus: Published, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "1 child of 2 due to Retire"                   | ['child2']                        | 'child'    | []                    | [[elementStatus: Retired, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "1 child of 2 due to search term"              | ['child2']                        | 'child2'   | []                    | [[elementStatus: Published, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "1 child of 2 due to dictionary entry"         | ['child2']                        | 'child'    | ['child1']            | [[elementStatus: Published, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | []                    | [[elementStatus: Published, label: 'c_child'], [elementStatus: Published, label: 'B_child'], [elementStatus: Published, label: 'a_child']]
        "2 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | []                    | [[elementStatus: Published, label: 'c_child'], [elementStatus: Published, label: 'B_child'], [elementStatus: Published, label: 'a_child']]
    }
	
	void "test getAllUnits"(){
		
	}

    private List<BardDescriptor> createDescendants(BardDescriptor directParent, List listOfMaps) {
        List<BardDescriptor> bardDescriptors = []
        for (Map map in listOfMaps) {
            BardDescriptor bd = BardDescriptor.buildWithoutSave(parent: directParent)
            bd.properties = map
            bd.fullPath = "${directParent.fullPath}> ${bd.label}"
            // setting up element with same properties as BardDescriptor
            bd.element.properties = map
//            println(bd.dump())
            bd.element.save()
            bd.save(flush: true)
            bardDescriptors.add(bd)
        }
        bardDescriptors
    }
}