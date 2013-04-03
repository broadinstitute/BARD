<g:if test="${!resultSize}">
    <tr>
        <td align="center" nowrap="wrap" style="width: ${columnWidth}%">
            <p class="text-error">N/A</p>
        </td>
    </tr>
</g:if>

<g:else>
    <g:if test="${landscapeLayout}">
        <tr>
    </g:if>
%{--Experiment's result-set--}%
    <g:each in="${results}" var="result">

        <g:if test="${!landscapeLayout}">
            <tr>
        </g:if>

        <td align="center" nowrap="wrap" style="width: ${columnWidth}%">
        %{--A curve--}%
            <g:if test="${result instanceof bardqueryapi.ConcentrationResponseSeriesValue}">
                <g:set var="concentrationSeries"
                       value="${result.value.concentrations}"/>
                <g:set var="activitySeries" value="${result.value.activities}"/>
                <table class="${innerBorder ? 'innerTableBorder' : ''}" align="center">
                    <tr>
                        <td align="center">
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
                <table class="${innerBorder ? 'innerTableBorder' : ''}" align="center">
                    <tr>
                        <td align="center">
                            <b><small>${pair.left}</small>
                                <g:if test="${result.dictionaryElement}">
                                    <a href="${result.dictionaryElement.value}" target="datadictionary">
                                        <i class="icon-question-sign"></i>
                                    </a>
                                </g:if>
                            </b>

                            <p><small>${pair.right}</small></p>
                        </td>
                    </tr>
                </table>
            </g:elseif>
        %{--A simple string-value result--}%
            <g:elseif test="${result instanceof bardqueryapi.StringValue}">
                <b><small>${result}</small></b>
            </g:elseif>

        </td>
        <g:if test="${!landscapeLayout}">
            </tr>
        </g:if>

    </g:each>

    <g:if test="${landscapeLayout}">
        </tr>
    </g:if>

</g:else>
