<%@ page import="molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; bardqueryapi.SpreadSheetActivity; bardqueryapi.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="bardqueryapi.MolSpreadSheetCellType;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService; molspreadsheet.MolSpreadSheetCell;" %>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>Molecular spreadsheet</title>
    <r:require modules="molecularSpreadSheet,promiscuity"/>
</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <div class="span12 pagination-centered">
            <h1>Molecular Spreadsheet</h1>
        </div>
    </div>
</div>

<div id="molecularSpreadSheet" href="${createLink(controller:'molSpreadSheet' , action: 'molecularSpreadSheet')}">

</div>
</body>
</html>
