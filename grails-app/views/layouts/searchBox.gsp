<%@ page import="bard.core.StructureSearchParams" %>
<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="BARD"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">

    <r:script>
            $(document).ready(function () {
                $('#modalDiv').modal({
                    show: false
                }).css({
                    height: '655',
                    width: '565'
                });
                $('#modalDiv').on('show', function () {
                    parent.$('#searchButton').click(function () {
                        var structureSearchTypeSelected = parent.$('input:radio[name=structureSearchType]:checked').val();
                        var marvinSketch = $('#MarvinSketch')[0];
                        var smiles = marvinSketch.getMol('smiles');

                        //construct the query into a form that we want
                        var constructedSearch =structureSearchTypeSelected + ":" + smiles;
                        parent.$('#searchString').attr('value', constructedSearch);
                        parent.$('#aidForm').submit();

                   });
            });
        });
    </r:script>

    <r:script>
        var trackStatus=0;
        var ajaxLocation='#cartIdentRefill'
        $(document).ready(function(){
            $(".trigger").click(function(){
                $(".panel").toggle("fast");
                $(this).toggleClass("active");
                if (trackStatus==1){
                    trackStatus = 0;
                    ajaxLocation='#cartIdentRefill';
                } else   {
                    trackStatus = 1;
                    ajaxLocation='#sarCartRefill';
                }
                return false;
            });
        });
    </r:script>

    <g:layoutHead/>
    <r:require modules="core,bootstrap,search,common" />
    <r:layoutResources />
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3"><a href="${createLink(controller:'BardWebInterface',action:'index')}"><img src="${resource(dir: 'images', file: 'bardLogo.png')}" alt="BioAssay Research Database" /></a></div>
        <g:form name="aidForm" controller="bardWebInterface" action="search" class="form-search" id="aidForm">
            <div class="span6" style="margin-top: 20px;">
                <div class="control-group">
                    <div class="controls">
                        <g:textField id="searchString" name="searchString" value="${params?.searchString}"
                                     class="input-block-level"/>
                        <p class="right-aligned"><i class="icon-search"></i> <a data-toggle="modal" href="#modalDiv">Create a structure for a search</a></p>
                    </div>
                </div>
            </div>
            <div class="span1" style="margin-top: 20px;">
                <g:submitButton name="search" value="Search" class="btn btn-primary" id="searchButton"/>
            </div>
        </g:form>

            <div class="span2">
                <div class="well">
                    <div class="row-fluid">
                        <h5><i class="icon-shopping-cart"></i><a class="trigger" href="#">Query Cart<span class="tinyclickfordetails">(click for details)</span></a></h5>
                    </div>
                    <g:render template="queryCartIndicator"/>
                </div>
            </div>

    </div>
    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>
    <g:layoutBody/>
    <div class="row-fluid">
        <div class="span2 offset10">
            <p class="right-aligned">
                <a href="http://www.chemaxon.com/"><img src="${resource(dir: 'images', file: 'chemaxon_logo.gif')}" alt="Powered by ChemAxon" /></a>
            </p>
        </div>
    </div>
</div>
<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
<g:javascript library="application"/>
<r:require modules="core,bootstrap,search,common"/>
<r:layoutResources />

<%-- MarvinSketch's modal window --%>
<div class="modal hide" id="modalDiv">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Draw or paste a structure</h3>
    </div>
    <div class="modal-body">
        <g:render template="/chemAxon/marvinSketch" />
    </div>
    <div class="modal-footer">
            <div class="control-group"><div class="controls">
                <g:radioGroup name="structureSearchType"
                              values="${StructureSearchParams.Type.values()}"
                              value="${StructureSearchParams.Type.Substructure}"
                              labels="${StructureSearchParams.Type.values()}">
                    <label class="radio inline">
                        ${it.radio} ${it.label}
                    </label>
                </g:radioGroup>
            </div></div>
            <a href="#" class="btn" data-dismiss="modal">Close</a>
            <g:submitButton name="searchButton" id="searchButton" value="Search" class="btn btn-primary" data-dismiss="modal"/>
    </div>
</div>



<div class="panel">
    <a class="trigger" href="#">Click to hide query cart</a>
    <g:render template="sarCartContent"/>
</div>



</body>
</html>