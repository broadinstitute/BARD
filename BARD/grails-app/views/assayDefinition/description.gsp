<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core,bootstrap"/>
<meta name="layout" content="basic"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
<title>CAP - Assay Definition</title>
<r:script>
	$(document).ready(function() {
		$( "#tabs" ).tabs();
		$("#datepicker").datepicker({dateFormat: 'yy/mm/dd'});	
	}) 
</r:script>

</head>
<body>
	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Assay Definition</h4>
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
    
    <div class="row-fluid">
	    <div class="span12">
	    	<div id="tabs">
				<ul>
					<li><a href="#tabs-1">Name, description</a></li>
					<li><a href="#tabs-2">Biology, Targets</a></li>
					<li><a href="#tabs-3">Components, Roles</a></li>
					<li><a href="#tabs-4">Readouts, Methods</a></li>
					<li><a href="#tabs-5">Result Types, Measures</a></li>
				</ul>
				
				<div id="tabs-1">				
					<div class="bs-docs">
					
						<g:form id="assayDescriptionForm" class="form-horizontal" action="save" >
							
							<g:render template="assayDescriptionForm"/>
							
							<div class="control-group">
								<div class="controls">
									<g:submitButton name="create" class="btn btn-primary" value="Create Assay" />
								</div>
							</div>
						</g:form>						
					</div>
					<jqvalui:renderValidationScript 
						for="bard.db.registration.Assay" 
						form="assayDescriptionForm"  
						renderErrorsOnTop="false"
					/>
				</div>	
				<div id="tabs-2">
					<p></p>
				</div>
				<div id="tabs-3">
					<p></p>
				</div>
				<div id="tabs-4">
					<p></p>
				</div>
				<div id="tabs-5">
					<p></p>
				</div>
			</div>  	
	    </div>
	</div>  
</body>
</html>