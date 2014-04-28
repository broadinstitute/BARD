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
  order by pci.value_display
"""


    String createNew(Long projectId, Long qaStatusId) {
        ProjectStatus alreadyExistsProjectStatus = ProjectStatus.findById(projectId)

        if (alreadyExistsProjectStatus != null) {
            return "Project Status already exists for project ID ${projectId}"
        } else {
            if (projectIdExistsInCapProduction(projectId)) {
                QaStatus qaStatus = QaStatus.findById(qaStatusId)

                ProjectStatus projectStatus = new ProjectStatus(projectId, qaStatus)
                projectStatus.save()

                if (!projectStatus.save()) {
                    StringBuilder builder = new StringBuilder()
                    projectStatus.errors.each {builder.append(it).append("  <br/>")}
                } else {
                    return null
                }
            } else {
                return "Project does not exist in CAP - ID:  ${projectId}"
            }
        }
    }

    void updateQaStatus(Long projectId, Long qaStatusId) {
        ProjectStatus projectStatus = ProjectStatus.findById(projectId)

        QaStatus qaStatus = QaStatus.findById(qaStatusId)

        projectStatus.qaStatus = qaStatus

        saveAndPrintErrorMessagesIfNeeded(projectStatus)
    }

    void updateNotes(Long projectId, String notes) {
        ProjectStatus projectStatus = ProjectStatus.findById(projectId)

        projectStatus.notes = notes

        saveAndPrintErrorMessagesIfNeeded(projectStatus)
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

                if (!projectStatusJiraIssue.save()) {
                    projectStatusJiraIssue.errors.each {println(it)}
                }
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

            saveAndPrintErrorMessagesIfNeeded(projectStatus)
        }
    }

    String updateQueueOrder(Long projectId, String queueOrderString) {
        ProjectStatus projectStatus = ProjectStatus.findById(projectId)

        queueOrderString = queueOrderString.trim()

        if (queueOrderString != null && ! queueOrderString.equals("")) {
            if (queueOrderString.isNumber()) {
                projectStatus.queueOrder = Double.valueOf(queueOrderString)
            } else {
                return "Entered queue order for project ID ${projectId} is not a number. Entered value:  $queueOrderString"
            }
        } else {
            projectStatus.queueOrder = null
        }

        saveAndPrintErrorMessagesIfNeeded(projectStatus)
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

        Collections.sort(result, new ProjectStatusComparator())

        return result
    }

    static void saveAndPrintErrorMessagesIfNeeded(ProjectStatus projectStatus) {
        projectStatus.save()
        if (!projectStatus.save()) {
            projectStatus.errors.each {println(it)}
        }
    }

    static class ProjectStatusComparator implements Comparator<ProjectStatus> {
        int compare(ProjectStatus o1, ProjectStatus o2) {

            if ((null == o1.queueOrder && null == o2.queueOrder) || o1.queueOrder.equals(o2.queueOrder)) {
                if (o1.laboratoryName != null && o2.laboratoryName != null) {
                    final int labNameCompare = o1.laboratoryName.compareTo(o2.laboratoryName)

                    if (labNameCompare == 0) {
                        return o1.id.compareTo(o2.id)
                    } else {
                        return labNameCompare
                    }
                } else {
                    if (o1.laboratoryName != null) {
                        return -1
                    } else if (o2.laboratoryName != null) {
                        return 1
                    } else {
                        return o1.id.compareTo(o2.id)
                    }
                }
            } else {
                if (o1.queueOrder != null && o2.queueOrder != null) {
                    return o1.queueOrder.compareTo(o2.queueOrder)
                } else {
                    return o1.queueOrder != null ? -1 : 1
                }
            }
        }
    }
}
