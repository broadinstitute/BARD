<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core,bootstrap,bootstrapplus"/>
<meta name="layout" content="basic"/>
<title>Search for Assay Definition by ID</title>
<r:script>
	 
</r:script>

</head>
<body>

	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Search for Assay Definition by ID</h4>
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
    		<g:hasErrors bean="${assayInstance}">
			<div class="alert alert-error">
				<g:renderErrors bean="${assayInstance}" as="list" />
			</div>
			</g:hasErrors>
    	</div>
    </div>
    
    <div class="row-fluid">
	    <div class="span12">
	    	<div class="bs-docs" style="padding: 20px 20px 20px;">
	        	<g:form action="findById" class="form-inline">	
	        		<label class="control-label" for="assayId">Enter Assay Definition ID:</label>
	        		<g:textField name="assayId" autofocus="true"/>
	        		<span class="help-inline">${hasErrors(bean: personInstance, field: 'assayId', 'error')}</span>														
					<g:submitButton name="search" value="Search" class="btn btn-primary"/>
				</g:form>
	        </div>
	    </div>
	</div>  
	
	
	<g:if test="${assayInstance?.id}">
		<div class="row-fluid">
			<div class="span12">
			<h3>Results</h3>			
					<table class="gridtable">
						<thead>
							<tr>
								<g:sortableColumn property="id" title="${message(code: 'assay.id.label', default: 'ID')}" />	
								<g:sortableColumn property="assayName" title="${message(code: 'assay.assayName.label', default: 'Assay Name')}" />							
								<g:sortableColumn property="designedBy" title="${message(code: 'assay.designedBy.label', default: 'Designed By')}" />						
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><g:link action="show" id="${assayInstance.id}">${fieldValue(bean: assayInstance, field: "id")}</g:link></td>
								<td>${fieldValue(bean: assayInstance, field: "assayName")}</td>															
								<td>${fieldValue(bean: assayInstance, field: "designedBy")}</td>
							</tr>
						</tbody>
					</table>
			</div>
		</div>						
	</g:if>
		
	
</body>
</html>