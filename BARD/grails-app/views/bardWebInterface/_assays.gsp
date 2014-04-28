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
<g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets"
              model="['facets': facets, 'appliedFilters': appliedFilters, 'formName': FacetFormType.AssayFacetForm]"/>
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
            <th>Name</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="assayAdapter" in="${assayAdapters}" status="i">
            <tr>
                <td class="align-center"><g:saveToCartButton id="${assayAdapter.capAssayId}"
                                                             name="${JavaScriptUtility.cleanup(assayAdapter.name)}"
                                                             type="${querycart.QueryItemType.AssayDefinition}"
                                                             hideLabel="true"/></td>
                <td>${assayAdapter.minimumAnnotation.getAssayFormat()}</td>
                <td>${assayAdapter.minimumAnnotation.getAssayType()}</td>
                <td>${assayAdapter.minimumAnnotation.getDetectionMethodType()}</td>
                <td>${assayAdapter.capAssayId}</td>
                <td><g:link controller="assayDefinition" action="show" id="${assayAdapter.capAssayId}">
                    ${assayAdapter.name}</g:link><br/>
                    <small class="muted">${assayAdapter.highlight}</small></td>
                <td class="align-center">
                    <g:render template="/common/statusIcons" model="[status:assayAdapter.assayStatus, entity: 'Assay']"/>
                      <small class="muted">${assayAdapter.assayStatus}</small>
                </td>
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
