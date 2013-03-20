<div class="row-fluid">
    <g:set var="columnWidth" value="${(100 / tableModel.columnHeaders.size()) as int}"/>
    <table align="center" class="table table-condensed">
        <thead>
        <tr>
            <g:each in="${tableModel.columnHeaders}" var="header" status="i">
                <th align="center" style="width: ${columnWidth}%">
                    ${header.value}
                </th>
            </g:each>
        </tr>
        </thead>
    </table>

    <g:each in="${tableModel.data}" var="row" status="i">
    %{--Each row is a separate table--}%
        <table style="border-style:solid; border-width:1px 1px 1px 1px; border-color:#000000; padding: 5px; margin: 10px; word-wrap: break-word;" align="center">
            <tbody>
            <tr>
                <g:each in="${row}" var="cell" status="j">
                %{--Assay description--}%
                    <g:if test="${cell instanceof bardqueryapi.AssayValue}">
                        <td align="center" style="width: ${columnWidth}%">
                            <g:assayDescription name="${cell.value.name}" adid="${cell.value.capAssayId}"/>
                        </td>
                    </g:if>
                %{--Project description--}%
                    <g:elseif test="${cell instanceof bardqueryapi.ProjectValue}">
                        <td align="center" style="width: ${columnWidth}%">
                            <g:projectDescription name="${cell.value.name}" pid="${cell.value.capProjectId}"/>
                        </td>
                    </g:elseif>
                %{--Structure rendering--}%
                    <g:elseif test="${cell instanceof bardqueryapi.StructureValue}">
                        <td align="center" style="width: ${columnWidth}%">
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
                    <g:elseif test="${cell instanceof bardqueryapi.StringValue}">
                        <td align="center" style="width: ${columnWidth}%">
                            <p>${cell.value}</p>
                        </td>
                    </g:elseif>
                    <g:elseif test="${cell instanceof bardqueryapi.ListValue}">
                        <td align="center" style="width: ${columnWidth}%">
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
                        <td align="center" style="width: ${columnWidth}%">
                            <table style="border-style:solid; border-width:1px 1px 1px 1px; border-color:#000000; padding: 3px; margin: 3px;" align="center">
                                %{--An experiment-box is a box with one experiment key and a list of result types (curves, single-points, etc.)--}%
                                `                            <g:set var="experimentValue"
                                                                    value="${cell.value.keySet().first()}"/>
                                <g:set var="experiment" value="${experimentValue.value}"/>
                                <g:set var="results" value="${cell.value[experimentValue]}"/>
                                <g:set var="resultSize" value="${results?.size()}"/>
                                <thead>
                                <tr>
                                    %{--First row is the experiment description--}%
                                    <th align="center" colspan="${resultSize}" style="width: ${columnWidth}%">
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
            </tr>
            </tbody>
        </table>
    </g:each>
</div>
