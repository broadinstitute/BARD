<%
/**
 * fourth wizard page / tab
 */
%>
<af:page>
	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />

	<g:if test="${'Fixed'.equals(valueType?.valueTypeOption)}">
		<g:if test="${fixedValue.isNumericValue}">
	    	<g:set var="valueText" value="${ [fixedValue?.valueQualifier, fixedValue?.numericValue , fixedValue?.valueUnitLabel].findAll().join(" ") }" />
		</g:if>
		<g:else>
	    	<g:set var="valueText" value="${ [fixedValue?.valueQualifier, fixedValue?.valueLabel, fixedValue?.valueUnitLabel].findAll().join(" ") }" />
		</g:else>
	</g:if>
	<g:elseif test="${'List'.equals(valueType?.valueTypeOption)}">
		<g:set var="valueText" value="${ listOfValues.size() } values set" />
	</g:elseif>
	<g:elseif test="${'Range'.equals(valueType?.valueTypeOption)}">
		<g:set var="valueText" value="${rangeValue.minValue} - ${rangeValue.maxValue} ${rangeValue.valueUnitLabel}" />
	</g:elseif>
	<g:elseif test="${'Free'.equals(valueType?.valueTypeOption)}">
		<g:set var="valueText" value="No value" />
	</g:elseif>

    <g:render template="common/itemWizardSelectionsTable"
              model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': valueText]"/>
    <br>

    <%-- Render list of values table if value type is List  --%>
    <g:if test="${'List'.equals(valueType?.valueTypeOption)}">
	    <g:set var="list" value="${listOfValues}" />
		<g:set var="enableEdit" value="${false}" />
		<g:render template="common/itemsInListTable" model="['listOfValues': list, 'enableEdit': enableEdit]"/>
		<br>
	</g:if>

    <h1>Please review the information for this item above.</h1>
	<p>
		Click <strong>"Save"</strong> below to create the new item (cannot use "Previous" after this)
	</p>
	<p>
		Click <strong>"Cancel"</strong> to return to editing the assay definition
	</p>
	<p>
		Click <strong>"Previous"</strong> to return to the previous page in the wizard
	</p>

	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
</af:page>
