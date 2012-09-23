<%@ page import="bardqueryapi.SpreadSheetActivity; bardqueryapi.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="bardqueryapi.MolSpreadSheetCell; bardqueryapi.MolSpreadSheetCellType; bardqueryapi.MolSpreadSheetData;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>
<%
    MolSpreadSheetData   molSpreadSheetData1 = new MolSpreadSheetData("f")
        //grailsApplication.classLoader.loadClass('bardqueryapi.MolecularSpreadSheetService').newInstance()
%>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>Molecular spreadsheet</title>
</head>
<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Molecular Spreadsheet</h1>
    </div>
</div>
    <div class="row-fluid">
        <div class="span2">
            <g:render template="../bardWebInterface/facets"
            model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
        </div>
        <div class="span10">
            <table class="molSpreadSheet">
                <thead>
                <tr class="molSpreadSheetHead">
                    <g:sortableColumn property="struct" title="Molecular structure"
                                       class="molSpreadSheetImg" />
                    <g:sortableColumn property="cid" title="CID"
                                      class="molSpreadSheetHeadCid" />
                    <% int looper = 2 %>
                    <g:each  var="colHeader" in="${molSpreadSheetData.mssHeaders}">
                        <g:if test="${looper>3}">
                            <g:sortableColumn property="var${looper++}" title="${colHeader}"
                                               class="molSpreadSheetHeadData" />
                        </g:if>
                        <g:else>
                           <% looper++ %>
                        </g:else>
                    </g:each>
                </tr>
                </thead>
                <tbody>
                <% Integer rowCount = 0 %>
                <g:each var="rowCnt" in="${0..(molSpreadSheetData.getRowCount()-1)}">
                    <% String retrievedName = """${molSpreadSheetData?.displayValue( rowCnt, 0 )["name"]}""".toString()  %>
                    <% String retrievedSmiles = """${molSpreadSheetData?.displayValue( rowCnt, 0 )["smiles"]}""".toString()  %>
                    <g:if test="${((rowCount++)%2)==0}">
                        <tr class="molSpreadSheet">
                    </g:if>
                    <g:else>
                        <tr class="molSpreadSheetGray">
                    </g:else>
                        <td class="molSpreadSheetImg"  property="struct">
                             <img alt="${retrievedSmiles}" title="${retrievedName}"
                                 src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: retrievedSmiles, width: 150, height: 120])}"/>
                         </td>
                        <td class="molSpreadSheet" propert="cid">
                            ${molSpreadSheetData?.displayValue( rowCnt, 1 )?."value"}
                        </td>
                    <g:each  var="colCnt" in="${2..(molSpreadSheetData.getColumnCount()-1)}">
                        <td class="molSpreadSheet" property="var${colCnt}">

                           <% SpreadSheetActivity experimentData = molSpreadSheetData?.findSpreadSheetActivity( rowCnt, colCnt )  %>
                            <g:if test="${experimentData!=null}">
                                <img alt="" title=""
                                     src="${createLink(controller: 'doseResponseCurve', action: 'doseResponseCurve', params: [sinf: experimentData?.hillCurveValue?.sInf, s0: experimentData?.hillCurveValue?.s0, ac50: experimentData?.hillCurveValue?.slope, hillSlope: experimentData?.hillCurveValue?.coef, concentrations: experimentData?.hillCurveValue?.conc, activities: experimentData?.hillCurveValue?.response])}"/>
                                <br/><br/>
                                <a href="#" class="requestDoseResponseImage">${molSpreadSheetData?.displayValue( rowCnt, colCnt )?."value"}</a>
                            </g:if>
                            <g:else>
                                Not tested in this experiment
                            </g:else>

                            %{--<p>AC50 = ${experimentData?.hillCurveValue?.slope}</p>--}%


                        </td>
                    </g:each>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>
    %{--<div class="plot" id="plot"></div>--}%
</body>
</html>
