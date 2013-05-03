package barddataqa

import org.hibernate.SQLQuery


class MarginalProductService {

    ConvertObjectToTypeService convertObjectToTypeService

    private static final Set<Integer> datasetIdParameterIndexSet = [0,1,3,4,5] as Set<Integer>
    private static final Set<Integer> holdUntilDateParameterIndexSet = [2,6] as Set<Integer>


    private static final String[] baseQueryStringArray = ["""
select project_uid, total, ready, diff, marginal_product, diff_maas, diff_rta, diff_hold from
  (select total.project_uid, total, ready, (total - ready) diff,
    case when total = ready then 0
      else round((total / ((total - ready_maas) + ((total - ready_rta)/5) + ((total - ready_hold)/2.5))),1)
      end marginal_product,
    (total - ready_maas) diff_maas, (total - ready_rta) diff_rta, (total - ready_hold) diff_hold
  from
    (select vpaj.project_uid, count(vpaj.aid) total from bard_data_qa_dashboard.vw_project_aid_join vpaj
        join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
        where vpaj.project_uid in (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=""",

            """)
          and ai.is_summary_aid <> 'y'
        group by vpaj.project_uid)
      total
    join
      (--query for aid's in each project that have maas and result type annotations and are not on hold
        select vpaj.project_uid, count(vpaj.aid) ready from bard_data_qa_dashboard.vw_project_aid_join vpaj
          join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
          where vpaj.project_uid in (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=""",

            """)
            and ai.is_summary_aid <> 'y'
            and vpaj.aid in (select aid from bard_data_qa_dashboard.maas_spreadsheet_aid)
            and vpaj.aid in (select aid from bard_data_qa_dashboard.result_type_annotation where status <> 'on hold')
            and ai.hold_until_date < '""",

            """'
          group by vpaj.project_uid)
      ready on ready.project_uid = total.project_uid
    join
      (--query for aid's in each project that have maas
        select vpaj.project_uid, count(vpaj.aid) ready_maas from bard_data_qa_dashboard.vw_project_aid_join vpaj
          join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
          where vpaj.project_uid in (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=""",

            """)
            and ai.is_summary_aid <> 'y'
            and vpaj.aid in (select aid from bard_data_qa_dashboard.maas_spreadsheet_aid)
          group by vpaj.project_uid)
      ready_maas on ready_maas.project_uid = total.project_uid
    join
      (--query for aid's in each project that have result type mapping
        select vpaj.project_uid, count(vpaj.aid) ready_rta from bard_data_qa_dashboard.vw_project_aid_join vpaj
          join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
          where vpaj.project_uid in (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=""",

            """)
            and ai.is_summary_aid <> 'y'
            and vpaj.aid in (select aid from bard_data_qa_dashboard.result_type_annotation where status not in ('on hold'))
          group by vpaj.project_uid)
      ready_rta on ready_rta.project_uid = total.project_uid
    join
      (--query for aid's in each project that are not on hold
        select vpaj.project_uid, count(vpaj.aid) ready_hold from bard_data_qa_dashboard.vw_project_aid_join vpaj
          join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
          where vpaj.project_uid in (select project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=""",

            """)
            and ai.is_summary_aid <> 'y'
            and ai.hold_until_date < '""",

            """'
          group by vpaj.project_uid)
      ready_hold on ready_hold.project_uid = total.project_uid)
  order by marginal_product desc, total desc, project_uid
"""]


    private static final String projectUidParamName = "projectUid"

    private static final String needMaasQueryString = """
select vpaj.aid from bard_data_qa_dashboard.vw_project_aid_join vpaj
  join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
  where ai.is_summary_aid='n' and vpaj.project_uid=:$projectUidParamName and vpaj.aid not in
    (select aid from bard_data_qa_dashboard.maas_spreadsheet_aid)
"""

    private static final String needRtaQueryString = """
select vpaj.aid from bard_data_qa_dashboard.vw_project_aid_join vpaj
  join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
  where ai.is_summary_aid='n' and vpaj.project_uid=:$projectUidParamName and vpaj.aid not in
    (select aid from bard_data_qa_dashboard.result_type_annotation where status <> 'on hold')
"""

    def sessionFactory

    /**
     * calculate marginal product of work for projects within dataset identified by provided datasetId
     * Based on AID's that are not summary AID's
     * @param datasetId
     * @return
     */
    List<MarginalProductForProject> runMarginalProductCalculationForDataset(long datasetId) {

        StringBuilder builder = new StringBuilder()

        String dateString = (new Date()).format("dd-MMM-yyyy")
        for (int i = 0; i < baseQueryStringArray.length; i++) {
            builder.append(baseQueryStringArray[i])

            if (datasetIdParameterIndexSet.contains(i)) {
                builder.append(datasetId)
            } else if (holdUntilDateParameterIndexSet.contains(i)) {
                builder.append(dateString)
            }
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(builder.toString())

        List<MarginalProductForProject> result = new LinkedList<MarginalProductForProject>()

        for (Object[] row : query.list()) {
            result.add(new MarginalProductForProject(projectUid: (Integer)row[0], totalAids: (Integer)row[1],
                    readyAids: (Integer)row[2], difference: (Integer)row[3], marginalProduct: (Double)row[4],
                    countThatNeedMaas: (Integer)row[5], countThatNeedRta: (Integer)row[6], countOnHold: (Integer)row[7]))
        }

        return result
    }

    List<Integer> findAidsNeedMaasForProject(Long projectUid) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(needMaasQueryString)
        query.setLong(projectUidParamName, projectUid)

        List<Integer> result = convertObjectToTypeService.convert(query.list())

        return result
    }

    List<Integer> findAidsNeedRtaForProject(Long projectUid) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(needRtaQueryString)
        query.setLong(projectUidParamName, projectUid)

        List<Integer> result = convertObjectToTypeService.convert(query.list())

        return result
    }
}


class MarginalProductForProject {
    Integer projectUid
    Integer totalAids
    Integer readyAids
    Integer difference
    Double marginalProduct
    Integer countThatNeedMaas
    Integer countThatNeedRta
    Integer countOnHold
}
