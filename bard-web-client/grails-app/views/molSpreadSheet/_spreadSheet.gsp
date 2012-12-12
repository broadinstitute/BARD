<%@ page import="molspreadsheet.HillCurveValueHolder; molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>

<script type="text/javascript">
    $(document).ready(function () {
        $('#showPromiscuityScores').click(function () {
            $('td:nth-child(3)').toggle();
            $('#promiscuitycol').toggle();
            PromiscuityHandler.setup();
        });
        $('td:nth-child(3)').toggle();
        $('#promiscuitycol').toggle();
        $("[rel=tooltip]").tooltip();
        $('#molspreadsheet').dataTable({
                    "bStateSave":true,
                    "aoColumnDefs":[
                        {"bSortable":false, 'aTargets':[0]}
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
            <g:set var="columnWidth" value="${100.0 / ((molSpreadSheetData?.getColumnCount() - 1) as float)}"/>
            <label class="checkbox">
                <input type="checkbox" defaultChecked="checked" checked name="showPromiscuityScores"
                       id="showPromiscuityScores">
                Hide Promiscuity Scores
            </label>
            <table cellpadding="0" cellspacing="0" border="0" class="molSpreadSheet display" id="molspreadsheet"
                   width="100%">
                <thead>
                <tr class="molSpreadSheetHead">
                    <th rowspan="2" class="molSpreadSheetImg">Molecular structure</th>
                    <th rowspan="2" class="molSpreadSheetHeadData" width="<%=columnWidth%>%">CID</th>
                    <% int column = 0 %>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${column == 2}">
                            <th rowspan="2" class="molSpreadSheetHeadData" id="promiscuitycol"
                                width="<%=columnWidth%>%"><%=molSpreadSheetData.mapColumnsToAssay[column]%><br/>${colHeader}
                            </th>

                        </g:if>

                        <g:if test="${column == 3}">
                            <th rowspan="2" class="display molSpreadSheetHeadData"
                                width="<%=columnWidth%>%">${colHeader}</th>
                        </g:if>
                        <% column++ %>
                    </g:each>
                    <g:each var="assayColumn" in="${molSpreadSheetData.determineResponseTypesPerAssay()}">
                        <th class="molSpreadSheetHeadData" rel="tooltip"
                            colspan="<%=assayColumn."numberOfResultTypes"%>"
                            title="<%=assayColumn."fullAssayName"%>"><a
                                href="../bardWebInterface/showAssay/<%=assayColumn."assayName"%>">
                            ADID=<%=assayColumn."assayName"%></a>
                        </th>
                    </g:each>
                </tr>
                <tr class="molSpreadSheetHead">
                    <% column = 0 %>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${column > 3}">
                            <th class="molSpreadSheetHeadData">${colHeader}
                            </th>
                        </g:if>
                        <% column++ %>
                    </g:each>
                </tr>

                </thead>
                <tbody>
                <% Integer rowCount = 0 %>
                <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                    <% String retrievedSmiles = """${molSpreadSheetData?.displayValue(rowCnt, 0)."smiles"}""".toString() %>
                    <% String cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
                    <% String activeVrsTested = """${molSpreadSheetData?.displayValue(rowCnt, 3)?."value"}""".toString() %>
                    <g:if test="${((rowCount++) % 2) == 0}">
                        <tr class="molSpreadSheet">
                    </g:if>
                    <g:else>
                        <tr class="molSpreadSheetGray">
                    </g:else>
                    <td class="molSpreadSheetImg" property="struct">
                        <g:if test="${retrievedSmiles == 'Unknown smiles'}">
                            ${retrievedSmiles}
                        </g:if>
                        <g:else>
                            <g:compoundOptions sid="${cid}" cid="${cid}" smiles="${retrievedSmiles}" imageWidth="150" imageHeight="120"/>
                        </g:else>

                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <g:link controller="bardWebInterface" action="showCompound" id="${cid}">${cid}</g:link>

                    </td>

                    <td class="molSpreadSheet">
                        <div class="promiscuity"
                             href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: cid])}"
                             id="${cid}_prom"></div>
                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <div>
                            <span class="badge badge-info">${activeVrsTested}</span>
                        </div>
                    </td>
                    <g:if test="${molSpreadSheetData.getColumnCount() > 4}">
                        <g:each var="colCnt" in="${4..(molSpreadSheetData.getColumnCount() - 1)}">

                            <% SpreadSheetActivityStorage spreadSheetActivityStorage = molSpreadSheetData?.findSpreadSheetActivity(rowCnt, colCnt) %>
                            <% int currentCol = colCnt %>
                            <g:if test="${spreadSheetActivityStorage != null}">

                                <% HillCurveValueHolder hillCurveValueHolder = spreadSheetActivityStorage.getHillCurveValueHolderList()[0] %>

                                <td class="molSpreadSheet" property="var${currentCol}">
                                <p>

                               <g:if test="${hillCurveValueHolder?.identifier}">
                                <div data-detail-id="drc_${spreadSheetActivityStorage.sid}_${colCnt}"
                                     class="drc-popover-link btn btn-link"
                                     data-original-title="${hillCurveValueHolder.identifier}"
                                     data-html="true"
                                     data-trigger="hover">
                                    ${hillCurveValueHolder.toString()}</div>
                               </g:if>
                                %{--<g:else>--}%
                                    %{--${hillCurveValueHolder.toString()}--}%
                                %{--</g:else>--}%
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
                                <td class="molSpreadSheet" property="var${colCnt}">
                                    Not tested in this experiment
                                </td>
                            </g:else>

                        </g:each>
                    </g:if>
                    </tr>
                </g:each>
                </tbody>
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

