<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<%@ page import="grails.converters.JSON" %>
<g:hiddenField name="totalCompounds" id="totalCompounds" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.CompoundFacetForm]"/>
    <div class="span9">
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
    <table class="table">
        <g:each var="compoundAdapter" in="${compoundAdapters}">
            <tr>
                <td style="min-width: 180px;">
                    <g:compoundOptions sid="${compoundAdapter.pubChemCID}" cid="${compoundAdapter.pubChemCID}" smiles="${compoundAdapter.structureSMILES}" imageWidth="180" imageHeight="150"/>
                </td>
                <td>
                    <h3>
                        <g:link action="showCompound" id="${compoundAdapter.pubChemCID}"
                                params='[searchString: "${searchString}"]'>
                            <g:if test="${compoundAdapter.name}">
                                ${compoundAdapter.name} <small>(PubChem CID: ${compoundAdapter.pubChemCID})</small>
                            </g:if>
                            <g:else>
                                PubChem CID: ${compoundAdapter.pubChemCID}
                            </g:else>
                        </g:link>
                    </h3>
                    <g:saveToCartButton id="${compoundAdapter.pubChemCID}"
                                        name="${JavaScriptUtility.cleanup(compoundAdapter.name)}"
                                        type="${querycart.QueryItemType.Compound}"
                                        smiles="${compoundAdapter.getStructureSMILES()}"
                                        numActive="${compoundAdapter.numberOfActiveAssays}"
                                        numAssays="${compoundAdapter.numberOfAssays}"
                    />
                    <dl>
                        <g:if test="${compoundAdapter.highlight}">
                            <dt>Search Match (highlighted in bold):</dt>
                            <dd>&hellip;${compoundAdapter.highlight}&hellip;</dd>
                        </g:if>
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

    <div class="pagination">
        <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}", nhits: "${nhits ? nhits : 0}"]'/>
    </div>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>
</div>
