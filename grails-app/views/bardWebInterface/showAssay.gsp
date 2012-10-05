<%@ page import="bard.core.AssayValues; bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Assay Definition : ADID ${assayAdapter?.assay?.id}</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Assay Definition: ${assayAdapter?.assay?.name}
            <small>(ADID: ${assayAdapter?.assay?.id})</small>
        </h1>

        <a href="#" class="addAssayToCart btn btn-mini"
           data-cart-name="${JavaScriptUtility.cleanup(assayAdapter?.assay?.name)}"
           data-cart-id="${assayAdapter?.assay?.id}">
            Save for later analysis
        </a>
        <a class="btn btn-mini" href="${grailsApplication.config.bard.cap.assay}${assayAdapter?.assay?.capAssayId}"
           title="Click To Edit Assay Definition In Cap" rel="tooltip">Edit in CAP</a>
    </div>
</div>

<div class="row-fluid">
    <div class="span6">
        <g:render template="assaySummary" model="[assay:assayAdapter.assay]"/>
    </div>
    <div class="span6">
        <dl>
            <dt>Associated Projects:</dt>
            <dd>
                <ul>
                    <g:each in="${projects}" var="project">
                        <li><g:link controller="bardWebInterface" action="showProject" id="${project.id}">${project.name} <small>(Project ID: ${project.id})</small></g:link>
                        </li>
                    </g:each>
                </ul>
            </dd>
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
                <a href="#assay-header" id="assay-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#assay-bio-info"><i
                        class="icon-chevron-right"></i> Assay and Biology Details
                </a>

                <div id="assay-bio-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <dl>
                        <g:each in="${assayAdapter?.annotations}" var="annotation">
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
                   data-target="#document-info"><i class="icon-chevron-right"></i> Documents
                    <small>(${[(assayAdapter.assay.protocol ? 'protocol' : 'no protocol'),
                            (assayAdapter.assay.description ? 'description' : 'no description'),
                            (assayAdapter.assay.comments ? 'comments' : 'no comments')].join(', ')})</small>
                </a>

                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assayDocuments" model="['assayAdapter': assayAdapter]"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#results-header" id="results-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#result-info"><i class="icon-chevron-right"></i> Experiments (${experiments.size()})</a>

                <div id="result-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="experiments" model="[experiments: experiments, showAssaySummary: false]" />
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
</body>
</html>