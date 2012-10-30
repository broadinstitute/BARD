<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Project : ID ${projectAdapter?.project?.id}</title>
    <r:script>
        $(document).ready(function () {
            $("#accordion").accordion({ autoHeight:false });
        })
    </r:script>

</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Project: ${projectAdapter?.name} <small>(Project ID: ${projectAdapter?.project?.id})</small></h1>
        <g:saveToCartButton id="${projectAdapter.project.id}"
                            name="${JavaScriptUtility.cleanup(projectAdapter.project.name)}"
                            type="${querycart.QueryItemType.Project}"/>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <dl class="dl-horizontal dl-horizontal-wide">
            <g:if test="${projectAdapter.project.getValue('grant number')}">
                <dt>Grant Number:</dt>
                <dd>${projectAdapter.project.getValue('grant number').value}</dd>
            </g:if>
            <g:if test="${projectAdapter.project.getValue('laboratory name')}">
                <dt>Laboratory Name:</dt>
                <dd>${projectAdapter.project.getValue('laboratory name').value}</dd>
            </g:if>
        </dl>
    </div>
</div>


<r:script>
    $(document).ready(function () {
        $('.collapse').on('show', function () {
            var icon = $(this).siblings().find("i.icon-chevron-right");
            icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
        });
        $('.collapse').on('hide', function () {
            var icon = $(this).siblings().find("i.icon-chevron-down");
            icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
        });
    })
</r:script>
<div class="row-fluid">

    <div class="span12 accordion">

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#annotations-header" id="annotations-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#annotations-info"><i
                        class="icon-chevron-right"></i> Annotations (${projectAdapter.annotations.size()})</a>

                <div id="annotations-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <dl>
                            <g:each in="${projectAdapter?.annotations}" var="annotation">
                                <dt>${annotation.id}</dt>
                                <dd>${annotation.value}</dd>
                            </g:each>
                        </dl>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#document-info"><i class="icon-chevron-down"></i> Description</a>

                <div id="document-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <g:textBlock>${projectAdapter.project.description}</g:textBlock>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#experiments-header" id="experiments-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#experiments-info"><i
                        class="icon-chevron-right"></i> Experiments (${projectAdapter.numberOfExperiments})</a>

                <div id="experiments-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="experiments" model="['experiments': experiments, showAssaySummary: false]"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="assays-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#assays-info"><i class="icon-chevron-right"></i> Assays (${assays?.size()})</a>

                <div id="assays-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:each var="assay" in="${assays}" status="i">
                            <div>
                                <p>
                                    <g:link controller="bardWebInterface" action="showAssay" id="${assay.id}" params='[searchString:"${searchString}"]'>${assay.name}</g:link>
                                </p>
                            </div>
                        </g:each>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
</body>
</html>