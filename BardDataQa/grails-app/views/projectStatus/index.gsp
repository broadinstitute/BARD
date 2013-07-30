<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 7/22/13
  Time: 4:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>QA Status of Projects in CAP</title>
</head>
<body>
<h1>QA Status of Projects in CAP</h1>
<h2>Track a new project</h2>
<g:form name="newProjectForm" url="[controller: 'projectStatus', action: 'create']">
    Project ID:<g:textField name="projectId" size="5"/> <br/>
    QA status:<g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"/><br/>
    <g:submitButton name="addProjectSubmitButton" value="Add Project"/>
</g:form>
<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>Project ID</th>
        <th>QA Status of Project</th>
        <th>New QA Status</th>
        <th>Project Name</th>
        <th>Laboratory Name</th>
        <th width="140">related JIRA issues</th>
        <th width="400">notes</th>
        <th>edit notes</th>
    </tr>

    <g:each in="${projectStatusList}" var="projectStatus">
        <tr>
            <td>${projectStatus.id}</td>
            <td>${projectStatus.qaStatus.name}</td>
            <td>
                <g:form name="changeStatusForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'updateStatus']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    <g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"
                              value="${projectStatus.qaStatus.id}" onchange="document.forms['changeStatusForm${projectStatus.id}'].submit()"/>
                </g:form>
            </td>
            <td>${projectStatus.projectName}</td>
            <td>${projectStatus.laboratoryName}</td>
            <td>
                <g:form name="editJiraIssuesForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'updateJiraIssues']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    Select JIRA issues to delete:<br/>
                    <g:each in="${projectStatus.jiraIssueSet}" var="jiraIssue" status="i">
                        ${jiraIssue.jiraIssueId}
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
<br/>
<h2>Track a new project</h2>
<g:form name="newProjectForm" url="[controller: 'projectStatus', action: 'create']">
    Project ID:<g:textField name="projectId" size="5"/> <br/>
    QA status:<g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"/><br/>
    <g:submitButton name="addProjectSubmitButton" value="Add Project"/>
</g:form>


</body>
</html>