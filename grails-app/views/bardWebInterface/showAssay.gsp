<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="details"/>
    <title>BARD : Assay Definition : ADID ${assayInstance?.id}</title>
</head>
<body>
<h1 class="detail">Assay Definition Detail for ADID ${assayInstance?.id}</h1>
<div class="row-fluid" style="clear:both;">
    <div class="span12 header">
        <h3>${assayInstance?.name}</h3>
        <dl class="dl-horizontal">
            <dt>Assay Format:</dt><dd></dd>
            <dt>Assay Type:</dt><dd></dd>
            <dt>Target:</dt><dd></dd>
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
                <a href="#project-header" id="project-header" class="accordion-toggle" data-toggle="collapse" data-target="#project-info"><i class="icon-chevron-right"></i> Projects</a>
            </div>
            <div id="project-info" class="accordion-body collapse">
                <div class="accordion-inner">
                    TBD
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#assay-header" id="assay-header" class="accordion-toggle" data-toggle="collapse" data-target="#assay-bio-info"><i class="icon-chevron-right"></i> Assay and Biology Details</a>
                <div id="assay-bio-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        TBD
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse" data-target="#document-info"><i class="icon-chevron-right"></i> Documents</a>
                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assayDocuments" model="['assayInstance': assayInstance]" />
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#results-header" id="results-header" class="accordion-toggle" data-toggle="collapse" data-target="#result-info"><i class="icon-chevron-right"></i> Results</a>
                <div id="result-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        TBD
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#registration-header" id="registration-header" class="accordion-toggle" data-toggle="collapse" data-target="#registration-info"><i class="icon-chevron-right"></i> Registration Info</a>
                <div id="registration-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assaySummary" model="['assayInstance': assayInstance]" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>