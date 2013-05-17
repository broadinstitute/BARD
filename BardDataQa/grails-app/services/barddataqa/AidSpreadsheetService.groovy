package barddataqa

class AidSpreadsheetService {
    public static final String[] headerArray = ["AID", "MAA Spreadsheet filename", "Annotater", "Center", "Is summary AID?",
            "Dataset ID"]

    private static final String queryString = """
select msa.aid, ms.filename, ms.person, ai.center, ai.is_summary_aid, vda.dataset_id
  from bard_data_qa_dashboard.maas_spreadsheet_aid msa
  join bard_data_qa_dashboard.maas_spreadsheet ms on ms.id = msa.maas_spreadsheet_id
  join bard_data_qa_dashboard.aid_info ai on ai.aid = msa.aid
  left join bard_data_qa_dashboard.vw_dataset_aid vda on vda.aid = msa.aid
  order by msa.aid, ms.filename
"""

    def sessionFactory

    List<Object[]> findAidSpreadsheetInfo() {
        return sessionFactory.getCurrentSession().createSQLQuery(queryString).list()
    }
}
