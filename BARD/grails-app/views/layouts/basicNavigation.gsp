%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

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
                    <a class="brand" href="${request.contextPath}">
                        <img width="140" height="43" src="${resource(dir: 'images', file: 'bard_logo_small.png')}"
                             alt="BioAssay Research Database"/>
                    </a>
                    <ul class="nav">
                        <li><a href="${request.contextPath}">CAP</a></li>

                        <sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    Admin
                                    <b class="caret"></b>
                                </a>

                                <ul class="dropdown-menu">
                                          <li class="controller"><g:link controller="person"
                                                                   action="list">List Person Table</g:link></li>
                                    <li class="controller"><g:link controller="moveExperiments"
                                                                   action="show">Move Experiments</g:link></li>
                                    <li class="controller"><g:link controller="mergeAssayDefinition"
                                                                   action="show">Merge Assays</g:link></li>
                                    <li class="controller"><g:link controller="assayDefinition"
                                                                   action="assayComparisonReport">Compare Assays</g:link></li>
                                    <li class="controller"><g:link controller="splitAssayDefinition"
                                                                   action="show">Split Assays</g:link></li>
                                </ul>

                            </li>
                        </sec:ifAnyGranted>

                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Assay Definitions
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="myAssays">My Assay Definitions</g:link></li>
                                %{--//You should belong to at least one team to create--}%
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="create">Create Assay Definition</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="assayComparisonReport">Compare Assay Definitions</g:link></li>
                            </ul>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Projects
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="controller"><g:link controller="project"
                                                               action="myProjects">My Projects</g:link></li>
                                %{--//You should belong to at least one team to create--}%
                                <li class="controller"><g:link controller="project"
                                                               action="create">Create a New Project</g:link></li>
                            </ul>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Panels
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="controller"><g:link controller="panel"
                                                               action="myPanels">My Panels</g:link></li>

                                %{--//You should belong to at least one team to create--}%
                                <li class="controller"><g:link controller="panel"
                                                               action="create">Create New Panel</g:link></li>
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
                            <button type="submit" class="btn" id="logoutButton">Logout</button>
                        </g:form>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <g:form class="navbar-form pull-right" name="loginForm" controller="login">
                            Logged in anonymously&nbsp;&nbsp;
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

<r:layoutResources/>
</body>
</html>
