package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll
import curverendering.Curve

/**
 * See the API for {@link ListCurveFactory} for usage instructions
 */
@Unroll
class ListCurveFactoryUnitSpec extends Specification {
    /**
     * {@link ListCurveFactory.create()}
     */
    void "test create"() {
        given:
       ListCurveFactory listCurveFactory = new ListCurveFactory()

        when:
        final Object object = listCurveFactory.create()
        then:
        assert object instanceof Curve
    }
}
