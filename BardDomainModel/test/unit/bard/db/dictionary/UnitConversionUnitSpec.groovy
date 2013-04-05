package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/15/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Build(UnitConversion)
@Unroll
class UnitConversionUnitSpec extends Specification {

    UnitConversion domainInstance

    @Before
    void doSetup() {
        domainInstance = UnitConversion.buildWithoutSave()
    }

    void "test conversion of valToConvert: #valToConvert offset: #offset  multiplier: #multiplier formula: #formula"() {
        given:
        domainInstance.offset = offset
        domainInstance.multiplier = multiplier
        domainInstance.formula = formula

        when:
        final actualValue = domainInstance.convert(valToConvert)

        then:
        actualValue == expectedValue

        where:
        desc                              | offset  | multiplier | formula              | valToConvert | expectedValue
        'negative offset'                 | -273.12 | null       | null                 | 10           | -263.12
        'positive offset'                 | 273.12  | null       | null                 | 10           | 283.12
        'positive offset with multiplier' | 1.0     | 2.0        | null                 | 10           | 22.0

        'multiplier '                     | null    | 1          | null                 | 1            | 1
        'multiplier '                     | null    | 0.5        | null                 | 10           | 5
        'multiplier '                     | null    | 0.25       | null                 | 10           | 2.5

        'multiplier '                     | null    | 0.1        | null                 | 1            | 0.1
        'multiplier '                     | null    | 0.01       | null                 | 1            | 0.01
        'multiplier '                     | null    | 0.001      | null                 | 1            | 0.001

        'multiplier '                     | null    | 10         | null                 | 1            | 10
        'multiplier '                     | null    | 100        | null                 | 1            | 100
        'multiplier '                     | null    | 1000       | null                 | 1            | 1000

        'formula'                         | null    | null       | "value * 2"          | 1            | 2
        'formula'                         | null    | null       | "value / 2"          | 1            | 0.5
        'formula'                         | null    | null       | "Math.pow(value, 3)" | 2            | 8
        'formula'                         | null    | null       | "Math.sqrt(value)"   | 25           | 5

    }


}