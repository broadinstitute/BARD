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

@Unroll
class SubstanceRestServiceIntegrationSpec extends IntegrationSpec {
    SubstanceRestService substanceRestService


    void "getSubstanceById sid - 6820697"() {
        given:
        Long sid = 6820697
        when:
        final Substance substance = substanceRestService.getSubstanceById(sid)
        then:
        assert substance
        assert substance.getId() == 6820697
        assert substance.getSid() == substance.getId()
        assert substance.getCid() == 600
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
        label                        | sids                        | bardExperimentIds
        "With sids and exptdata ids" | [103050164, 103050165, 333] | [1417, 1418]
    }

    void "findSubstancesByCid"() {
        given:
        Long cid = 2722
        when:
        List<Substance> substances = substanceRestService.findSubstancesByCid(cid)
        then:
        assert substances
    }

    void "findExperimentDataBySid"() {
        given:
        final Long sid = 103050164
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
        final Long sid = 103050164
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
        SubstanceResult substanceResult = substanceRestService.findSubstances(substanceSearchType,searchParam)
        then:
        assert substanceResult
        assert substanceResult.substances
        assert substanceResult.substances.size() == 10

    }

}

