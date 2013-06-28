<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core,bootstrap,bootstrapplus"/>
<meta name="layout" content="basic"/>
<title>Search for Project by ID</title>
</head>
<body>

	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Search for Project by ID</h4>
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
    		<g:hasErrors bean="${instance}">
			<div class="alert alert-error">
				<g:renderErrors bean="${instance}" as="list" />
			</div>
			</g:hasErrors>
    	</div>
    </div>
    
    <div class="row-fluid">
	    <div class="span12">
	    	<div class="bs-docs" style="padding: 20px 20px 20px;">
	        	<g:form action="findById" class="form-inline">	
                    <input type="text" size="50" id="projectId" name='projectId' value="${params.projectId}" autofocus="true" placeholder="Enter Project ID" class="input-large search-query">

                    <span class="help-inline">${hasErrors(bean: instance, field: 'id', 'error')}</span>
					<g:submitButton name="search" value="Search" class="btn btn-primary"/>
				</g:form>
	        </div>
	    </div>
	</div>
</body>
</html>