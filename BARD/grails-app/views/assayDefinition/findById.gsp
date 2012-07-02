<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core"/>
<meta name="layout" content="basic"/>
<title>Find Assay</title>
<r:script>
	 
</r:script>

</head>
<body>
<div>
	<div class="ui-widget">
		<formset>
			<legend><h3>Search assay by ID</h3></legend>
			<g:if test="${flash.message}">
				<div class="ui-widget">
					<div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
					<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
					<strong>${flash.message}</strong></p>
					</div>
				</div>
			</g:if>
			<g:form action="findById">
				<label for="assayId">Enter ID:</label>
				<g:textField name="assayId" value="${params.assayId}"/>
				
				<g:submitButton name="search" value="Search"/>
			</g:form>
		</formset>
	</div>
	<div>
		<g:if test="${assayInstance?.id}">
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
		</g:if>
		
	</div>
	
</div><!-- End body div -->
</body>
</html>