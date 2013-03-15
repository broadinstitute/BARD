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


    <table class="table table-condensed">
        <thead>
        <tr>
            <g:each in="${tableModel.columnHeaders}" var="header" status="i">
                <th style="width: ${!i ? '200px;' : 'auto;'}">
                    ${header.value}
                </th>
            </g:each>
        </tr>
        </thead>
    </table>

    <g:each in="${tableModel.data}" var="row" status="i">
    %{--Each row is a separate table--}%
        <table style="border-style:solid; border-width:1px 1px 1px 1px; border-color:#000000; padding: 5px; margin: 10px; word-wrap: break-word;">
            <tbody>
            <tr>
                %{--First cell in the row is always the resource: an assay or a project--}%
                <th style="width: 200px;">
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
                            <table style="border-style:solid; border-width:1px 1px 1px 1px; border-color:#000000; padding: 3px; margin: 3px;">
                                %{--An experiment-box is a box with one experiment key and a list of result types (curves, single-points, etc.)--}%
                                <g:set var="experimentValue" value="${experimentBox.value.keySet().first()}"/>
                                <g:set var="experiment" value="${experimentValue.value}"/>
                                <g:set var="results" value="${experimentBox.value[experimentValue]}"/>
                                <g:set var="resultSize" value="${results?.size()}"/>
                                <thead>
                                <tr>
                                    %{--First row is the experiment description--}%
                                    <th colspan="${resultSize}" style="max-width: 500px;">
                                        <g:experimentDescription name="${experiment.name}"/>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <g:if test="${!resultSize}">
                                        <td>
                                            <p class="text-error">N/A</p>
                                        </td>
                                    </g:if>
                                    <g:else>
                                        %{--Experiment's result-set--}%
                                        <g:each in="${results}" var="result">
                                            <td align="center" nowrap="wrap">
                                                %{--A curve--}%
                                                <g:if test="${result instanceof bardqueryapi.ConcentrationResponseSeriesValue}">
                                                    <g:set var="concentrationSeries"
                                                           value="${result.value.concentrations}"/>
                                                    <g:set var="activitySeries" value="${result.value.activities}"/>
                                                    <table>
                                                        <tr>
                                                            <td align="center">
                                                                <g:curvePlot
                                                                        concentrationSeries="${concentrationSeries}"
                                                                        activitySeries="${activitySeries}"
                                                                        curveFitParameters="${result.curveFitParameters}"
                                                                        slope="${result.slope}"
                                                                        responseUnit="${result.responseUnit}"
                                                                        testConcentrationUnit="${result.testConcentrationUnit}"/>
                                                            </td>
                                                            <td align="center">
                                                                <g:curveValues
                                                                        title="${result.title}"
                                                                        concentrationSeries="${concentrationSeries}"
                                                                        activitySeries="${activitySeries}"
                                                                        responseUnit="${result.responseUnit}"
                                                                        testConcentrationUnit="${result.testConcentrationUnit}"/>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </g:if>
                                                %{--A key/value pair result--}%
                                                <g:elseif test="${result instanceof bardqueryapi.PairValue}">
                                                    <g:set var="pair" value="${result.value}"/>
                                                    <table style="border-style:solid; border-width:1px 1px 1px 1px; border-color:#000000; padding: 3px; margin: 3px;">
                                                        <tr>
                                                            <td align="center">
                                                                <b><small>${pair.left}</small></b>

                                                                <p><small>${pair.right}</small></p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </g:elseif>
                                            </td>
                                        </g:each>
                                    </g:else>
                                </tr>
                                </tbody>
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