package bard.core.rest

import bard.core.adapter.AssayAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.interfaces.SearchResult
import bard.core.rest.helper.RESTTestHelper
import grails.plugin.spock.IntegrationSpec
import junit.framework.Assert
import spock.lang.Unroll
import bard.core.*
import org.junit.Assert

/**
 * Tests for RESTAssayService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class CombinedRestServiceIntegrationSpec extends IntegrationSpec {
    CombinedRestService combinedRestService

    void "test searchResult with Assay"() {
        given:
        final Compound compound = new Compound()
        compound.setId(new Integer(313619))
        when:
        final SearchResult<Assay> searchResult = this.combinedRestService.searchResultByCompound(compound, Assay.class)

        then:
        final List<Assay> assays = searchResult.searchResults
        assert !assays.isEmpty()

    }

    void "test pulling an arbitrary set of compounds out of an experiment and then looking at their experimental values #label"() {

        when: "The get method is called with the given experiment ID: #experimentid"
        final Experiment experiment = this.combinedRestService.restExperimentService.get(experimentid)
        final SearchResult<Compound> compoundSearchResult = this.combinedRestService.compounds(experiment)
        then: "An experiment is returned with the expected information"
        assert experiment
        assert compoundSearchResult
        List<Compound> compoundList = compoundSearchResult.next(numVals)
        Object etag = combinedRestService.restCompoundService.newETag(label, compoundList*.id);
        SearchResult<Value> eiter = this.combinedRestService.restExperimentService.activities(experiment, etag);
        Assert.assertNotNull eiter.searchResults

        Value value = eiter.searchResults.get(0)
        assert value
        where:
        label                | experimentid  | numVals
        "Find an experiment" | new Long(883) | 2
    }

    void "test Projects From Single Experiment"() {
        given:
        Experiment experiment = this.combinedRestService.restExperimentService.get(197);
        assert experiment
        when:
        SearchResult<Project> iter =
            this.combinedRestService.searchResultByExperiment(experiment, Project.class);
        assert iter.searchResults
        Collection<Project> projects = iter.next(100);
        then:
        assert projects
        assert !projects.isEmpty()
    }

    void "testExperimentsFromSingleProject"() {
        given:
        Project project = this.combinedRestService.restProjectService.get(1);
        assert project

        when:
        SearchResult<Experiment> iter =
            combinedRestService.searchResultByProject(project, Experiment.class);
        then:
        Collection<Experiment> exprs = iter.next(100);
        assert exprs
        assert !exprs.isEmpty()
    }

    void "test searchResult with Compound"() {
        given:
        final Experiment experiment = new Experiment()
        experiment.setId(new Integer(2273))
        when:
        final SearchResult<Compound> searchResult = this.combinedRestService.searchResultByExperiment(experiment, Compound.class)

        then:
        final List<Compound> compounds = searchResult.searchResults
        assert !compounds.isEmpty()

    }

    void "test searchResult with Experiment"() {
        given:
        final Compound compound = new Compound()
        compound.setId(new Integer(313619))
        when:
        final SearchResult<Experiment> searchResult = this.combinedRestService.searchResultByCompound(compound, Experiment.class)

        then:
        final List<Experiment> experiments = searchResult.searchResults
        assert !experiments.isEmpty()

    }
    /**
     *
     */
    void "test projects with assays #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = combinedRestService.restProjectService.get(pid)
        then: "A Project is returned with the expected information"
        assert project
        assert pid == project.id
        final ProjectAdapter projectAdapter = new ProjectAdapter(project)
        assert projectAdapter.numberOfExperiments

        final SearchResult<Assay> searchResults = combinedRestService.searchResultByProject(project, Assay.class)
        final List<Assay> assays = searchResults.searchResults
        assert assays
        assert !assays.isEmpty()
        where:
        label                      | pid
        "Find an existing Project" | new Integer(179)
    }
    /**
     *
     */
    void "test projects with experiments #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = combinedRestService.restProjectService.get(pid)
        then: "A Project is returned with the expected information"
        assert project
        assert pid == project.id
        final ProjectAdapter projectAdapter = new ProjectAdapter(project)
        assert projectAdapter.numberOfExperiments

        final SearchResult<Experiment> searchResults = combinedRestService.searchResultByProject(project, Experiment.class)
        final List<Experiment> experiments = searchResults.searchResults
        assert experiments
        for (Experiment experiment : experiments) {
            assert experiment
        }
        where:
        label                      | pid
        "Find an existing Project" | new Integer(179)
    }

    void "test Project From Single Assay #label=#adid"() {
        given:
        Assay a = combinedRestService.restAssayService.get(adid);
        when:
        SearchResult<Project> iter =
            combinedRestService.searchResultByAssay(a, Project.class);
        then:
        Collection<Project> projects = iter.next(1000);
        and:
        assert projects
        assert !projects.isEmpty()
        and:

        for (Project project : projects) {
            assert project.getId()
        }
        where:
        label      | adid
        "Assay ID" | 2868

    }
    /**
     * if you search for the this assay via the REST api, you get one project associated with the assay: http://bard.nih.gov/api/latest/assays/588591/projects
     */
    void "test Get Assays with projects #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        final Assay assay = this.combinedRestService.restAssayService.get(adid)

        then: "We expect to get back a list of assays"
        assertAssays([assay], false)
        final SearchResult<Project> searchResult = combinedRestService.searchResultByAssay(assay, Project.class)
        final List<Project> entities = searchResult.searchResults
        assert entities
        assert !entities.isEmpty()
        for (Project project : entities) {
            assert project
        }


        where:
        label                           | adid
        "Search with a single assay id" | 644
    }

    /**
     * if you search for the this assay via the REST api, you get one experiment associated with the assay
     */
    void "test Get Assays with experiment #label"() {
        when: "We call the get method of the the RESTAssayService with an assay ids"
        final Assay assay = this.combinedRestService.restAssayService.get(adid)

        then: "We expect to get back a list of assays"
        assertAssays([assay], false)
        final SearchResult<Experiment> searchResult = this.combinedRestService.searchResultByAssay(assay, Experiment.class)
        final List<Experiment> entities = searchResult.searchResults
        assert entities
        assert !entities.isEmpty()
        for (Experiment experiment : entities) {
            assert experiment
        }


        where:
        label                           | adid
        "Search with a single assay id" | 644
    }

/**
 *
 * @param assays
 * @param isStringSearch - Means that this is a result of a string search and not an id search
 * string searches do not have sids, but they do have highlights and annotations
 */
    void assertAssays(Collection<Assay> assays, boolean isStringSearch = false) {
        for (Assay assay : assays) {
            assertAssay(assay, isStringSearch)
        }
    }

/**
 *
 * @param assay
 * @param isFreeTextSearch
 */
    void assertAssay(final Assay assay, final boolean isFreeTextSearch = false) {
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        assert assay.id == assayAdapter.id
        assert assay.comments == assayAdapter.comments
        assert assay.name == assayAdapter.name
        assert assay.description == assayAdapter.description
        assert assay.category == assayAdapter.category
        assert assay.type == assayAdapter.type
        assert assay.role == assayAdapter.role

        if (isFreeTextSearch) { //if we are doing a free text search
            final Collection<Value> annotations = assayAdapter.getAnnotations()
            assert annotations
            assert !annotations.isEmpty()
            assert assayAdapter.searchHighlight
        }

    }
    void "test retrieving assays from a compound #label"() {

        when: "The get method is called with the given CID: #cid"
        final Compound compound = this.combinedRestService.restCompoundService.get(cid)
        then: "A Compound is returned with the expected information"
        Collection<Assay> allAssaysForThisCompound = this.combinedRestService.getTestedAssays(compound, false)
        for (Assay assay : allAssaysForThisCompound) {
            Assert.assertNotNull assay
        }
        Collection<Assay> activeAssaysForThisCompound = this.combinedRestService.getTestedAssays(compound, true)
        for (Assay assay : activeAssaysForThisCompound) {
            Assert.assertNotNull assay
        }
        assert allAssaysForThisCompound.size() > activeAssaysForThisCompound.size()   // might not hold for all compounds, but it holds for these
        where:
        label                     | cid
        // "Find a compound 313619"  | new Long(313619)
        "Find a compound 9660191" | new Long(9660191)
    }
    void projToExptData() {

        given: "That we can retrieve the expts for project 274" //////////
        // This next section is the improved version, which goes directly from projects to experiments, rather than going through the assays explicitly.
        // Both approaches seem to produce the same results, but this alternate approach is more concise.

        final List<Long> cartProjectIdList = new ArrayList<Long>()
        cartProjectIdList.add(new Long(274))
        final Collection<Project> projects = this.combinedRestService.restProjectService.get(cartProjectIdList)
        final List<Experiment> allExperiments = []
        for (final Project project : projects) {
            final SearchResult<Experiment> searchResult = this.combinedRestService.searchResultByProject(project, Experiment.class)
            final Collection<Experiment> experiments = searchResult.searchResults
            allExperiments.addAll(experiments)
        }
        when: "We define an etag for a compound used in this project"  /////////////
        final List<Long> cartCompoundIdList = new ArrayList<Long>()
        cartCompoundIdList.add(new Long(5281847))
        Object etag = this.combinedRestService.restCompoundService.newETag((new Date()).toString(), cartCompoundIdList);


        then: "when we step through the value in the expt"    ////////

        int dataCount = 0
        for (Experiment experiment in allExperiments) {

            SearchResult<Value> experimentResult = this.combinedRestService.restExperimentService.activities(experiment, etag)
            dataCount = dataCount + experimentResult.count
        }

        // we expect to see some data, but we don't
        assert dataCount > 0   // this fails in V6, but not in V5
    }
}