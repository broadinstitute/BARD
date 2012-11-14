package jdo

import bard.core.Experiment
import bard.core.Project

import bard.core.Value
import spock.lang.Unroll
import bard.core.interfaces.SearchResult

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/4/12
 * Time: 7:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(jdo.helper.RESTTestHelper)
@Unroll
class RESTCombinedServiceIntegrationSpec extends AbstractRESTServiceSpec {

    void projToExptData() {

        given: "That we can retrieve the expts for project 274" //////////
        // This next section is the improved version, which goes directly from projects to experiments, rather than going through the assays explicitly.
        // Both approaches seem to produce the same results, but this alternate approach is more concise.

        List<Long> cartProjectIdList = new ArrayList<Long>()
        cartProjectIdList.add(new Long(274))
        final Collection<Project> projects = this.restProjectService.get(cartProjectIdList)
        List<Experiment> allExperiments = []
        for (Project project : projects) {
            final SearchResult<Experiment> searchResult = this.restProjectService.searchResult(project, Experiment.class)
            Collection<Experiment> experiments = searchResult.searchResults
            allExperiments.addAll(experiments)
        }





        when: "We define an etag for a compound used in this project"  /////////////

        List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(new Long(5281847))
        Object etag = this.restCompoundService.newETag((new Date()).toString(), cartCompoundIdList);


        then: "when we step through the value in the expt"    ////////

        int dataCount = 0
        for (Experiment experiment in allExperiments) {

            SearchResult<Value> experimentResult = this.restExperimentService.activities(experiment, etag)
             dataCount= dataCount + experimentResult.count
        }

        // we expect to see some data, but we don't
        assert dataCount > 0   // this fails in V6, but not in V5
    }
}
