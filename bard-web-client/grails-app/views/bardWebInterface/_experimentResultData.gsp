<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<p>
    <b>Title: ${tableModel?.additionalProperties.experimentName}</b>
</p>


<p>
    <b>Assay ID :
    <g:if test="${tableModel?.additionalProperties.searchString}">
        <g:link controller="bardWebInterface" action="showAssay"
                id="${tableModel?.additionalProperties.bardAssayId}"
                params='[searchString: "${tableModel?.additionalProperties.searchString}"]'>
            ${tableModel?.additionalProperties.capAssayId}
        </g:link>
    </g:if>
    <g:else>
        <g:link controller="bardWebInterface" action="showAssay"
                id="${tableModel?.additionalProperties.bardAssayId}">
            ${tableModel?.additionalProperties.capAssayId}

        </g:link>
    </g:else>
    </b>
    <b>Confidence Level: ${tableModel?.additionalProperties.confidenceLevel}</b>
</p>

<div class="row-fluid">
    <g:hiddenField name="paginationUrl"
                   id="paginationUrl"/> %{--Used to hold the pagination url, if a paging link has been clicked--}%
    <div class="pagination offset3">
        <g:paginate
                total="${tableModel?.additionalProperties?.total ? tableModel?.additionalProperties?.total : 0}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>

    <div id="resultData">
    <g:render template="experimentResultRenderer" model="[tableModel: tableModel, landscapeLayout: false, innerBorder: innerBorder]"/>
    </div>

    <div class="pagination offset3">
        <g:paginate
                total="${tableModel?.additionalProperties?.total ? tableModel?.additionalProperties?.total : 0}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
</div>
