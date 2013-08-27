package bard.core.rest.spring

import spock.lang.Unroll
import grails.plugin.spock.IntegrationSpec
import bard.core.rest.spring.experiment.ActivityData

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/19/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ActivityDataIntegrationSpec extends IntegrationSpec {


    void "getDictionaryLabel #label"() {
        given:
        ActivityData activityData = new ActivityData(dictElemId: elementId)


        when:
        final String foundLabel = activityData.getDictionaryLabel()

        then:
        assert foundLabel == expectedLabel

        where:
        label                                       | elementId | expectedLabel
        "Element Id=3"                              | 3         | "assay protocol"
        "Element Id=4"                              | 4         | "assay component"
        "Element Id=3 again. Should only hit cache" | 3         | "assay protocol"
        "Element Id=4 again. Should only hit cache" | 4         | "assay component"


    }

}
