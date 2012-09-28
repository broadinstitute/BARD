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
					
						<g:form class="form-horizontal" action="save" >
							<div class="control-group">
								<label class="control-label" for="assayStatus"><g:message code="assay.assayStatus.label" default="Assay Status:" /></label>
								<div class="controls">
									<g:select name="assayStatus" from="${bard.db.registration.AssayStatus?.values()}" keys="${bard.db.registration.AssayStatus.values()*.name()}" required="" value="${assayInstance?.assayStatus?.name()}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="assayTitle"><g:message code="assay.assayTitle.label" default="Assay Title:" /></label>
								<div class="controls">
									<g:textField name="assayTitle" maxlength="250" required="" value="${assayInstance?.assayTitle}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="assayName"><g:message code="assay.assayName.label" default="Assay Name" /></label>
								<div class="controls">
									<g:textArea name="assayName" cols="40" rows="5" maxlength="1000" required="" value="${assayInstance?.assayName}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="assayVersion"><g:message code="assay.assayVersion.label" default="Assay Version" /></label>
								<div class="controls">
									<g:textField name="assayVersion" maxlength="10" required="" value="${assayInstance?.assayVersion}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="designedBy"><g:message code="assay.designedBy.label" default="Designed By" /></label>
								<div class="controls">
									<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="assayType"><g:message code="assay.assayType.label" default="Assay Type" /></label>
								<div class="controls">
									<g:select name="assayType" from="${assayInstance.constraints.assayType.inList}" value="${assayInstance?.assayType}" valueMessagePrefix="assay.assayType" noSelection="['': '']"/>
								</div>
							</div>
							
							<div class="control-group">
								<div class="controls">
									<g:submitButton name="create" class="btn btn-primary" value="Create Assay" />
								</div>
							</div>
						</g:form>
						
						%{--
						<g:form class="form-horizontal" action="save" >
							
							<div class="control-group">
								<label class="control-label" for="id"><g:message code="assay.id.label" default="ID #:" /></label>
								<div class="controls">
									<g:textField name="id" maxlength="100" value="${assayInstance?.id}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="assayStatus"><g:message code="assay.assayStatus.label" default="Status:" /></label>
								<div class="controls">
									<g:select name="assayStatus" from="${AssayStatus}" value="${AssayStatus}" valueMessagePrefix="assay.assayStatus" noSelection="['': '']"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="lastUpdated"><g:message code="assay.lastUpdated.label" default="Last Updated:" /></label>
								<div class="controls">
									<g:textField name="lastUpdated" id="datepicker" maxlength="100" value="${assayInstance?.lastUpdated}"/>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label" for="assayName"><g:message code="assay.assayName.label" default="Name:" /></label>
								<div class="controls">
									<g:textField name="assayName" maxlength="128" value="${assayInstance?.assayName}"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="assayVersion"><g:message code="assay.assayVersion.label" default="Version:" /></label>
								<div class="controls">
									<g:textField name="assayVersion" maxlength="10" value="${assayInstance?.assayVersion}"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="modifiedBy"><g:message code="assay.modifiedBy.label" default="Entered By:" /></label>
								<div class="controls">
									<g:textField name="modifiedBy" maxlength="40" value="${assayInstance?.modifiedBy}"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="designedBy"><g:message code="assay.designedBy.label" default="Biologist:" /></label>
								<div class="controls">
									<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
								</div>
							</div>
															
							<div class="control-group">
								<div class="controls">
									<g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.button.create.label', default: 'Submit')}" />
								</div>
							</div>
						</g:form>
						--}%
					</div>
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
    
    %{--
  <div>
  	<div><p><h1>CAP Assay Definition</h1></p></div>
  	<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Name, description</a></li>
		<li><a href="#tabs-2">Biology, Targets</a></li>
		<li><a href="#tabs-3">Components, Roles</a></li>
		<li><a href="#tabs-4">Readouts, Methods</a></li>
		<li><a href="#tabs-5">Result Types, Measures</a></li>
	</ul>
	<div id="tabs-1">
	<div>
	<g:if test="${flash.message}">
		<div class="ui-widget">
		<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
			<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
			<strong>${flash.message}</strong>
		</div>
		</div>
	</g:if>
	</div>
	<div>
	<g:form action="save" >
		<div>
			<label for="id">
				<g:message code="assay.id.label" default="ID #:" />		
			</label>
			<g:textField name="id" maxlength="100" value="${assayInstance?.id}"/>
		</div>
		<div>
			<label for="assayStatus">
				<g:message code="assay.assayStatus.label" default="Status:" />
			</label>
			<g:select name="assayStatus" from="${assayInstance.constraints.assayStatus.inList}" value="${assayInstance?.assayStatus}" valueMessagePrefix="assay.assayStatus" noSelection="['': '']"/>
		</div>
		<div>
			<label for="lastUpdated">
				<g:message code="assay.lastUpdated.label" default="Last Updated:" />		
			</label>
			<g:textField name="lastUpdated" id="datepicker" maxlength="100" value="${assayInstance?.lastUpdated}"/>
		</div>
		<div>
			<label for="assayName">
				<g:message code="assay.assayName.label" default="Name:" />		
			</label>
			<g:textField name="assayName" maxlength="128" value="${assayInstance?.assayName}"/>
		</div>
		<div>
			<label for="assayVersion">
				<g:message code="assay.assayVersion.label" default="Version:" />		
			</label>
			<g:textField name="assayVersion" maxlength="10" value="${assayInstance?.assayVersion}"/>
		</div>
		<div>
			<label for="modifiedBy">
				<g:message code="assay.modifiedBy.label" default="Entered By:" />		
			</label>
			<g:textField name="modifiedBy" maxlength="40" value="${assayInstance?.modifiedBy}"/>
		</div>
		<div>
			<label for="designedBy">
				<g:message code="assay.designedBy.label" default="Biologist:" />		
			</label>
			<g:textField name="designedBy" maxlength="100" value="${assayInstance?.designedBy}"/>
		</div>
		
		<!-- 
		<div>
			<label for="document">
				<g:message code="assay.assayDocuments.label" default="Documents:" />		
			</label>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="documentName" title="${message(code: 'assay.assayDocuments.label', default: 'Name')}" />
					</tr>
				</thead>
				<tbody>
					<g:each in="${assayInstance?.assayDocuments}" status="i" var="assayDocument">
						<tr>
							<td>${fieldValue(bean: assayDocument, field: "documentName")}</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</div>
		 -->
		<div><a href="#" id="dialog_link_adddocument" class="ui-state-default ui-corner-all">Add Document</a></div>
		<div><a href="#" id="dialog_link_deletedocument" class="ui-state-default ui-corner-all">Delete Document</a></div>
		<!-- ui-modal-dialog -->
		<div id="dialog-modal" title="Add Document">
			<p>Add Protocol Form</p>
		</div>
		<div>
		<g:submitButton name="create" class="ui-state-default ui-corner-all" value="${message(code: 'default.button.create.label', default: 'Submit')}" />
		</div>
	</g:form>
	</div>
	</div>	
	<div id="tabs-2">
		<p>Tab 2</p>
	</div>
	<div id="tabs-3">
		<p>Tab 3</p>
	</div>
	<div id="tabs-4">
		<p>Tab 4</p>
	</div>
	<div id="tabs-5">
		<p>Tab 5</p>
	</div>
	</div>  	
  </div>
  --}%
  
</body>
</html>