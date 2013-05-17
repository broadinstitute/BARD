package barddataqa

import org.hibernate.SQLQuery

class MarginalProductService {

    private static final String datasetIdParam = "datasetId"
    private static final String onHoldDateParam = "onHoldDate"

    private static final String missingView = "bard_data_qa_dashboard.vw_ds_prjct_missing_aid"
    private static final String noMaasView = "bard_data_qa_dashboard.vw_ds_prjct_no_maas_aid"
    private static final String noRtaView = "bard_data_qa_dashboard.vw_ds_prjct_no_rta_aid"

    public static final Map<ResultMapProblemEnum, String> rmProblemViewMap = Collections.unmodifiableMap(
            [(ResultMapProblemEnum.duplicateResult) : "bard_data_qa_dashboard.vw_rm_dup_rslt_aid",
                    (ResultMapProblemEnum.resulttypeContextConflict) : "bard_data_qa_dashboard.vw_rm_cnflct_aid",
                    (ResultMapProblemEnum.relationshipProblem) : "bard_data_qa_dashboard.vw_rm_relat_prblm_aid",
                    (ResultMapProblemEnum.nothingMapped) : "bard_data_qa_dashboard.vw_rm_nothing_mapped",
                    (ResultMapProblemEnum.attributeOrValueNotElement) : "bard_data_qa_dashboard.vw_rm_attr_val_not_element",
                    (ResultMapProblemEnum.pubChemOutcomeOrScoreNotMapped) : "bard_data_qa_dashboard.vw_rm_pc_out_score_not_mapped"
            ]
    )

    private static final List<String> rmProblemViewList =
        Collections.unmodifiableList(new ArrayList<String>(rmProblemViewMap.values()))

    private static final String mpQueryBeforeRmProblem = """
select project_uid,
    total,
    missing,
    no_maas,
    no_rta,
    rm_prob,
    on_hold,
    case when total_prob = 0 then 0
      else total / (missing + no_maas + 0.2*no_rta + 0.2*rm_prob + on_hold)
      end marginal_product
from (
select project_uid, total, missing,no_maas,no_rta,rm_prob,on_hold,
  (missing + no_maas + no_rta + rm_prob + on_hold) total_prob
from (
select total.project_uid, total, nvl(missing,0) missing, nvl(no_maas,0) no_maas, nvl(no_rta,0) no_rta,
    nvl(rm_prob,0) rm_prob, nvl(on_hold,0) on_hold
  from
    (select project_uid, count(*) total from bard_data_qa_dashboard.vw_ds_prjct_not_summary_aid
      where dataset_id=:$datasetIdParam
      group by project_uid) total
  left join
    (select project_uid, count(*) missing  from $missingView
      where dataset_id=:$datasetIdParam
      group by project_uid) missing on missing.project_uid = total.project_uid
  left join
    (select project_uid, count(*) no_maas from $noMaasView
      where dataset_id=:$datasetIdParam
      group by project_uid) no_maas on no_maas.project_uid = total.project_uid
  left join
    (select project_uid, count(*) no_rta from $noRtaView
      where dataset_id = :$datasetIdParam
      group by project_uid) no_rta on no_rta.project_uid = total.project_uid
  left join
    (select project_uid, count(distinct aid) rm_prob from (
"""
    private static final String mpQueryAfterRmProblem = """
   ) group by project_uid) rm_prob on rm_prob.project_uid = total.project_uid
  left join
    (select vpaj.project_uid, count(*) on_hold from bard_data_qa_dashboard.vw_project_aid_join vpaj
      join bard_data_qa_dashboard.aid_info ai on ai.aid = vpaj.aid
      where ai.hold_until_date > :$onHoldDateParam
      group by vpaj.project_uid) on_hold on on_hold.project_uid = total.project_uid
)
)
order by marginal_product desc
"""

    private static final String indivResultMapProblemQueryPrefix = "select vpaj.project_uid, vrmp.aid from "
    private static final String indivResultMapProblemQuerySuffix = """vrmp
  join BARD_DATA_QA_DASHBOARD.vw_project_aid_join vpaj on vpaj.aid = vrmp.aid
  where vpaj.project_uid in
    (select project_uid from BARD_DATA_QA_DASHBOARD.dataset_project_uid where dataset_id = :$datasetIdParam)
"""

    private static final String projectUidParam = "projectUid"
    private static final String selectColumnsFrom = "select aid from "
    private static final String whereOrderBy = " where dataset_id=:$datasetIdParam and project_uid=:$projectUidParam order by aid"

    def sessionFactory

    ConvertObjectToTypeService convertObjectToTypeService

    List<MarginalProductForProject> calculate(Long datasetId, Date onHoldDate) {
        String queryString = "$mpQueryBeforeRmProblem ${buildRmProbQuery()} ${mpQueryAfterRmProblem}"

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
        query.setLong(datasetIdParam, datasetId)
        query.setDate(onHoldDateParam, onHoldDate)

        List<Object[]> rawList = query.list()

        List<MarginalProductForProject> result = new ArrayList<MarginalProductForProject>(rawList.size())

        for (Object[] row : rawList) {
            result.add(new MarginalProductForProject((Integer)row[0], (Integer)row[1], (Integer)row[2], (Integer)row[3],
                    (Integer)row[4], (Integer)row[5], (Integer)row[6], (Double)row[7]))
        }

        return result
    }

    static String buildRmProbQuery() {
        List<String> rmProblemQueryList = new LinkedList<String>()

        for (String rmProbView : rmProblemViewList) {
            rmProblemQueryList.add("$indivResultMapProblemQueryPrefix $rmProbView $indivResultMapProblemQuerySuffix")
        }

        return rmProblemQueryList.join("union ")
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
    List<Integer> findAidsThatAreMissing(Long datasetId, Integer projectUid) {
        return findAidsThatNeed(datasetId, projectUid, missingView)
    }

    Map<ResultMapProblemEnum, List<Integer>> findAidsWithResultMapProblem(Integer projectUid) {
        Map<ResultMapProblemEnum, List<Integer>> result = new TreeMap<ResultMapProblemEnum, List<Integer>>()

        for (ResultMapProblemEnum resultMapProblemEnum : rmProblemViewMap.keySet()) {
            String view = rmProblemViewMap.get(resultMapProblemEnum)

            String queryString = """select distinct aid from $view where aid in
            (select aid from bard_data_qa_dashboard.vw_project_aid_join where project_uid = :$projectUidParam) order by aid"""

            SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
            query.setInteger(projectUidParam, projectUid)

            List<Integer> aidList = convertObjectToTypeService.convert(query.list())

            if (aidList.size() > 0) {
                result.put(resultMapProblemEnum, aidList)
            }
        }

        return result
    }
}


class MarginalProductForProject {
    Integer projectUid
    Integer totalAidCount
    Integer missingCount
    Integer countThatNeedMaas
    Integer countThatNeedRta
    Integer countWitheResultMapProblem
    Integer countOnHold
    Double marginalProduct

    MarginalProductForProject(Integer projectUid, Integer totalAidCount, Integer missingCount, Integer countThatNeedMaas,
                              Integer countThatNeedRta, Integer countWitheResultMapProblem,
                              Integer countOnHold, Double marginalProduct) {

        this.projectUid = projectUid
        this.totalAidCount = totalAidCount
        this.missingCount = missingCount
        this.countThatNeedMaas = countThatNeedMaas
        this.countThatNeedRta = countThatNeedRta
        this.countWitheResultMapProblem = countWitheResultMapProblem
        this.countOnHold = countOnHold
        this.marginalProduct = marginalProduct
    }
}

