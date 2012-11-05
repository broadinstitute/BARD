package bardqueryapi

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
class IDSearchTypeUnitSpec extends Specification {
    /**
     * {@link IDSearchType#getLabel()}
     */
    void "test getLabel #label"() {
        when:
        final String returnedLabel = searchType.label
        then:
        assert returnedLabel == expectedLabel
        where:
        label                 | searchType        | expectedLabel
        "Assay Definition ID" | IDSearchType.ADID | "Assay Definition IDs"
        "Compound ID"         | IDSearchType.CID  | "PubChem CIDs"
        "Project ID"          | IDSearchType.PID  | "Project IDs"
    }

}
