package barddataqa

import org.hibernate.Query

class DatasetAidService {

    private static final String datasetIdParam = "datasetId"
        private static final String queryString = """
select aid from bard_data_qa_dashboard.vw_project_aid_join
  where project_uid in
    (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id = :$datasetIdParam)
  order by aid"""

    def sessionFactory

    def convertObjectToTypeService

    List<Integer> lookupAidForDataset(long datasetId) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setLong(datasetIdParam, datasetId)

        List<Integer> result = convertObjectToTypeService.convert(query.list())

        return result
    }
}
