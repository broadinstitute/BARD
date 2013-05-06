package barddataqa

import org.hibernate.SQLQuery

class MarginalProductService {

    private static final String datasetIdParam = "datasetId"
    private static final String onHoldDateParam = "onHoldDate"

    private static final String noMaasView = "bard_data_qa_dashboard.vw_ds_prjct_no_maas_aid"
    private static final String noRtaView = "bard_data_qa_dashboard.vw_ds_prjct_no_rta_aid"
    private static final String rmConflictView = "bard_data_qa_dashboard.vw_ds_prjct_rm_cnflct_aid"
    private static final String duplicateResultView = "bard_data_qa_dashboard.vw_ds_prjct_rm_dup_rslt_aid"
    private static final String rmRelationshipProblemView = "bard_data_qa_dashboard.vw_ds_prjct_rm_relat_prblm_aid"

    private static final String marginalProductQueryString = """
select project_uid,
       total,
       no_maas,
       no_rta,
       rm_cnflct,
       rm_dup_rslt,
       rm_relat_prblm,
       on_hold,
       case when total_prob = 0 then 0
        else total / (no_maas+0.2*no_rta+0.2*rm_cnflct+0.2*rm_dup_rslt+0.2*rm_relat_prblm+on_hold)
        end marginal_product
  from (
select project_uid, total, no_maas,no_rta,rm_cnflct,rm_dup_rslt,rm_relat_prblm,on_hold,
  no_maas+no_rta+rm_cnflct+rm_dup_rslt+rm_relat_prblm+on_hold total_prob
from (
select total.project_uid, total, nvl(no_maas,0) no_maas, nvl(no_rta,0) no_rta,
    nvl(rm_cnflct,0) rm_cnflct, nvl(rm_dup_rslt,0) rm_dup_rslt, nvl(rm_relat_prblm,0) rm_relat_prblm,
    nvl(on_hold,0) on_hold
  from
    (select project_uid, count(*) total from bard_data_qa_dashboard.vw_ds_prjct_not_summary_aid
      where dataset_id=:$datasetIdParam
      group by project_uid) total
  left join
    (select project_uid, count(*) no_maas from $noMaasView
      where dataset_id=:$datasetIdParam
      group by project_uid) no_maas on no_maas.project_uid = total.project_uid
  left join
    (select project_uid, count(*) no_rta from $noRtaView
      where dataset_id=:$datasetIdParam
      group by project_uid) no_rta on no_rta.project_uid = total.project_uid
  left join
    (select project_uid, count(*) rm_cnflct from $rmConflictView
      where dataset_id=:$datasetIdParam
      group by project_uid) rm_cnflct on rm_cnflct.project_uid = total.project_uid
  left join
    (select project_uid, count(*) rm_dup_rslt from $duplicateResultView
      where dataset_id=:$datasetIdParam
      group by project_uid) rm_dup_rslt on rm_dup_rslt.project_uid = total.project_uid
  left join
    (select project_uid, count(*) rm_relat_prblm from $rmRelationshipProblemView
      where dataset_id=:$datasetIdParam
      group by project_uid) rm_relat_prblm on rm_relat_prblm.project_uid = total.project_uid
  left join
    (select vpaj.project_uid, count(*) on_hold from bard_data_qa_dashboard.vw_project_aid_join vpaj
      join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
      where ai.hold_until_date > :$onHoldDateParam
      group by vpaj.project_uid) on_hold on on_hold.project_uid = total.project_uid
)
)
order by marginal_product desc
    """

    private static final String projectUidParam = "projectUid"
    private static final String selectColumnsFrom = "select aid from "
    private static final String whereOrderBy = " where dataset_id=:$datasetIdParam and project_uid=:$projectUidParam order by aid"

    def sessionFactory

    ConvertObjectToTypeService convertObjectToTypeService

    List<MarginalProductForProject> calculate(Long datasetId, Date onHoldDate) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(marginalProductQueryString)
        query.setLong(datasetIdParam, datasetId)
        query.setDate(onHoldDateParam, onHoldDate)

        List<Object[]> rawList = query.list()

        List<MarginalProductForProject> result = new ArrayList<MarginalProductForProject>(rawList.size())

        for (Object[] row : rawList) {
            result.add(new MarginalProductForProject((Integer)row[0], (Integer)row[1], (Integer)row[2], (Integer)row[3],
                    (Integer)row[4], (Integer)row[5], (Integer)row[6], (Integer)row[7], (Double)row[8]))
        }

        return result
    }


    private List<Integer> findAidsThatNeed(Long datasetId, Integer projectUid, String view) {
        String queryString = "$selectColumnsFrom $view $whereOrderBy"
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setLong(datasetIdParam, datasetId)
        query.setInteger(projectUidParam, projectUid)

        List<Integer> aidList = convertObjectToTypeService.convert(query.list())
        return aidList
    }

    List<Integer> findAidsThatNeedMaas(Long datasetId, Integer projectUid) {
        return findAidsThatNeed(datasetId, projectUid, noMaasView)
    }
    List<Integer> findAidsThatNeedRta(Long datasetId, Integer projectUid) {
        return findAidsThatNeed(datasetId, projectUid, noRtaView)
    }
    List<Integer> findAidsThatHaveRmConflict(Long datasetId, Integer projectUid) {
        return findAidsThatNeed(datasetId, projectUid, rmConflictView)
    }
    List<Integer> findAidsThatHaveDuplicateResult(Long datasetId, Integer projectUid) {
        return findAidsThatNeed(datasetId, projectUid, duplicateResultView)
    }
    List<Integer> findAidsThatHaveRmRelationshipProblem(Long datasetId, Integer projectUid) {
        return findAidsThatNeed(datasetId, projectUid, rmRelationshipProblemView)
    }
}


class MarginalProductForProject {
    Integer projectUid
    Integer totalAidCount
    Integer countThatNeedMaas
    Integer countThatNeedRta
    Integer countWitheResultMapConflictBetweenResultTypeAndContextItem
    Integer countWithResultMapDuplicateResult
    Integer countWithResultMapRelationshipProblem
    Integer countOnHold
    Double marginalProduct

    MarginalProductForProject(Integer projectUid, Integer totalAidCount, Integer countThatNeedMaas,
                              Integer countThatNeedRta, Integer countWitheResultMapConflictBetweenResultTypeAndContextItem,
                              Integer countWithResultMapDuplicateResult, Integer countWithResultMapRelationshipProblem,
                              Integer countOnHold, Double marginalProduct) {

        this.projectUid = projectUid
        this.totalAidCount = totalAidCount
        this.countThatNeedMaas = countThatNeedMaas
        this.countThatNeedRta = countThatNeedRta
        this.countWitheResultMapConflictBetweenResultTypeAndContextItem = countWitheResultMapConflictBetweenResultTypeAndContextItem
        this.countWithResultMapDuplicateResult = countWithResultMapDuplicateResult
        this.countWithResultMapRelationshipProblem = countWithResultMapRelationshipProblem
        this.countOnHold = countOnHold
        this.marginalProduct = marginalProduct
    }
}

