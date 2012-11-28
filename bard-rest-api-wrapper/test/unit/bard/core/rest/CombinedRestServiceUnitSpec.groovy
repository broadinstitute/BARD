package bard.core.rest

import bard.core.interfaces.SearchResult
import com.fasterxml.jackson.databind.ObjectMapper
import grails.test.mixin.TestFor
import jdo.JSONNodeTestHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/1/12
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@TestFor(CombinedRestService)
class CombinedRestServiceUnitSpec extends Specification {
    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String TESTED_ASSAY_NODE = JSONNodeTestHelper.TESTED_ASSAY_NODE

    void setup() {
        RESTSubstanceService restSubstanceService = new RESTSubstanceService("base")
        RESTCompoundService restCompoundService = new RESTCompoundService("base")
        RESTProjectService restProjectService = new RESTProjectService("base")
        RESTAssayService restAssayService = new RESTAssayService("base")
        RESTExperimentService restExperimentService = new RESTExperimentService("base")

        this.service.restCompoundService = restCompoundService
        this.service.restAssayService = restAssayService
        this.service.restExperimentService = restExperimentService
        this.service.restProjectService = restProjectService
        this.service.restSubstanceService = restSubstanceService
    }

    void "extractedTestAssays #label"() {
        when:
        List<Assay> assays = service.extractedTestAssays(rootNode)
        then:
        service.restAssayService.getEntity(_, _) >> {}
        assert assays.isEmpty() == isNodeEmpty
        where:
        label                                   | rootNode                           | isNodeEmpty
        "Root Node has Full Properties"         | mapper.readTree(TESTED_ASSAY_NODE) | false
        "Root Node Does not Contain Collection" | mapper.readTree("{\"abc\":233}")   | true
        "Null Root Node"                        | null                               | true


    }

    void "searchResultBySubstance"() {
        when:
        SearchResult<Project> project = this.service.searchResultBySubstance(new Substance(), Project.class)
        then:
        assert !project
    }

    void "searchResultByCompound with Exceptions"() {
        given:
        final Compound compound = new Compound()
        when:
        this.service.searchResultByCompound(compound, Substance.class)
        then:
        thrown(IllegalArgumentException)

    }
    /**
     * if you search for the this assay via the REST api, you get one experiment associated with the assay
     */
    void "test searchResultByAssay Illegal Argument excpetion"() {
        given:
        final Assay assay = new Assay()
        when:
        service.searchResultByAssay(assay, Compound.class)
        then:
        thrown(IllegalArgumentException)

        where:
        label                           | adid
        "Search with a single assay id" | 644
    }

    void "searchResultByExperiment with Exceptions"() {
        given:
        final Experiment experiment = new Experiment()
        when:
        this.service.searchResultByExperiment(experiment, Substance.class)
        then:
        thrown(IllegalArgumentException)

    }

    void "searchResultByProject with Exceptions"() {
        given:
        final Project project = new Project()
        when:
        this.service.searchResultByProject(project, Substance.class)
        then:
        thrown(IllegalArgumentException)

    }

}
