package barddataqa

import org.hibernate.Query

class DatasetProjectUidService {

    private static final String datasetIdParam = "datasetId"
    private static final String queryString = "select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id = :$datasetIdParam"

    def sessionFactory

    def convertObjectToTypeService

    List<Integer> lookupProjectUidForDataset(long datasetId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setLong(datasetIdParam, datasetId)

        List<Integer> result = convertObjectToTypeService.convert(query.list())

        return result
    }
}
