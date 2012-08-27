<r:script>
	$('#measureContextItemsTable').dataTable({
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
</r:script>
<div>
				<g:if test="${assayInstance?.assayContexts}">
					<div>
						<table id="measureContextItemsTable" cellpadding="0" cellspacing="0" border="0" class="display">
						<thead>
							<tr>						
								<th>Attribute Type</th>
								<th>Attribute Element</th>
								<th>Value Display</th>
								<th>Value</th>
								<th>Value Min</th>
								<th>Value Max</th>
								
								%{--<g:sortableColumn property="attributeType" title="${message(code: 'assayContextItem.id.label', default: 'ID')}" />
								<g:sortableColumn property="attributeType" title="${message(code: 'assayContextItem.attributeType.label', default: 'Attribute Type')}" />							
								<g:sortableColumn property="attributeType" title="${message(code: 'assayContextItem.attributeElement.label.label', default: 'Attribute Element')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'assayContextItem.valueDisplay.label', default: 'Value Display')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'assayContextItem.valueNum.label', default: 'Value')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'assayContextItem.valueMin.label', default: 'Value Min')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'assayContextItem.valueMax.label', default: 'Value Max')}" /> 	--}%
							
							</tr>
						</thead>
						<tbody>
						<g:each in="${assayInstance?.assayContexts}" status="a" var="assayContext">
						<g:each in="${assayContext?.assayContextItems}" status="i" var="assayContextItemInstance">
							%{--<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">--}%
							<tr>
								%{--<td>${fieldValue(bean: assayContextItemInstance, field: "id")}</td>--}%
								<td>${fieldValue(bean: assayContextItemInstance, field: "attributeType")}</td>	
								<td>${fieldValue(bean: assayContextItemInstance, field: "attributeElement.label")}</td>						
								<td>${fieldValue(bean: assayContextItemInstance, field: "valueDisplay")}</td>								
								<td>${fieldValue(bean: assayContextItemInstance, field: "valueNum")}</td>
								<td>${fieldValue(bean: assayContextItemInstance, field: "valueMin")}</td>
								<td>${fieldValue(bean: assayContextItemInstance, field: "valueMax")}</td>
							</tr>
						</g:each>
						</g:each>
						</tbody>
					</table>
					</div>
				</g:if>
				<g:else>
					<span>No Assay Contexts Items found</span>
				</g:else>
</div>