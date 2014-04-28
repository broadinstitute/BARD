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

<g:set var="widestRowColumnCount" value="${[tableModel.data*.size().max(), tableModel.columnHeaders.size()].max()}"/>

<div class="row-fluid">
    <g:set var="columnHeaderSize" value="${tableModel?.columnHeaders?.size()}"/>
    <table class="cbasOutterTable">
        <thead>
        <tr align="center">
            <g:each in="${tableModel.columnHeaders}" var="header" status="i">
                <th>
                    ${header.value}
                </th>
            </g:each>
        </tr>
        </thead>

        <tbody>
        <g:each in="${tableModel.data}" var="row" status="i">
            <tr class="cbasOutterTableRow" align="center">
                <g:each in="${row}" var="cell" status="j">
                %{--Assay description--}%
                    <g:if test="${cell instanceof bardqueryapi.AssayValue}">
                        <td class="cbasOutterTableCell">
                            <g:assayDescription assayAdapter="${cell?.value}"/>
                        </td>
                    </g:if>
                %{--Project description--}%
                    <g:elseif test="${cell instanceof bardqueryapi.ProjectValue}">
                        <td class="cbasOutterTableCell">
                            <g:projectDescription projectAdapter="${cell?.value}"/>
                        </td>
                    </g:elseif>
                %{--Structure rendering--}%
                    <g:elseif test="${cell instanceof bardqueryapi.StructureValue}">
                        <td class="cbasOutterTableCell">
                            <g:compoundOptions
                                    sid="${cell.getValue().sid}"
                                    cid="${cell.getValue().cid}"
                                    smiles="${cell.getValue().smiles}"
                                    name="${bardqueryapi.JavaScriptUtility.cleanup(cell.getValue().name)}"
                                    imageWidth="180"
                                    imageHeight="150"/>
                        </td>
                    </g:elseif>
                %{--A URL Link--}%
                    <g:elseif test="${cell instanceof bardqueryapi.LinkValue}">
                        <td class="cbasOutterTableCell">
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
                        <td class="cbasOutterTableCell">
                            <p>${cell.value}</p>
                        </td>
                    </g:elseif>
                    <g:elseif test="${cell instanceof bardqueryapi.ListValue}">
                        <td class="cbasOutterTableCell">
                            <g:set var="results" value="${cell.value}"/>
                            <g:set var="resultSize" value="${results?.size()}"/>
                            <table>
                                <tbody>
                                <g:render template="listValueRenderer"
                                          model="[resultSize: resultSize, results: results, landscapeLayout: landscapeLayout, innerBorder: innerBorder]"/>
                                </tbody>
                            </table>
                        </td>
                    </g:elseif>
                %{--At the current state, the only MapValue use is for an 'experimentBox', so the map's only key is an experiment.--}%
                    <g:elseif test="${cell instanceof bardqueryapi.MapValue}">
                        <td class="cbasOutterTableCell">
                            <div>
                                <table class="cbasInnerTable ${innerBorder ? 'cbasInnerTableBorder' : ''}">
                                    %{--An experiment-box is a box with one experiment key and a list of result types (curves, single-points, etc.)--}%
                                    `                            <g:set var="experimentValue"
                                                                        value="${cell.value.keySet().first()}"/>
                                    <g:set var="experiment" value="${experimentValue.value}"/>
                                    <g:set var="results" value="${cell.value[experimentValue]}"/>
                                    <g:set var="resultSize" value="${results?.size()}"/>
                                    <thead>
                                    <tr align="center">
                                        %{--First row is the experiment description--}%
                                        <th class="cbasOutterTableCell" colspan="${resultSize}">
                                            <g:experimentDescription name="${experiment.name}"
                                                                     eid="${experiment.capExptId}"
                                                                     capExptId="${experiment.capExptId}"/>
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:render template="listValueRenderer"
                                              model="[resultSize: resultSize, results: results, landscapeLayout: landscapeLayout, columnWidth: columnWidth, innerBorder: innerBorder]"/>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </g:elseif>
                </g:each>

            %{--Span the remaining <td> with empty cells so that we will have an aligned table--}%
                <g:set var="currentRowSize" value="${row.size()}"/>
                <g:set var="columnSpan" value="${widestRowColumnCount - currentRowSize}"/>
                <g:if test="${columnSpan}">
                    <td class="cbasOutterTableCell" colspan="${columnSpan}"></td>
                </g:if>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
