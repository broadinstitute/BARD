<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
    <div class="span9">
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
    <r:script type="text/javascript">
        $(document).ready(function () {
            var saveToCartButtons = $('.saveToCart');
            saveToCartButtons.tooltip('show');
            setTimeout(function () {
                saveToCartButtons.tooltip('hide');
            }, 3000);
        })
    </r:script>
    <table class="resultTable table table-striped table-bordered">
        <caption
                class="right-aligned">Showing ${params.offset + 1}-${Math.min((params.offset + params.max), nhits)} of ${nhits} results</caption>
        <thead>
        <tr>
            <th>
                <a class="saveToCart btn btn-mini" href="#" id="addAllItemsToCart" maindivname="assays"
                   data-toggle="tooltip" data-placement="top" title="Save to cart for analysis">
                    Add All
                </a>
            </th>
            <th>Assay Format</th>
            <th>Assay Type</th>
            <th>Detection Method Type</th>
            <th>ADID</th>
            <th>Assay Title</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="assayAdapter" in="${assayAdapters}" status="i">
            <tr>
                <td class="align-center"><g:saveToCartButton id="${assayAdapter.id}"
                                                             name="${JavaScriptUtility.cleanup(assayAdapter.name)}"
                                                             type="${querycart.QueryItemType.AssayDefinition}"
                                                             hideLabel="true"/></td>
                <td>${assayAdapter.minimumAnnotation.getAssayFormat()}</td>
                <td>${assayAdapter.minimumAnnotation.getAssayType()}</td>
                <td>${assayAdapter.minimumAnnotation.getDetectionMethodType()}</td>
                <td>${assayAdapter.capAssayId}</td>
                <td><g:link action="showAssay" id="${assayAdapter.id}">
                    ${assayAdapter.name}</g:link><br/>
                    <small class="muted">${assayAdapter.highlight}</small></td>
                <td class="align-center"><g:if test="${assayAdapter.assayStatus == 'Draft'}">
                    <img src="${resource(dir: 'images', file: 'draft_retired.png')}"
                         alt="Draft" title="Warning this assay definition has not yet been reviewed for accuracy"/>
                </g:if>
                    <g:elseif
                            test="${assayAdapter.assayStatus == 'Approved' || assayAdapter.assayStatus == 'Witnessed'}">
                        <img src="${resource(dir: 'images', file: 'witnessed.png')}"
                             alt="Approved" title="This assay has been reviewed for accuracy"/>
                    </g:elseif>
                    <small class="muted">${assayAdapter.assayStatus}</small></td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <g:if test="${params.max || params.offset}">
        <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'
                    class="pagination-small pagination-right"/>
    </g:if>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>
</div>