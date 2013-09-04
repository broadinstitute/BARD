<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>Queue Order</th>
        <th>Project ID</th>
        <th>QA Status of Project</th>
        <th>New QA Status</th>
        <th>Project Name</th>
        <th>Laboratory Name</th>
        <th width="140">related JIRA issues</th>
        <th width="400">notes</th>
        <th>edit notes</th>
    </tr>

    <g:each in="${psList}" var="projectStatus">
        <tr>
            <td>
                <g:form name="changeQueueOrderForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'updateQueueOrder']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    <g:textField name="queueOrderString" value="${projectStatus.queueOrder}" size="4"/><br/>
                </g:form>
            </td>
            <td>
                <a href="https://bard.broadinstitute.org/BARD/project/show/${projectStatus.id}" target="_blank">${projectStatus.id}</a>
            </td>
            <td>${projectStatus.qaStatus.name}</td>
            <td>
                <g:form name="changeStatusForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'updateStatus']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    <g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"
                              value="${projectStatus.qaStatus.id}" onchange="document.forms['changeStatusForm${projectStatus.id}'].submit()"/>
                </g:form>
            </td>
            <td>
                <a href="https://bard.broadinstitute.org/BARD/project/show/${projectStatus.id}" target="_blank">${projectStatus.projectName}</a>
            </td>
            <td>${projectStatus.laboratoryName}</td>
            <td>
                <g:form name="editJiraIssuesForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'updateJiraIssues']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    Select JIRA issues to delete:<br/>
                    <g:each in="${projectStatus.jiraIssueSet}" var="jiraIssue" status="i">
                        <a href="https://jira.broadinstitute.org/browse/${jiraIssue.jiraIssueId?.toUpperCase()}" target="_blank">${jiraIssue.jiraIssueId}</a>
                        <g:checkBox name="deleteJiraIssueList" value="${jiraIssue.id}" checked="${false}"/>
                        <br/>
                    </g:each>
                    <br/>
                    JIRA issue to add:
                    <g:textField name="addJiraIssue" size="20"/>
                    <g:submitButton id="editJiraIssueSubmitButton" name="Submit"/>
                </g:form>
            </td>
            <td>${projectStatus.notes?.replace("\n","<br/>")}</td>
            <td>
                <g:form name="editNotesForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'updateNotes']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    <g:textArea name="notes" rows="10" cols="40" value="${projectStatus.notes}"/>
                    <g:submitButton id="editNotesSubmitButton" name="Submit"/>
                </g:form>
            </td>
        </tr>
    </g:each>

</table>