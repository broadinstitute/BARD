<r:script>
	$('#measureContextItemsTable').dataTable({
		"bJQueryUI": true,
		"sPaginationType": "full_numbers"
	});
</r:script>
<div>
				<g:if test="${assayInstance?.measureContextItems}">
					<div>
						<table id="measureContextItemsTable" cellpadding="0" cellspacing="0" border="0" class="display">
						<thead>
							<tr>
								<g:sortableColumn property="attributeType" title="${message(code: 'measureContextItem.id.label', default: 'ID')}" />	
								<g:sortableColumn property="attributeType" title="${message(code: 'measureContextItem.attributeType.label', default: 'Attribute Type')}" />							
								<g:sortableColumn property="attributeType" title="${message(code: 'measureContextItem.attributeElement.label.label', default: 'Attribute Element')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueDisplay.label', default: 'Value Display')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueNum.label', default: 'Value')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueMin.label', default: 'Value Min')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueMax.label', default: 'Value Max')}" />
							
							</tr>
						</thead>
						<tbody>
						<g:each in="${assayInstance?.measureContextItems.sort{it.id}}" status="i" var="measureContextItemInstance">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
								<td>${fieldValue(bean: measureContextItemInstance, field: "id")}</td>
								<td>${fieldValue(bean: measureContextItemInstance, field: "attributeType")}</td>	
								<td>${fieldValue(bean: measureContextItemInstance, field: "attributeElement.label")}</td>						
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueDisplay")}</td>								
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueNum")}</td>
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueMin")}</td>
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueMax")}</td>
							</tr>
						</g:each>
						</tbody>
					</table>
					</div>
				</g:if>
				<g:else>
					<span>No Measure Contexts Items found</span>
				</g:else>
</div>