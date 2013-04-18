<g:if test="${!resultSize}">
    <tr class="cbasInnerTableRow" align="center">
        <td class="cbasInnerTableCell">
            <p class="text-error">N/A</p>
        </td>
    </tr>
</g:if>

<g:else>
    <g:if test="${landscapeLayout}">
        <tr class="cbasInnerTableRow" align="center">
    </g:if>
%{--Experiment's result-set--}%
    <g:each in="${results}" var="result">

        <g:if test="${!landscapeLayout}">
            <tr class="cbasInnerTableRow" align="center">
        </g:if>

        <td class="cbasInnerTableCell">
            <div style="padding: 10px;">
            %{--A curve--}%
                <g:if test="${result instanceof bardqueryapi.ConcentrationResponseSeriesValue}">
                    <g:set var="concentrationSeries"
                           value="${result.value.concentrations}"/>
                    <g:set var="activitySeries" value="${result.value.activities}"/>
                    <table class="cbasInnerTable ${innerBorder ? 'cbasInnerTableBorder' : ''}">
                        <tr class="cbasInnerTableRow" align="center">
                            <td class="cbasInnerTableCell">
                                <g:curvePlot
                                        concentrationSeries="${concentrationSeries}"
                                        activitySeries="${activitySeries}"
                                        curveFitParameters="${result.curveFitParameters}"
                                        slope="${result.slope}"
                                        responseUnit="${result.responseUnit}"
                                        testConcentrationUnit="${result.testConcentrationUnit}"
                                        yNormMin="${result.yNormMin}"
                                        yNormMax="${result.yNormMax}"
                                        yAxisLabel="${result.yAxisLabel}"
                                        xAxisLabel="${result.xAxisLabel}"
                                        title="${result.title}"/>
                            </td>
                            <td class="cbasInnerTableCell" style="padding-left: 10px;">
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
                    <table class="cbasInnerTable ${innerBorder ? 'cbasInnerTableBorder' : ''}">
                        <tr class="cbasInnerTableRow" align="center">
                            <td class="cbasInnerTableCell">
                                <p><b><small>${pair.left}</small>
                                    <g:if test="${result.dictionaryElement}">
                                        <a href="${result.dictionaryElement.value}" target="datadictionary">
                                            <i class="icon-question-sign"></i>
                                        </a>
                                    </g:if>
                                </b></p>

                                <p><small>${pair.right ?: 'No data available'}</small></p>
                            </td>
                        </tr>
                    </table>
                </g:elseif>
            %{--A simple string-value result--}%
                <g:elseif test="${result instanceof bardqueryapi.StringValue}">
                    <p><b><small>${result}</small></b></p>
                </g:elseif>
            </div>
        </td>
        <g:if test="${!landscapeLayout}">
            </tr>
        </g:if>

    </g:each>

    <g:if test="${landscapeLayout}">
        </tr>
    </g:if>

</g:else>
