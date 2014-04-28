%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<g:if test="${!resultSize}">
    <tr align="center">
        <td>
            <p class="text-error">N/A</p>
        </td>
    </tr>
</g:if>

<g:else>
    <g:if test="${landscapeLayout}">
        <tr align="center">
    </g:if>
%{--Experiment's result-set--}%
    <g:each in="${results}" var="result">

        <g:if test="${!landscapeLayout}">
            <tr align="center">
        </g:if>

        <td>
            <div>
            %{--A curve--}%
                <g:if test="${result instanceof bardqueryapi.ConcentrationResponseSeriesValue}">
                    <g:set var="concentrationSeries"
                           value="${result.value.concentrations}"/>
                    <g:set var="activitySeries" value="${result.value.activities}"/>
                    <table class="cbasInnerTable ${innerBorder ? 'cbasInnerTableBorder' : ''}">
                        <tr align="center">
                            <td>
                                <g:curvePlot
                                        concentrationSeries="${concentrationSeries}"
                                        activitySeries="${activitySeries}"
                                        curveFitParameters="${result.curveFitParameters}"
                                        slope="${result.slope}"
                                        responseUnit="${result.responseUnit}"
                                        testConcentrationUnit="${result.testConcentrationUnit}"
                                        qualifier="${result.qualifier}"
                                        yNormMin="${result.yNormMin}"
                                        yNormMax="${result.yNormMax}"
                                        yAxisLabel="${result.yAxisLabel}"
                                        xAxisLabel="${result.xAxisLabel}"
                                        title="${result.title}"/>
                            </td>
                            <td class='lineSpacing' style="padding-left: 10px;">
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
                    <table class="${innerBorder ? 'cbasInnerTableBorder' : ''}">
                        <tr align="center">
                            <td class='lineSpacing'>
                                <p><b><small>${pair.left}</small>
                                    <g:if test="${result.dictionaryElement}">
                                        <a href="${result.dictionaryElement.value}" target="datadictionary">
                                            <i class="icon-question-sign"></i>
                                        </a>
                                    </g:if>
                                    :</b>
                                    <small>${pair.right ?: 'No data available'}</small></p>
                            </td>
                        </tr>
                    </table>
                </g:elseif>
            %{--A simple string-value result--}%
                <g:elseif test="${result instanceof bardqueryapi.StringValue}">
                    <tr><td><p class="lineSpacing"><b><small>${result}</small></b></p></td> </tr>
                </g:elseif>
            %{--Call the list-rendering recursively--}%
                <g:elseif test="${result instanceof bardqueryapi.ListValue}">
                    <td class="cbasOutterTableCell">
                        <g:set var="results" value="${result.value}"/>
                        <g:set var="resultSize" value="${results?.size()}"/>
                        <table>
                            <tbody>
                            <g:render template="listValueRenderer"
                                      model="[resultSize: resultSize, results: results, landscapeLayout: null, innerBorder: null]"/>
                            </tbody>
                        </table>
                    </td>
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
