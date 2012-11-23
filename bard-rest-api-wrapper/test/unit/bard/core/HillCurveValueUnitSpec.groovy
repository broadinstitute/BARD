package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class HillCurveValueUnitSpec extends Specification {
    @Shared String name = "name"
    @Shared DataSource dataSource = new DataSource(name, "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        HillCurveValue currentHillCurveValue = hillCurveValue
        then:
        currentHillCurveValue.getValue() == null
        where:
        label                               | hillCurveValue
        "1 arg constructor"                 | new HillCurveValue(parent)
        "1 arg constructor with datasource" | new HillCurveValue(dataSource)
        "2 arg constructor with datasource" | new HillCurveValue(dataSource, id)
        "2 arg constructor"                 | new HillCurveValue(parent, id)
        "Empty arg constructor"             | new HillCurveValue()
    }

    void "test add"() {
        given:
        final Double concentration = new Double("2.0")
        final Double response = new Double("4.0")
        final HillCurveValue currentHillCurveValue = new HillCurveValue()
        when:
        currentHillCurveValue.add(concentration, response)
        then:
        assert currentHillCurveValue.getResponse().length > 0
        assert currentHillCurveValue.getConc()[0] == concentration
        assert currentHillCurveValue.getResponse()[0] == response
        assert currentHillCurveValue.size() == 1
        assert !currentHillCurveValue.getConcentrationUnits()

    }

    void "test evaluate #label"() {
        given:
        final Double concentration = new Double("2.0")
        final HillCurveValue currentHillCurveValue = new HillCurveValue()
        currentHillCurveValue.setS0(s0)
        currentHillCurveValue.setSinf(sInf)
        currentHillCurveValue.setCoef(coeff)
        currentHillCurveValue.setSlope(slope)
        when:
        final Double result = currentHillCurveValue.evaluate(concentration)
        then:
        assert result?.intValue() == expectedResult
        assert currentHillCurveValue.getCoef() == coeff
        assert currentHillCurveValue.getS0() == s0
        assert currentHillCurveValue.getSinf() == sInf
        assert currentHillCurveValue.getSlope() == slope
        where:
        label                | coeff | s0   | sInf | slope | expectedResult
        "slope==null"        | 2.0   | null | null | null  | null
        "coeff==null"        | null  | null | null | 2.0   | null
        "slope==coeff==null" | null  | null | null | null  | null
        "s0==null"           | 2.0   | null | null | 4.0   | 19
        "sInf==null"         | 2.0   | 2.0  | null | 4.0   | 21
        "No nulls"           | 2.0   | 2.0  | 2.0  | 4.0   | 2
    }
}

