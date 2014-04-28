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

<%@ page import="bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalProjects" id="totalProjects" value="${nhits}"/>

<div data-role="header">
    <h1>Projects</h1>
</div><!-- /header -->

<div data-role="content">
    <g:if test="${nhits > 0}">
        <ul class="unstyled results">
            <g:each var="projectAdapter" in="${projectAdapters}">
                <li>
                %{--<h3>--}%
                    <g:link controller="project" action="show" id="${projectAdapter.capProjectId}"
                            params='[searchString: "${searchString}"]'>${projectAdapter.name} <small>(Project ID: ${projectAdapter.capProjectId})</small></g:link>
                %{--</h3>--}%
                    <g:if test="${projectAdapter?.getNumberOfExperiments()}">
                        <dl>
                            <dt>Number Of Experiments:</dt>
                            <dd><span class="badge badge-info">${projectAdapter.getNumberOfExperiments()}</span></dd>
                        </dl>
                    </g:if>
                </li>
            </g:each>
        </ul>

        <div id="paginateBar" class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </g:if>
    <g:else>
        <div class="tab-message">No search results found</div>
    </g:else>
</div>
