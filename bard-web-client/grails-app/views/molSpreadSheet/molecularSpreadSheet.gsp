<%@ page import="molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>
<r:require module="export"/>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>Molecular spreadsheet</title>
    <r:require modules="molecularSpreadSheet,promiscuity,compoundOptions"/>
</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <div class="span12 pagination-centered">
            <h1>Molecular Spreadsheet</h1>
        </div>
    </div>
</div>

<div id="molecularSpreadSheet" href="${createLink(controller: 'molSpreadSheet', action: 'molecularSpreadSheet', params: [pid:pid, cid:cid, norefresh:norefresh, transpose:transpose])}">
 </div>

<g:render template="spreadsheetColorKey" />

</body>
</html>
