package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ServiceParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        final ServiceParams currentServiceParams = serviceParams
        then:
        assert currentServiceParams.depth == expectedDepth
        assert currentServiceParams.skip == expectedSkip
        assert currentServiceParams.top == expectedTop
        assert !currentServiceParams.partial
        assert !currentServiceParams.ordering
        where:
        label                    | serviceParams                               | expectedDepth | expectedSkip | expectedTop
        "Empty Constructor"      | new ServiceParams()                         | 3             | null         | null
        "Single arg Constructor" | new ServiceParams(2)                        | 2             | null         | null
        "Two arg Constructor"    | new ServiceParams(new Long(2), new Long(2)) | 3             | 2            | 2
    }

    void "test all setters #label"() {
        given:
        final ServiceParams currentServiceParams = new ServiceParams()
        final Integer maxDepth = 2
        final String ordering = "ordering"
        final Long skip = 2
        final Long top = 8
        when:
        currentServiceParams.setMaxDepth(maxDepth)
        currentServiceParams.setOrdering(ordering)
        currentServiceParams.setPartial(true)
        currentServiceParams.setSkip(skip)
        currentServiceParams.setTop(top)

        then:
        currentServiceParams.maxDepth == maxDepth
        currentServiceParams.ordering == ordering
        currentServiceParams.skip == skip
        currentServiceParams.top == top
        assert currentServiceParams.partial
    }
}

