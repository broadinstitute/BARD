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
    <b>Confidence Level: ${webQueryTableModel?.additionalProperties.confidenceLevel}</b>
</p>

<div class="row-fluid">
    <g:hiddenField name="paginationUrl"
                   id="paginationUrl"/> %{--Used to hold the pagination url, if a paging link has been clicked--}%
    <div class="pagination offset3">
        <g:paginate
                total="${webQueryTableModel?.additionalProperties?.total ? webQueryTableModel?.additionalProperties?.total : 0}"
                params='[id: "${params?.id}", normalizeYAxis: "${webQueryTableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
    <table class="table table-condensed">
        <thead>
        <tr>
            <g:each in="${webQueryTableModel.columnHeaders}" var="header" status="j">
                <th>
                    <g:if test="${header.getValue() instanceof java.util.Map}">
                        <%
                            List<String> priorityDisplays = header.getValue().priorityDisplays ?: []
                        %>
                        <g:if test="${priorityDisplays}">
                            Results
                        </g:if>
                        <g:else></g:else>

                        <g:each var="dictionaryId" in="${header.getValue().dictionaryIds}">
                            <g:if test="${dictionaryId}">
                                <a href="/bardwebclient/dictionaryTerms/#${dictionaryId}"
                                   target="datadictionary">
                                    <i class="icon-question-sign"></i>
                                </a>
                            </g:if>
                        </g:each>
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
                %{--Handles the key/value result-types--}%
                    <g:elseif test="${currentRow.getValue() instanceof List}">
                        <td><g:each in="${currentRow.getValue()}" var="d">
                            ${d}<br/>
                        </g:each>
                        </td>
                    </g:elseif>
                %{--Handles the curves--}%
                    <g:elseif test="${currentRow.getValue() instanceof Map}">
                        <g:each in="${currentRow.getValue().ConcentrationResponseSeriesList}" var="concRespMap">
                            <td>
                                <table class="table table-striped table-condensed">
                                    <thead>
                                    <tr>
                                        <th>
                                            ${concRespMap.dictionaryLabel}
                                            <g:if test="${concRespMap?.dictionaryDescription}">
                                                <a href="/bardwebclient/dictionaryTerms/#${concRespMap?.dictElemId}"
                                                   target="datadictionary"><i class="icon-question-sign"></i></a>
                                            </g:if>
                                        </th>
                                        <th>Concentration</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${concRespMap.activityToConcentratonList}"
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
                                <img alt="${concRespMap.title}" title="${concRespMap.title}"
                                     src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurves',
                                             params: ['curves[0].sinf': concRespMap.plot.sinf,
                                                     'curves[0].s0': concRespMap.plot.s0,
                                                     'curves[0].slope': concRespMap.plot.slope,
                                                     'curves[0].hillSlope': concRespMap.plot.hillSlope,
                                                     'curves[0].concentrations': concRespMap.plot.concentrations,
                                                     'curves[0].activities': concRespMap.plot.activities,
                                                     'curves[0].yAxisLabel': concRespMap.plot.yAxisLabel,
                                                     'curves[0].xAxisLabel': concRespMap.plot.xAxisLabel,
                                                     'curves[0].yNormMin': concRespMap.plot.yNormMin,
                                                     'curves[0].yNormMax': concRespMap.plot.yNormMax
                                             ])}"/>
                                <br/>
                                <g:if test="${concRespMap.curveFitParams}">
                                    <p>
                                        <g:each in="${concRespMap.curveFitParams}" var="curveFitParam">
                                            ${curveFitParam.toString()}<br/>
                                        </g:each>
                                    </p>
                                    <br/>
                                    <br/>
                                </g:if>

                            </td>
                            <td>
                                <g:each in="${concRespMap.miscData}" var="miscData">
                                    <g:if test="${miscData?.dictionaryDescription}">
                                        ${miscData.toDisplay()}<a
                                            href="/bardwebclient/dictionaryTerms/#${miscData?.dictElemId}"
                                            target="datadictionary"><i class="icon-question-sign"></i></a>
                                    </g:if>
                                    <br/>
                                </g:each>
                            </td>
                        </g:each>
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
