<%@ page import="bard.core.CompoundValues; bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
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
                        <img alt="${compoundAdapter.structureSMILES}" title="${compoundAdapter.name}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compoundAdapter.structureSMILES, width: 180, height: 150])}"/>
                    </td>
                    <td>
                        <h3>
                            <g:link action="showCompound" id="${compoundAdapter.pubChemCID}" target="_blank">
                                <g:if test="${compoundAdapter.name}">
                                    ${compoundAdapter.name} <small>(PubChem CID: ${compoundAdapter.pubChemCID})</small>
                                </g:if>
                                <g:else>
                                    PubChem CID: ${compoundAdapter.pubChemCID}
                                </g:else>
                            </g:link>
                        </h3>
                        ${compoundAdapter.compound.getValue('MolecularType')}
                        <a href="#" class="addCompoundToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(compoundAdapter.name)}"
                                 data-cart-id="${compoundAdapter.pubChemCID}" data-cart-smiles="${compoundAdapter.getStructureSMILES()}">
                            Save for later analysis
                        </a>
                        <dl>
                            <g:if test="${compoundAdapter.searchHighlight}">
                                <dt>Search Match (highlighted in bold):</dt>
                                <dd>&hellip;${compoundAdapter.searchHighlight}&hellip;</dd>
                            </g:if>
                            <dt>Scaffold Promiscuity Analysis:</dt>
                            <dd>
                                <div class="promiscuity" href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compoundAdapter.pubChemCID])}" id="${compoundAdapter.pubChemCID}_prom"></div>
                            </dd>
                        </dl>
                    </td>
                </tr>
            </g:each>
        </table>
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
</g:if>
<g:else>
        <div class="tab-message">No search results found</div>
</g:else>
    </div>
</div>