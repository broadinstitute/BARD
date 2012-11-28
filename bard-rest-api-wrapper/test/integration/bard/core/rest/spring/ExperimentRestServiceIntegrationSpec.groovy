package bard.core.rest.spring

import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentResult
import bard.core.rest.spring.experiment.ExperimentSearch
import grails.plugin.spock.IntegrationSpec
import spock.lang.Timeout
import spock.lang.Unroll
import bard.core.rest.spring.assays.Assay

/**
 * Tests for ProjectRestService
 */
@Mixin(RESTTestHelper)
@Unroll
class ExperimentRestServiceIntegrationSpec extends IntegrationSpec {
    ExperimentRestService experimentRestService
    CompoundRestService compoundRestService


    void "test pull other values out of an experiment"() {

        when: "The get method is called with the given experiment ID: #experimentid"
        final ExperimentSearch experiment = this.experimentRestService.getExperimentById(experimentid)
        then: "An experiment is returned with the expected information"
        assert experiment.getExptId() == experimentid
        List<Assay> assays = experiment.getAssays()
        assert assays
        List<Long> projectCollection = experiment.getProjectIdList()
        assert projectCollection
        int projectCount = experiment.getProjectCount()
        assert projectCollection.size() == projectCount
        where:
        label                | experimentid
        "Find an experiment" | new Long(346)
    }

    void "testExperiment"() {
        given:
        final List<Long> experimentIds = [460, 461, 197, 198, 4171, 3278, 3274, 3277, 2362, 2637]
        when:
        final ExperimentResult experimentResult = experimentRestService.searchExperimentsByIds(experimentIds)
        then:
        assert experimentResult
        assert experimentResult.experiments
        assert experimentResult.experiments.size() == experimentIds.size()
    }

    void "test step through entire Experiment iterator #experimentId"() {

        when: "The get method is called with the given experiment ID: #experimentId"
        final ExperimentSearch experimentSearch = this.experimentRestService.getExperimentById(experimentId)
        then: "An experiment is returned with the expected information"
        assert experimentSearch
        assert experimentSearch.getExptId() == experimentId
        assertExperimentSearchResult(experimentSearch)
        where:
        label                | experimentId
        "Find an experiment" | new Long(346)  // short expt (3000 values)
        "Find an experiment" | new Long(1326)  // longer expt (193717 values) -- runs for 8 minutes!
    }

    void "test retrieving the experimental data for known compounds in an experiment #label"() {
        given:
        final String etag = compoundRestService.newETag(label, cids);

        when: "We call the getFactest matehod"
        final ExperimentData experimentData = this.experimentRestService.activities(experimentid, etag);

        then: "We expect to get back a list of facets"
        assert experimentData
        assert experimentData.activities
        for (Activity activity : experimentData.activities) {
            assert activity
        }

        where:
        label                            | experimentid   | cids
        "Search with a list of CIDs"     | new Long(883)  | [3233285, 3234360, 3235360]
        "Search with a single of CID"    | new Long(2273) | [16001802]
        "Search with another set of CID" | new Long(2273) | [3237462, 3240101, 16001802]
    }

    @Timeout(10)
    void "tests getExperimentActivities #label"() {
        when: "We call the restExperimentService.activities method with the experiment"
        final ExperimentData experimentData = experimentRestService.activities(experimentId);

        then: "We expect activities for each of the experiments to be found"
        assert experimentData
        final List<Activity> activities = experimentData.activities
        assert activities
        where:
        label                                        | experimentId
        "For an existing experiment with activities" | new Long(346)
    }

    void "testExperimentActivity"() {
        given:
        final Long experimentId = 197
        when:
        final ExperimentData experimentData = experimentRestService.activities(experimentId)
        then:
        final List<Activity> activities = experimentData.activities
        assert activities
    }

    public void "testExperimentActivityWithCompounds"() {
        given: "That an etag is created from a list of compound IDs"
        final String etag = compoundRestService.newETag("foo cids", [3237462L, 3240101L, 16001802L]);
        and: "That an experiment exists"
        final Long experimentId = 2273
        when: "Experiment with the compound etags"
        final ExperimentData experimentData = experimentRestService.activities(experimentId, etag)
        then:
        assert experimentData
        final List<Activity> activities = experimentData.activities
        assert activities
        assert !activities.isEmpty()
        for (Activity activity : activities) {
            assert activity
        }
        assert activities.size() == 3
    }
    /**
     * Step through the experiment with the iterator
     * @param experiment
     */
    void assertExperimentSearchResult(final ExperimentSearch experimentSearch) {
        final ExperimentData experimentData = this.experimentRestService.activities(experimentSearch.exptId)
        final List<Activity> activities = experimentData.activities
        assert activities
        for (Activity activity : activities) {
            assert activity
        }

    }
}