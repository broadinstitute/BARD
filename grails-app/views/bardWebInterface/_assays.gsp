<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<div class="row-fluid">
    <g:render template="facets" model="['facets': facets, 'formName' : FacetFormType.AssayFacetForm]"/>
<div class="span9">
    <ul class="unstyled results">
    <g:each var="assayAdapter" in="${assayAdapters}">
        <li>
            <g:link action="showAssay" id="${assayAdapter.assay.id}" target="_blank">ADID: ${assayAdapter.assay.id} - ${assayAdapter.name}</g:link>
            <a href="#" class="addAssayToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(assayAdapter.name)}" data-cart-id="${assayAdapter.assay.id}">Save for later analysis</a>
              <ul>
                <li>${assayAdapter.searchHighlight}</li>
            </ul>
        </li>
    </g:each>
    </ul>
    <g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>
    <div class="pagination">
        <g:paginate total="${nhits?nhits:0}" params='[searchString:"${searchString}"]'/>
    </div>
</div>
</div>