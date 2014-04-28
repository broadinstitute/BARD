%{-- Copyright (c) 2014, The Broad Institute
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
 --}%

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
