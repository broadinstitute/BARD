package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.substances.Substance
import bard.core.rest.spring.substances.SubstanceResult
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.rest.spring.util.SubstanceSearchType
import spock.lang.Shared

@Unroll
class SubstanceRestServiceIntegrationSpec extends IntegrationSpec {
    SubstanceRestService substanceRestService
    @Shared
    List<Long> SIDS_FOR_TESTING = [136349013, 136349014, 136349015, 842899]
    @Shared
    List<Long> EIDS_FOR_TESTING = [11795, 10790]
    @Shared
    Long CID_FOR_TESTING = 2722

    void "getSubstanceById sid - 6820697"() {
        given:
        Long sid = 6820697
        Long expectedCID = 600
        when:
        final Substance substance = substanceRestService.getSubstanceById(sid)
        then:
        assert substance
        assert substance.getId() == sid
        assert substance.getSid() == substance.getId()
        assert substance.getCid() == expectedCID
        assert substance.getDepRegId() == "6914582"
        assert substance.getSourceName() == "ChemDB"
        assert substance.getUrl() == "http://cdb.ics.uci.edu/CHEMDB/Web/cgibin/ChemicalDetailWeb.py?chemical_id=6914582"
        assert !substance.getPatentIds()
        assert substance.getSmiles() == "C(C1C(C(C(C(=O)O1)O)O)O)OP(=O)(O)O"
        assert substance.getDeposited() == "2005-09-16"
        assert substance.getUpdated() == "2005-09-16"
        assert substance.getResourcePath() == "/substances/6820697"

    }

    void "getResourceContext"() {

        when:
        final String resourceContext = substanceRestService.getResourceContext()
        then:
        assert resourceContext == RestApiConstants.SUBSTANCES_RESOURCE
    }


    void "findExperimentData - Should return activities"() {
        when:
        final List<Activity> activities = substanceRestService.findExperimentData(sids, bardExperimentIds)
        then:
        assert activities
        for (Activity activity : activities) {
            assert activity
        }
        where:
        label                        | sids             | bardExperimentIds
        "With sids and exptdata ids" | SIDS_FOR_TESTING | EIDS_FOR_TESTING
    }

    void "findSubstancesByCid Expanded Search"() {
        when:
        List<Substance> substances = substanceRestService.findSubstancesByCidExpandedSearch(CID_FOR_TESTING)
        then:
        assert substances
    }

    void "findSubstancesByCid Not expanded"() {
        when:
        List<Long> substances = substanceRestService.findSubstancesByCid(CID_FOR_TESTING)
        then:
        assert substances
        assert substances.contains(new Long(70319))
    }

    void "findExperimentDataBySid"() {
        given:
        final Long sid = SIDS_FOR_TESTING.get(0)
        when:
        ExperimentData experimentData = substanceRestService.findExperimentDataBySid(sid)
        then:
        assert experimentData
        assert experimentData.activities
        for (Activity activity : experimentData.activities) {
            assert activity
        }
    }

    void "findExperimentsBySid"() {
        given:
        final Long sid = SIDS_FOR_TESTING.get(0)
        when:
        ExperimentSearchResult experimentSearchResult = substanceRestService.findExperimentsBySid(sid)
        then:
        assert experimentSearchResult
        assert experimentSearchResult.experiments
    }


    void "findSubstances"() {
        given:
        final SearchParams searchParam = new SearchParams(skip: 0, top: 10)
        final SubstanceSearchType substanceSearchType = SubstanceSearchType.MLSMR
        when:
        SubstanceResult substanceResult = substanceRestService.findSubstances(substanceSearchType, searchParam)
        then:
        assert substanceResult
        assert substanceResult.substances
        assert substanceResult.substances.size() == 10

    }

}

