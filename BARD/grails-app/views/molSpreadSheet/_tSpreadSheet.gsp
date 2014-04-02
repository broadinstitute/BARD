<%@ page import="molspreadsheet.HillCurveValueHolder; molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>

<script type="text/javascript">
    $(document).ready(function () {
        $('#showPromiscuityScores').click(function () {
            $('tr:nth-child(3)').toggle();
            PromiscuityHandler.setup();
        });
        $('tr:nth-child(3)').toggle();
        $("[rel=tooltip]").tooltip();
    });
    function inactiveCheckboxHandler(checkbox){
        if (checkbox.checked){
            window.location.href="../molSpreadSheet/index?transpose=true&norefresh=false&cid=${params.cid}&showActive=false";
        } else {
            window.location.href="../molSpreadSheet/index?transpose=true&norefresh=false&cid=${params.cid}&showActive=true";
        }
    }
</script>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12" role="status"><p style="color: #3A87AD;">${flash.message}</p></div>
    </div>
</g:if>
<div class="row-fluid">
    <div class="span12">
        <g:if test="${molSpreadSheetData?.getColumnCount() > 0}">
            <g:set var="columnHeaders" value="${molSpreadSheetData?.getColumns()}"/>
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
                <a href="../molSpreadSheet/index?norefresh=true&cid=${params.cid.join('&cid=')}&pid=${params.pid}" class="pull-right tranposeSymbol"
                   title="Transpose columns and rows">
            </g:if>
            <g:elseif test="${params?.cid?.size()>0}">
                <a href="../molSpreadSheet/index?norefresh=false&cid=${params.cid}" class="pull-right tranposeSymbol"
              title="Transpose columns and rows">
            </g:elseif>
            <g:else>
                <a href="../molSpreadSheet/index?norefresh=true" class="pull-right tranposeSymbol"
                  title="Transpose columns and rows">
            </g:else>
                <div class="centerEverything">
                    T<br/>

                    <div class="shiftTextUp">
                        Transpose columns<br/>
                        <span class="tinytextsqueeze">and rows</span>
                    </div>
                </div>
            </a>
            <table cellpadding="0" cellspacing="0" border="1" class="molSpreadSheet display" id="molspreadsheet"
                   width="100%">
                <tr>
                    <th class="molSpreadSheetImg" colspan=3>Molecular structure</th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <td class="molSpreadSheetImg">
                            <% String retrievedSmiles = """${molSpreadSheetData?.displayValue(rowCnt, 0)?."smiles"}""".toString() %>
                            <% String cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                            <% String name = """${molSpreadSheetData?.displayValue(rowCnt, 0)?."name"}""".toString() %>
                            <g:imageCell cid="${cid}" smiles="${retrievedSmiles}" name="${name}"/>
                        </td>
                    </g:each>
                </tr>
                <tr>
                    <th class="molSpreadSheetImg" colspan=3>CID</th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet" property="cid">
                            <g:cidCell cid="${cid}"/>
                        </td>
                    </g:each>
                </tr>

                <tr>
                    <th class="molSpreadSheetImg"
                        colspan=3><%="${((columnHeaders?.size() > 2) ? (columnHeaders[2]) : 'promiscuity')}"%></th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet">
                            <g:promiscuityCell cid="${cid}"/>
                        </td>
                    </g:each>
                </tr>

                <tr>
                    <th class="molSpreadSheetImg"
                        colspan=3><%="${((columnHeaders?.size() > 3) ? (columnHeaders[3]) : 'inactive/active')}"%></th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% String activeVrsTested = """${molSpreadSheetData?.displayValue(rowCnt, 3)?."value"}""".toString() %>
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet" property="cid">
                            <g:activeVrsTestedCell activeVrsTested="${activeVrsTested}"  cid="${cid}"/>
                        </td>
                    </g:each>
                </tr>

                <% int rowsToSkipBeforeNextAssayid = 0 %>
                <% int rowsToSkipBeforeNextExperimentId = 0 %>
                <% int currentRowCounter = 0 %>
                <g:set var="assayHeaders" value="${molSpreadSheetData.determineResponseTypesPerAssay()}"/>
                <g:set var="experimentsPerAssay" value="${molSpreadSheetData.determineExperimentPerAssay()}"/>
                <g:if test="${(assayHeaders.size() > 0)}">
                    <g:set var="assayHeaderIterator" value="${assayHeaders.iterator()}"/>
                    <g:set var="experimentHeaderIterator" value="${experimentsPerAssay.iterator()}"/>
                    <g:set var="columnDictionaryLookup" value="${molSpreadSheetData?.getColumnsDescr()}"/>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${currentRowCounter > 3}">
                            <tr>
                                <g:if test="${(rowsToSkipBeforeNextAssayid == 0)}">
                                    <g:set var="currentAssayIdHeader" value="${assayHeaderIterator.next()}"/>
                                    <% rowsToSkipBeforeNextAssayid = currentAssayIdHeader."numberOfResultTypes"
                                    def bardAssayId = currentAssayIdHeader."bardAssayId"
                                    %>
                                    <th class="molSpreadSheetHeadData"
                                        rowspan="<%=rowsToSkipBeforeNextAssayid%>">
                                        <g:link controller="assayDefinition" action="show" id="${bardAssayId}">
                                            <%=currentAssayIdHeader."fullAssayName"%>
                                            <g:render template="/common/statusIcons" model='[status:currentAssayIdHeader."status", entity: "Assay"]'/>
                                        </g:link>

                                    </th>
                                </g:if>
                                <g:if test="${(rowsToSkipBeforeNextExperimentId == 0)}">
                                    <g:set var="currentExperimentIdHeader" value="${experimentHeaderIterator.next()}"/>
                                    <%
                                        rowsToSkipBeforeNextExperimentId = currentExperimentIdHeader.colspan
                                    %>
                                    <th class="molSpreadSheetHeadData" rowspan="<%=rowsToSkipBeforeNextExperimentId%>">
                                        <g:link controller="experiment" action="show" id="${currentExperimentIdHeader.eid}">
                                            EID=<%=currentExperimentIdHeader.eid%>
                                            <g:render template="/common/statusIcons" model='[status:currentExperimentIdHeader."status", entity: "Experiment"]'/>

                                        </g:link>

                                    </th>
                                </g:if>
                                <% rowsToSkipBeforeNextAssayid-- %>
                                <% rowsToSkipBeforeNextExperimentId-- %>
                                <th class="molSpreadSheetHeadData">
                                    <g:if test="${columnDictionaryLookup[currentRowCounter]}">
                                        ${colHeader}
                                        <g:if test="${columnDictionaryLookup[currentRowCounter]}">
                                            <a href="${request.contextPath}/dictionaryTerms/#${columnDictionaryLookup[currentRowCounter]}"
                                               class="desc_tip mssheader" data-placement="top"
                                               target="datadictionary">
                                                <i class="icon-question-sign"></i>
                                            </a>
                                        </g:if>
                                    </g:if>
                                    <g:else>
                                        ${colHeader}
                                    </g:else>
                                </th>

                                <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                                    <g:exptDataCell colCnt="${currentRowCounter}"
                                                    mssHeaders="${molSpreadSheetData?.mssHeaders}"
                                                    molSpreadSheetData="${molSpreadSheetData}"
                                                    spreadSheetActivityStorage="${molSpreadSheetData?.findSpreadSheetActivity(rowCnt, currentRowCounter)}"/>
                                </g:each>

                            </tr>
                        </g:if>
                        <% currentRowCounter++ %>
                    </g:each>
                </g:if>
            </table>
        </g:if>
        <g:else>
            <g:render template="../molSpreadSheet/noSpreadSheet"/>
        </g:else>

    </div>
</div>
<div class="row-fluid">
    <div class="span12">
        <export:formats formats="['csv', 'excel', 'pdf']" params="['cid':[params.cid]]"/>
    </div>
</div>