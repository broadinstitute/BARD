package bard.db.dictionary

import bard.db.audit.BardContextUtils
import bard.db.dictionary.ElementStatus as ES
import static bard.db.enums.ExpectedValueType.*
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.Session
import org.hibernate.SessionFactory
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
    SessionFactory sessionFactory

    OntologyDataAccessService ontologyDataAccessService

    void setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        grandParent = BardDescriptor.build(fullPath: "BARD> grandParent", leaf: false, label: 'grandParent')
        String parentFullPath = "${grandParent.fullPath}> parent"
        parent = BardDescriptor.build(fullPath: parentFullPath, leaf: false, parent: grandParent, label: 'parent')
        dictionaryParent = BardDictionaryDescriptor.build()
        parent.save(flush: true)
    }

    void "test getElementsForValues with direct Children desc: '#desc' expectedLabels: #expectedLabels"() {

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

    void "test getElementsForValues with grandChildren desc: '#desc' expectedLabels: #expectedLabels"() {

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

    void "test getElementsForValues wildcard and escaping desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<Element> results = ontologyDataAccessService.getElementsForValues(parent.element.id, searchTerm)

        then:

        expectedLabels == results*.label

        where:
        desc                                            | expectedLabels | searchTerm | childProps
        "0 hits confirm % not treated as wildcard"      | []             | '%'        | [[elementStatus: Published, label: 'child1', abbreviation: 'ch1']]
        "1 hits with literal % in label"                | ['%child1']    | '%'        | [[elementStatus: Published, label: '%child1', abbreviation: 'ch1']]
        "1 hits with literal % in abbreviation"         | ['child1']     | '%'        | [[elementStatus: Published, label: 'child1', abbreviation: '%ch1']]

        "0 hits confirm _ not treated as char wildcard" | []             | '_'        | [[elementStatus: Published, label: 'child1', abbreviation: 'ch1']]
        "1 hits with literal _ in label"                | ['_child1']    | '_'        | [[elementStatus: Published, label: '_child1', abbreviation: 'ch1']]
        "1 hits with literal _ in abbreviation"         | ['child1']     | '_'        | [[elementStatus: Published, label: 'child1', abbreviation: '_ch1']]

        "1 hit \\ escaped properly in label"            | ['\\child1']   | '\\child1' | [[elementStatus: Published, label: '\\child1', abbreviation: 'ch1']]
        "1 hit \\ escaped properly in abbreviation"     | ['child1']     | '\\ch1'    | [[elementStatus: Published, label: 'child1', abbreviation: '\\ch1']]
    }

    void "test getElementsForAttributes desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        List<BardDescriptor> bardDescriptors = createDescendants(parent, childProps)

        for (BardDescriptor bardDescriptor : bardDescriptors) {
            if (bardDescriptor.label in dictionaryEntryLabels) {
                BardDictionaryDescriptor.build(element: bardDescriptor.element, parent: dictionaryParent)
            }
        }
        BardDescriptor.withSession { Session session -> session.flush() }

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getElementsForAttributes(searchTerm)


        then:
        expectedLabels == results*.label

        where:
        desc                                           | expectedLabels                    | searchTerm | dictionaryEntryLabels | childProps
        "0 children"                                   | []                                | 'child'    | []                    | []
        "0 children due to search term no match"       | []                                | 'notFound' | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC]]
        "0 children due to null search term"           | []                                | null       | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC]]
        "0 children due to retired"                    | []                                | 'child'    | []                    | [[elementStatus: Retired, label: 'child1', expectedValueType: NUMERIC]]
        "0 children due to expectedValueType none"     | []                                | 'child'    | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NONE]]
        "1 child with status Published"                | ['child1']                        | 'child'    | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC]]
        "1 child with status Pending"                  | ['child1']                        | 'child'    | []                    | [[elementStatus: Pending, label: 'child1', expectedValueType: NUMERIC]]
        "1 child with status Deprecated"               | ['child1']                        | 'child'    | []                    | [[elementStatus: ES.Deprecated, label: 'child1', expectedValueType: NUMERIC]]
        "1 child ensuring searchTerm trimmed"          | ['child1']                        | ' child'   | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC]]
        "1 child with case insensitive contains match" | ['child1']                        | 'HILD'     | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC]]
        "2 children"                                   | ['child1', 'child2']              | 'child'    | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC], [elementStatus: Published, label: 'child2', expectedValueType: NUMERIC]]
        "1 child of 2 due to Retire"                   | ['child2']                        | 'child'    | []                    | [[elementStatus: Retired, label: 'child1', expectedValueType: NUMERIC], [elementStatus: Published, label: 'child2', expectedValueType: NUMERIC]]
        "1 child of 2 due to search term"              | ['child2']                        | 'child2'   | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC], [elementStatus: Published, label: 'child2', expectedValueType: NUMERIC]]
        "1 child of 2 due to dictionary entry"         | ['child2']                        | 'child'    | ['child1']            | [[elementStatus: Published, label: 'child1', expectedValueType: NUMERIC], [elementStatus: Published, label: 'child2', expectedValueType: NUMERIC]]
        "1 child of 2 due to expectedValueType none"   | ['child2']                        | 'child'    | []                    | [[elementStatus: Published, label: 'child1', expectedValueType: NONE], [elementStatus: Published, label: 'child2', expectedValueType: NUMERIC]]
        "3 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | []                    | [[elementStatus: Published, label: 'c_child', expectedValueType: NUMERIC], [elementStatus: Published, label: 'B_child', expectedValueType: NUMERIC], [elementStatus: Published, label: 'a_child', expectedValueType: NUMERIC]]
        "2 children sorted by label case insensitive"  | ['a_child', 'B_child', 'c_child'] | 'child'    | []                    | [[elementStatus: Published, label: 'c_child', expectedValueType: NUMERIC], [elementStatus: Published, label: 'B_child', expectedValueType: NUMERIC], [elementStatus: Published, label: 'a_child', expectedValueType: NUMERIC]]
    }

    void "test getElementsForAttributes wildcard and escaping desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        List<BardDescriptor> bardDescriptors = createDescendants(parent, childProps)
        BardDescriptor.withSession { Session session -> session.flush() }

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getElementsForAttributes(searchTerm)


        then:
        expectedLabels == results*.label

        where:
        desc                                            | expectedLabels | searchTerm | childProps
        "0 hits confirm % not treated as wildcard"      | []             | '%'        | [[elementStatus: Published, label: 'child1', abbreviation: 'ch1', expectedValueType: NUMERIC]]
        "1 hits with literal % in label"                | ['%child1']    | '%'        | [[elementStatus: Published, label: '%child1', abbreviation: 'ch1', expectedValueType: NUMERIC]]
        "1 hits with literal % in abbreviation"         | ['child1']     | '%'        | [[elementStatus: Published, label: 'child1', abbreviation: '%ch1', expectedValueType: NUMERIC]]

        "0 hits confirm _ not treated as char wildcard" | []             | '_'        | [[elementStatus: Published, label: 'child1', abbreviation: 'ch1', expectedValueType: NUMERIC]]
        "1 hits with literal _ in label"                | ['_child1']    | '_'        | [[elementStatus: Published, label: '_child1', abbreviation: 'ch1', expectedValueType: NUMERIC]]
        "1 hits with literal _ in abbreviation"         | ['child1']     | '_'        | [[elementStatus: Published, label: 'child1', abbreviation: '_ch1', expectedValueType: NUMERIC]]

        "1 hit \\ escaped properly in label"            | ['\\child1']   | '\\child1' | [[elementStatus: Published, label: '\\child1', abbreviation: 'ch1', expectedValueType: NUMERIC]]
        "1 hit \\ escaped properly in abbreviation"     | ['child1']     | '\\ch1'    | [[elementStatus: Published, label: 'child1', abbreviation: '\\ch1', expectedValueType: NUMERIC]]
    }


    void "test getDescriptorsForAttributes desc: '#desc'"() {

        given:

        BardDescriptor parentA = BardDescriptor.build(fullPath: "${grandParent.fullPath}> parent A", leaf: false, parent: grandParent, label: "parent A")
        createDescendants(parentA, parentAChildren).each { println(it.fullPath) }

        BardDescriptor parentB = BardDescriptor.build(fullPath: "${grandParent.fullPath}> parent B", leaf: false, parent: grandParent, label: "parent B")
        createDescendants(parentB, parentBChildren).each { println(it.fullPath) }

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getDescriptorsForAttributes(startOfFullPath)


        then:
        expectedLabels == results*.label

        where:
        desc                                       | expectedLabels       | startOfFullPath          | parentAChildren                                                               | parentBChildren
        "0 children"                               | []                   | null                     | []                                                                            | []
        "0 children due to retired"                | []                   | null                     | [[elementStatus: Retired, label: 'childA', expectedValueType: NUMERIC]]       | []
        "0 children due to expectedValueType none" | []                   | null                     | [[elementStatus: Published, label: 'childA', expectedValueType: NONE]]        | []
        "1 child with status Published"            | ['childA']           | null                     | [[elementStatus: Published, label: 'childA', expectedValueType: NUMERIC]]     | []
        "1 child with status Pending"              | ['childA']           | null                     | [[elementStatus: Pending, label: 'childA', expectedValueType: NUMERIC]]       | []
        "1 child with status Deprecated"           | ['childA']           | null                     | [[elementStatus: ES.Deprecated, label: 'childA', expectedValueType: NUMERIC]] | []

        "0 children due to startOfFullPath"        | []                   | 'notFound'               | [[elementStatus: Published, label: 'childA', expectedValueType: NUMERIC]]     | [[elementStatus: Published, label: 'childB', expectedValueType: NUMERIC]]
        "1 children due to startOfFullPath"        | ['childA']           | 'grandParent> parent A>' | [[elementStatus: Published, label: 'childA', expectedValueType: NUMERIC]]     | []
        "0 children due to startOfFullPath"        | []                   | 'grandParent> parent B>' | [[elementStatus: Published, label: 'childA', expectedValueType: NUMERIC]]     | []
        "2 children"                               | ['childA', 'childB'] | null                     | [[elementStatus: Published, label: 'childA', expectedValueType: NUMERIC]]     | [[elementStatus: Published, label: 'childB', expectedValueType: NUMERIC]]
        "1 child of 2 due to Retire"               | ['childB']           | null                     | [[elementStatus: Retired, label: 'childA', expectedValueType: NUMERIC]]       | [[elementStatus: Published, label: 'childB', expectedValueType: NUMERIC]]

    }

    void "test getDescriptorsForValues with direct Children desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getDescriptorsForValues(parent.element.id)

        then:

        expectedLabels == results*.label

        where:
        desc                                          | expectedLabels                    | childProps
        "0 children"                                  | []                                | []
        "0 children due to Retired"                   | []                                | [[elementStatus: Retired, label: 'child1']]
        "1 child with status Published"               | ['child1']                        | [[elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                 | ['child1']                        | [[elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"              | ['child1']                        | [[elementStatus: ES.Deprecated, label: 'child1']]
        "1 child of 2 due to Retired"                 | ['child2']                        | [[elementStatus: Retired, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive" | ['a_child', 'B_child', 'c_child'] | [[elementStatus: Published, label: 'c_child'], [elementStatus: Published, label: 'B_child'], [elementStatus: Published, label: 'a_child']]
    }

    void "test getDescriptorsForValues with grandChildren desc: '#desc' expectedLabels: #expectedLabels"() {

        given:
        createDescendants(parent, childProps)

        when:
        List<BardDescriptor> results = ontologyDataAccessService.getDescriptorsForValues(parent.element.id)

        then:
        expectedLabels == results*.label

        where:
        desc                                          | expectedLabels                    | childProps
        "0 children"                                  | []                                | []
        "0 children due to Retired"                   | []                                | [[elementStatus: Retired, label: 'child1']]
        "1 child with status Published"               | ['child1']                        | [[elementStatus: Published, label: 'child1']]
        "1 child with status Pending"                 | ['child1']                        | [[elementStatus: Pending, label: 'child1']]
        "1 child with status Deprecated"              | ['child1']                        | [[elementStatus: ES.Deprecated, label: 'child1']]
        "1 child of 2 due to Retired"                 | ['child2']                        | [[elementStatus: Retired, label: 'child1'], [elementStatus: Published, label: 'child2']]
        "3 children sorted by label case insensitive" | ['a_child', 'B_child', 'c_child'] | [[elementStatus: Published, label: 'c_child'], [elementStatus: Published, label: 'B_child'], [elementStatus: Published, label: 'a_child']]
    }


    void "test getConvertibleUnits #desc"() {

        given:
        Element someUnit = Element.build(label: someUnitLabel)
        Element someConvertibleUnit = Element.build(label: someConvertibleUnitLabel)
        UnitConversion.build(toUnit: someUnit, fromUnit: someConvertibleUnit)

        when:
        List<Element> actualResults = ontologyDataAccessService.getConvertibleUnits(Element.findByLabel(searchLabel)?.id)

        then:
        actualResults*.label == expectedResultLabels

        where:
        desc                                  | someUnitLabel | someConvertibleUnitLabel | searchLabel           | expectedResultLabels
        'happy case'                          | 'someUnit'    | 'someConvertibleUnit'    | 'someUnit'            | ['someConvertibleUnit', 'someUnit']
        'no convertible units, only identity' | 'someUnit'    | 'someConvertibleUnit'    | 'someConvertibleUnit' | ['someConvertibleUnit']
        'no convertible units, only identity' | 'someUnit'    | 'someConvertibleUnit'    | 'non existent label'  | []


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