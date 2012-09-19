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
                        <img alt="${compoundAdapter.structureSMILES}" title="${compoundAdapter.name}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compoundAdapter.structureSMILES, width: 180, height: 150])}"/>
                    </td>
                    <td>
                        <g:link action="showCompound" id="${compoundAdapter.pubChemCID}" target="_blank">
                            PubChem CID: ${compoundAdapter.pubChemCID}
                            <g:if test="${compoundAdapter.name}">
                                - ${compoundAdapter.name}
                            </g:if>
                        </g:link>
                        <a href="#" class="addCompoundToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(compoundAdapter.name)}"
                                 data-cart-id="${compoundAdapter.pubChemCID}" data-cart-smiles="${compoundAdapter.getStructureSMILES()}">
                            Save for later analysis
                        </a>
                        <g:if test="${compoundAdapter.searchHighlight}">
                            <ul>
                                <li>${compoundAdapter.searchHighlight}</li>
                            </ul>
                        </g:if>
                    </td>
                    <td>
                        Promiscuity Scores: <div class="promiscuity" href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compoundAdapter.pubChemCID])}" id="${compoundAdapter.pubChemCID}_prom"></div>
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