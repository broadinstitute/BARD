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
</script>

<div class="row-fluid">
    <g:if test="${flash.message}">
        <div class="span12" role="status"><p style="color: #3A87AD;">${flash.message}</p></div>
    </g:if>

    <div class="span2">
        <g:render template="../bardWebInterface/facets"
                  model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
    </div>

    <div class="span10">
        <g:if test="${molSpreadSheetData?.getColumnCount() > 0}">
            <g:set var="columnHeaders" value="${molSpreadSheetData?.getColumns()}"/>
            <g:set var="columnWidth" value="${100.0 / ((molSpreadSheetData?.getColumnCount() - 1) as float)}"/>
            <span>
                <input type="checkbox" class="pull-left" defaultChecked="checked" checked
                       name="showPromiscuityScores"
                       id="showPromiscuityScores">

                <p style="padding-left: 15px;">Hide Promiscuity Scores</p>
            </span>
            <a href="../molSpreadSheet/index?norefresh=true" class="pull-right tranposeSymbol"
               title="Transpose columns and rows">
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
                    <th class="molSpreadSheetImg" colspan=2>Molecular structure</th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <td class="molSpreadSheetImg">
                            <% String retrievedSmiles = """${molSpreadSheetData?.displayValue(rowCnt, 0)."smiles"}""".toString() %>
                            <% String cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                            <g:imageCell cid="${cid}" smiles="${retrievedSmiles}"/>
                        </td>
                    </g:each>
                </tr>
                <tr>
                    <th class="molSpreadSheetImg" colspan=2>CID</th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet" property="cid">
                            <g:cidCell cid="${cid}"/>
                        </td>
                    </g:each>
                </tr>

                <tr>
                    <th class="molSpreadSheetImg"
                        colspan=2><%="${((columnHeaders?.size() > 2) ? (columnHeaders[2]) : 'promiscuity')}"%></th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet">
                            <g:promiscuityCell cid="${cid}"/>
                        </td>
                    </g:each>
                </tr>

                <tr>
                    <th class="molSpreadSheetImg"
                        colspan=2><%="${((columnHeaders?.size() > 3) ? (columnHeaders[3]) : 'inactive/active')}"%></th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% String activeVrsTested = """${molSpreadSheetData?.displayValue(rowCnt, 3)?."value"}""".toString() %>
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet" property="cid">
                            <g:activeVrsTestedCell activeVrsTested="${activeVrsTested}"  cid="${cid}"/>
                        </td>
                    </g:each>
                </tr>

                <% int rowsToSkipBeforeNextAssayid = 0 %>
                <% int currentRowCounter = 0 %>
                <g:set var="assayHeaders" value="${molSpreadSheetData.determineResponseTypesPerAssay()}"/>
                <g:if test="${(assayHeaders.size() > 0)}">
                    <g:set var="assayHeaderIterator" value="${assayHeaders.iterator()}"/>
                    <g:set var="columnDictionaryLookup" value="${molSpreadSheetData?.getColumnsDescr()}"/>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${currentRowCounter > 3}">
                            <tr>
                                <g:if test="${(rowsToSkipBeforeNextAssayid == 0)}">
                                    <g:set var="currentAssayIdHeader" value="${assayHeaderIterator.next()}"/>
                                    <% rowsToSkipBeforeNextAssayid = currentAssayIdHeader."numberOfResultTypes" %>
                                    <th class="molSpreadSheetHeadData"
                                        rowspan="<%=rowsToSkipBeforeNextAssayid%>">
                                        <g:link controller="assayDefinition" action="show" id='<%=currentAssayIdHeader."bardAssayId"%>'>
                                            <%=currentAssayIdHeader."fullAssayName"%>
                                        </g:link>
                                    </th>
                                </g:if>
                                <% rowsToSkipBeforeNextAssayid-- %>
                                <th class="molSpreadSheetHeadData">
                                    <g:if test="${columnDictionaryLookup[currentRowCounter]}">
                                        ${colHeader}
                                        <g:if test="${columnDictionaryLookup[currentRowCounter]}">
                                            <a href="/BARD/dictionaryTerms/#${columnDictionaryLookup[currentRowCounter]}"
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

    <div class="span10 pull-right">
        <export:formats formats="['csv', 'excel', 'pdf']"/>
    </div>
</div>
