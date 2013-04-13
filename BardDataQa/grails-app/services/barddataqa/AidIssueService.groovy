package barddataqa

import org.hibernate.Hibernate
import org.hibernate.SQLQuery
import org.hibernate.type.Type

class AidIssueService {

    private static final AidIssueColumn[] aidIssueColumns = [new AidIssueColumn("ID", "id", Hibernate.INTEGER),
            new AidIssueColumn("AID", "aid", Hibernate.INTEGER),
            new AidIssueColumn("Issue Type", "issue_type", Hibernate.STRING),
            new AidIssueColumn("Sort Order", "sort_order", Hibernate.FLOAT),
            new AidIssueColumn("Assigned To", "assigned_to", Hibernate.STRING),
            new AidIssueColumn("Disocvered During", "discovered_in", Hibernate.STRING),
            new AidIssueColumn("Discovered By", "discovered_by", Hibernate.STRING),
            new AidIssueColumn("Status", "status", Hibernate.STRING),
            new AidIssueColumn("Notes", "notes", Hibernate.STRING)]

    private static final String queryString = """
select ai.id,
ai.aid,
ait.name issue_type,
ai.sort_order,
ai.assigned_to,
dqp.name discovered_in,
ai.discovered_by,
ai.status,
ai.notes
from bard_data_qa_dashboard.aid_issue ai
join bard_data_qa_dashboard.aid_issue_type ait on ait.id = ai.issue_type
join bard_data_qa_dashboard.data_qa_process dqp on dqp.id = ai.discovered_in
order by ai.status desc, ai.sort_order, ai.id"""

    def sessionFactory

    List<Object[]> serviceMethod() {
        def session = sessionFactory.getCurrentSession()

        SQLQuery query = session.createSQLQuery(queryString)
        for (AidIssueColumn aidIssueColumn : aidIssueColumns) {
            query.addScalar(aidIssueColumn.sqlQueryName, aidIssueColumn.hibernateType)
        }

        List<Object[]> result = query.list()

        return result
    }

    List<String> getColumns() {
        List<String> result = new ArrayList<String>(aidIssueColumns.size())
        for (AidIssueColumn aidIssueColumn : aidIssueColumns) {
            result.add(aidIssueColumn.displayName)
        }

        return result
    }
}

class AidIssueColumn {
    String displayName
    String sqlQueryName
    Type hibernateType

    AidIssueColumn(String displayName, String sqlQueryName, Type hibernateType) {
        this.displayName = displayName
        this.sqlQueryName = sqlQueryName
        this.hibernateType = hibernateType
    }
}
