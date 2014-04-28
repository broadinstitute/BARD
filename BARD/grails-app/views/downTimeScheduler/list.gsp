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

<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="main"/>
    <title>History of Scheduled Down Time</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <b>History of Scheduled Down Time</b>
        <g:if test="${downTimeSchedulerList}">

            <table id="downTime" class="table table-striped table-hover table-bordered">
                <thead>

                <tr>
                    <th data-sort="int">ID</th><th data-sort="int">Down Time</th><th
                        data-sort="string">Display Value</th> <th data-sort="string">Created By</th><th
                        data-sort="int">Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${downTimeSchedulerList}" var="downTimeScheduler">
                    <tr>
                        <td><g:link controller="downTimeScheduler" action="show" id="${downTimeScheduler.id}">
                            ${downTimeScheduler.id}
                        </g:link>
                        </td>
                        <td data-sort-value="${downTimeScheduler.downTimeAsLong}">${downTimeScheduler.downTimeAsString}</td>
                        <td>${downTimeScheduler.displayValue}</td>
                        <td style="line-height: 150%"><p>${downTimeScheduler.createdBy}</p></td>
                        <td data-sort-value="${downTimeScheduler.dateCreated?.time}"><g:formatDate
                                date="${downTimeScheduler.dateCreated}" format="MM/dd/yyyy HH:mm:ss"/></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
        <br/>
    </div>
</div>
</body>
</html>
