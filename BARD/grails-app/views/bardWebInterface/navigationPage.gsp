<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>BARD: Catalog of Assay Protocols</title>
</head>

<body>
<div class="container-fluid">

    <div class="row-fluid">
        <div class="span12">
            <div class="navbar navbar-inverse navbar-static-top">
                <div>
                    <ul>

                        <sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
                            <li>
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    Admin
                                </a>

                                <ul>
                                    <li class="controller"><g:link
                                            controller="aclClass">ACL Management</g:link></li>
                                    <li class="controller"><g:link
                                            controller="downTimeScheduler" action="create">Schedule Down Time</g:link></li>
                                    <li class="controller"><g:link
                                            controller="downTimeScheduler" action="list">View Down Times</g:link></li>
                                    <li class="controller"><g:link
                                            controller="register">Register External BARD User</g:link></li>
                                    <li class="controller"><g:link controller="register"
                                                                   action="listUsersAndGroups">List External BARD Users</g:link></li>
                                    <li class="controller"><g:link controller="person"
                                                                   action="list">List Person Table</g:link></li>
                                    <li class="controller"><g:link controller="moveExperiments"
                                                                   action="show">Move Experiments</g:link></li>

                                    <li class="controller"><g:link controller="assayDefinition"
                                                                   action="assayComparisonReport">Compare Assays</g:link></li>
                                    <li class="controller"><g:link controller="splitAssayDefinition"
                                                                   action="show">Split Assays</g:link></li>
                                </ul>

                            </li>
                        </sec:ifAnyGranted>

                        <li>
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Assay Definitions

                            </a>
                            <ul>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="groupAssays">My Assay Definitions</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="create">Create Assay Definition</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="assayComparisonReport">Compare Assay Definitions</g:link></li>
                            </ul>
                        </li>
                        <li>
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Projects

                            </a>
                            <ul>
                                <li class="controller"><g:link controller="project"
                                                               action="groupProjects">My Projects</g:link></li>
                                <li class="controller"><g:link controller="project"
                                                               action="create">Create a New Project</g:link></li>
                            </ul>
                        </li>
                        <li>
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Experiments

                            </a>
                            <ul>
                                <li class="controller"><g:link controller="experiment"
                                                               action="myExperiments">My Experiments</g:link></li>
                                <li class="controller"><g:link controller="jobs"
                                                               action="index">My import jobs</g:link></li>
                            </ul>
                        </li>
                        <li>
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Panels

                            </a>
                            <ul>
                                <li class="controller"><g:link controller="panel"
                                                               action="myPanels">My Panels</g:link></li>
                                <li class="controller"><g:link controller="panel"
                                                               action="create">Create New Panel</g:link></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>