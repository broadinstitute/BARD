package bard.db.dictionary

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/3/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */

@Unroll
class ElementAbbreviationLabelComparatorUnitSpec extends Specification {

    void "test comparator #desc"() {
        given:
        def elements = initial.collect {
            new Element(label: it.l, abbreviation: it.a)
        }
        when:
        elements.sort(true, new ElementAbbreviationLabelComparator())
        then:
        elements*.abbreviation == expectedAbbreviationOrder
        elements*.label == expectedLabelOrder

        where:
        desc                                   | initial                                                                                        | expectedAbbreviationOrder | expectedLabelOrder
        'happy case all lowercase'             | [[a: 'c', l: 'c'], [a: 'b', l: 'b'], [a: 'a', l: 'a']]                                         | ['a', 'b', 'c']           | ['a', 'b', 'c']
        'happy case case-insensitive'          | [[a: 'C', l: 'C'], [a: 'b', l: 'b'], [a: 'A', l: 'A']]                                         | ['A', 'b', 'C']           | ['A', 'b', 'C']
        'null abbreviation, fallback to label' | [[a: 'C', l: 'C'], [a: null, l: 'b'], [a: 'A', l: 'A']]                                        | ['A', null, 'C']          | ['A', 'b', 'C']
        'abbreviations same, sort on label'    | [[a: '%', l: 'mass percentage'], [a: '%', l: 'percent'], [a: '%', l: 'confluence percentage']] | ['%', '%', '%']           | ['confluence percentage', 'mass percentage', 'percent']

    }
}
