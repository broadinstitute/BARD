<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<%@ page import="grails.converters.JSON" %>
<div class="row-fluid">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.CompoundFacetForm]"/>
    <div class="span9">
        <table class="table">
            <g:each var="compoundAdapter" in="${compoundAdapters}">
                <tr>
                    <td>
                        <img alt="${compoundAdapter.structureSMILES}" title="${compoundAdapter.name}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compoundAdapter.structureSMILES, width: 150, height: 120])}"/>
                    </td>
                    <td>
                        <g:link action="showCompound" id="${compoundAdapter.pubChemCID}" target="_blank">
                            PubChem CID: ${compoundAdapter.pubChemCID}
                            <g:if test="${compoundAdapter.name}">
                                - ${compoundAdapter.name}
                            </g:if>
                        </g:link>
                        <a href="#" class="addCompoundToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(compoundAdapter.name)}" data-cart-id="${compoundAdapter.pubChemCID}">
                            Save for later analysis
                        </a>
                        <ul>
                            <li>${compoundAdapter.searchHighlight}</li>
                        </ul>
                    </td>
                </tr>
            </g:each>
        </table>
        <g:hiddenField name="totalCompounds" id="totalCompounds" value="${nhits}"/>
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</div>