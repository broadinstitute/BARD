<%@ page import="molspreadsheet.HillCurveValueHolder; molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>
<script type="text/javascript">
    jQuery.extend( jQuery.fn.dataTableExt.oSort, {
        "num-html-pre": function ( a ) {
            var x = a.replace( /<.*?>/g, "" );
            return parseFloat( x );
        },

        "num-html-asc": function ( a, b ) {
            return ((a < b) ? -1 : ((a > b) ? 1 : 0));
        },

        "num-html-desc": function ( a, b ) {
            return ((a < b) ? 1 : ((a > b) ? -1 : 0));
        }
    } );
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
        $("[rel=tooltip]").tooltip();
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
<div class="row-fluid">
    <g:if test="${flash.message}">
        <div class="span12" role="status"><p style="color: #3A87AD;">${flash.message}</p></div>
    </g:if>

    <div class="span2">
        <g:render template="../bardWebInterface/facets"
                  model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
    </div>

    <div class="span10">
        <g:if test="${molSpreadSheetData?.getRowCount() > 0}">
            <g:set var="columnWidth" value="${100.0 / ((molSpreadSheetData?.getColumnCount() - 1) as float)}"/>
                <label class="checkbox" class="pull-left">
                    <input type="checkbox" defaultChecked="checked" checked name="showPromiscuityScores"
                           id="showPromiscuityScores">
                    Hide Promiscuity Scores
                </label>
                <a href="../molSpreadSheet/index?transpose=true&norefresh=true" class="pull-right tranposeSymbol" title="Transpose array elements">T</a>
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
                        <g:imageCell cid="${cid}" smiles="${retrievedSmiles}"/>
                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <g:cidCell  cid="${cid}"/>
                    </td>

                    <td class="molSpreadSheet">
                        <g:promiscuityCell cid="${cid}"/>
                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <g:activeVrsTestedCell activeVrsTested="${activeVrsTested}"/>
                    </td>
                    <g:if test="${molSpreadSheetData.getColumnCount() > 4}">
                        <g:each var="colCnt" in="${4..(molSpreadSheetData.getColumnCount() - 1)}">
                            <g:exptDataCell colCnt="${colCnt}" spreadSheetActivityStorage="${molSpreadSheetData?.findSpreadSheetActivity(rowCnt, colCnt)}"/>
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

    <div class="span10 pull-right">
<g:if test="${molSpreadSheetData?.getRowCount() > 0}">
    <export:formats formats="['csv', 'excel', 'pdf']"/>
</g:if>
    </div>
</div>

