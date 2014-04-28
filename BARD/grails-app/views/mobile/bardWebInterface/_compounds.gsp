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
<%@ page import="grails.converters.JSON" %>
<g:hiddenField name="totalCompounds" id="totalCompounds" value="${nhits}"/>

<div data-role="content">
    <g:if test="${nhits > 0}">
        <table class="table">
            <g:each var="compoundAdapter" in="${compoundAdapters}">
                <tr>
                    <td>
                        <img class="structureThumbnail" alt="${compoundAdapter.structureSMILES}"
                             title="${compoundAdapter.name}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: compoundAdapter.structureSMILES, width: 180, height: 150])}"/>
                    </td>
                    <td>
                        <g:link action="showCompound" id="${compoundAdapter.pubChemCID}"
                                params='[searchString: "${searchString}"]'>
                            <g:if test="${compoundAdapter.name}">
                                ${compoundAdapter.name} <small>(PubChem CID: ${compoundAdapter.pubChemCID})</small>
                            </g:if>
                            <g:else>
                                PubChem CID: ${compoundAdapter.pubChemCID}
                            </g:else>
                        </g:link>
                        <dl>
                            <g:if test="${compoundAdapter.isDrug()}">
                                <p><span class="badge badge-success">Drug</span></p>
                            </g:if>
                            <g:elseif test="${compoundAdapter.isProbe()}">
                                <p><span class="badge badge-info">Probe</span></p>
                            </g:elseif>
                            <dt>Assays - Active vs Tested:</dt>
                            <dd>
                                <div class="activeVrsTested">
                                    <div>
                                        <span class="badge badge-info">${compoundAdapter?.numberOfActiveAssays} / ${compoundAdapter?.numberOfAssays}</span>
                                    </div>

                                </div>
                            </dd>
                        </dl>
                    </td>
                </tr>
            </g:each>
        </table>

        <g:if test="${params.max || params.offset}">
            <div id="paginateBar" class="pagination">
                <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]' data-ajax="false"/>
            </div>
        </g:if>
    </g:if>
    <g:else>
        <div class="tab-message">No search results found</div>
    </g:else>
</div>
