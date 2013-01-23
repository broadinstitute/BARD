<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.core.util.ExperimentalValueUtil; bard.core.rest.spring.experiment.ResultData; bard.core.rest.spring.experiment.ActivityData; bard.core.rest.spring.experiment.PriorityElement; bardqueryapi.ActivityOutcome; bard.core.rest.spring.experiment.CurveFitParameters; bard.core.rest.spring.experiment.ConcentrationResponsePoint; bard.core.rest.spring.experiment.ConcentrationResponseSeries; results.ExperimentalValueType; results.ExperimentalValueUnit;  molspreadsheet.MolSpreadSheetCell; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>

<p><b>Title: ${experimentDataMap?.experiment?.name}</b></p>

<p>
    <b>Assay ID : <g:link controller="bardWebInterface" action="showAssay"
                          id="${experimentDataMap?.experiment?.adid}" params='[searchString: "${searchString}"]'>
        ${experimentDataMap?.experiment?.adid}
    </g:link>
    </b>
</p>

<div class="row-fluid">


    <div class="pagination offset3">
        <g:paginate total="${experimentDataMap?.total ? experimentDataMap?.total : 0}" params='[id: "${params?.id}"]'/>
    </div>
    <table class="table table-condensed">
        <thead>
        <tr>
            <th>SID</th>
            <th>CID</th>
            <th>Structure</th>
            <th>Outcome</th>
             <th>
                 ${experimentDataMap?.priorityDisplay ?: ""}
                 <g:if test="${experimentDataMap?.dictionaryId}">
                     <a class="btn" href="/bardwebclient/dictionaryTerms/#${experimentDataMap?.dictionaryId}" target="datadictionary"><i class="icon-question-sign"></i></a>
                 </g:if>
            </th>
            <th>Experiment Descriptors</th>
            <g:if test="${experimentDataMap?.hasChildElements}">
                <th>Child Elements</th>
            </g:if>
            <g:if test="${experimentDataMap?.hasPlot}">
                <th>Concentration Response Series</th>
                <th>Concentration Response Plot</th>
                <th>Misc Data</th>
            </g:if>
        </tr>
        </thead>
        <g:each in="${experimentDataMap?.activities}" var="activity">
            <%
                PriorityElement priorityElement = null
                ResultData resultData = activity?.resultData
                if (resultData?.hasPriorityElements()) {
                    priorityElement = resultData?.priorityElements.get(0)  //we assume that there is only one priority element
                }
            %>
            <tr>
                <td>
                    <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${activity.sid}">
                        <img src="${resource(dir: 'images', file: 'pubchem.png')}" alt="PubChem"/>
                        ${activity.sid}</a>
                </td>
                <td>
                    <a href="${createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: activity.cid, searchString: "${searchString}"])}">${activity.cid}</a>
                </td>
                <td style="min-width: 180px;">
                    <g:compoundOptions sid="${activity.sid}" cid="${activity.cid}" imageWidth="180"
                                       imageHeight="150"/>
                </td>
                <td>
                    <g:if test="${resultData.getOutcome()}">
                        ${resultData.getOutcome()}
                    </g:if>
                </td>
                <td>

                    ${priorityElement?.toDisplay()}

                </td>
                <td>
                    <g:if test="${!resultData.isMapped()}">
                        <b>TIDs not yet mapped to a result hierarchy</b><br/><br/>
                    </g:if>
                    <g:each in="${resultData?.rootElements}" var="rootElement">
                        <g:if test="${rootElement?.toDisplay()}">${rootElement.toDisplay()} <br/></g:if>
                    </g:each>
                </td>
                <g:if test="${experimentDataMap?.hasChildElements}">
                    <td>
                        <g:each in="${priorityElement?.childElements}" var="childElement">

                            <g:if test="${childElement.toDisplay()}">${childElement.toDisplay()}<br/></g:if>

                        </g:each>
                    </td>
                </g:if>
                <g:each in="${priorityElement?.concentrationResponseSeries}" var="concRespSeries">
                    <%
                        List<ConcentrationResponsePoint> concentrationResponsePoints = concRespSeries.concentrationResponsePoints
                        Map doseResponsePointsMap = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponsePoints)
                        CurveFitParameters curveFitParameters = concRespSeries.curveFitParameters
                    %>
                    <td>
                        <table class="table table-striped table-condensed">
                            <thead><tr>
                                <th>
                                    ${concRespSeries.dictionaryLabel}
                                    <g:if test="${concRespSeries?.dictionaryDescription}">
                                        <a class="btn" href="/bardwebclient/dictionaryTerms/#${concRespSeries?.dictElemId}" target="datadictionary"><i class="icon-question-sign"></i></a>
                                    </g:if>
                                </th>
                                <th>Concentration</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${concentrationResponsePoints}" var="concentrationResponsePoint">
                                <tr>
                                    <td>${concentrationResponsePoint.displayActivity()}</td>
                                    <td>${concentrationResponsePoint.displayConcentration(concRespSeries.testConcentrationUnit)}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>

                    </td>
                    <g:if test="${!concentrationResponsePoints?.isEmpty()}">
                        <td>
                            <img alt="Plot for CID ${resultData.cid}" title="Plot for CID ${resultData.cid}"
                                 src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurve',
                                         params: [sinf: curveFitParameters?.getSInf(),
                                                 s0: curveFitParameters?.getS0(),
                                                 slope: priorityElement?.getSlope(),
                                                 hillSlope: curveFitParameters?.getHillCoef(),
                                                 concentrations: doseResponsePointsMap.concentrations,
                                                 activities: doseResponsePointsMap.activities,
                                                 yAxisLabel: "${concRespSeries?.getYAxisLabel()}",
                                                 xAxisLabel: "Log Concentration ${priorityElement.testConcentrationUnit}"
                                         ])}"/>
                            <br/>
                            <g:if test="${curveFitParameters != null}">
                                <p>
                                    ${experimentDataMap?.priorityDisplay?:''} : ${priorityElement.slope} <br/>
                                    sInf : ${(new ExperimentalValueUtil(curveFitParameters.sInf, false)).toString()}<br/>
                                    s0 : ${(new ExperimentalValueUtil(curveFitParameters.s0, false)).toString()}<br/>
                                    HillSlope : ${(new ExperimentalValueUtil(curveFitParameters.hillCoef, false)).toString()}<br/>
                                </p>
                                <br/>
                                <br/>
                            </g:if>

                        </td>

                    </g:if>
                    <td>
                        <g:each in="${concRespSeries.miscData}" var="miscData">
                            <g:if test="${miscData?.dictionaryDescription}">
                                ${miscData.toDisplay()}<a class="btn" href="/bardwebclient/dictionaryTerms/#${miscData?.dictElemId}" target="datadictionary"><i class="icon-question-sign"></i></a>
                            </g:if>
                            <br/>
                        </g:each>
                    </td>
                </g:each>
            </tr>
        </g:each>
    </table>
    <div class="pagination offset3">
        <g:paginate total="${experimentDataMap?.total ? experimentDataMap?.total : 0}" params='[id: "${params?.id}"]'/>
    </div>
</div>
