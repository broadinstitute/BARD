<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="searchBox"/>
</head>

<body>
<div class="row-fluid">
    <div class="span9">
        <div id="resultTab" class="span9">

            <ul id="resultTabUL" class="nav nav-tabs">
                <li class="active"  id="assaysTabLi"><a href="#assays" data-toggle="tab" id="assaysTab">Assays (0)</a></li>
                <li id="compoundsTabLi"><a href="#compounds" data-toggle="tab" id="compoundsTab">Compounds (0)</a></li>
                <li id="projectsTabLi"><a href="#projects" data-toggle="tab" id="projectsTab">Projects (0)</a></li>
            </ul>

                 <div id="resultTabContent" class="tab-content">

                    <div class="tab-pane fade in active" id="assays">
                        <g:render template="assays"/>
                    </div>

                    <div class="tab-pane fade" id="compounds">
                        <g:render template="compounds"/>
                    </div>

                    <div class="tab-pane fade" id="projects">
                        <g:render template="projects"/>
                    </div>

                </div>
        </div>
    </div>

    <div class="span3">

            <g:render template="sarCartContent"/>

    </div>

</div>
</body>
</html>