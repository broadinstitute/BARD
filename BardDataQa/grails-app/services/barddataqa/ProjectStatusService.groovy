package barddataqa

import org.hibernate.SQLQuery

class ProjectStatusService {

    def sessionFactory

    private static final String projectIdParam = "projectId"
    private static final String projectIdExistsInCapProductionQueryString = """
select count(*) from bard_cap_prod.project where project_id = :$projectIdParam
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

        projectStatus.save()
    }

    boolean projectIdExistsInCapProduction(Long projectId) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(projectIdExistsInCapProductionQueryString)
        query.setLong(projectIdParam, projectId)

        Integer count = (Integer)query.list().get(0)

        return (1 == count)
    }
}
