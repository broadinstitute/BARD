package bardqueryapi

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
class NormalizeAxisUnitSpec extends Specification {
    /**
     * {@link NormalizeAxis#getLabel()}
     */
    void "test getLabel #label"() {
        when:
        final String returnedLabel = normalizeAxis.label
        then:
        assert returnedLabel == expectedLabel
        where:
        label                | normalizeAxis               | expectedLabel
        "Denormalize Y-Axis" | NormalizeAxis.Y_DENORM_AXIS | "DeNormalize Y-Axis"
        "Normalize Y-Axis"   | NormalizeAxis.Y_NORM_AXIS   | "Normalize Y-Axis"
    }


}
