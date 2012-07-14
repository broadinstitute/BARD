import bardelasticsearch.ElasticSearchAssayIndexService

//get the service from the context
ElasticSearchAssayIndexService elasticSearchAssayIndexService = ctx.getBean("elasticSearchAssayIndexService")
assert elasticSearchAssayIndexService

//the name of the elasticsearch index for assays
final String indexName = "assays"
final String indexType ="assay"


//url to root of NCGC REST API
final String ncgcRootURL = elasticSearchAssayIndexService.ncgcRootURL
assert ncgcRootURL
//the relative url of the NCGC REST API services - /bard/rest/v1/
final String ncgcRelativeURL = elasticSearchAssayIndexService.ncgcRelativeURL
assert ncgcRelativeURL

//construct url to NCGC to fetch assays
final String ncgcAssaysURL = "${ncgcRootURL}${ncgcRelativeURL}assays/"
assert ncgcAssaysURL

//url to root of elasticsearch server
final String elasticSearchURL = elasticSearchAssayIndexService.elasticSearchURL
assert elasticSearchURL

//println elasticSearchURL
//create the mapping for assays
//elasticSearchAssayIndexService.createIndexAndMappingForAssayType(indexName, indexType)
//now index assays from ncgc
elasticSearchAssayIndexService.indexAssays(indexName, indexType,ncgcAssaysURL)




    
    