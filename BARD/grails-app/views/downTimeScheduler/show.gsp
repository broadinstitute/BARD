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
    <title>View Down Time</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <g:if test="${downTimeScheduler}">
            <dl class="dl-horizontal">

                <dt>ID:</dt>
                <dd>
                    ${downTimeScheduler.id}
                </dd>
                <dt>Down Time - Date and Time:</dt>
                <dd>
                    ${downTimeScheduler.downTimeAsString}
                </dd>

                <dt>Display Value:</dt>
                <dd>
                    ${downTimeScheduler.displayValue}
                </dd>
                <dt>Created By:</dt>
                <dd>
                    ${downTimeScheduler.createdBy}
                </dd>

                <dt>Date Created:</dt>
                <dd>
                    <g:formatDate date="${downTimeScheduler.dateCreated}" format="MM/dd/yyyy HH:mm:ss"/>
                </dd>
            </dl>
        </g:if>
        <g:else>
            No Information Found
        </g:else>
        <br/>
    </div>
</div>
</body>
</html>
