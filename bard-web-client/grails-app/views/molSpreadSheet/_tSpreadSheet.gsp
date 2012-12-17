<%@ page import="molspreadsheet.HillCurveValueHolder; molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>

<script type="text/javascript">
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
            <label class="checkbox" class="pull-left">
                <input type="checkbox" defaultChecked="checked" checked name="showPromiscuityScores"
                       id="showPromiscuityScores">
                Hide Promiscuity Scores
            </label>
            <a href="../molSpreadSheet/index" class="pull-right">Re-Transpose</a>
            <table cellpadding="0" cellspacing="0" border="1" class="molSpreadSheet display" id="molspreadsheet"
                   width="100%">
                <tr>
                    <th class="molSpreadSheetImg" colspan=2>Molecular structure</th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <td class="molSpreadSheetImg">
                            <% String retrievedSmiles = """${molSpreadSheetData?.displayValue(rowCnt, 0)."smiles"}""".toString() %>
                            <% String cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                            <g:if test="${retrievedSmiles == 'Unknown smiles'}">
                                ${retrievedSmiles}
                            </g:if>
                            <g:else>
                                <g:compoundOptions sid="${cid}" cid="${cid}" smiles="${retrievedSmiles}" imageWidth="150" imageHeight="120"/>
                            </g:else>
                        </td>
                    </g:each>
                </tr>
                <tr>
                    <th class="molSpreadSheetImg"  colspan=2>CID</th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet" property="cid">
                            <g:link controller="bardWebInterface" action="showCompound" id="${cid}">${cid}</g:link>
                        </td>
                    </g:each>
                </tr>

                <tr>
                    <th class="molSpreadSheetImg"  colspan=2><%="${((columnHeaders?.size() > 2)?(columnHeaders[2]):'promiscuity')}"%></th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                        <td class="molSpreadSheet">
                            <div class="promiscuity"
                                 href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: cid])}"
                                 id="${cid}_prom"></div>
                        </td>
                    </g:each>
                </tr>

                <tr>
                    <th class="molSpreadSheetImg"  colspan=2><%="${((columnHeaders?.size() > 3)?(columnHeaders[3]):'inactive/active')}"%></th>
                    <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                        <% String activeVrsTested = """${molSpreadSheetData?.displayValue(rowCnt, 3)?."value"}""".toString() %>
                        <td class="molSpreadSheet" property="cid">
                            <div>
                                <span class="badge badge-info">${activeVrsTested}</span>
                            </div>
                        </td>
                    </g:each>
                </tr>

                <% int rowsToSkipBeforeNextAssayid = 0 %>
                <% int currentRowCounter =0 %>
                <g:set var="assayHeaders" value="${molSpreadSheetData.determineResponseTypesPerAssay()}" />
                <g:if test="${(assayHeaders.size()>0)}">
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${currentRowCounter > 3}">
                            <g:set var="assayHeaderIterator" value="${assayHeaders.iterator()}" />
                            <tr>
                                <g:if test="${(rowsToSkipBeforeNextAssayid==0)}">
                                    <g:set var="currentAssayIdHeader" value="${assayHeaderIterator.next()}" />
                                    <%rowsToSkipBeforeNextAssayid = currentAssayIdHeader."numberOfResultTypes"%>
                                    <th class="molSpreadSheetHeadData" rel="tooltip"
                                        rowspan="<%=rowsToSkipBeforeNextAssayid%>"
                                        title="<%=currentAssayIdHeader."fullAssayName"%>"><a
                                            href="../bardWebInterface/showAssay/<%=currentAssayIdHeader."assayName"%>">
                                        ADID=<%=currentAssayIdHeader."assayName"%></a>
                                    </th>
                                </g:if>
                                <%rowsToSkipBeforeNextAssayid--%>
                                <th class="molSpreadSheetHeadData">${colHeader}
                                </th>



                                <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                                    <% SpreadSheetActivityStorage spreadSheetActivityStorage = molSpreadSheetData?.findSpreadSheetActivity(rowCnt, currentRowCounter) %>
                                    <% int currentCol = currentRowCounter %>
                                        <div>
                                            <g:if test="${spreadSheetActivityStorage != null}">
                                                <td class="molSpreadSheet" property="var${currentCol}">

                                                    <% HillCurveValueHolder hillCurveValueHolder = spreadSheetActivityStorage.getHillCurveValueHolderList()[0] %>
                                                      <p>

                                                    <g:if test="${hillCurveValueHolder?.identifier}">
                                                        <div data-detail-id="drc_${spreadSheetActivityStorage.sid}_${colCnt}"
                                                             class="drc-popover-link btn btn-link"
                                                             data-original-title="${hillCurveValueHolder.identifier}"
                                                             data-html="true"
                                                             data-trigger="hover">
                                                            ${hillCurveValueHolder.toString()}</div>
                                                    </g:if>
                                                    </p>


                                                    <g:if test="${hillCurveValueHolder?.conc?.size() > 1}">
                                                        <div class='popover-content-wrapper'
                                                             id="drc_${spreadSheetActivityStorage.sid}_${colCnt}"
                                                             style="display: none;">
                                                            <div class="center-aligned">
                                                                <img alt="${spreadSheetActivityStorage.sid}"
                                                                     title="Substance Id : ${spreadSheetActivityStorage.sid}"
                                                                     src="${createLink(
                                                                             controller: 'doseResponseCurve',
                                                                             action: 'doseResponseCurve',
                                                                             params: [
                                                                                     sinf: hillCurveValueHolder?.sInf,
                                                                                     s0: hillCurveValueHolder?.s0,
                                                                                     ac50: hillCurveValueHolder?.slope,
                                                                                     hillSlope: hillCurveValueHolder?.coef,
                                                                                     concentrations: hillCurveValueHolder?.conc,
                                                                                     activities: hillCurveValueHolder?.response,
                                                                                     yAxisLabel: hillCurveValueHolder?.identifier
                                                                             ]
                                                                     )}"/>
                                                            </div>
                                                        </div>

                                                    </g:if>

                                                    </td>
                                            </g:if>
                                            <g:else>
                                                <td class="molSpreadSheet" property="var${currentRowCounter}">
                                                    Not tested in this experiment
                                                </td>
                                            </g:else>
                                        </div>
                                 </g:each>

                            </tr>
                        </g:if>
                        <% currentRowCounter++ %>
                    </g:each>
                </g:if>
             </table>
        </g:if>
        <g:else>
            <div class="alert">
                <button class="close" data-dismiss="alert">×</button>
                Cannot display molecular spreadsheet without at least one assay or one compound
            </div>
        </g:else>

    </div>

    <div class="span10 pull-right">
        <export:formats/>
    </div>
</div>
