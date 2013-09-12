<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core,bootstrap,bootstrapplus"/>
<meta name="layout" content="basic"/>
<title>Search for Panel by ID</title>
<r:script>
	 
</r:script>

</head>
<body>

	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Search for Panel by ID</h4>
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
    		<g:hasErrors bean="${panelInstance}">
			<div class="alert alert-error">
				<g:renderErrors bean="${panelInstance}" as="list" />
			</div>
			</g:hasErrors>
    	</div>
    </div>
    
    <div class="row-fluid">
	    <div class="span12">
	    	<div class="bs-docs" style="padding: 20px 20px 20px;">
	        	<g:form action="findById" class="form-inline">
                    <input type="text" size="50" id="id" name='id' value="${params.name}" autofocus="true" placeholder="Enter Panel ID" class="input-large search-query" required="">
	        		<span class="help-inline">${hasErrors(bean: panelInstance, field: 'id', 'error')}</span>
					<g:submitButton name="search" value="Search" class="btn btn-primary"/>
				</g:form>
	        </div>
	    </div>
	</div>  
	
	
	<g:if test="${panelInstance?.id}">
		<div class="row-fluid">
			<div class="span12">
			<h3>Results</h3>			
					<table class="gridtable">
						<thead>
							<tr>
								<g:sortableColumn property="id" title="${message(code: 'panel.id.label', default: 'ID')}" />
								<g:sortableColumn property="name" title="${message(code: 'panel.name.label', default: 'Panel Name')}" />
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><g:link action="show" id="${panelInstance.id}">${fieldValue(bean: panelInstance, field: "id")}</g:link></td>
								<td>${fieldValue(bean: panelInstance, field: "name")}</td>
							</tr>
						</tbody>
					</table>
			</div>
		</div>						
	</g:if>
		
	
</body>
</html>