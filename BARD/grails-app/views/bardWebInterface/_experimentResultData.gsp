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
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${preview}">
    <h2 style="background-color:green;">PREVIEW OF Experiment: ${tableModel?.additionalProperties?.experimentName}</h2>
</g:if>
<g:else>
    <h2>Experiment: ${tableModel?.additionalProperties?.experimentName}</h2>
</g:else>




<g:render template="/experiment/experimentReferences"
          model="[experiment: capExperiment, excludedLinks: ['bardWebInterface.showExperiment']]"/>

<div class="row-fluid">
<g:if test="${tableModel.data}">
    <g:if test="${!preview}">
        <div class="row-fluid ">
            <div id="histogramHere" class="span12"></div>
        </div>
    </g:if>


    <div class="row-fluid">
    <g:hiddenField name="paginationUrl"
                   id="paginationUrl"/> %{--Used to hold the pagination url, if a paging link has been clicked--}%
    <div class="pagination offset2">

        <g:paginate
                total="${totalNumOfCmpds}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>

    <div id="resultData">

        <g:render template="experimentResultRenderer"
                  model="[tableModel: tableModel, landscapeLayout: true, innerBorder: innerBorder]"/>
    </div>

    <div class="pagination offset2">
        <g:paginate
                total="${totalNumOfCmpds}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
</g:if>
<g:else>
    <p class="text-info"><i
            class="icon-warning-sign"></i> No experimental data found
    </p>
</g:else>
</div>

