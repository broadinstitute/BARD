<%@ page import="molspreadsheet.HillCurveValueHolder; molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>

<script type="text/javascript">
    $(document).ready(function() {
        $('#showPromiscuityScores').click(function() {
            $('td:nth-child(3), th:nth-child(3)').toggle();
        });
    });
</script>

<div class="row-fluid">
    <div class="span2">
        <g:render template="../bardWebInterface/facets"
                  model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
    </div>

    <div class="span10">
        <g:if test="${molSpreadSheetData?.getColumnCount() > 0}">
            <label class="checkbox">
                <input type="checkbox" defaultChecked="unchecked" name="showPromiscuityScores" id="showPromiscuityScores">
                Hide Promiscuity Scores
            </label>
            <table class="molSpreadSheet">
                <thead>
                <tr class="molSpreadSheetHead">
                    <th class="molSpreadSheetImg sortable">Molecular structure</th>

                    <th class="molSpreadSheetHeadData sortable">CID</th>
                    <% int looper = 2 %>
                    <g:each var="colHeader" in="${molSpreadSheetData?.getColumns()}">
                        <g:if test="${looper > 3}">
                            <th class="molSpreadSheetHeadData sortable">${colHeader}</th>
                        </g:if>
                        <g:else>
                            <% looper++ %>
                        </g:else>
                    </g:each>
                </tr>
                </thead>
                <tbody>
                <% Integer rowCount = 0 %>
                <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount() - 1)}">
                    <% String retrievedSmiles = """${molSpreadSheetData?.displayValue(rowCnt, 0)["smiles"]}""".toString() %>
                    <% String cid = """${molSpreadSheetData?.displayValue(rowCnt, 1)?."value"}""".toString() %>
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
                            <div data-detail-id="smiles_${cid}" class="pop_smiles btn btn-link"
                                 data-original-title="Copy SMILES for structure to clipboard"
                                 data-title="Copy SMILES for structure to clipboard"
                                 data-trigger="click" data-placement="left" data-content="${retrievedSmiles}">
                                <img alt="${retrievedSmiles}" title="Click to Copy SMILES"
                                     src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: retrievedSmiles, width: 150, height: 120])}"/>
                            </div>
                        </g:else>

                    </td>
                    <td class="molSpreadSheet" property="cid">
                        <g:link controller="bardWebInterface" action="showCompound" id="${cid}"
                               >${cid}</g:link>

                    </td>
                    <td class="molSpreadSheet">
                        <div class="promiscuity"
                             href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: cid])}"
                             id="${cid}_prom"></div>
                    </td>
                    <g:if test="${molSpreadSheetData.getColumnCount() > 3}">
                        <g:each var="colCnt" in="${3..(molSpreadSheetData.getColumnCount() - 1)}">
                            <% SpreadSheetActivityStorage spreadSheetActivityStorage = molSpreadSheetData?.findSpreadSheetActivity(rowCnt, colCnt) %>
                            <% int currentCol =  colCnt %>
                            <g:if test="${spreadSheetActivityStorage != null}">

                                %{--<g:each var="subColCnt" in="${0..(spreadSheetActivityStorage.getHillCurveValueHolderList().size()-1)}">--}%

                                    <% HillCurveValueHolder hillCurveValueHolder =  spreadSheetActivityStorage.getHillCurveValueHolderList()[0] %>

                                    <td class="molSpreadSheet" property="var${currentCol}">
                                    <p>


                                    <div data-detail-id="drc_${spreadSheetActivityStorage.sid}_${colCnt}"
                                         class="drc-popover-link btn btn-link"
                                         data-original-title="chg->spreadSheetActivityStorage.hillCurveValueId"
                                         data-html="true"
                                         data-trigger="hover">
                                        ${hillCurveValueHolder.toString()}</div>
                                    %{--${molSpreadSheetData?.displayValue(rowCnt, colCnt, subColCnt)?."value"}</div>--}%
                                    </p>



                                    <div class='popover-content-wrapper' id="drc_${spreadSheetActivityStorage.sid}_${colCnt}"
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

                                </td>
                                %{--</g:each>--}%
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

</div>

