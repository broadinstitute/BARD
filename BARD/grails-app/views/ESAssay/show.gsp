<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core"/>
<meta name="layout" content="basic"/>
<title>Assay Definition</title>
<r:script>
	$(document).ready(function() {
		$( "#accordion" ).accordion({ autoHeight: false });
	}) 
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
		
		<g:if test="${assayInstance?.aid}">
		<div id="accordion">
		
			<h3><a href="#">Summary for Assay Definition ID: ${assayInstance.aid}</a></h3>
			<div>
				<g:render template="assaySummary" model="['assayInstance': assayInstance]" />							
			</div>		
	
			<h3><a href="#">Documents</a></h3>
			<div>
				<g:render template="assayDocuments" model="['assayInstance': assayInstance]" />	
			</div>
							
		</div>	<!-- End accordion -->
		</g:if>
	</div><!-- End body div -->
</body>
</html>