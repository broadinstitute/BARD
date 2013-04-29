package barddataqa

import org.hibernate.SQLQuery

class CheckForMissingAidsService {

    public static final String[] headerArray = ["Dataset ID", "AID"]

    private static final String queryString = """
select vda.dataset_id, vda.aid from bard_data_qa_dashboard.vw_dataset_aid vda
  left join bard_data_qa_dashboard.aid_info ai on ai.aid = vda.aid
  left join bard_data_qa.vw_data_mig_external_reference dm on dm.ext_assay_ref = ('aid=' || vda.aid)
  where ext_assay_ref is null
    and (ai.aid is null or ai.is_summary_aid = 'n')
  order by vda.dataset_id, vda.aid
"""

    def sessionFactory

    List<Object[]> findAidsThatAreMissingFromDatasets() {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)

        return query.list()
    }
}
