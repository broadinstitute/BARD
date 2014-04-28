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

<%@ page import="molspreadsheet.HillCurveValueHolder; molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>
<script type="text/javascript">
    jQuery.extend(jQuery.fn.dataTableExt.oSort, {
        "num-html-pre":function (a) {
            // First remove HTML tags and any text.  There may be quite a bit of code to remove in each
            // cell, but we can depend on a <nobr> variant to identify the place where the numbers start.
            var tagfree =  a.replace(/<.*?(nobr>\s|nobr>&gt;\s|nobr>&lt;\s)/g, "");
            var firstvalue = tagfree.split(" ") ;
            var getnumbers=firstvalue[0].replace(/[^\d.-]/g,"");
            var stringlen= getnumbers.length;
            var textFormOfFloat;
            // Insert a zero in front of the number ( otherwise a number starting with a decimal is treated as text )
            //  Special case: if the number is negative then the zero has to go behind the negative sign
            if (getnumbers.substring(0,1)=='-') {
                textFormOfFloat = '-0'+getnumbers.substring(1, stringlen);
            } else {
                textFormOfFloat = '0'+getnumbers;
            }
            // Finally we have an acceptable floating-point number. Convert it.
            return parseFloat(textFormOfFloat);
        },

        "num-html-asc":function (a, b) {
            return ((a < b) ? -1 : ((a > b) ? 1 : 0));
        },

        "num-html-desc":function (a, b) {
            return ((a < b) ? 1 : ((a > b) ? -1 : 0));
        }
    });
    function inactiveCheckboxHandler(checkbox){

        if (checkbox.checked){
            console.log('checked');
            window.location.href="../molSpreadSheet/index?transpose=false&norefresh=false&showActive=false&cid=${params.cid}";
        } else {
            console.log('unchecked');
            window.location.href="../molSpreadSheet/index?transpose=false&norefresh=false&showActive=true&cid=${params.cid}";
        }
    }
</script>

<script type="text/javascript">
    $(document).ready(function () {
        $('#showPromiscuityScores').click(function () {
            $('td:nth-child(3)').toggle();
            $('#promiscuitycol').toggle();
            PromiscuityHandler.setup();
        });
        $('td:nth-child(3)').toggle();
        $('#promiscuitycol').toggle();
        $("[rel=tooltip]").tooltip({container:'body'});
        <g:if test="${((molSpreadSheetData?.getRowCount() > 0) && (molSpreadSheetData.getColumnCount()>4))}">
        $('#molspreadsheet').dataTable({
                    "bStateSave":false,
                    "aoColumnDefs":[
                        {"bSortable":false, 'aTargets':[0]},
                        <g:each var="column" in="${1..(molSpreadSheetData.getColumnCount()-1)}">
                        {"sType":"num-html", 'aTargets':[${column}]}
                        <g:if test="${column<(molSpreadSheetData.getColumnCount()-1)}">,
                        </g:if>
                        </g:each>
                    ],
                    <g:if test="${molSpreadSheetData.getRowCount()>50}">%{--If we have a lot of data then use full-featured pagination--}%
                    "sPaginationType":"full_numbers",
                    </g:if>
                    "aLengthMenu":[
                        [5, 10, 25, 50, -1],
                        [5, 10, 25, 50, "All"]
                    ]
                }
        );
        </g:if>
    });
</script>
<% molSpreadSheetData.flipNormalizationForAdid (assayNormalizationSwap) %>
<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12" role="status"><p style="color: #3A87AD;">${flash.message}</p></div>
    </div>
</g:if>
<div class="row-fluid">
    <div class="span12">
        <g:if test="${molSpreadSheetData?.getRowCount() > 0}">
            <g:set var="columnWidth" value="${100.0 / ((molSpreadSheetData?.getColumnCount() - 1) as float)}"/>

                <span>
                    <input type="checkbox" class="pull-left" defaultChecked="checked" checked
                           name="showPromiscuityScores"
                           id="showPromiscuityScores">

                    <p style="padding-left: 15px;">Hide Promiscuity Scores</p>
                </span>
                <span>
                    <input type="checkbox" class="pull-left"
                           <%=(showActive)?:'checked'%>
                           name="showActiveInactiveChoice"
                           id="showActiveInactiveChoice" <%=(!disableInactiveCheckbox) ?: "disabled"%>
                           onclick='inactiveCheckboxHandler(this);'>
                    <p style="padding-left: 15px;">Show Inactive Results</p>
                </span>


            <g:if test="${params?.cid?.getClass()?.isArray()}">
                <a href="../molSpreadSheet/index?transpose=true&norefresh=true&cid=${params.cid.join('&cid=')}&pid=${params.pid}" class="pull-right tranposeSymbol"
                   title="Transpose columns and rows"> </a>
            </g:if>
            <g:elseif test="${params?.cid?.size()>0}">
                <a href="../molSpreadSheet/index?transpose=true&norefresh=true&cid=${params.cid}" class="pull-right tranposeSymbol"
                   title="Transpose columns and rows"> </a>
            </g:elseif>
            <g:else>
                <a href="../molSpreadSheet/index?transpose=true&norefresh=true" class="pull-right tranposeSymbol"
                   title="Transpose columns and rows"> </a>
            </g:else>





                <div class="centerEverything">
                    T<br/>

                    <div class="shiftTextUp">
                        Transpose columns<br/>
                        <span class="tinytextsqueeze">and rows</span>
                    </div>
                </div>
            </a>
            <table cellpadding="0" cellspacing="0" border="0" class="molSpreadSheet display" id="molspreadsheet"
                   width="100%">
                <thead>
                <tr class="molSpreadSheetHead">
                    <th rowspan="3" class="molSpreadSheetImg">Molecular structure</th>
                    <th rowspan="3" class="molSpreadSheetHeadData" width="<%=columnWidth%>%">CID</th>
                    <% int column = 0 %>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${column == 2}">
                            <th rowspan="3" class="molSpreadSheetHeadData" id="promiscuitycol"
                                width="<%=columnWidth%>%"><%=molSpreadSheetData.mapColumnsToAssay[column]%><br/>${colHeader}
                            </th>

                        </g:if>

                        <g:if test="${column == 3}">
                            <th rowspan="3" class="display molSpreadSheetHeadData"
                                width="<%=columnWidth%>%">${colHeader}</th>
                        </g:if>
                        <% column++ %>
                    </g:each>
                    <!-- assay identifier line -->
                    <g:each var="assayColumn" in="${molSpreadSheetData.determineResponseTypesPerAssay()}">
                        <th class="molSpreadSheetHeadData" rel="tooltip"
                            colspan="<%=assayColumn."numberOfResultTypes"%>"
                            title="<%=assayColumn."fullAssayName"%>">
                            <%
                               def bardAssayId =assayColumn."bardAssayId"
                               %>
                            <g:link controller="assayDefinition" action="show" id="${bardAssayId}">
                                ADID=${bardAssayId}
                                <g:render template="/common/statusIcons" model='[status:assayColumn."status", entity: "Assay"]'/>
                            </g:link>
                            <br/>

                            <div>
                                <g:if test="${assayColumn."normalized"}">
                                    <a class="normalizationtext" href="${createLink(controller: 'molSpreadSheet', action: 'index', params: [ChangeNorm:assayColumn."bardAssayId",norefresh:true])}">
                                     Denormalize
                                     </a>
                                </g:if>
                                <g:else>
                                    <a  class="normalizationtext" href="${createLink(controller: 'molSpreadSheet', action: 'index', params: [ChangeNorm:assayColumn."bardAssayId",norefresh:true])}">
                                    Normalize
                                    </a>
                                </g:else>
                            </div>
                        </th>
                    </g:each>
                </tr>
                <!-- assay identifier line -->
                <tr class="molSpreadSheetHead">
                <g:each var="experimentColumn" in="${molSpreadSheetData.determineExperimentPerAssay()}">
                   <th class="molSpreadSheetHeadData"
                        colspan="<%=experimentColumn."colspan"%>">
                    <g:link controller="experiment" action="show" id="${experimentColumn.eid}">
                        EID=<%=experimentColumn."eid"%>
                        <g:render template="/common/statusIcons" model='[status:experimentColumn."status", entity: "Experiment"]'/>
                    </g:link>

                   </th>
                </g:each>
                </tr>
                <!-- result types line -->
                <tr class="molSpreadSheetHead">
                    <% column = 0 %>
                    <g:set var="columnDictionaryLookup" value="${molSpreadSheetData?.getColumnsDescr()}"/>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${column > 3}">
                            <th class="molSpreadSheetHeadData">
                                ${colHeader}

                                <g:if test="${(columnDictionaryLookup[column]!=null) && ("0"!=columnDictionaryLookup[column])}">
                                    <a href="${request.contextPath}/dictionaryTerms/#${columnDictionaryLookup[column]}"
                                       class="desc_tip mssheader" data-placement="top"
                                       target="datadictionary">
                                        <i class="icon-question-sign"></i>
                                    </a>
                                </g:if>
                            </th>
                        </g:if>
                        <% column++ %>
                    </g:each>
                </tr>

                </thead>
                <tbody>
                <% Integer rowCount = 0 %>
                <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                    <% String retrievedSmiles = """${molSpreadSheetData?.displayValue(rowCnt, 0)?."smiles"}""".toString() %>
                    <% String name = """${molSpreadSheetData?.displayValue(rowCnt, 0)?."name"}""".toString() %>
                    <% String cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                    <% String activeVrsTested = """${molSpreadSheetData?.displayValue(rowCnt, 3)?."value"}""".toString() %>
                    <g:if test="${((rowCount++) % 2) == 0}">
                        <tr class="molSpreadSheet">
                    </g:if>
                    <g:else>
                        <tr class="molSpreadSheetGray">
                    </g:else>
                    <td class="molSpreadSheetImg" property="struct">
                        <g:imageCell cid="${cid}" smiles="${retrievedSmiles}" name="${name}"/>
                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <g:cidCell cid="${cid}"/>
                    </td>

                    <td class="molSpreadSheet">
                        <g:promiscuityCell cid="${cid}"/>
                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <g:activeVrsTestedCell activeVrsTested="${activeVrsTested}"  cid="${cid}"/>
                    </td>
                    <g:if test="${molSpreadSheetData.getColumnCount() > 4}">
                        <g:each var="colCnt" in="${4..(molSpreadSheetData.getColumnCount() - 1)}">
                            <g:exptDataCell colCnt="${colCnt}"
                                            spreadSheetActivityStorage="${molSpreadSheetData?.findSpreadSheetActivity(rowCnt, colCnt)}"
                                            mssHeaders="${molSpreadSheetData?.mssHeaders}"
                                            molSpreadSheetData="${molSpreadSheetData}"    />
                        </g:each>
                    </g:if>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
        <g:else>
            <g:render template="../molSpreadSheet/noSpreadSheet"/>
        </g:else>

    </div>
</div>
<g:if test="${molSpreadSheetData?.getRowCount() > 0}">
    <div class="row-fluid">
        <div class="span12">
            <export:formats formats="['csv', 'excel', 'pdf']" params="['cid':[params.cid]]"/>
        </div>
    </div>
</g:if>
