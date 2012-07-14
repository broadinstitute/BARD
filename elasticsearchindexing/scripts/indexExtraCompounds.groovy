import bardelasticsearch.ElasticSearchCompoundsIndexService
//Reindex compound records that failed indexing from the "indexCompounds.groovy" script

//get the service from the context
final ElasticSearchCompoundsIndexService elasticSearchCompoundsIndexService = ctx.getBean("elasticSearchCompoundsIndexService")
assert elasticSearchCompoundsIndexService


//The following compounds failed indexing when we run the indexCompounds.groovy script
//We attempt to reindex those compounds in this script
//
def compoundsThatFailedIndexing =
    [
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3785328',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/8069391',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/24980288',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/5728937',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/360849',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1768056',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/20953585',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3811993',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/860095',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/540523',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2965100',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/24980673',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2972420',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2345101',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3239310',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/5807939',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1870175',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/9368524',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1244098',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/24982555',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/628325',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3240168',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/22553804',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1246027',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16033878',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/886876',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2983498',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/25102699',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2985820',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/4898345',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16034266',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1917121',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2274467',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3975251',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/6880338',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/23723021',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16248655',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16012692',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/23723343',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1945308',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/6883499',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/9550502',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3139937',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/721008',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1960218',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2997722',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16284289',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1283831',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/9551319',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/23724166',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/6104612',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/9551698',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1989738',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/1289870',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/728524',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16759035',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2826621',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/42601240',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/902008',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/4075726',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/6172754',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16014262',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2397017',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/6223846',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16759636',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/906845',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/4088240',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/24671701',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2999122',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/2403716',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3243911',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16014639',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/915546',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3754587',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/17756868',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/6253566',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/648800',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16015449',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/16324459',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3089085',
//            'http://assay.nih.gov:8080/bard/rest/v1/compounds/3121126'
    ]
int counter = 1

//log the exceptions here
def exceptions = []
for (String compoundURL : compoundsThatFailedIndexing) {
    final String cid = compoundURL.replaceAll("http://assay.nih.gov:8080/bard/rest/v1/compounds/", "")
    final String threadName = "Thread_${counter}"
    try {
        elasticSearchCompoundsIndexService.indexCompounds(cid, compoundURL, threadName)
        println compoundURL
    } catch (Exception ee) {
        exceptions.add(compoundURL)
        println ee.message
    }
    ++counter
}
println("Failed indexing: " + exceptions)

   