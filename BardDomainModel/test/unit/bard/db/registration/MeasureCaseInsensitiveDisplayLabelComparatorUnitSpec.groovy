package bard.db.registration

import bard.db.dictionary.Element
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/16/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class MeasureCaseInsensitiveDisplayLabelComparatorUnitSpec extends Specification {

    void "test compareTo"() {

        when:
        SortedSet measures = new TreeSet(new MeasureCaseInsensitiveDisplayLabelComparator())

        for (String label in input) {
            measures << new Measure(resultType: new Element(label: label))
        }

        then:
        measures*.displayLabel == expected

        where:
        input           | expected
        ['b', 'a']      | ['a', 'b']
        ['B', 'a']      | ['a', 'B']
        ['c', 'B', 'a'] | ['a', 'B', 'c']

    }
}
