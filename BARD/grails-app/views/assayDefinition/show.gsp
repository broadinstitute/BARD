<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core"/>
<meta name="layout" content="basic"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
<title>Assay Definition</title>
<r:script>
	$(document).ready(function() {		
		$( "#accordion" ).accordion({ autoHeight: false });		
		$( "#dialog:ui-dialog" ).dialog( "destroy" );
	}); 
</r:script>

</head>
<body>

<div>
  	<div class="ui-widget"><p><h1>Assay View</h1></p></div>
  	
	<g:if test="${flash.message}">
		<div class="ui-widget">
		<div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
			<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
			<strong>${flash.message}</strong>
		</div>
		</div>
	</g:if>	
	
	<g:if test="${assayInstance?.id}">
	<div id="accordion">
	
		<h3><a href="#">Summary for Assay ID: ${assayInstance?.id}</a></h3>
		<g:render template="assaySummaryView" model="['assayInstance': assayInstance]" />

        <h3><a href="#">Assay and Biology Details</a></h3>
        <g:render template="cardDtoView" model="['cardDtoList': cardDtoList]" />

		<h3><a href="#">Documents</a></h3>
		<g:render template="assayDocumentsView" model="['assayInstance': assayInstance]" />
			
		<h3><a href="#">Measure Contexts</a></h3>
		<g:render template="measureContextsView" model="['assayInstance': assayInstance]" />
			
		<h3><a href="#">Measures</a></h3>
		<g:render template="measuresView" model="['assayInstance': assayInstance]" />	
			
		<h3><a href="#">Measure Context Items</a></h3>
		<g:render template="measureContextItemsView" model="['assayInstance': assayInstance]" />
			
	</div>	<!-- End accordion -->
	</g:if>
</div><!-- End body div -->
</body>
</html>