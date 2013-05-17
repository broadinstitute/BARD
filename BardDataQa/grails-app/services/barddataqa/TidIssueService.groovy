package barddataqa

import org.hibernate.SQLQuery

class TidIssueService {
    public static final String[] columns = ["AID",
    "TID",
    "TIDNAME",
    "PARENTTID",
    "RELATIONSHIP",
    "QUALIFIERTID",
    "RESULTTYPE",
    "STATS_MODIFIER",
    "CONTEXTTID",
    "CONTEXTITEM",
    "CONCENTRATION",
    "CONCENTRATIONUNIT",
    "DATE_CREATE",
    "LAST_UPDATED",
    "MODIFIED_BY",
    "PANELNO",
    "ATTRIBUTE1",
    "VALUE1",
    "EXCLUDED_POINTS_SERIES_NO",
    "ATTRIBUTE2",
    "VALUE2",
    "SERIESNO"]

    private final static String aidParam = "aid"

    private static final String resultMapAbbrev = "rm"

    private static final String columnsString = columns.join(", ")

    def sessionFactory

    public List<Object[]> findTidsWithResultMapProblem(ResultMapProblemEnum problem, Integer aid) {
        String view = MarginalProductService.rmProblemViewMap.get(problem)

        String queryString = """
select $columnsString from bard_data_qa_dashboard.vw_data_mig_result_map
    where tid in (select tid from $view where aid = :$aidParam)
    and aid = :$aidParam
    order by tid
"""

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setInteger(aidParam, aid)

        return query.list()
    }
}
