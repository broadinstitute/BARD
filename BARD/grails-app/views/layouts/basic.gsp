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
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Assays
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="/BARD/assayDefinition/findById">Find assay by ID</a></li>
                                <li><a href="/BARD/assayDefinition/findByName">Find assay by Name</a></li>
                            </ul>
                        </li>
                        <li>
                            <g:link url="${grailsApplication.config.bard.home.page}">Bard Web Client</g:link>
                        </li>
                    </ul>
                    <sec:ifLoggedIn>
                        <g:form class="navbar-form pull-right" name="logoutForm" controller="logout">
                            Logged in as: <span
                                style="color: white; font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;
                            <button type="submit" class="btn btn-primary">Logout</button>
                        </g:form>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <g:form class="navbar-form pull-right" name="loginForm" controller="login">
                            Not logged in&nbsp;&nbsp;
                            <button type="submit" class="btn btn-primary">Login</button>
                        </g:form>
                    </sec:ifNotLoggedIn>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12"><br><br>

            <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                            default="Loading&hellip;"/></div>
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