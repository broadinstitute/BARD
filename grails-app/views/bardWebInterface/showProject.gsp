<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>

    <meta name="layout" content="main"/>
    <r:require modules="core"/>
    <r:require modules="bootstrap"/>

    <title>Project</title>
<r:script>
	$(document).ready(function() {
		$( "#accordion" ).accordion({ autoHeight: false });
	}) 
</r:script>

</head>
<body>
	<div>
	  	<div class="ui-widget"><h1>Project View</h1></div>
	  	
		<g:if test="${flash.message}">
			<div class="ui-widget">
			<div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
				<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
				<strong>${flash.message}</strong>
			</div>
			</div>
		</g:if>
		
		<g:if test="${projectInstance?.id}">
		<div id="accordion">
		
			<h3><a href="#">Summary for Project ID: ${projectInstance.id}</a></h3>
			<div>
				<g:render template="projectSummary" model="['projectInstance': projectInstance]" />
			</div>		
	
			<h3><a href="#">Documents</a></h3>
			<div>
				<g:render template="projectDocuments" model="['projectInstance': projectInstance]" />
			</div>
							
		</div>	<!-- End accordion -->
		</g:if>
	</div><!-- End body div -->
</body>
</html>