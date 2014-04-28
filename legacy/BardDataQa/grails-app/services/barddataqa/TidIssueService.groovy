/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

    private static final String columnsString = columns.join(", ")

    private final static String duplicateResultTypeQueryString = """
select orm.${columns.join(",orm.")}
from bard_data_qa_dashboard.vw_data_mig_result_map orm
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
from bard_data_qa_dashboard.vw_data_mig_result_map rm where
not exists (select 1 from bard_data_qa_dashboard.vw_data_mig_result_map r where r.aid = rm.aid and
r.qualifiertid = rm.tid)
and not exists (select 1 from bard_data_qa_dashboard.vw_data_mig_result_map r where r.aid = rm.aid and
r.contexttid = rm.tid and r.contexttid <> rm.tid)
and resulttype is not null
and resulttype not in ('standard deviation', 'confidence limit 95%')
and aid = :$aidParam
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
where orm.aid = :$aidParam
order by orm.aid, tid
"""


    def sessionFactory

    public List<Object[]> findTidsWithResultMapProblem(ResultMapProblemEnum problem, Integer aid) {

        String queryString = null

        if (problem != ResultMapProblemEnum.duplicateResult) {
            String view = MarginalProductService.rmProblemViewMap.get(problem)

            queryString = """
select $columnsString from bard_data_qa_dashboard.vw_data_mig_result_map
    where tid in (select tid from $view where aid = :$aidParam)
    and aid = :$aidParam
    order by tid
"""
        } else {
            queryString = duplicateResultTypeQueryString
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setInteger(aidParam, aid)

        return query.list()
    }
}
