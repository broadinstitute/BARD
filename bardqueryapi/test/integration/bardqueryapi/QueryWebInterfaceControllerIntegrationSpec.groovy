package bardqueryapi

import elasticsearchplugin.ESXCompound
import elasticsearchplugin.ElasticSearchService
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

class QueryWebInterfaceControllerIntegrationSpec extends IntegrationSpec  {

    BardWebInterfaceController bardWebInterfaceController
    ElasticSearchService elasticSearchService
    QueryExecutorInternalService queryExecutorInternalService


    void setup() {
    }


    void tearDown() {

    }



    @Unroll("Use controller search")
    def "test elastic search APID search"() {

        given:
        assert bardWebInterfaceController
        assert elasticSearchService
        bardWebInterfaceController.elasticSearchService = elasticSearchService

        when:
        def  aidQuerySpecifier =  "644"
        bardWebInterfaceController.request.addParameter("searchString",aidQuerySpecifier)
        def foo = bardWebInterfaceController.search()


        then:
        assert bardWebInterfaceController
        assert bardWebInterfaceController.modelAndView.model.assays.size() == 1
        assert bardWebInterfaceController.modelAndView.model.compounds.size() >= 206
    }

    @Unroll("Use controller search")
    def "test elastic search single CID search"() {

        given:
        assert bardWebInterfaceController
        assert elasticSearchService
        bardWebInterfaceController.elasticSearchService = elasticSearchService

        when:
        def  cidQuerySpecifier =  "174"
        bardWebInterfaceController.request.addParameter("searchString",cidQuerySpecifier)
        bardWebInterfaceController.search()


        then:
        assert bardWebInterfaceController
        assert bardWebInterfaceController.modelAndView.model.assays.size() >= 1
        assert bardWebInterfaceController.modelAndView.model.compounds.size() == 1
    }


    @Unroll("Use controller search to retrieve all APIDs associated with a set of compounds")
    def "test elastic search multiple CID search retrieving the combined assays for both compounds"() {

        given:
        assert bardWebInterfaceController
        assert elasticSearchService
        bardWebInterfaceController.elasticSearchService = elasticSearchService

        when:
        def  cidQuerySpecifier =  "174 833970"
        bardWebInterfaceController.request.addParameter("searchString",cidQuerySpecifier)
        bardWebInterfaceController.search()


        then:
        assert bardWebInterfaceController
        assert bardWebInterfaceController.modelAndView.model.compounds.size() == 2
        def esxCompoundList = new ArrayList<ESXCompound>()
        for (  ESXCompound eSXCompound in  bardWebInterfaceController.modelAndView.model.compounds )
            esxCompoundList <<  eSXCompound
        List<Integer> apidList = ESXCompound.combinedApids(esxCompoundList)
        assert  apidList.size() > 200

    }



    @Unroll("Use controller search")
    def "test elastic search target search"() {

        given:
        assert bardWebInterfaceController
        assert elasticSearchService
        bardWebInterfaceController.elasticSearchService = elasticSearchService

        when:
        def  cidQuerySpecifier =  "RHO"
        bardWebInterfaceController.request.addParameter("searchString",cidQuerySpecifier)
        bardWebInterfaceController.search()


        then:
        assert bardWebInterfaceController
        assert bardWebInterfaceController.modelAndView.model.assays.size() >= 114

    }

}
