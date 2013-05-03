package barddataqa

class ResultMapIssueService {

    private static final String countDuplicateQueryString = """
select count(*) from
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
    ) group by aid) x
"""

    private static final String countResultTypeAndContextCollisionQuery = """
select count(distinct aid) from bard_data_qa.vw_data_mig_result_map where RESULTTYPE is not null and
(contexttid is not null and contexttid <> tid)
"""


    public static final Map<String, String> nameCountQueryMap = [duplicates:countDuplicateQueryString,
            collisions:countResultTypeAndContextCollisionQuery]

    public static final String[] duplicateResultTypeColumns = ["AID", "Project_UID", "Dataset ID", "Dataset Name", "Load Order"]

    private static final String duplicateResultTypeQueryString = """
select rm_prob.aid, vpaj.project_uid, dpu.dataset_id, ds.name, ds.load_order from
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
  order by ds.load_order, vpaj.project_uid, rm_prob.aid
"""

    def sessionFactory

    List<Object[]> findAidsWithDuplicateResultTypesInResultMap() {
        def session = sessionFactory.getCurrentSession()

        return session.createSQLQuery(duplicateResultTypeQueryString).list()
    }

    Map<String, Integer> calculateIssueTypeCounts() {
        def session = sessionFactory.getCurrentSession()

        Map<String, Integer> result = new HashMap<String, Integer>()
        for (String name : nameCountQueryMap.keySet()) {
            String queryString = nameCountQueryMap.get(name)

            int value = (int)session.createSQLQuery(queryString).list().get(0)

            result.put(name, value)
        }

        return result
    }


}
