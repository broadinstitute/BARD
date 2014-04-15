<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>Search</title>
    <r:require modules="historyJsHtml5,search,promiscuity,compoundOptions,addAllItemsToCarts, histogramAddon"/>
    <sitemesh:parameter name="noSocialLinks" value="${true}"/>
    <style>
        #idnavlist li{
            display: inline;
            list-style-type: none;
            padding-right: 5px;
        }
    </style>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div id="resultTab">

            <ul id="resultTabUL" class="nav nav-tabs">
                <li class="active" id="assaysTabLi"><a href="#assays" data-toggle="tab"
                                                       id="assaysTab">Assay Definitions (0)</a></li>
                <li id="compoundsTabLi"><a href="#compounds" data-toggle="tab" id="compoundsTab">Compounds (0)</a></li>
                <li id="projectsTabLi"><a href="#projects" data-toggle="tab" id="projectsTab">Projects (0)</a></li>
                <li id="experimentsTabLi"><a href="#experiments" data-toggle="tab" id="experimentsTab">Experiments (0)</a></li>
            </ul>

            <div id="resultTabContent" class="tab-content">

                <div class="tab-pane fade in active" id="assays" data-target="#assays">
                    <g:render template="assays"/>
                </div>
                <div class="tab-pane fade" id="compounds" data-target="#compounds">
                    <g:render template="compounds"/>
                </div>

                <div class="tab-pane fade" id="projects" data-target="#projects">
                    <g:render template="projects"/>
                </div>
                <div class="tab-pane fade" id="experiments" data-target="#experiments">
                    <g:render template="experimentResults"/>
                </div>

            </div>
        </div>
    </div>

    <r:require modules="historyJsHtml5,search,promiscuity"/>

</div>
</body>
</html>