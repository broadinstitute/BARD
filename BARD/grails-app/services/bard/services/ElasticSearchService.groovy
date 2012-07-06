package bard.services

import org.codehaus.groovy.grails.web.json.JSONObject

class ElasticSearchService {

    QueryExecutorService queryExecutorService
    def grailsApplication

    def searchForAssay(Integer assayId) {
        String baseUrl = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        queryExecutorService.executeGetRequestJSON("${baseUrl}/assays/assay/${assayId}", null)
    }
}
