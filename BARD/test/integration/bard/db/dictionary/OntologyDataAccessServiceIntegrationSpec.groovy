package bard.db.dictionary

import bard.db.dictionary.ElementStatus as ES
import grails.plugin.spock.IntegrationSpec
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

    OntologyDataAccessService ontologyDataAccessService

    void setup() {
        grandParent = BardDescriptor.build(fullPath: "grandParent", leaf: false, label: 'grandParent')
        String parentFullPath = "${grandParent.fullPath}> parent"
        parent = BardDescriptor.build(fullPath: parentFullPath, leaf: false, parent: grandParent, label: 'parent')
    }

    void "test getValueDescriptors with direct Children desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getValueDescriptors(parent.element.id, '', searchTerm)

        then:

        expectedLabels == results*.label

        where:
        desc                                           | expectedLabels                    | searchTerm | childProps
        "0 children"                                   | []                                | 'child'    | []
        "0 children due to no leafs"                   | []                                | 'child'    | [[leaf: false, elementStatus: Published, label: 'child1']]
        "0 children due to retired"                    | []                                | 'child'    | [[leaf: true, elementStatus: Retired, label: 'child1']]
        "0 children due to search term no match"       | []                                | 'foo'      | [[leaf: true, elementStatus: Published, label: 'child1']]
        "1 child with status Published"                | ['child1']                        | 'child'    | [[leaf: true, elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                  | ['child1']                        | 'child'    | [[leaf: true, elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"               | ['child1']                        | 'child'    | [[leaf: true, elementStatus: ES.Deprecated, label: 'child1']]
        "1 child with case insensitive contains match" | ['child1']                        | 'HILD'     | [[leaf: true, elementStatus: Published, label: 'child1']]
        "1 child of 2 due to leaf"                     | ['child2']                        | 'child'    | [[leaf: false, elementStatus: Published, label: 'child1'], [leaf: true, elementStatus: Published, label: 'child2']]
        "1 child of 2 due to Retire"                   | ['child2']                        | 'child'    | [[leaf: true, elementStatus: Retired, label: 'child1'], [leaf: true, elementStatus: Published, label: 'child2']]
        "1 child of 2 due to search term"              | ['child2']                        | 'child2'   | [[leaf: true, elementStatus: Published, label: 'child1'], [leaf: true, elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | [[leaf: true, elementStatus: Published, label: 'c_child'], [leaf: true, elementStatus: Published, label: 'B_child'], [leaf: true, elementStatus: Published, label: 'a_child']]

    }

    void "test getValueDescriptors with grandChildren desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getValueDescriptors(grandParent.element.id, '', searchTerm)

        then:

        expectedLabels == results*.label

        where:
        desc                                           | expectedLabels                    | searchTerm | childProps
        "0 children"                                   | []                                | 'child'    | []
        "0 children due to no leafs"                   | []                                | 'child'    | [[leaf: false, elementStatus: Published, label: 'child1']]
        "0 children due to retired"                    | []                                | 'child'    | [[leaf: true, elementStatus: Retired, label: 'child1']]
        "0 children due to search term no match"       | []                                | 'foo'      | [[leaf: true, elementStatus: Published, label: 'child1']]
        "1 child with status Published"                | ['child1']                        | 'child'    | [[leaf: true, elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                  | ['child1']                        | 'child'    | [[leaf: true, elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"               | ['child1']                        | 'child'    | [[leaf: true, elementStatus: ES.Deprecated, label: 'child1']]
        "1 child with case insensitive contains match" | ['child1']                        | 'HILD'     | [[leaf: true, elementStatus: Published, label: 'child1']]
        "1 child of 2 due to leaf"                     | ['child2']                        | 'child'    | [[leaf: false, elementStatus: Published, label: 'child1'], [leaf: true, elementStatus: Published, label: 'child2']]
        "1 child of 2 due to Retire"                   | ['child2']                        | 'child'    | [[leaf: true, elementStatus: Retired, label: 'child1'], [leaf: true, elementStatus: Published, label: 'child2']]
        "1 child of 2 due to search term"              | ['child2']                        | 'child2'   | [[leaf: true, elementStatus: Published, label: 'child1'], [leaf: true, elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | [[leaf: true, elementStatus: Published, label: 'c_child'], [leaf: true, elementStatus: Published, label: 'B_child'], [leaf: true, elementStatus: Published, label: 'a_child']]

    }

    private void createDescendants(BardDescriptor directParent, List listOfMaps) {
        for (Map map in listOfMaps) {
            BardDescriptor bd = BardDescriptor.build(parent: directParent)
            bd.properties = map
            bd.fullPath = "${directParent.fullPath}> ${bd.label}"
        }
    }
}