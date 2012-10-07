package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link ListUtilsFactory} for usage instructions
 */
@Unroll
class ListUtilsFactoryUnitSpec extends Specification {
    /**
     * {@link ListUtilsFactory.create ( )}
     */
    void "test create"() {
        given:
        ListUtilsFactory listUtilsFactory = new ListUtilsFactory()

        when:
        final Object object = listUtilsFactory.create()
        then:
        assert object instanceof Double
        Double d = (Double) object
        assert d == new Double(0)
    }
}
