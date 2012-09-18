<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<%@ page import="grails.converters.JSON" %>
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
                    <td>
                        <img alt="${compoundAdapter.structureSMILES}" title="${compoundAdapter.compound.preferredName}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compoundAdapter.structureSMILES, width: 150, height: 120])}"/>
                    </td>
                    <td>
                        <g:link action="showCompound" id="${compoundAdapter.pubChemCID}" target="_blank">
                            PubChem CID: ${compoundAdapter.pubChemCID}
                            <g:if test="${compoundAdapter.compound.preferredName}">
                                - ${compoundAdapter.compound.preferredName}
                            </g:if>
                        </g:link>
                        <a href="#" class="addCompoundToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(compoundAdapter.compound.preferredName)}"
                                 data-cart-id="${compoundAdapter.pubChemCID}" data-cart-smiles="${compoundAdapter.getStructureSMILES()}">
                            Save for later analysis
                        </a>
                        <g:if test="${compoundAdapter.searchHighlight}">
                            <ul>
                                <li>${compoundAdapter.searchHighlight}</li>
                            </ul>
                        </g:if>
                    </td>
                </tr>
            </g:each>
        </table>
        <g:hiddenField name="totalCompounds" id="totalCompounds" value="${nhits}"/>
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>