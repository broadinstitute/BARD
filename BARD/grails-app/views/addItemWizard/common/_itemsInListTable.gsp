	<g:if test="${listOfValues.size() != 0}">
		<table class="table table-hover table-condensed">
			<caption><b><i style="color: blue">Number of items in the list: </i>${ listOfValues.size() }</b></caption>
			<thead>
				<tr>
					<th>Qualifier</th><th>Value</th><th>Unit</th>
					<g:if test="${enableEdit}"><th></th></g:if>
				</tr>
			</thead>
			<tbody>
				<g:set var="listIndex" value="${ 0 }" />
				<g:each var="value" in="${listOfValues}">
					<tr>
						<td>${value ? value.valueQualifier : ""}</td>
						<g:if test="${value.isNumericValue}"><td>${value.numericValue}</td></g:if>
						<g:else><td>${value.valueLabel}</td></g:else>						
						<td>${value ? value.valueUnitLabel : ""}</td>
						<g:if test="${enableEdit}">
							<td style="width: 20px">
								<af:ajaxButton name="removeItemFromList" value="Remove" id="${listIndex}" afterSuccess="afterSuccess();" class="btn btn-mini" />							
								%{-- <a href="#" onclick="removeItemFromList(${listIndex}); return false;" title="Remove item from the list"><i class="icon-trash"></i></a> --}%
							</td>
						</g:if>
					</tr>
					<g:set var="listIndex" value="${ listIndex + 1 }" />
				</g:each>
			</tbody>
		</table>
	</g:if>		
	<g:else>
		<table class="table table-bordered table-hover table-condensed">
			<caption><b><i style="color: red">No items added yet to the list</i></b></caption>
		</table>
	</g:else>	