<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <g:layoutHead/>
    <r:layoutResources/>
    <title>BARD: Catalog of Assay Protocols</title>
    <r:external uri="/css/layout.css"/>
    <r:external uri="/css/table.css"/>
</head>

<body>
<div class="navbar navbar-inverse navbar-static-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span12">
                    <a class="brand" href="/BARD">
                        <img width="140" height="43" src="${resource(dir: 'images', file: 'bard_logo_small.png')}"
                             alt="BioAssay Research Database"/>
                    </a>
                    <ul class="nav">
                        <li><a href="/BARD">CAP</a></li>

                            <sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                        Admin
                                        <b class="caret"></b>
                                    </a>

                                    <ul class="dropdown-menu">
                                        <li class="controller"><g:link
                                                controller="register">Register External BARD User</g:link></li>
                                        <li class="controller"><g:link controller="register"
                                                                       action="listUsersAndGroups">List External BARD Users</g:link></li>
                                        <li class="controller"><g:link controller="person"
                                                                       action="list">List Person Table</g:link></li>
                                        <li class="controller"><g:link controller="mergeAssayDefinition"
                                                                       action="show">Merge Assays</g:link></li>
                                        <li class="controller"><g:link controller="assayDefinition"
                                                                       action="assayComparisonReport">Compare Assays</g:link></li>
                                    </ul>

                                </li>
                            </sec:ifAnyGranted>

                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Assay Definitions
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="/BARD/assayDefinition/findById">Search by Assay Definition ID</a></li>
                                <li><a href="/BARD/assayDefinition/findByName">Search by Assay Definition Name</a></li>
                                <li><a href="/BARD/assayDefinition/assayComparisonReport">Compare Assays</a></li>
                            </ul>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Projects
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="/BARD/project/findById">Search by Project ID</a></li>

                                <li><a href="/BARD/project/findByName">Search by Project Name</a></li>
                                <li><a href="/BARD/project/create">Create a New Project</a></li>

                            </ul>
                        </li>
                        <li>
                            <g:link url="${grailsApplication.config.bard.home.page}">Bard Web Client</g:link>
                        </li>
                    </ul>
                    <sec:ifLoggedIn>
                        <g:form class="navbar-form pull-right" name="logoutForm" controller="logout">
                            <span
                                    style="color: white; font-weight: bold;">Logged in as: <sec:username/></span>&nbsp;&nbsp;
                            <button type="submit" class="btn">Logout</button>
                        </g:form>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <g:form class="navbar-form pull-right" name="loginForm" controller="login">
                            Not logged in&nbsp;&nbsp;
                            <button type="submit" class="btn">Login</button>
                        </g:form>
                    </sec:ifNotLoggedIn>
                </div>
            </div>
        </div>
    </div>
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

    <div class="row-fluid">
        <div class="span12 cap-footer">
            <b>Version:</b> ${grailsApplication?.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
        </div>
    </div>
</div>

%{-- <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>  --}%

<r:layoutResources/>
</body>
</html>