<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.core.adapter.CompoundAdapter; bardqueryapi.NormalizeAxis; bard.core.util.ExperimentalValueUtil; bard.core.rest.spring.experiment.ResultData; bard.core.rest.spring.experiment.ActivityData; bard.core.rest.spring.experiment.PriorityElement; bardqueryapi.ActivityOutcome; bard.core.rest.spring.experiment.CurveFitParameters; bard.core.rest.spring.experiment.ConcentrationResponsePoint; bard.core.rest.spring.experiment.ConcentrationResponseSeries; results.ExperimentalValueType; results.ExperimentalValueUnit;  molspreadsheet.MolSpreadSheetCell; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>

<p>
    <b>Title: ${webQueryTableModel?.additionalProperties.experimentName}</b>
</p>


<p>
    <b>Assay ID :
    <g:if test="${webQueryTableModel?.additionalProperties.searchString}">
        <g:link controller="bardWebInterface" action="showAssay"
                id="${webQueryTableModel?.additionalProperties.bardAssayId}"
                params='[searchString: "${webQueryTableModel?.additionalProperties.searchString}"]'>
            ${webQueryTableModel?.additionalProperties.capAssayId}
        </g:link>
    </g:if>
    <g:else>
        <g:link controller="bardWebInterface" action="showAssay"
                id="${webQueryTableModel?.additionalProperties.bardAssayId}">
            ${webQueryTableModel?.additionalProperties.capAssayId}
        </g:link>
    </g:else>
    </b>
</p>

<div class="row-fluid">
    <g:hiddenField name="skip" id="skip" value="${params?.offset ?: 0}"/>
    <g:hiddenField name="top" id="top" value="${params?.max ?: 10}"/>
    <div class="pagination offset3">
        <g:paginate
                total="${webQueryTableModel?.additionalProperties?.total ? webQueryTableModel?.additionalProperties?.total : 0}"
                params='[id: "${params?.id}", normalizeYAxis: "${webQueryTableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
    <table class="table table-condensed">
        <thead>
        <tr>
        <tr>
            <g:each in="${webQueryTableModel.columnHeaders}" var="header" status="j">
                <th>
                    <g:if test="${header.getValue() instanceof java.util.Map}">
                        ${header.getValue()?.priorityDisplay ?: ""}
                        <g:if test="${header.getValue().dictionaryId}">
                            <a href="/bardwebclient/dictionaryTerms/#${header.getValue()?.dictionaryId}"
                               target="datadictionary">
                                <i class="icon-question-sign"></i>
                            </a>
                        </g:if>
                    </g:if>
                    <g:else>
                        ${header.toString()}
                    </g:else>

                </th>
            </g:each>
        </tr>
        </thead>
        <g:each in="${webQueryTableModel.data}" var="rowData">
            <tr>

                <g:each in="${rowData}" var="currentRow" status="i">
                    <g:if test="${i == 0}">
                        <td>
                            <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${currentRow.getValue()}">
                                <img src="${resource(dir: 'images', file: 'pubchem.png')}"
                                     alt="PubChem"/>  ${currentRow.getValue()}
                            </a>
                        </td>
                    </g:if>
                    <g:elseif test="${i == 1}">
                        <td>
                            <g:if test="${webQueryTableModel?.additionalProperties.searchString}">
                                <a href="${createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: currentRow.getValue(), searchString: "${webQueryTableModel?.additionalProperties.searchString}"])}">${currentRow.getValue()}</a>
                            </g:if>
                            <g:else>
                                <a href="${createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: currentRow.getValue()])}">${currentRow.getValue()}</a>
                            </g:else>
                        </td>
                    </g:elseif>
                    <g:elseif test="${i == 2}">
                        <td style="min-width: 180px;">
                            <g:compoundOptions
                                    sid="${currentRow.getValue().sid}"
                                    cid="${currentRow.getValue().cid}"
                                    smiles="${currentRow.getValue().smiles}"
                                    name="${bardqueryapi.JavaScriptUtility.cleanup(currentRow.getValue().cname)}"
                                    numActive="${currentRow.getValue().numberOfActiveAssays}"
                                    numAssays="${currentRow.getValue().numberOfAssays}"
                                    imageWidth="180"
                                    imageHeight="150"/>
                        </td>
                    </g:elseif>
                    <g:elseif test="${currentRow.getValue() instanceof List}">
                        <td><g:each in="${currentRow.getValue()}" var="d">
                            ${d}<br/>
                        </g:each>
                        </td>
                    </g:elseif>
                    <g:elseif test="${currentRow.getValue() instanceof Map}">
                        <td>
                            <table class="table table-striped table-condensed">
                                <thead><tr>
                                    <th>
                                        ${currentRow.getValue().dictionaryLabel}
                                        <g:if test="${currentRow.getValue()?.dictionaryDescription}">
                                            <a href="/bardwebclient/dictionaryTerms/#${currentRow.getValue()?.dictElemId}"
                                               target="datadictionary"><i class="icon-question-sign"></i></a>
                                        </g:if>
                                    </th>
                                    <th>Concentration</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${currentRow.getValue().activityToConcentratonList}"
                                        var="concentrationResponsePoint">
                                    <tr>
                                        <td>${concentrationResponsePoint.key}</td>
                                        <td>${concentrationResponsePoint.value}</td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>

                        </td>
                        <td>
                            <img alt="${currentRow.getValue().title}" title="${currentRow.getValue().title}"
                                 src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurves',
                                         params: [
                                                 'curves[0].sinf': currentRow.getValue().plot.sinf,
                                                 'curves[0].s0': currentRow.getValue().plot.s0,
                                                 'curves[0].slope': currentRow.getValue().plot.slope,
                                                 'curves[0].hillSlope': currentRow.getValue().plot.hillSlope,
                                                 'curves[0].concentrations': currentRow.getValue().plot.concentrations,
                                                 'curves[0].activities': currentRow.getValue().plot.activities,
                                                 'curves[0].yAxisLabel': currentRow.getValue().plot.yAxisLabel,
                                                 'curves[0].xAxisLabel': currentRow.getValue().plot.xAxisLabel,
                                                 'curves[0].yNormMin': currentRow.getValue().plot.yNormMin,
                                                 'curves[0].yNormMax': currentRow.getValue().plot.yNormMax
                                         ])}"/>
                            <br/>
                            <g:if test="${currentRow.getValue().curveFitParams}">
                                <p>
                                    <g:each in="${currentRow.getValue().curveFitParams}" var="curveFitParam">
                                        ${curveFitParam.toString()}<br/>
                                    </g:each>
                                </p>
                                <br/>
                                <br/>
                            </g:if>

                        </td>
                        <td>
                            <g:each in="${currentRow.getValue().miscData}" var="miscData">
                                <g:if test="${miscData?.dictionaryDescription}">
                                    ${miscData.toDisplay()}<a
                                        href="/bardwebclient/dictionaryTerms/#${miscData?.dictElemId}"
                                        target="datadictionary"><i class="icon-question-sign"></i></a>
                                </g:if>
                                <br/>
                            </g:each>
                        </td>
                    </g:elseif>
                    <g:else>
                        <td>
                            ${currentRow.getValue()}
                        </td>
                    </g:else>

                </g:each>
            </tr>
        </g:each>
    </table>

    <div class="pagination offset3">
        <g:paginate
                total="${webQueryTableModel?.additionalProperties?.total ? webQueryTableModel?.additionalProperties?.total : 0}"
                params='[id: "${params?.id}", normalizeYAxis: "${webQueryTableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
</div>
