package bard.utils

import spock.lang.Specification
import spock.lang.Unroll



@Unroll
class NumberUtilsUnitSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test isScientificNotationValue #desc"() {
        when:
        boolean isScientificNotation = NumberUtils.isScientificNotationValue(scientificNotationValue)

        then:
        isScientificNotation == expectedResult

        where:
        desc                                   | expectedResult | scientificNotationValue
        "Well formed number with upper case E" | true           | '12345E-2'
        "Well formed number with lower case e" | true           | '12345e-2'
        "Well formed negative number"          | true           | '-12345e-2'
        "Well formed positive number"          | true           | '+12345e-2'
        "Well formed decimal number"           | true           | '7.819e-3'
        "Well formed negative decimal number"  | true           | '-7.819e-3'
        "BAD formed number - two signs"        | false          | '+-12345E-2'
        "BAD formed number - two Es"           | false          | '12345EE-2'
        "No scientific notation value"         | false          | '12345'
    }

    void "test convertScientificNotationValue  #desc"() {
        when:
        BigDecimal convertedValue = NumberUtils.convertScientificNotationValue(scientificNotationValue)

        then:
        convertedValue == expectedResult

        where:
        desc                                   | expectedResult              | scientificNotationValue
        "good uppercase E neg"                 | new BigDecimal('0.007819')  | '7.819E-3'
        "good lowercase e neg"                 | new BigDecimal('0.007819')  | '7.819e-3'
        "good neg lowercase e neg"             | new BigDecimal('-0.007819') | '-7.819e-3'
        "good integer"                         | new BigDecimal('1.0')       | '1'
        "good neg integer "                    | new BigDecimal('-1.0')      | '-1'
        "good decimal"                         | new BigDecimal('1.0')       | '1'
        "good neg decimal"                     | new BigDecimal('-1.25')     | '-1.25'
        "good decimal"                         | new BigDecimal('1.25')      | '1.25'
        "good decimal with whitespace"         | new BigDecimal('1.25')      | '  1.25  '
        "Well formed number with upper case E" | null                        | '7.819eE-3'

    }
}