<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core"/>
<meta name="layout" content="basic"/>
<title>Find Assay</title>
<r:script>
	$(document).ready(function() {
		var autoOpts = {
			source: "/BARD/assayJSon/getNames",
			minLength: 2
		}
	 	$( "#name" ).autocomplete(autoOpts);
	 	$( "#results_accordion" ).accordion({ autoHeight: false });
	})
</r:script>

</head>
<body>
<div>
	<div class="ui-widget">
		<formset>
			<legend><h3>Search assay by name</h3></legend>
			<g:if test="${flash.message}">
				<div class="ui-widget">
					<div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
					<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
					<strong>${flash.message}</strong></p>
					</div>
				</div>
			</g:if>
			<g:form action="findByName">
				<label for="assayId">Enter Name:</label>
				<g:textField id="name" name="assayName" value="${params.assayName}"/>
				
				<g:submitButton name="search" value="Search"/>
			</g:form>
		</formset>
	</div>
	<div class="ui-widget">
		<g:if test="${assays}">
			<div id="results_accordion">
			
				<h3><a href="#">Assays (${assays.size()})</a></a></h3>
				<div>
					<g:if test="${assays.size() > 0}">
						<div>		
							<table class="gridtable">
								<thead>
									<tr>
										<g:sortableColumn property="id" title="${message(code: 'assay.id.label', default: 'ID')}" />	
										<g:sortableColumn property="assayName" title="${message(code: 'assay.assayName.label', default: 'Assay Name')}" />							
										<g:sortableColumn property="designedBy" title="${message(code: 'assay.designedBy.label', default: 'Designed By')}" />						
									</tr>
								</thead>
								<tbody>
								<g:each in="${assays}" status="i" var="assayInstance">
									<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
										<td>${fieldValue(bean: assayInstance, field: "id")}</td>
										<td><g:link action="show" id="${assayInstance.id}">${fieldValue(bean: assayInstance, field: "assayName")}</g:link></td>															
										<td>${fieldValue(bean: assayInstance, field: "designedBy")}</td>
									</tr>
								</g:each>
								</tbody>
							</table>
						</div>
					</g:if>	
				</div>
				
				<h3><a href="#">Compounds</a></h3>
				<div>No data</div>
				
				<h3><a href="#">Experiments</a></h3>
				<div>No data</div>
				
				<h3><a href="#">Projects</a></h3>
				<div>No data</div>
					
			</div>
					
		</g:if>
		
	</div>
	
</div><!-- End body div -->
</body>
</html>