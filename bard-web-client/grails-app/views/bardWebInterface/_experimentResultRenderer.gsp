<g:set var="widestRowColumnCount" value="${[tableModel.data*.size().max(), tableModel.columnHeaders.size()].max()}"/>

<div class="row-fluid">
    <g:set var="columnHeaderSize" value="${tableModel?.columnHeaders?.size()}"/>
    %{--<g:set var="columnWidth" value="${columnHeaderSize ? (100 / tableModel.columnHeaders.size()) as int : 'auto;'}"/>--}%
    <table align="center">
        <thead>
        <tr>
            <g:each in="${tableModel.columnHeaders}" var="header" status="i">
                <th align="center">
                    ${header.value}
                </th>
            </g:each>
        </tr>
        </thead>
        %{--</table>--}%

        <tbody>
        <g:each in="${tableModel.data}" var="row" status="i">
            <tr class="rowBorder" align="center">
                <g:each in="${row}" var="cell" status="j">
                %{--Assay description--}%
                    <g:if test="${cell instanceof bardqueryapi.AssayValue}">
                        <td align="center">
                            <g:assayDescription name="${cell?.value?.name ?: ''}"
                                                adid="${cell?.value?.capAssayId ?: ''}"/>
                        </td>
                    </g:if>
                %{--Project description--}%
                    <g:elseif test="${cell instanceof bardqueryapi.ProjectValue}">
                        <td align="center">
                            <g:projectDescription name="${cell?.value?.name ?: ''}"
                                                  pid="${cell?.value?.capProjectId ?: ''}"/>
                        </td>
                    </g:elseif>
                %{--Structure rendering--}%
                    <g:elseif test="${cell instanceof bardqueryapi.StructureValue}">
                        <td align="center">
                            <g:compoundOptions
                                    sid="${cell.getValue().sid}"
                                    cid="${cell.getValue().cid}"
                                    smiles="${cell.getValue().smiles}"
                                    name="${bardqueryapi.JavaScriptUtility.cleanup(cell.getValue().name)}"
                                    numActive="${cell.getValue().numberOfActiveAssays}"
                                    numAssays="${cell.getValue().numberOfAssays}"
                                    imageWidth="180"
                                    imageHeight="150"/>
                        </td>
                    </g:elseif>
                %{--A URL Link--}%
                    <g:elseif test="${cell instanceof bardqueryapi.LinkValue}">
                        <td align="center">
                            <a href="${cell.value}">
                                <g:if test="${cell.imgFile}">
                                    <g:img dir="images" file="${cell.imgFile}" alt="${cell.imgAlt}"/>
                                </g:if>
                                ${cell.text}
                            </a>
                        </td>
                    </g:elseif>
                %{--String--}%
                    <g:elseif test="${cell instanceof bardqueryapi.StringValue}">
                        <td align="center">
                            <p>${cell.value}</p>
                        </td>
                    </g:elseif>
                    <g:elseif test="${cell instanceof bardqueryapi.ListValue}">
                        <td align="center">
                            <g:set var="results" value="${cell.value}"/>
                            <g:set var="resultSize" value="${results?.size()}"/>
                            <table align="center">
                                <tbody>
                                <g:render template="listValueRenderer"
                                          model="[resultSize: resultSize, results: results, landscapeLayout: landscapeLayout]"/>
                                </tbody>
                            </table>
                        </td>
                    </g:elseif>
                %{--At the current state, the only MapValue use is for an 'experimentBox', so the map's only key is an experiment.--}%
                    <g:elseif test="${cell instanceof bardqueryapi.MapValue}">
                        <td align="center">
                            <table class="${innerBorder ? 'innerTableBorder' : ''}" align="center">
                                %{--An experiment-box is a box with one experiment key and a list of result types (curves, single-points, etc.)--}%
                                `                            <g:set var="experimentValue"
                                                                    value="${cell.value.keySet().first()}"/>
                                <g:set var="experiment" value="${experimentValue.value}"/>
                                <g:set var="results" value="${cell.value[experimentValue]}"/>
                                <g:set var="resultSize" value="${results?.size()}"/>
                                <thead>
                                <tr>
                                    %{--First row is the experiment description--}%
                                    <th align="center" colspan="${resultSize}">
                                        <g:experimentDescription name="${experiment.name}"
                                                                 bardExperimentId="${experiment.capExptId}"/>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:render template="listValueRenderer"
                                          model="[resultSize: resultSize, results: results, landscapeLayout: landscapeLayout, columnWidth: columnWidth]"/>
                                </tbody>
                            </table>
                        </td>
                    </g:elseif>
                </g:each>

            %{--Span the remaining <td> with empty cells so that we will have an aligned table--}%
                <g:set var="currentRowSize" value="${row.size()}"/>
                <g:set var="columnSpan" value="${widestRowColumnCount - currentRowSize}"/>
                <g:if test="${columnSpan}">
                    <td colspan="${columnSpan}"></td>
                </g:if>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
