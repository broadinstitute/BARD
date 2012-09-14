package bardqueryapi

import grails.plugin.spock.IntegrationSpec

import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.format

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentalResultsServiceIntegrationSpec extends IntegrationSpec {
    ExperimentalResultsService experimentalResultsService


    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }


    void "experimental results"() {
        given: "An experimental results service"
        assert experimentalResultsService != null
        when:
        assertNotNull experimentalResultsService.fakeMe()
        then:
        for( int rowCnt in 0..experimentalResultsService.fakeMe().getRowCount())   {
            for( int colCnt in 0..experimentalResultsService.fakeMe().mssHeaders.size())   {
                String  key_val = "${rowCnt}_${colCnt}"
                println   key_val
                assertNotNull  experimentalResultsService.fakeMe().mssData[key_val]
            }
        }
    }

}
