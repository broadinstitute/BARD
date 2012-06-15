<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core"/>
<meta name="layout" content="basic"/>
<title>Assay Definition</title>
<r:script>
	$(document).ready(function() {
	$( "#tabs" ).tabs();
	$("#datepicker").datepicker({dateFormat: 'yy/mm/dd'});
	$( "#dialog:ui-dialog" ).dialog( "destroy" );
	$('#dialog-modal').dialog({
		height: 240,
		autoOpen: false,
		modal: true
	});
	$('#dialog_link_adddocument').click(function(){
		$('#dialog-modal').dialog('open');
		return false;
	});
	$('#dialog_link_deletedocument').click(function(){
		return false;
	});
	}) 
</r:script>

</head>
<body>
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
</body>
</html>