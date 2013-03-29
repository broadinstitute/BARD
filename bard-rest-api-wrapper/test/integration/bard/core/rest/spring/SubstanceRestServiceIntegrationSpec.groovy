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
    List<Long> SIDS_FOR_TESTING = [26726047, 125082031, 135378232, 85799539]
    @Shared
    List<Long> EIDS_FOR_TESTING = [1, 2]
    @Shared
    Long CID_FOR_TESTING = 2382353

    void "getSubstanceById sid - 6820697"() {
        given:
        Long sid = 26726047
        Long expectedCID = 647652
        when:
        final Substance substance = substanceRestService.getSubstanceById(sid)
        then:
        assert substance
        assert substance.getId() == sid
        assert substance.getSid() == substance.getId()
        assert substance.getCid() == expectedCID
        assert substance.getDepRegId() == "MLS000889589"
        assert substance.getSourceName() == "MLSMR"
//        assert substance.getUrl() == "http://cdb.ics.uci.edu/CHEMDB/Web/cgibin/ChemicalDetailWeb.py?chemical_id=6914582"
        assert !substance.getPatentIds()
        assert substance.getSmiles() == "C1CN(C2=C1C=C(C=C2)C3=CSC(=N3)NC(=O)CN4CCOCC4)S(=O)(=O)C5=CC=CC=C5"
        assert substance.getDeposited() == "2007-10-11"
        assert substance.getUpdated() == "2012-03-01"
        assert substance.getResourcePath() == "/substances/26726047"

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
        assert substances.contains(new Long(2796946))
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

