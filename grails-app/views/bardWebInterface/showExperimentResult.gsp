<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.core.AssayValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : EID</title>
</head>

<body>
<div class="row-fluid">
    <table class="table table-condensed">
        <thead>
        <tr>
            <th>SID</th>
            <th>CID</th>
            <th>Structure</th>
            <th>Readout</th>
            <th>Outcome</th>
            <th>Curve</th>
        </tr>
        </thead>
        <g:each in="${spreadSheetActivities}" var="experimentData">
            <tr>
                <td>${experimentData.sid}</td>
                <td>${experimentData.cid}</td>
                <td style="min-width: 180px;">
                    <img alt="SID: ${experimentData.sid}" title="SID: ${experimentData.sid}"
                         src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [cid: experimentData.cid, width: 180, height: 150])}"/>
                </td>
                <td>
                    <g:each in="${0..(experimentData.hillCurveValue.size() - 1)}" var="i">
                        ${experimentData.hillCurveValue.response[i]} @ ${experimentData.hillCurveValue.conc[i]}
                        <br/>
                    </g:each>
                </td>
                <td>${experimentData.hillCurveValue.id}</td>
                <td>
                    <g:if test="${role != AssayValues.AssayRole.Primary}">
                        <img alt="" title=""
                             src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurve', params: [sinf: experimentData.hillCurveValue.sInf, s0: experimentData.hillCurveValue.s0, ac50: experimentData.hillCurveValue.slope, hillSlope: experimentData.hillCurveValue.coef, concentrations: experimentData.hillCurveValue.conc, activities: experimentData.hillCurveValue.response])}"/>
                    </g:if>
                </td>
            </tr>
        </g:each>
    </table>
</div>
</body>
</html>