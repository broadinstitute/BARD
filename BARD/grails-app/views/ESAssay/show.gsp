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
	
	<!-- Assay fields -->
		<h3><a href="#">Summary for Assay ID: ${assayInstance.aid}</a></h3>
		<div>
				<g:if test="${assayInstance?.aid}">
				<li>
					<span>ID:</span>				
					<span>${assayInstance.aid}</span>					
				</li>
				</g:if>
				<g:if test="${assayInstance?.name}">
				<li>
					<span">Name:</span>
					<span>${assayInstance.name}</span>				
				</li>				
				</g:if>
				<g:if test="${assayInstance?.source}">
				<li>
					<span">Source:</span>
					<span>${assayInstance.source}</span>				
				</li>				
				</g:if>
								
			</div>
			
			<!-- Assay Documents fields -->
			<h3><a href="#">Documents</a></h3>
			<div>
				<g:if test="${assayInstance?.protocol}">
					<li>
						<span>Protocol</span><br>				
						<span><p>${assayInstance.protocol}</p></span>					
					</li>
				</g:if>		
				<g:if test="${assayInstance?.description}">
					<li>
						<span>Description</span><br>				
						<span><p>${assayInstance.description}</p></span>					
					</li>
				</g:if>
				<g:if test="${assayInstance?.comments}">
					<li>
						<span>Comments</span><br>				
						<span><p>${assayInstance.comments}</p></span>					
					</li>
				</g:if>		
			</div>
			
			%{--
			<!-- Assay-Measure Context fields -->
			<h3><a href="#">Measure Contexts</a></h3>
			<div>
				<span>No Measure Contexts found</span>
			</div>
			
			<!-- Assay-Measure fields -->
			<h3><a href="#">Measures</a></h3>
			<div>
				<span>No Measures found</span>
			</div>
			<h3><a href="#">Measure Context Items</a></h3>
			<div>
				<span>No Measure Contexts Items found</span>
			</div>
			--}%
			
	</div>	<!-- End accordion -->
	</g:if>
</div><!-- End body div -->
</body>
</html>