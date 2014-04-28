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

<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalCompounds" id="totalCompounds" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.CompoundFacetForm]"/>
    <div class="span9">
    <g:if test="${params.max || params.offset}">
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}"
                        params='[searchString: "${searchString}", nhits: "${nhits ? nhits : 0}"]'/>
        </div>
    </g:if>
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
    <div align="right">
        <g:selectAllItemsInPage mainDivName="compounds"/>
    </div>

    <table class="table table-striped">
        <g:each var="compoundAdapter" in="${compoundAdapters}">
            <tr>
                <td style="min-width: 180px;">
                    <g:compoundOptions
                            sid="${compoundAdapter.pubChemCID}"
                            cid="${compoundAdapter.pubChemCID}"
                            smiles="${compoundAdapter.structureSMILES}"
                            name="${JavaScriptUtility.cleanup(compoundAdapter.name)}"
                            imageWidth="180"
                            imageHeight="150"/>
                </td>
                <td>
                    <h3>
                        <g:if test="${searchString}">
                            <g:link action="showCompound" id="${compoundAdapter.pubChemCID}"
                                    params='[searchString: "${searchString}"]'>
                                <g:if test="${compoundAdapter.name}">
                                    ${compoundAdapter.name} <small>(PubChem CID: ${compoundAdapter.pubChemCID})</small>
                                </g:if>
                                <g:else>
                                    PubChem CID: ${compoundAdapter.pubChemCID}
                                </g:else>
                            </g:link>
                        </g:if>
                        <g:else>
                            <g:link action="showCompound" id="${compoundAdapter.pubChemCID}">
                                <g:if test="${compoundAdapter.name}">
                                    ${compoundAdapter.name} <small>(PubChem CID: ${compoundAdapter.pubChemCID})</small>
                                </g:if>
                                <g:else>
                                    PubChem CID: ${compoundAdapter.pubChemCID}
                                </g:else>
                            </g:link>
                        </g:else>


                        <g:if test="${compoundAdapter.isDrug()}">
                            <span class="badge badge-success">Drug</span>
                        </g:if>
                        <g:elseif test="${compoundAdapter.isProbe()}">
                            <span class="badge badge-info">Probe</span>
                        </g:elseif>
                    </h3>
                    <g:saveToCartButton id="${compoundAdapter.pubChemCID}"
                                        name="${JavaScriptUtility.cleanup(compoundAdapter.name)}"
                                        type="${querycart.QueryItemType.Compound}"
                                        smiles="${compoundAdapter.getStructureSMILES()}"/>
                    <dl>
                        <g:if test="${compoundAdapter.highlight}">
                            <dt>Search Match:</dt>
                            <dd>${compoundAdapter.highlight}</dd>
                        </g:if>
                        <dt>Assays - Active vs Tested:</dt>
                        <dd>
                            <div class="activeVrsTested">
                                <div>
                                    <span class="badge badge-info">
                                        <g:link controller="molSpreadSheet" action="showExperimentDetails" style="color: white; text-decoration: underline"
                                                params="[cid: compoundAdapter.pubChemCID, transpose: true]">${compoundAdapter?.numberOfActiveAssays}</g:link>
                                         /${compoundAdapter?.numberOfAssays}</span>
                                </div>

                            </div>
                        </dd>

                        <dt>Scaffold Promiscuity Analysis:</dt>
                        <dd>
                            <div class="promiscuity"
                                 href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compoundAdapter.pubChemCID])}"
                                 id="${compoundAdapter.pubChemCID}_prom"></div>
                        </dd>
                    </dl>
                </td>
            </tr>
        </g:each>
    </table>

    <g:if test="${params.max || params.offset}">
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}"
                        params='[searchString: "${searchString}", nhits: "${nhits ? nhits : 0}"]'/>
        </div>
    </g:if>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>
</div>
