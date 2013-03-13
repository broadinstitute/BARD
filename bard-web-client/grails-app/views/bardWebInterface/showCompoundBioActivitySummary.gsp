<%@ page import="bardqueryapi.FacetFormType" contentType="text/html;charset=UTF-8" %>
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
    <g:hiddenField name="compoundId" id='compoundId' value="${params?.id}"/>

    <h2>Compound Bio Activity Summary <small>(cid: ${tableModel?.additionalProperties?.id})</small></h2>

    <table class="table table-condensed">
        <thead>
        <tr>
            <g:each in="${tableModel.columnHeaders}" var="header">
                <th>
                    ${header.value}
                </th>
            </g:each>
        </tr>
        </thead>
    </table>

    <g:each in="${tableModel.data}" var="row" status="i">
    %{--Each row is a separate table--}%
        <table border="1" style="padding: 2px; margin-top: 2px;">
            <tbody>
            <tr>
                %{--First cell in the row is always the resource: an assay or a project--}%
                <th>
                    <g:if test="${tableModel.additionalProperties.resourceType == bardqueryapi.GroupByTypes.ASSAY}">
                        <g:assayDescription name="${row[0].value.name}"/>
                    </g:if>
                    <g:else>
                        <g:projectDescription name="${row[0].value.name}"/>
                    </g:else>
                </th>

            %{--The following items in the list are experiment 'boxes', each box containing the experiment and a list of results (curves, single-points, etc.)--}%
                <g:each in="${row}" var="experimentBox" status="j">
                    <g:if test="${j}">%{--we already handled cell #0--}%
                        <th>
                            <table border="1" style="margin: 3px; padding: 3px;">
                                <thead>
                                <th>
                                    %{--An experiment-box is a box with one experiment key and a list of result types (curves, single-points, etc.)--}%
                                    <g:experimentDescription name="${experimentBox.value.keySet().first().value.name}"/>
                                </th>
                                </thead>
                            </table>
                        </th>
                    </g:if>
                </g:each>
            </tr>
            </tbody>
        </table>
    </g:each>

    <div class="span9">
        <div id="compoundBioActivitySummaryDiv">
        </div>
    </div>
</div>
</body>
</html>