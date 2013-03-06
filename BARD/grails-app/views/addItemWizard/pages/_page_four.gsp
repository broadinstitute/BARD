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
	    	<g:set var="valueText" value="${ fixedValue?.numericValue + " " + fixedValue?.valueUnitLabel }" />
		</g:if>
		<g:else>
	    	<g:set var="valueText" value="${ fixedValue?.valueQualifier + " " + fixedValue?.valueLabel + " " + fixedValue?.valueUnitLabel }" />
		</g:else> 
	</g:if>
	<g:elseif test="${'List'.equals(valueType?.valueTypeOption)}">
		<g:set var="valueText" value="${ listOfValues.size() } values set" />
	</g:elseif>
	
	<%--
	<g:if test="${fixedValue.isNumericValue}">
    	<g:set var="valueText" value="${ fixedValue?.numericValue + " " + fixedValue?.valueUnitLabel }" />
	</g:if>
	<g:else>
    	<g:set var="valueText" value="${ fixedValue?.valueQualifier + " " + fixedValue?.valueLabel + " " + fixedValue?.valueUnitLabel }" />
	</g:else> 
	--%>
	
	
	  
    <g:render template="common/itemWizardSelectionsTable"
              model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': valueText]"/>

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
