<%@ page import="bardqueryapi.GroupByTypes; bardqueryapi.FacetFormType" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Compound Bio-Activity Summary: ${tableModel?.additionalProperties?.id}</title>
    <r:require modules="core"/>
</head>

<body>
<div class="row-fluid">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.CompoundBioActivitySummaryForm]"/>

    <h2>Compound Bio Activity Summary <small>(cid: ${tableModel?.additionalProperties?.id})</small></h2>

    <g:form action="showCompoundBioActivitySummary" id="${params.id}">
        <g:hiddenField name="compoundId" id='compoundId' value="${params?.id}"/>
        <div style="text-align: left; vertical-align: middle;">
            <label for="groupByTypeSelect" style="display: inline; vertical-align: middle;">Group-by:</label>
            <g:select id="groupByTypeSelect" name="groupByType"
                      from="${[GroupByTypes.ASSAY, GroupByTypes.PROJECT]}" value="${resourceType}"
                      style="display: inline; vertical-align: middle;"/>
            <g:submitButton class="btn btn-primary" name="groupByTypeButton" value="Group"
                            style="display: inline; vertical-align: middle;"/>
        </div>

    </g:form>

    <div class="span9">
        <div id="compoundBioActivitySummaryDiv">
            <g:render template="experimentResultRenderer" model="[tableModel: tableModel, landscapeLayout: false]"/>
        </div>
    </div>
</div>
</body>
</html>