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
    %{--<table class="table table-condensed">--}%
        %{--<thead>--}%
        %{--<tr>--}%
            %{--<g:each in="${tableModel.columnHeaders}" var="header" status="j">--}%
                %{--<th>--}%
                    %{--<g:if test="${header.getValue() instanceof java.util.Map}">--}%
                        %{--<%--}%
                            %{--List<String> priorityDisplays = header.getValue().priorityDisplays ?: []--}%
                        %{--%>--}%
                        %{--<g:if test="${priorityDisplays}">--}%
                            %{--Results--}%
                        %{--</g:if>--}%
                        %{--<g:else></g:else>--}%

                        %{--<g:each var="dictionaryId" in="${header.getValue().dictionaryIds}">--}%
                            %{--<g:if test="${dictionaryId}">--}%
                                %{--<a href="/bardwebclient/dictionaryTerms/#${dictionaryId}"--}%
                                   %{--target="datadictionary">--}%
                                    %{--<i class="icon-question-sign"></i>--}%
                                %{--</a>--}%
                            %{--</g:if>--}%
                        %{--</g:each>--}%
                    %{--</g:if>--}%
                    %{--<g:else>--}%
                        %{--${header.toString()}--}%
                    %{--</g:else>--}%

                %{--</th>--}%
            %{--</g:each>--}%
        %{--</tr>--}%
        %{--</thead>--}%
        %{--<g:each in="${tableModel.data}" var="rowData">--}%
            %{--<tr>--}%

                %{--<g:each in="${rowData}" var="currentRow" status="i">--}%
                    %{--<g:if test="${i == 0}">--}%
                        %{--<td>--}%
                            %{--<a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${currentRow.getValue()}">--}%
                                %{--<img src="${resource(dir: 'images', file: 'pubchem.png')}"--}%
                                     %{--alt="PubChem"/>  ${currentRow.getValue()}--}%
                            %{--</a>--}%
                        %{--</td>--}%
                    %{--</g:if>--}%
                    %{--<g:elseif test="${i == 1}">--}%
                        %{--<td>--}%
                            %{--<g:if test="${tableModel?.additionalProperties.searchString}">--}%
                                %{--<a href="${createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: currentRow.getValue(), searchString: "${tableModel?.additionalProperties.searchString}"])}">${currentRow.getValue()}</a>--}%
                            %{--</g:if>--}%
                            %{--<g:else>--}%
                                %{--<a href="${createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: currentRow.getValue()])}">${currentRow.getValue()}</a>--}%
                            %{--</g:else>--}%
                        %{--</td>--}%
                    %{--</g:elseif>--}%
                    %{--<g:elseif test="${i == 2}">--}%
                        %{--<td style="min-width: 180px;">--}%
                            %{--<g:compoundOptions--}%
                                    %{--sid="${currentRow.getValue().sid}"--}%
                                    %{--cid="${currentRow.getValue().cid}"--}%
                                    %{--smiles="${currentRow.getValue().smiles}"--}%
                                    %{--name="${bardqueryapi.JavaScriptUtility.cleanup(currentRow.getValue().cname)}"--}%
                                    %{--numActive="${currentRow.getValue().numberOfActiveAssays}"--}%
                                    %{--numAssays="${currentRow.getValue().numberOfAssays}"--}%
                                    %{--imageWidth="180"--}%
                                    %{--imageHeight="150"/>--}%
                        %{--</td>--}%
                    %{--</g:elseif>--}%
                %{--Handles the key/value result-types--}%
                    %{--<g:elseif test="${currentRow.getValue() instanceof List}">--}%
                        %{--<td><g:each in="${currentRow.getValue()}" var="d">--}%
                            %{--${d}<br/>--}%
                        %{--</g:each>--}%
                        %{--</td>--}%
                    %{--</g:elseif>--}%
                %{--Handles the curves--}%
                    %{--<g:elseif test="${currentRow.getValue() instanceof Map}">--}%
                        %{--<g:each in="${currentRow.getValue().ConcentrationResponseSeriesList}" var="concRespMap">--}%
                            %{--<td>--}%
                                %{--<table class="table table-striped table-condensed">--}%
                                    %{--<thead>--}%
                                    %{--<tr>--}%
                                        %{--<th>--}%
                                            %{--${concRespMap.dictionaryLabel}--}%
                                            %{--<g:if test="${concRespMap?.dictionaryDescription}">--}%
                                                %{--<a href="/bardwebclient/dictionaryTerms/#${concRespMap?.dictElemId}"--}%
                                                   %{--target="datadictionary"><i class="icon-question-sign"></i></a>--}%
                                            %{--</g:if>--}%
                                        %{--</th>--}%
                                        %{--<th>Concentration</th>--}%
                                    %{--</tr>--}%
                                    %{--</thead>--}%
                                    %{--<tbody>--}%
                                    %{--<g:each in="${concRespMap.activityToConcentratonList}"--}%
                                            %{--var="concentrationResponsePoint">--}%
                                        %{--<tr>--}%
                                            %{--<td>${concentrationResponsePoint.key}</td>--}%
                                            %{--<td>${concentrationResponsePoint.value}</td>--}%
                                        %{--</tr>--}%
                                    %{--</g:each>--}%
                                    %{--</tbody>--}%
                                %{--</table>--}%
                            %{--</td>--}%
                            %{--<td>--}%
                                %{--<img alt="${concRespMap.title}" title="${concRespMap.title}"--}%
                                     %{--src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurves',--}%
                                             %{--params: ['curves[0].sinf': concRespMap.plot.sinf,--}%
                                                     %{--'curves[0].s0': concRespMap.plot.s0,--}%
                                                     %{--'curves[0].slope': concRespMap.plot.slope,--}%
                                                     %{--'curves[0].hillSlope': concRespMap.plot.hillSlope,--}%
                                                     %{--'curves[0].concentrations': concRespMap.plot.concentrations,--}%
                                                     %{--'curves[0].activities': concRespMap.plot.activities,--}%
                                                     %{--'curves[0].yAxisLabel': concRespMap.plot.yAxisLabel,--}%
                                                     %{--'curves[0].xAxisLabel': concRespMap.plot.xAxisLabel,--}%
                                                     %{--'curves[0].yNormMin': concRespMap.plot.yNormMin,--}%
                                                     %{--'curves[0].yNormMax': concRespMap.plot.yNormMax--}%
                                             %{--])}"/>--}%
                                %{--<br/>--}%
                                %{--<g:if test="${concRespMap.curveFitParams}">--}%
                                    %{--<p>--}%
                                        %{--<g:each in="${concRespMap.curveFitParams}" var="curveFitParam">--}%
                                            %{--${curveFitParam.toString()}<br/>--}%
                                        %{--</g:each>--}%
                                    %{--</p>--}%
                                    %{--<br/>--}%
                                    %{--<br/>--}%
                                %{--</g:if>--}%

                            %{--</td>--}%
                            %{--<td>--}%
                                %{--<g:each in="${concRespMap.miscData}" var="miscData">--}%
                                    %{--<g:if test="${miscData?.dictionaryDescription}">--}%
                                        %{--${miscData.toDisplay()}<a--}%
                                            %{--href="/bardwebclient/dictionaryTerms/#${miscData?.dictElemId}"--}%
                                            %{--target="datadictionary"><i class="icon-question-sign"></i></a>--}%
                                    %{--</g:if>--}%
                                    %{--<br/>--}%
                                %{--</g:each>--}%
                            %{--</td>--}%
                        %{--</g:each>--}%
                    %{--</g:elseif>--}%
                    %{--<g:else>--}%
                        %{--<td>--}%
                            %{--${currentRow.getValue()}--}%
                        %{--</td>--}%
                    %{--</g:else>--}%

                %{--</g:each>--}%
            %{--</tr>--}%
        %{--</g:each>--}%
    %{--</table>--}%

    <div id="resultData">
    <g:render template="experimentResultRenderer" model="[tableModel: tableModel, landscapeLayout: false]"/>
    </div>

    <div class="pagination offset3">
        <g:paginate
                total="${tableModel?.additionalProperties?.total ? tableModel?.additionalProperties?.total : 0}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
</div>
