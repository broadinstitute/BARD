<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 7/22/13
  Time: 4:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="barddataqa.QaStatus; barddataqa.ProjectStatus" contentType="text/html;charset=UTF-8" %>
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

<h2>Broad work in progress</h2>
<g:render template="projectStatusTable" model="[psList: inProgressList]"/>

<h2>Broad work finished</h2>
<g:render template="projectStatusTable" model="[psList: doneList]"/>

<br/>
<h2>Track a new project</h2>
<g:form name="newProjectForm" url="[controller: 'projectStatus', action: 'create']">
    Project ID:<g:textField name="projectId" size="5"/> <br/>
    QA status:<g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"/><br/>
    <g:submitButton name="addProjectSubmitButton" value="Add Project"/>
</g:form>
</body>
</html>