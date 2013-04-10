package barddataqa

import org.hibernate.Query

class DatasetAidService {

    private static final String datasetIdParam = "datasetId"
    private static final String loadOrderParam = "loadOrder"
    private static final String queryString = """
select distinct aid from bard_data_qa_dashboard.vw_project_aid_join
  where project_uid in
    (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id = :$datasetIdParam)
  and aid not in
    (select aid from bard_data_qa_dashboard.vw_project_aid_join where project_uid in
      (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id in
        (select id from bard_data_qa_dashboard.dataset where load_order < :$loadOrderParam)))
  order by aid"""

    def sessionFactory

    def convertObjectToTypeService

    List<Integer> lookupAidForDataset(long datasetId) {
        Dataset dataset = Dataset.findById(datasetId)

        Query query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setLong(datasetIdParam, datasetId)
        query.setInteger(loadOrderParam, dataset.loadOrder)

        List<Integer> result = convertObjectToTypeService.convert(query.list())

        return result
    }
}
