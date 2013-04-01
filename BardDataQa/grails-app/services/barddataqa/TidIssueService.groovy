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

    private final static String queryFromString = """
from bard_data_qa.result_map orm
join (
select aid,
  nvl(resulttype, 'NULL') resulttype,
  nvl(''||seriesno, 'NULL') seriesno,
  nvl(''||concentration, 'NULL') concentration,
  nvl(concentrationunit, 'NULL') concentrationunit,
  nvl(attribute1,'NULL') attribute1,
  nvl(value1,'NULL') value1,
  nvl(attribute2, 'NULL') attribute2,
  nvl(value2, 'NULL') value2
from bard_data_qa.result_map rm where
not exists (select 1 from bard_data_qa.result_map r where r.aid = rm.aid and
r.qualifiertid = rm.tid)
and not exists (select 1 from bard_data_qa.result_map r where r.aid = rm.aid and
r.contexttid = rm.tid and r.contexttid <> rm.tid)
and resulttype is not null
and resulttype not in ('standard deviation', 'confidence limit 95%')
and aid = 624332
group by aid, resulttype, seriesno, concentration, concentrationunit,
attribute1, value1, attribute2, value2 having count(*) > 1)
sub on (nvl(orm.resulttype, 'NULL') = sub.resulttype
  and nvl(''||orm.seriesno, 'NULL') = sub.seriesno
  and nvl(''||orm.concentration, 'NULL') = sub.concentration
  and nvl(orm.concentrationunit, 'NULL') = sub.concentrationunit
  and nvl(orm.attribute1,'NULL') = sub.attribute1
  and nvl(orm.value1,'NULL') = sub.value1
  and nvl(orm.attribute2, 'NULL') = sub.attribute2
  and nvl(orm.value2, 'NULL') = sub.value2
  )
where orm.aid in (:aidSet)
order by orm.aid, tid
"""

    def sessionFactory
    def resultMapIssueService

    List<Object[]> findTidIssues() {
        List<Object[]> resultMapProblems = resultMapIssueService.findAidsWithDuplicateResultTypesInResultMap()

        Set<Integer> aidSet = new HashSet<Integer>()
        for (Object[] problem : resultMapProblems) {
            if (problem[0]) {
                aidSet.add(Integer.valueOf(problem[0].toString()))
            }
        }

        final String queryString = "select orm.${columns.join(",orm.")} $queryFromString"

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setParameterList("aidSet", aidSet)

        return query.list()
    }
}
