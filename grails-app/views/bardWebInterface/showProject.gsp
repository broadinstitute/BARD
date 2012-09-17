<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Project : ID ${projectAdapter?.project?.id}</title>
    <r:script>
	$(document).ready(function() {
		$( "#accordion" ).accordion({ autoHeight: false });
	}) 
</r:script>

</head>
<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Project: ${projectAdapter?.name} <small>(Project ID: ${projectAdapter?.project?.id})</small></h1>
        <a href="#" class="addProjectToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(projectAdapter?.name)}" data-cart-id="${projectAdapter?.project?.id}">
            Save for later analysis
        </a>
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
                <a href="#summary-header" id="summary-header" class="accordion-toggle" data-toggle="collapse" data-target="#summary-info"><i class="icon-chevron-down"></i> Summary Info</a>
                <div id="summary-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <g:render template="projectSummary" model="['projectAdapter': projectAdapter]" />
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse" data-target="#document-info"><i class="icon-chevron-right"></i> Documents</a>
                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="projectDocuments" model="['projectAdapter': projectAdapter]" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>