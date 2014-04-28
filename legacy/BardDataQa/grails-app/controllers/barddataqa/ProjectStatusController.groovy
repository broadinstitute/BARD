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

class ProjectStatusController {

    ProjectStatusService projectStatusService

    def index() {
        List<ProjectStatus> projectStatusList = projectStatusService.retrieveAllProjectStatusWithMetaInfoSorted()

        List<ProjectStatus> inProgressList = projectStatusList.findAll({ProjectStatus ps ->
            ps.qaStatus.id < QaStatus.broadDone
        })
        List<ProjectStatus> doneList = projectStatusList.findAll({ProjectStatus ps ->
            ps.qaStatus.id >= QaStatus.broadDone
        })

        [inProgressList: inProgressList, doneList: doneList, qaStatusList:  QaStatus.withCriteria({order("id")})]
    }

    def create(Long projectId, Long qaStatusId) {
        String errorMessage = projectStatusService.createNew(projectId, qaStatusId)

        if (errorMessage != null) {
            render(errorMessage)
        } else {
            redirect(action: 'index')
        }
    }

    def updateStatus(Long projectId, Long qaStatusId) {
        projectStatusService.updateQaStatus(projectId, qaStatusId)
        redirect(action: 'index')
    }

    def updateNotes(Long projectId, String notes) {
        if (notes.length() > 4000) {
            render("notes section must be 400 characters or less, it currently is:  ${notes.length()}")
        } else {
            projectStatusService.updateNotes(projectId, notes)
            redirect(action: 'index')
        }
    }

    def updateJiraIssues(JiraIssueCommand jiraIssueCommand) {
        if (jiraIssueCommand.addJiraIssue && jiraIssueCommand.addJiraIssue.length() > 20) {
            render("new jira issue must be 20 characters or less, it is currently:  ${jiraIssueCommand.addJiraIssue.length()}")
        } else {
            projectStatusService.updateJiraIssues(jiraIssueCommand)
            redirect(action: 'index')
        }
    }

    def updateQueueOrder(Long projectId, String queueOrderString) {
        projectStatusService.updateQueueOrder(projectId, queueOrderString)
        redirect(action: 'index')
    }
}

class JiraIssueCommand {
    Long projectId

    List<Long> deleteJiraIssueList

    String addJiraIssue
}
