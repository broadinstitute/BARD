package bardqueryapi

import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class MolecularSpreadSheetServiceIntegrationSpec extends IntegrationSpec {

    MolecularSpreadSheetService molecularSpreadSheetService
    QueryServiceWrapper queryServiceWrapper
    RESTCompoundService restCompoundService
    RESTExperimentService restExperimentService

    @Before
    void setup() {
        this.restCompoundService = queryServiceWrapper.getRestCompoundService()
        this.restExperimentService = queryServiceWrapper.getRestExperimentService()
    }

    @After
    void tearDown() {

    }

    void "test  #label"() {
        given: ""

        when: ""

        then: ""

        where:
        label                        | experimentid  | cids
        "Search with a list of CIDs" | new Long(346) | [3232584, 3232585, 3232586]

    }
//    /**
//     * Copied from JDO Wrapper tests
//     */
//    void "test retrieving the experimental data for known compounds in an experiment #label"() {
//        given:
//        Object etag = restCompoundService.newETag("My awesome compound collection", cids);
//        when: "We call the getFactest matehod"
//        final Experiment experiment = this.restExperimentService.get(experimentid)
//        Collection<Compound> compounds = this.restCompoundService.get(cids)
//        then: "We expect to get back a list of facets"
//        ServiceIterator<Value> eiter = this.restExperimentService.activities(experiment, etag);
//        while (eiter.hasNext()) {
//            Value v = eiter.next();
//            println v.source
//            Iterator<Value> valueIterator = v.children()
//            // HillCurveValue hillCurveValue
//            // Object payload
//            while (valueIterator.hasNext()) {
//                Value internalValue = valueIterator.next()
//                String identifier = internalValue.id
//                println identifier
//                if (identifier == 'Activity') {
//
//                    println internalValue.getClass().getName()
//                }
//            }
//        }
//        where:
//        label                        | experimentid  | cids
//        "Search with a list of CIDs" | new Long(346) | [3232584, 3232585, 3232586]
//        // "Search with a single of CID" | new Long(346)   |   [3232584]
//        // "Search with another set of CID" | new Long(1326)   |   [11289,5920,442428]
//    }
}
