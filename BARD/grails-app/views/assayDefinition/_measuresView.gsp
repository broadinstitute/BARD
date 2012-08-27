<r:script>
	$('#measuresTable').dataTable({
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
</r:script>
<div>
				<g:if test="${assayInstance?.measures}">
						<div>
						<table id="measuresTable" cellpadding="0" cellspacing="0" border="0" class="display"">
							<thead>
								<tr>
									<th><g:message code="measure.id.label" default="ID" /></th>
									<th><g:message code="measure.entryUnit.label" default="Unit" /></th>
									<th><g:message code="measure.element.label.label" default="Label" /></th><!--  -->	
									<th><g:message code="measure.element.description.label" default="Description" />
									<th><g:message code="measure.element.abbreviation.label" default="Abbreviation" />
									<th><g:message code="measure.element.synonyms.label" default="Synonyms" />																							
									<g:sortableColumn property="modifiedBy" title="${message(code: 'measure.modifiedBy.label', default: 'Modified By')}" />																																								
								</tr>
							</thead>
							<tbody>
							<g:each in="${assayInstance.measures.sort{it.id}}" status="i" var="measureInstance">
								<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
									<td>${fieldValue(bean: measureInstance, field: "id")}</td>										
									<td>${fieldValue(bean: measureInstance, field: "entryUnit.unit")}</td>			
									<td>${fieldValue(bean: measureInstance, field: "element.label")}</td>
									<td>${fieldValue(bean: measureInstance, field: "element.description")}</td>
									<td>${fieldValue(bean: measureInstance, field: "element.abbreviation")}</td>
									<td>${fieldValue(bean: measureInstance, field: "element.synonyms")}</td>											
									<td>${fieldValue(bean: measureInstance, field: "modifiedBy")}</td>														
																	
								</tr>
							</g:each>
							</tbody>
						</table>
						</div>
				</g:if>
				<g:else>
					<span>No Measures found</span>
				</g:else>	
</div>