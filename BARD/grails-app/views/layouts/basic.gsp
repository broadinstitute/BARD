<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <g:layoutHead/>

    <title>BARD: Catalog of Assay Protocols</title>
    <r:external uri="/css/layout.css"/>
    <r:external uri="/css/table.css"/>
    <r:external uri="/css/bardHomepage/BardHeaderFooter.css"/>
    <script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js"></script>
    <script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js"></script>
    <r:require modules="autocomplete"/>
    <r:require module="cart"/>
    <r:require module="idSearch"/>
    <%@ page defaultCodec="none" %>
    <%@ page import="bardqueryapi.IDSearchType" %>
    <r:layoutResources/>

</head>

<body>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>

<div class="container-fluid">

    <header class="navbar navbar-static-top" id="header">
        <div class="container-fluid">

            <strong class="logo"><a
                    href="${createLink(controller: 'BardWebInterface', action: 'index')}">BARD BioAssay Research Database</a>
            </strong>

            <nav class="nav-panel">
                <div class="center-aligned">
                    <div id="login-form">
                        <sec:ifLoggedIn>

                            <g:form name="logoutForm" controller="bardLogout">
                                Logged in as: <span
                                    style="font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;
                                <button type="submit" class="btn btn-mini" id="logoutButton">Logout</button>
                            </g:form>
                        </sec:ifLoggedIn>
                        <sec:ifNotLoggedIn>
                            <g:form name="loginForm" controller="bardLogin">
                                Not logged in&nbsp;&nbsp;
                                <button type="submit" class="btn btn-mini">Login</button>
                            </g:form>
                            <g:if env="development">
                                OR
                                <a class="btn btn-mini" id='signin'>Sign in with your Email</a>
                            </g:if>
                        </sec:ifNotLoggedIn>
                    </div>

                </div>

                <div class="qcart">

                    <div class="well well-small">
                        <g:if test="${flash.searchString}">
                            <g:include controller="queryCart" action="refreshSummaryView"
                                       params="[searchString: flash.searchString]"/>
                        </g:if>
                        <g:elseif test="${params?.searchString}">
                            <g:include controller="queryCart" action="refreshSummaryView"
                                       params="[searchString: params.searchString]"/>
                        </g:elseif>
                        <g:else>
                            <g:include controller="queryCart" action="refreshSummaryView"/>
                        </g:else>
                    </div>

                    <div class="panel" style="z-index: 10">
                        <a class="trigger" href="#">Click to hide query cart</a>
                        <g:if test="${flash.searchString}">
                            <g:include controller="queryCart" action="refreshDetailsView"
                                       params="[searchString: flash.searchString]"/>
                        </g:if>
                        <g:elseif test="${params?.searchString}">
                            <g:include controller="queryCart" action="refreshDetailsView"
                                       params="[searchString: params.searchString]"/>
                        </g:elseif>
                        <g:else>
                            <g:include controller="queryCart" action="refreshDetailsView"/>
                        </g:else>
                    </div>

                </div>
            </nav>
        </div>
    </header>


    <div class="search-panel">
        <div class="container-fluid">
            <div class="search-block">
            %{--<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="form-inline">--}%
                <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm"
                        class="search-form">
                    <div class="row-fluid">
                        <div class="search-field input-append" style="display: table; width:100%;">
                            <div class="text-field">
                                <g:if test="${flash?.searchString}">
                                    <g:textField id="searchString" name="searchString" value="${flash.searchString}"/>
                                </g:if>
                                <g:elseif test="${params?.searchString}">
                                    <g:textField id="searchString" name="searchString" value="${params?.searchString}"/>
                                </g:elseif>
                                <g:else>
                                    <g:textField id="searchString" name="searchString" value=""/>
                                </g:else>
                            </div>

                            <div class="btn-field">
                                <button type="submit" name="search" class="btn btn-primary"
                                        id="searchButton">Search</button>
                            </div>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="span10">
                            %{--<r:script> $(document).on('click', '#structureSearchLink', showJSDrawEditor)</r:script>--}%
                            <div class="pull-right"><g:link controller="bardWebInterface" action="jsDrawEditor">
                                <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"
                                     alt="Draw or paste a structure"
                                     title="Draw or paste a structure"/> Draw or paste a structure</g:link> or <a
                                    data-toggle="modal" href="#idModalDiv">list of IDs for search</a></div>
                        </div>
                    </div>
                </g:form>
            </div>
            <a href='/BARD/bardWebInterface/navigationPage' style="float: right; color: white; margin-bottom: 5px"
               class="btn btn-primary">Submissions</a>
        </div>
    </div>


    <div class="modal hide" id="idModalDiv">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>

            <h3>Enter a Comma separated list of IDs</h3>
        </div>

        <div class="modal-body">
            <textarea class="field span12" id="idSearchString" name="idSearchString" rows="15"></textarea>
        </div>

        <div class="modal-footer">
            <g:form name="idSearchForm" class="form-inline">
                <div>
                    <g:radioGroup name="idSearchType"
                                  values="${IDSearchType.values()}"
                                  value="${IDSearchType.ALL}"
                                  labels="${IDSearchType.values().label}">
                        <label class="radio inline">
                            ${it.radio} ${it.label}
                        </label>
                    </g:radioGroup>
                </div>

                <br>

                <div>
                    <a href="#" class="btn" data-dismiss="modal" id="closeButton21">Close</a>
                    <a href="#" class="idSearchButton btn btn-primary" data-dismiss="modal">Search</a>
                </div>
            </g:form>
        </div>

    </div>

    %{--<div class="modal hide" id="idModalDiv">--}%
        %{--<div class="modal-header">--}%
            %{--<a class="close" data-dismiss="modal">×</a>--}%

            %{--<h3>Enter a Comma separated list of IDs</h3>--}%
        %{--</div>--}%

        %{--<div class="modal-body">--}%
            %{--<textarea class="field span12" class="idSearchString" name="idSearchString" rows="15"></textarea>--}%
        %{--</div>--}%

        %{--<div class="modal-footer">--}%
            %{--<g:form name="idSearchForm" class="form-inline">--}%
                %{--<div>--}%
                    %{--<g:radioGroup name="idSearchType"--}%
                                  %{--values="${IDSearchType.values()}"--}%
                                  %{--value="${IDSearchType.ALL}"--}%
                                  %{--labels="${IDSearchType.values().label}">--}%
                        %{--<label class="radio inline">--}%
                            %{--${it.radio} ${it.label}--}%
                        %{--</label>--}%
                    %{--</g:radioGroup>--}%
                %{--</div>--}%

                %{--<br>--}%

                %{--<div>--}%
                    %{--<a href="#" class="btn" data-dismiss="modal" id="closeButton22">Close</a>--}%
                    %{--<a href="#" class="idSearchButton btn btn-primary" data-dismiss="modal">Search</a>--}%
                %{--</div>--}%
            %{--</g:form>--}%
        %{--</div>--}%

    %{--</div>--}%



    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>

</div>


<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="spinner-container">
                <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                default="Loading&hellip;"/></div>
            </div>
            <g:layoutBody/>
        </div>
    </div>


    <div class="row-fluid bard-footer">
        <footer id="footer">
            <div class="footer-columns">
                <div class="container-fluid">
                    <div class="row-fluid">

                        <div class="span5 bard-footer-versioninfo muted">
                            <div>
                                <b>Version:</b> ${grailsApplication.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
                            </div>
                        </div>

                        <div class="span5">
                        </div>


                        <div class="span2 right-aligned">
                            <a href="http://www.chemaxon.com/" target="chemAxon"><img
                                    src="${resource(dir: 'images/bardHomepage', file: 'logo-by.png')}"
                                    alt="Powered by ChemAxon"/></a>
                        </div>
                    </div>
                </div>
            </div>

            %{--The bottom line of the whole page--}%
            <div class="footer-info">
                <div class="container-fluid">
                    <ul>
                        <li><a href="#">National Institutes of Health</a></li>
                        <li><a href="#">U.S. Department of Health and Human Services</a></li>
                        <li><a href="#">USA.gov – Government Made Easy</a></li>
                    </ul>
                </div>
            </div>
        </footer>
    </div>

</div>

<r:layoutResources/>
</body>
</html>

%{--<!DOCTYPE html>--}%
%{--<html>--}%
%{--<head>--}%
%{--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">--}%
%{--<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">--}%
%{--<g:layoutHead/>--}%
%{--<r:layoutResources/>--}%
%{--<title>BARD: Catalog of Assay Protocols</title>--}%
%{--<r:external uri="/css/layout.css"/>--}%
%{--<r:external uri="/css/table.css"/>--}%
%{--</head>--}%

%{--<body>--}%
%{--<div class="navbar navbar-inverse navbar-static-top">--}%
%{--<div class="navbar-inner">--}%
%{--<div class="container-fluid">--}%
%{--<div class="row-fluid">--}%
%{--<div class="span12">--}%
%{--<a class="brand" href="/BARD">--}%
%{--<img width="140" height="43" src="${resource(dir: 'images', file: 'bard_logo_small.png')}"--}%
%{--alt="BioAssay Research Database"/>--}%
%{--</a>--}%
%{--<ul class="nav">--}%
%{--<li><a href="/BARD">CAP</a></li>--}%

%{--<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">--}%
%{--<li class="dropdown">--}%
%{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
%{--Admin--}%
%{--<b class="caret"></b>--}%
%{--</a>--}%

%{--<ul class="dropdown-menu">--}%
%{--<li class="controller"><g:link--}%
%{--controller="aclClass">ACL Management</g:link></li>--}%
%{--<li class="controller"><g:link--}%
%{--controller="register">Register External BARD User</g:link></li>--}%
%{--<li class="controller"><g:link controller="register"--}%
%{--action="listUsersAndGroups">List External BARD Users</g:link></li>--}%
%{--<li class="controller"><g:link controller="person"--}%
%{--action="list">List Person Table</g:link></li>--}%
%{--<li class="controller"><g:link controller="moveExperiments"--}%
%{--action="show">Move Experiments</g:link></li>--}%
%{--<li class="controller"><g:link controller="mergeAssayDefinition"--}%
%{--action="show">Merge Assays</g:link></li>--}%
%{--<li class="controller"><g:link controller="assayDefinition"--}%
%{--action="assayComparisonReport">Compare Assays</g:link></li>--}%
%{--<li class="controller"><g:link controller="splitAssayDefinition"--}%
%{--action="show">Split Assays</g:link></li>--}%
%{--</ul>--}%

%{--</li>--}%
%{--</sec:ifAnyGranted>--}%

%{--<li class="dropdown">--}%
%{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
%{--Assay Definitions--}%
%{--<b class="caret"></b>--}%
%{--</a>--}%
%{--<ul class="dropdown-menu">--}%
%{--<li class="controller"><g:link controller="assayDefinition"--}%
%{--action="groupAssays">My Assay Definitions</g:link></li>--}%
%{--<li class="controller"><g:link controller="assayDefinition"--}%
%{--action="findById">Search by Assay Definition ID</g:link></li>--}%
%{--<li class="controller"><g:link controller="assayDefinition"--}%
%{--action="findByName">Search by Assay Definition Name</g:link></li>--}%
%{--<li class="controller"><g:link controller="assayDefinition"--}%
%{--action="create">Create Assay Definition</g:link></li>--}%
%{--<li class="controller"><g:link controller="assayDefinition"--}%
%{--action="assayComparisonReport">Compare Assay Definitions</g:link></li>--}%
%{--</ul>--}%
%{--</li>--}%
%{--<li class="dropdown">--}%
%{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
%{--Projects--}%
%{--<b class="caret"></b>--}%
%{--</a>--}%
%{--<ul class="dropdown-menu">--}%
%{--<li class="controller"><g:link controller="project"--}%
%{--action="groupProjects">My Projects</g:link></li>--}%
%{--<li class="controller"><g:link controller="project"--}%
%{--action="findById">Search by Project ID</g:link></li>--}%
%{--<li class="controller"><g:link controller="project"--}%
%{--action="findByName">Search by Project Name</g:link></li>--}%
%{--<li class="controller"><g:link controller="project"--}%
%{--action="create">Create a New Project</g:link></li>--}%
%{--</ul>--}%
%{--</li>--}%
%{--<li class="dropdown">--}%
%{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
%{--Panels--}%
%{--<b class="caret"></b>--}%
%{--</a>--}%
%{--<ul class="dropdown-menu">--}%
%{--<li class="controller"><g:link controller="panel"--}%
%{--action="myPanels">My Panels</g:link></li>--}%

%{--<li class="controller"><g:link controller="panel"--}%
%{--action="findById">Search by Panel ID</g:link></li>--}%
%{--<li class="controller"><g:link controller="panel"--}%
%{--action="findByName">Search by Panel Name</g:link></li>--}%
%{--<li class="controller"><g:link controller="panel"--}%
%{--action="create">Create New Panel</g:link></li>--}%
%{--</ul>--}%
%{--</li>--}%
%{--<li>--}%
%{--<g:link url="${grailsApplication.config.bard.home.page}">Bard Web Client</g:link>--}%
%{--</li>--}%
%{--</ul>--}%
%{--<sec:ifLoggedIn>--}%
%{--<g:form class="navbar-form pull-right" name="logoutForm" controller="logout">--}%
%{--<span--}%
%{--style="color: white; font-weight: bold;">Logged in as: <sec:username/></span>&nbsp;&nbsp;--}%
%{--<button type="submit" class="btn" id="logoutButton">Logout</button>--}%
%{--</g:form>--}%
%{--</sec:ifLoggedIn>--}%
%{--<sec:ifNotLoggedIn>--}%
%{--<g:form class="navbar-form pull-right" name="loginForm" controller="login">--}%
%{--Not logged in&nbsp;&nbsp;--}%
%{--<button type="submit" class="btn">Login</button>--}%
%{--</g:form> OR--}%
%{--<a class="btn btn-large" id='signin'>Sign in with your Email</a>--}%
%{--</sec:ifNotLoggedIn>--}%
%{--</div>--}%
%{--</div>--}%
%{--</div>--}%
%{--</div>--}%
%{--</div>--}%

%{--<div class="container-fluid">--}%
%{--<div class="row-fluid">--}%
%{--<div class="span12">--}%
%{--<div class="spinner-container">--}%
%{--<div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"--}%
%{--default="Loading&hellip;"/></div>--}%
%{--</div>--}%
%{--<g:layoutBody/>--}%
%{--</div>--}%
%{--</div>--}%

%{--<div class="row-fluid">--}%
%{--<div class="span12 cap-footer">--}%
%{--<b>Version:</b> ${grailsApplication?.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}--}%
%{--</div>--}%
%{--</div>--}%
%{--</div>--}%

%{--<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>--}%

%{--<r:layoutResources/>--}%
%{--</body>--}%
%{--</html>--}%
