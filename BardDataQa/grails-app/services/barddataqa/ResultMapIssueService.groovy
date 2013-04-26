package barddataqa

class ResultMapIssueService {

    public static final String[] columns = ["AID", "Project_UID", "Dataset ID", "Dataset Name"]

    private static final String queryString = """
select rm_prob.aid, vpaj.project_uid, dpu.dataset_id, ds.name from
    (select aid from
    (select aid, count(*) from (
    select aid, resulttype, seriesno, concentration, concentrationunit,
    attribute1, value1, attribute2, value2, count(*)
    from bard_data_qa.vw_data_mig_result_map rm where
    not exists (select 1 from bard_data_qa.vw_data_mig_result_map r where r.aid = rm.aid and
    r.qualifiertid = rm.tid)
    and not exists (select 1 from bard_data_qa.vw_data_mig_result_map r where r.aid = rm.aid and
    r.contexttid = rm.tid and r.contexttid <> rm.tid)
    and resulttype is not null
    and resulttype not in ('standard deviation', 'confidence limit 95%')
    group by aid, resulttype, seriesno, concentration, concentrationunit,
    attribute1, value1, attribute2, value2 having count(*) > 1
    ) group by aid) x ) rm_prob
  join bard_data_qa_dashboard.vw_project_aid_join vpaj on vpaj.aid=rm_prob.aid
  join bard_data_qa_dashboard.dataset_project_uid dpu on dpu.project_uid=vpaj.project_uid
  join bard_data_qa_dashboard.dataset ds on ds.id = dpu.dataset_id
  order by dpu.dataset_id, vpaj.project_uid, rm_prob.aid
"""

    def sessionFactory

    List<Object[]> findAidsWithDuplicateResultTypesInResultMap() {
        def session = sessionFactory.getCurrentSession()

        return session.createSQLQuery(queryString).list()
    }
}
