package bard.core.rest.spring

import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.experiment.Experiment
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentResult
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.ExpandedProjectResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Tests for RESTAssayService in JDO
 */
@Unroll
class RestCombinedServiceIntegrationSpec extends IntegrationSpec {
    RestCombinedService restCombinedService
    /**
     *
     */
    void "test find compounds with experiment ID"() {
        given:
        final Long eid = 883
        when:
        final List<Long> cids = this.restCombinedService.compounds(eid)
        then:
        assert !cids.isEmpty()
        for (Long cid : cids) {
            println cid
        }
    }

    void "test findProjectsByAssayId #label=#adid"() {
        when:
        final ProjectResult projectResult = restCombinedService.findProjectsByAssayId(adid)
        then:
        List<Project> projects = projectResult.projects
        and:
        assert projects
        assert !projects.isEmpty()
        and:

        for (Project project : projects) {
            assert project.getProjectId()
        }
        where:
        label      | adid
        "Assay ID" | 2868

    }

    /**
     * if you search for the this assay via the REST api, you get one experiment associated with the assay
     */
    void "test findExperimentsByAssayId #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        List<Experiment> experiments = this.restCombinedService.findExperimentsByAssayId(adid)
        then: "We expect to get back a list of assays"
        assert experiments


        where:
        label                           | adid
        "Search with a single assay id" | 644
    }
    /**
     *
     */
    void "test findAssaysByCID"() {
        given:
        Long cid = 999
        when:
        final AssayResult assayResult = this.restCombinedService.findAssaysByCID(cid)
        then:
        assert assayResult
        assert assayResult.assays

    }

    void "test findProjectsByCID #label"() {
        when:
        final ProjectResult projectResult = this.restCombinedService.findProjectsByCID(cid)
        then:
        final List<Project> projects = projectResult.projects
        assert !projects.isEmpty()

        where:
        label                       | cid
        "Find an existing compound" | 2722
    }


    void "test findCompoundsByExperimentId"() {
        given:
        Long eid = 2273
        when:
        CompoundResult compoundResult = this.restCombinedService.findCompoundsByExperimentId(eid)

        then:
        assert compoundResult
        assert compoundResult.compounds

    }


    void "test findExperimentsByProjectId"() {
        given:
        Long pid = 17
        when:
        List<Assay> assays = this.restCombinedService.findAssaysByProjectId(pid)
        then:
        assert assays
    }

    void projToExptData() {

        given: "That we can retrieve the expts for project 274" //////////
        // This next section is the improved version, which goes directly from projects to experiments, rather than going through the assays explicitly.
        // Both approaches seem to produce the same results, but this alternate approach is more concise.

        final List<Long> cartProjectIdList = new ArrayList<Long>()
        cartProjectIdList.add(new Long(274))
        ExpandedProjectResult projectResult = this.restCombinedService.projectRestService.searchProjectsByIds(cartProjectIdList)
        final List<Project> projects = projectResult.projects
        final List<Experiment> allExperiments = []
        for (final Project project : projects) {
            List<Experiment> experiments = this.restCombinedService.findExperimentsByProjectId(project.id)
            allExperiments.addAll(experiments)
        }
        when: "We define an etag for a compound used in this project"  /////////////
        final List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(new Long(5281847))
        String etag = this.restCombinedService.compoundRestService.newETag((new Date()).toString(), cartCompoundIdList);


        then: "when we step through the value in the expt"    ////////

        int dataCount = 0
        for (Experiment experiment in allExperiments) {

            ExperimentData experimentData = this.restCombinedService.experimentRestService.activities(experiment.exptId, etag)
            dataCount = dataCount + experimentData.activities.size()
        }

        // we expect to see some data, but we don't
        assert dataCount > 0   // this fails in V6, but not in V5
    }
    /**
     * if you search for the this assay via the REST api, you get one project associated with the assay: http://bard.nih.gov/api/latest/assays/588591/projects
     */
    /**
     * if you search for the this assay via the REST api, you get one experiment associated with the assay
     */
    void "test Get Assays with experiment #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        final ExpandedAssay assay = this.restCombinedService.assayRestService.getAssayById(adid)

        then: "We expect to get back a list of assays"
        assert assay
        List<Experiment> experiments = this.restCombinedService.findExperimentsByAssayId(assay.id)
        assert experiments
        for (Experiment experiment : experiments) {
            assert experiment
        }
        where:
        label                           | adid
        "Search with a single assay id" | 644
    }

    void "test Get Assays with projects #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        final ExpandedAssay assay = this.restCombinedService.assayRestService.getAssayById(adid)

        then: "We expect to get back a list of assays"
        assert assay
        ProjectResult projectResult = restCombinedService.findProjectsByAssayId(assay.id)
        final List<Project> projects = projectResult.projects
        assert projects
        for (Project project : projects) {
            assert project
        }


        where:
        label                           | adid
        "Search with a single assay id" | 644
    }

    void "test Project From Single Assay #label=#adid"() {
        given:
        ExpandedAssay assay = restCombinedService.assayRestService.getAssayById(adid);
        when:
        ProjectResult projectResult = restCombinedService.findProjectsByAssayId(assay.id);
        then:
        assert projectResult.projects
        and:
        for (Project project : projectResult.projects) {
            assert project.getId()
        }
        where:
        label      | adid
        "Assay ID" | 2868

    }

    void "test projects with experiments #label"() {

        when: "The get method is called with the given PID: #pid"
        Project project = this.restCombinedService.projectRestService.getProjectById(pid)
        then: "A ProjectSearchResult is returned with the expected information"
        assert project
        assert pid == project.id
        assert project.experimentCount


        final List<Experiment> experiments = restCombinedService.findExperimentsByProjectId(project.id)
        assert experiments
        for (Experiment experiment : experiments) {
            assert experiment
        }
        where:
        label                                  | pid
        "Find an existing ProjectSearchResult" | 179
    }

    void "test projects with assays #label"() {

        when: "The get method is called with the given PID: #pid"
        Project project = this.restCombinedService.projectRestService.getProjectById(pid)
        then: "A ProjectSearchResult is returned with the expected information"
        assert project
        assert pid == project.id
        assert project.experimentCount

        final List<Assay> assays = restCombinedService.findAssaysByProjectId(project.id)
        assert assays
        assert !assays.isEmpty()
        where:
        label                                  | pid
        "Find an existing ProjectSearchResult" | new Integer(179)
    }

    void "testExperimentsFromSingleProject"() {
        given:
        Project project = this.restCombinedService.projectRestService.getProjectById(1);
        assert project

        when:
        List<Experiment> experiments = this.restCombinedService.findExperimentsByProjectId(project.projectId)
        then:
        assert experiments
    }

    void "test pulling an arbitrary set of compounds out of an experiment and then looking at their experimental values #label"() {

        when: "The get method is called with the given experiment ID: #experimentid"
        final ExperimentSearch experiment = this.restCombinedService.experimentRestService.getExperimentById(experimentid)
        final List<Long> cids = this.restCombinedService.compounds(experimentid)
        then: "An experiment is returned with the expected information"
        assert experiment
        assert cids
        String etag = restCombinedService.compoundRestService.newETag(label, cids);
        ExperimentData experimentData = this.restCombinedService.experimentRestService.activities(experimentid, etag);
        assert experimentData.activities
        assert experimentData.activities.get(0)
        where:
        label                | experimentid  | numVals
        "Find an experiment" | new Long(883) | 2
    }

    void "test findAssaysByProjectId"() {
        given:
        Long pid = 17
        when:
        List<Experiment> experiments = this.restCombinedService.findExperimentsByProjectId(pid)
        then:
        assert experiments
    }
    /**
     *
     */
    void "test findProjectsByExperimentId"() {
        given:
        final Long eid = 197
        when:
        final ProjectResult projectResult = restCombinedService.findProjectsByExperimentId(eid)

        then:
        assert projectResult
        assert projectResult.projects
    }

    void "test findExperimentsByCID"() {
        given:
        Long cid = 313619
        when:
        ExperimentResult experimentResult = this.restCombinedService.findExperimentsByCID(cid)
        then:
        assert experimentResult
        assert experimentResult.experiments
    }
}