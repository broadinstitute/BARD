package barddataqa

import org.hibernate.SQLQuery

class CheckForMissingAidsService {

    public static final String[] headerArray = ["Dataset ID", "AID"]

    private static final String baseQueryString = """
select vda.dataset_id, vda.aid from bard_data_qa_dashboard.vw_dataset_aid vda
  left join bard_data_qa_dashboard.aid_info ai on ai.aid = vda.aid
  left join bard_data_qa_dashboard.vw_data_mig_external_reference dm on dm.ext_assay_ref = ('aid=' || vda.aid)
  where ext_assay_ref is null
    and (ai.aid is null or ai.is_summary_aid = 'n')
"""

    private static final String queryString = """
$baseQueryString
order by vda.dataset_id, vda.aid
"""

    private static final String countQueryString = """
select count(*) from
($baseQueryString)
"""

    def sessionFactory

    List<Object[]> findAidsThatAreMissingFromDatasets() {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)

        return query.list()
    }

    int countAidsThatAreMissingFromDatasets() {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(countQueryString)

        return (int)query.list().get(0)
    }
}
