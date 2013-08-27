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