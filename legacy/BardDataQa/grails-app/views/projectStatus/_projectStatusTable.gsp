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
