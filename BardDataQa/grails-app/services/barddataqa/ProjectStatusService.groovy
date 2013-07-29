package barddataqa

import org.hibernate.SQLQuery

class ProjectStatusService {

    def sessionFactory

    private static final String projectIdParam = "projectId"
    private static final String projectIdExistsInCapProductionQueryString = """
select count(*) from bard_cap_prod.project where project_id = :$projectIdParam
"""

    private static final String projectNameAndLabNameQueryString = """
select pci.value_display, p.project_name from bard_cap_prod.project p
  left join bard_cap_prod.project_context pc on pc.project_id = p.project_id
  left join bard_cap_prod.project_context_item pci on pci.project_context_id = pc.project_context_id and pci.attribute_id = 559
  where p.project_id = :$projectIdParam
"""


    String createNew(ProjectStatusCommand projectStatusCommand) {
        ProjectStatus alreadyExistsProjectStatus = ProjectStatus.findById(projectStatusCommand.projectId)

        if (alreadyExistsProjectStatus != null) {
            return "Project Status already exists for project ID ${projectStatusCommand.projectId}"
        } else {
            if (projectIdExistsInCapProduction(projectStatusCommand.projectId)) {
                QaStatus qaStatus = QaStatus.findById(projectStatusCommand.qaStatusId)

                ProjectStatus projectStatus = new ProjectStatus(projectStatusCommand.projectId, qaStatus)
                projectStatus.save()

                return null
            } else {
                return "Project does not exist in CAP - ID:  ${projectStatusCommand.projectId}"
            }
        }
    }

    void updateExisting(ProjectStatusCommand projectStatusCommand) {
        ProjectStatus projectStatus = ProjectStatus.findById(projectStatusCommand.projectId)

        QaStatus qaStatus = QaStatus.findById(projectStatusCommand.qaStatusId)

        projectStatus.qaStatus = qaStatus

        projectStatus.notes = projectStatusCommand.projectStatusNotes

        projectStatus.save()
    }

    void updateJiraIssues(JiraIssueCommand jiraIssueCommand) {
        if (jiraIssueCommand.addJiraIssue ||
                (jiraIssueCommand.deleteJiraIssueList && jiraIssueCommand.deleteJiraIssueList.size() > 0)) {

            ProjectStatus projectStatus = ProjectStatus.findById(jiraIssueCommand.projectId)

            if (jiraIssueCommand.addJiraIssue) {
                ProjectStatusJiraIssue projectStatusJiraIssue = new ProjectStatusJiraIssue()
                projectStatusJiraIssue.projectStatus = projectStatus
                projectStatusJiraIssue.jiraIssueId = jiraIssueCommand.addJiraIssue
                projectStatus.jiraIssueSet.add(projectStatusJiraIssue)

                projectStatusJiraIssue.save()
            }

            if (jiraIssueCommand.deleteJiraIssueList && jiraIssueCommand.deleteJiraIssueList.size() > 0) {
                Iterator<ProjectStatusJiraIssue> iter = projectStatus.jiraIssueSet.iterator()
                while(iter.hasNext()) {
                    ProjectStatusJiraIssue current = iter.next()

                    if (jiraIssueCommand.deleteJiraIssueList.contains(current.id)) {
                        iter.remove()
                        current.delete()
                    }
                }
            }

            projectStatus.save()
        }
    }

    boolean projectIdExistsInCapProduction(Long projectId) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(projectIdExistsInCapProductionQueryString)
        query.setLong(projectIdParam, projectId)

        Integer count = (Integer)query.list().get(0)

        return (1 == count)
    }

    void addProjectNameAndLabName(ProjectStatus projectStatus) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(projectNameAndLabNameQueryString)
        query.setLong(projectIdParam, projectStatus.id)

        List result = query.list()

        if (result.size() > 0) {
            Object[] row = (Object[])query.list().get(0)

            projectStatus.laboratoryName = row[0]
            projectStatus.projectName = row[1]
        }
    }

    List<ProjectStatus> retrieveAllProjectStatusWithMetaInfoSorted() {
        List<ProjectStatus> result = ProjectStatus.findAll()

        for (ProjectStatus projectStatus : result) {
            addProjectNameAndLabName(projectStatus)
        }

        Collections.sort(result, new LabNameIdComparator())

        return result
    }

    static class LabNameIdComparator implements Comparator<ProjectStatus> {
        int compare(ProjectStatus o1, ProjectStatus o2) {
            final int labNameCompare = o1.laboratoryName.compareTo(o2.laboratoryName)

            if (labNameCompare == 0) {
                return o1.id.compareTo(o2.id)
            } else {
                return labNameCompare
            }
        }
    }
}
