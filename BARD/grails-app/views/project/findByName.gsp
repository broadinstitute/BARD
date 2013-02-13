<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core,bootstrap"/>
<meta name="layout" content="basic"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
<title>CAP - Find Project by Name</title>
<r:script>
	$(document).ready(function() {
		var autoOpts = {
			source: "getProjectNames",
			minLength: 3
		}
	 	$( "#projectName" ).autocomplete(autoOpts);
	 	$( "#results_accordion" ).accordion({ autoHeight: false });
	})
</r:script>

</head>
<body>
	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1">
	        	<h4>Search Project by name</h4>
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
	    	<div class="bs-docs" style="padding: 20px 20px 20px;">
	        	<g:form action="findByName" class="form-inline">
	        		<label class="control-label" for="projectName">Enter Project Name:</label>
	        		<input type="text" size="50" id="projectName" name='projectName' value="${params.projectName}">
					<g:submitButton name="search" value="Search" class="btn btn-primary"/>
				</g:form>
	        </div>
	    </div>
	</div>

	<g:if test="${projects}">
	<div class="row-fluid">
	    <div class="span12" id="results_accordion">
	    		<h3>Projects (${projects.size()})</h3>
				<div>
					<g:if test="${projects.size() > 0}">
						<div>
							<table class="gridtable">
								<thead>
									<tr>
										<g:sortableColumn property="id" title="${message(code: 'project.id.label', default: 'ID')}" params="${params}" />
                                        <g:sortableColumn property="name" title="${message(code: 'project.name.label', default: 'Project Name')}" params="${params}"/>
									</tr>
								</thead>
								<tbody>
								<g:each in="${projects}" status="i" var="instance">
									<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
										<td><g:link action="show" id="${instance.id}">${instance.id}</g:link></td>
                                        <td>${fieldValue(bean: instance, field: "name")}</td>
									</tr>
								</g:each>
								</tbody>
							</table>
						</div>
					</g:if>
				</div>
	    </div>
	</div>
	</g:if>
</body>
</html>