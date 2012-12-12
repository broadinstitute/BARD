<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="results.ExperimentalValueType; results.ExperimentalValueUnit; results.ExperimentalValue; molspreadsheet.MolSpreadSheetCell; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>

<p><b>Title: ${experimentDataMap?.experiment?.name}</b></p>

<p>
    <b>Assay ID : <g:link controller="bardWebInterface" action="showAssay"
                         id="${experimentDataMap?.experiment?.adid}" params='[searchString: "${searchString}"]'>
                ${experimentDataMap?.experiment?.adid}
        </g:link>
    </b>
</p>

<div class="row-fluid">
    <table class="table table-condensed">
        <thead>
        <tr>
            <th>SID</th>
            <th>CID</th>
            <th>Structure</th>
            <th>Outcome</th>
            <th>Potency</th>
            <g:if test="${!experimentDataMap?.spreadSheetActivities?.isEmpty()}">
                <g:each in="${experimentDataMap?.spreadSheetActivities?.get(0)?.hillCurveValueList}" var="readout">
                    <th>${readout.id}</th>
                    <g:if test="${readout.response.length > 1}">
                        <th>${readout.id} Plot</th>
                    </g:if>
                </g:each>
            </g:if>
        </tr>
        </thead>
        <g:each in="${experimentDataMap?.spreadSheetActivities}" var="experimentData">
            <tr>
                <td>
                    <img src="${resource(dir: 'images', file: 'pubchem-logo-sm.png')}" alt="PubChem" />
                    <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${experimentData.sid}">${experimentData.sid}</a>
                </td>
                <td>
                     <a href="${createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: experimentData.cid, searchString: "${searchString}"])}">${experimentData.cid}</a>
                </td>
                <td style="min-width: 180px;">
                    <g:compoundOptions sid="${experimentData.sid}" cid="${experimentData.cid}" imageWidth="180" imageHeight="150"/>
                </td>
                <td>${experimentData.activityOutcome?.label}</td>
                <td>
                    <g:if test="${experimentData.potency}">
                        <%
                            ExperimentalValue potency = new ExperimentalValue(experimentData.potency, false)
                        %>
                        ${potency.toString()}
                    </g:if>
                </td>
                <g:each in="${experimentData?.hillCurveValueList}" var="readout">

                    <td>
                        <g:each in="${0..(readout.size() - 1)}" var="i">
                            <%
                                ExperimentalValue resp = new ExperimentalValue(readout.response[i], false)
                                ExperimentalValue conc = new ExperimentalValue(readout.conc[i], ExperimentalValueUnit.getByValue(readout?.concentrationUnits), ExperimentalValueType.numeric)
                            %>
                            ${resp.toString()}
                            <g:if test="${readout.conc[i]}">
                                @ ${conc.toString()}
                            </g:if>
                            <br/>
                        </g:each>
                    </td>
                    <g:if test="${readout.response.length > 1}">
                        <td>
                            <img alt="Plot for CID ${experimentData.cid}" title="Plot for CID ${experimentData.cid}"
                                 src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurve',
                                         params: [sinf: readout.sInf,
                                                 s0: readout.s0,
                                                 ac50: readout.slope,
                                                 hillSlope: readout.coef,
                                                 concentrations: readout.conc,
                                                 activities: readout.response,
                                                 yAxisLabel: readout.id
                                         ])}"/>
                            <br/>
                            <g:if test="${readout.slope}">
                                <p>
                                    AC50 : ${(new ExperimentalValue(readout.slope, false)).toString()} <br/>
                                    sInf : ${(new ExperimentalValue(readout.sInf, false)).toString()}<br/>
                                    s0 : ${(new ExperimentalValue(readout.s0, false)).toString()}<br/>
                                    HillSlope : ${(new ExperimentalValue(readout.slope, false)).toString()}<br/>
                                </p>
                                <br/>
                                <br/>
                            </g:if>
                        </td>
                    </g:if>
                </g:each>
            </tr>
        </g:each>
    </table>

    <div class="pagination">
        <g:paginate total="${experimentDataMap?.total ? experimentDataMap?.total : 0}" params='[id: "${params?.id}"]'/>
    </div>
</div>
