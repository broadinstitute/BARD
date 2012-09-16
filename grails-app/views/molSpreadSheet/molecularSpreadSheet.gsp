<%@ page import="bardqueryapi.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="bardqueryapi.MolSpreadSheetCell; bardqueryapi.MolSpreadSheetCellType; bardqueryapi.MolSpreadSheetData;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>
<%
    MolSpreadSheetData   molSpreadSheetData1 = new MolSpreadSheetData("f")
        //grailsApplication.classLoader.loadClass('bardqueryapi.MolecularSpreadSheetService').newInstance()
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Molecular spreadsheet</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">

<r:script>
    var trackStatus=0;
    var ajaxLocation='#cartIdentRefill'
    $(document).ready(function(){
        $(".trigger").click(function(){
            $(".panel").toggle("fast");
            $(this).toggleClass("active");
            if (trackStatus==1){
                ajaxLocation='#cartIdentRefill';
                trackStatus = 0;
                jQuery.ajax({  type:'POST',
                    data:{'stt':trackStatus},
                    url:'/bardwebquery/sarCart/updateOnscreenCart',
                    success:function(data,textStatus){
                        jQuery(ajaxLocation).html(data);
                    }
                });

            } else   {
                ajaxLocation='#sarCartRefill';
                trackStatus = 1;
                jQuery.ajax({  type:'POST',
                    data:{'stt':trackStatus},
                    url:'/bardwebquery/sarCart/updateOnscreenCart',
                    success:function(data,textStatus){
                        jQuery(ajaxLocation).html(data);
                    }
                });
            }
            return false;
        });
    });
</r:script>

<r:require modules="core,bootstrap,search" />
<r:layoutResources />

</head>
<body>

<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
<r:require modules="core,bootstrap,search"/>
<r:layoutResources/>


<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3"><a href="${createLink(controller: 'BardWebInterface', action: 'index')}"><img
                src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/></a>
        </div>
        <g:form name="searchForm" controller="bardWebInterface" action="search" class="form-search" id="searchForm">
            <div class="span6" style="margin-top: 20px;">
                <div class="control-group">
                    <div class="controls">
                        <g:textField id="searchString" name="searchString" value="${params?.searchString}"
                                     class="input-block-level"/>
                        <p class="right-aligned"><i class="icon-search"></i> <a data-toggle="modal"
                                                                                href="#modalDiv">Create a structure for a search</a>
                        </p>
                    </div>
                </div>
            </div>

            <div class="span1" style="margin-top: 20px;">
                <g:submitButton name="search" value="Search" class="btn btn-primary" id="searchButton"/>
            </div>
        </g:form>

        <div class="span2">
            <div class="well wellmod">
                <g:render template="../bardWebInterface/queryCartIndicator"/>
                <div class="row-fluid" style="height: 30px">
                    <h5><nobr><a class="trigger" href="#">View details/edit</a></nobr></h5>
                </div>

            </div>
        </div>

    </div>
    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>

    <div class="row-fluid">
        <div class="span2 offset10">
            <p class="right-aligned">
                <a href="http://www.chemaxon.com/"><img src="${resource(dir: 'images', file: 'chemaxon_logo.gif')}"
                                                        alt="Powered by ChemAxon"/></a>
            </p>
        </div>
    </div>
 </div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">
            <g:render template="../bardWebInterface/facets"
            model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
        </div>
        <div class="span8">
            <table width=100% height=100%>
                <g:each var="rowCnt" in="${0..(molSpreadSheetData1.getRowCount()-1)}">
                    <tr>
                        <g:each  var="colCnt" in="${0..(molSpreadSheetData1.mssHeaders.size()-1)}">
                            <td>
                                ${molSpreadSheetData1.displayValue(  colCnt, rowCnt)}
                            </td>
                        </g:each>
                    </tr>
                </g:each>
            </table>
            %{--<table width=100% height=100%>--}%
                %{--<g:each var="rowCnt" in="${0..molecularSpreadSheetService.fakeMe().getRowCount()}">--}%
                    %{--<tr>--}%
                        %{--<g:each  var="colCnt" in="${0..molecularSpreadSheetService.fakeMe().mssHeaders.size()}">--}%
                            %{--<td>--}%
                                %{--"${molecularSpreadSheetService.fakeMe().mssData["${rowCnt}_${colCnt}"]}--}%
                            %{--</td>--}%
                        %{--</g:each>--}%
                    %{--</tr>--}%
                %{--</g:each>--}%
            %{--</table>--}%
        </div>
        <div class="span2">
                    <g:render template="../bardWebInterface/sarCartContent"/>
        </div>
    </div>
</div>

</body>
</html>
