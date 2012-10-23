<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap, assaycards"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Assay Definition</title>
</head>

<body>
	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Assay View</h4>
	        </div>
	    </div>
	</div>

    <g:if test="${flash.message}">
	    <div class="row-fluid">
		    <div class="span12">
		        <div class="ui-widget">
		            <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
		                <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
		                    <strong>${flash.message}</strong>
		            </div>
		        </div>
		    </div>
	    </div>
    </g:if>

    <g:if test="${assayInstance?.id}">
	    <div class="row-fluid">
	        <div id="accordion" class="span12">
	            <h3><a href="#">Summary for Assay ID: ${assayInstance?.id}</a></h3>
	            <g:render template="assaySummaryView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Assay and Biology Details</a></h3>
	            <g:render template="cardDtoView" model="['cardDtoMap': cardDtoMap, 'assayId': assayInstance.id]"/>

	            <h3><a href="#">Documents</a></h3>
	            <g:render template="assayDocumentsView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Assay Contexts</a></h3>
	            <g:render template="measureContextsView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Measures</a></h3>
	            <g:render template="measuresView" model="['assayInstance': assayInstance]"/>

	            <h3><a href="#">Assay Context Items</a></h3>
	            <g:render template="measureContextItemsView" model="['assayInstance': assayInstance]"/>


	        </div>    <!-- End accordion -->
	    </div>
    </g:if>

</body>
</html>