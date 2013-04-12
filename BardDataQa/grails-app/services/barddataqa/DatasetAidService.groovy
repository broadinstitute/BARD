package barddataqa

import org.hibernate.Query

class DatasetAidService {

    private static final String datasetIdParam = "datasetId"

    private static final String baseQueryString = """
select vda.aid from bard_data_qa_dashboard.vw_dataset_aid vda
  join bard_data_qa_dashboard.aid_info ai on ai.aid = vda.aid
  where vda.dataset_id = :${datasetIdParam}
    and ai.is_summary_aid
    """

    private static final String notSummaryAidClause = " = 'n' "
    private static final String isSummaryAidClause = " = 'y' "
    private static final String unknownSummaryAidClause = " is null "

    private static final String orderByClause = "order by aid"

    def sessionFactory

    def convertObjectToTypeService

    DatasetAids lookupAidForDataset(long datasetId) {
        DatasetAids datasetAids = new DatasetAids()

        Dataset dataset = Dataset.findById(datasetId)

        Query query = sessionFactory.getCurrentSession().createSQLQuery(baseQueryString + notSummaryAidClause + orderByClause)
        query.setLong(datasetIdParam, datasetId)
        datasetAids.notSummaryAidList = convertObjectToTypeService.convert(query.list())

        query = sessionFactory.getCurrentSession().createSQLQuery(baseQueryString + isSummaryAidClause + orderByClause)
        query.setLong(datasetIdParam, datasetId)
        datasetAids.isSummaryAidList = convertObjectToTypeService.convert(query.list())

        query = sessionFactory.getCurrentSession().createSQLQuery(baseQueryString + unknownSummaryAidClause + orderByClause)
        query.setLong(datasetIdParam, datasetId)
        datasetAids.unknownSummaryAidList = convertObjectToTypeService.convert(query.list())

        return datasetAids
    }
}


class DatasetAids {
    List<Integer> notSummaryAidList
    List<Integer> isSummaryAidList
    List<Integer> unknownSummaryAidList

    int calculateTotalCount() {
        return notSummaryAidList.size() + isSummaryAidList.size() + unknownSummaryAidList.size()
    }
}