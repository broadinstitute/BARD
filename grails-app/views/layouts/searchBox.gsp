<%@ page import="bardqueryapi.StructureSearchType" %>
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
                    url = '${request.contextPath}/chemAxon/marvinSketch';
                    $("#modalIFrame").attr('src',url);
                });
            });
    </r:script>

    <g:layoutHead/>
    <r:layoutResources />
    <r:require modules="bootstrap"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bard.css')}" type="text/css">
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="row-fluid">
                <div class="span3"><a href="${createLink(controller:'BardWebInterface',action:'index')}"><img src="${resource(dir: 'images', file: 'bardLogo.png')}" alt="BioAssay Research Database" /></a></div>
                <g:form name="aidForm" controller="bardWebInterface" action="search" class="form-search">
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
                        <g:submitButton name="search" value="Search" class="btn btn-primary"/>
                    </div>
                </g:form>
                <div class="span2">
                    <div class="well">
                        <h5><i class="icon-shopping-cart"></i> Query Cart</h5>
                        <div class="btn-group">
                            <a class="btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                                <i class="icon-eye-open"></i>
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="#">Visualize as Molecular Spreadsheet</a></li>
                                <li><a href="#">Visualize in Advanced Analysis Client</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <g:layoutBody/>
    <div class="row-fluid">
        <div class="span12">
            <div class="row-fluid">
                <div class="span2 offset10">
                    <p class="right-aligned">
                        <a href="http://www.chemaxon.com/"><img src="${resource(dir: 'images', file: 'chemaxon_logo.gif')}" alt="Powered by ChemAxon" /></a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
<g:javascript library="application"/>
<r:layoutResources />
<r:require modules="bootstrap"/>

<%-- MarvinSketch's modal window --%>
<div class="modal hide" id="modalDiv">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Draw or paste a structure</h3>
    </div>
    <div>
        <iframe name="modalIFrame" id="modalIFrame" width="550px" height="485px" marginWidth="0"
                marginHeight="0" frameBorder="0"
                scrolling="auto">
        </iframe>
    </div>
    <div class="modal-footer">
        <g:form name="structureSearchForm" id="structureSearchForm" controller="bardWebInterface" action="structureSearch" class="form-inline">
            <g:hiddenField name="smiles" id='hiddenFieldSmiles'/>
            <div class="control-group"><div class="controls">
                <g:radioGroup name="structureSearchType"
                              values="${StructureSearchType.values()}"
                              value="${StructureSearchType.SUB_STRUCTURE}"
                              labels="${StructureSearchType.values().collect{it.description}}">
                    <label class="radio">
                        ${it.radio} ${it.label}
                    </label>
                </g:radioGroup>
            </div></div>
            <a href="#" class="btn" data-dismiss="modal">Close</a>
            <g:submitButton name="searchButton" id="searchButton" value="Search" class="btn btn-primary"/>
        </g:form>
    </div>
</div>
</body>
</html>