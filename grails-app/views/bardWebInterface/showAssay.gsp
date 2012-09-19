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
        <h1>Assay Definition: ${assayAdapter?.assay?.name} <small>(ADID: ${assayAdapter?.assay?.id})</small></h1>
        <a href="#" class="addAssayToCart btn btn-mini"
           data-cart-name="${JavaScriptUtility.cleanup(assayAdapter?.assay?.name)}"
           data-cart-id="${assayAdapter?.assay?.id}">
            Save for later analysis
        </a>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <dl class="dl-horizontal">
            <dt>Assay Type:</dt><dd>${assayAdapter.assay.type}</dd>
            %{--<dt>Assay Format:</dt><dd></dd>--}%
            %{--<dt>Biological Process:</dt><dd></dd>--}%
            %{--<dt>Target:</dt><dd></dd>--}%
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
                <a href="#project-header" id="project-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#project-info"><i class="icon-chevron-right">
                </i> Projects <small>(${assayAdapter.assay.projects.size()} Project(s))</small></a>
            </div>

            <div id="project-info" class="accordion-body collapse">
                <div class="accordion-inner">
                    <g:render template="assayProjects" model="['assayAdapter': assayAdapter]"/>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#assay-header" id="assay-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#assay-bio-info"><i class="icon-chevron-right"></i> Assay and Biology Details<small> ( Assay Detection Method Type: ${assayDetectionMethod}, Assay Detection Instrument: ${assayDetectionInstrument})</small></a>

                <div id="assay-bio-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:each in="${assayAdapter?.annotations}" var="annotation">
                            <dt>${annotation.id}</dt>
                            <dd>${annotation.value}</dd>
                        </g:each>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#document-info"><i class="icon-chevron-right"></i> Documents</a>

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
                   data-target="#result-info"><i class="icon-chevron-right"></i> Results</a>

                <div id="result-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        TBD
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#registration-header" id="registration-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#registration-info"><i class="icon-chevron-right">
                </i> Registration Info <small>(ID: ${assayAdapter?.assay.id}, Created by: ${assayAdapter?.assay?.getValue(AssayValues.AssaySourceValue)?.value})</small>
                </a>

                <div id="registration-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assaySummary" model="['assayAdapter': assayAdapter]"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>