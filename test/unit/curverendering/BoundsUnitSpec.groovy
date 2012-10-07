package curverendering

import promiscuity.Scaffold
import promiscuity.WarningLevel
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BoundsUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test expandToContain"() {
        given:" A Bound Object"
        double xMin = 1
        double xMax = 2
        double yMin = 1
        double yMax = 2

        //expanded values
        double exMin = 2
        double exMax = 4
        double eyMin = 2
        double eyMax = 4
        Bounds bounds = new Bounds(exMin,exMax,eyMin,eyMax)
        when: "We call the expandToContain() method on the given Boundss object"
        bounds.expandToContain(xMin,xMax,yMin,yMax)
        then: "The expected to get back the expected warning level"
        bounds.xMax == exMax
        bounds.yMax == eyMax
        bounds.xMin == xMin
        bounds.xMax == exMax

    }



}

