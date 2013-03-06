<%
    /**
     * last wizard page / tab
     *
     * @author Jeroen Wesbeek <work@osx.eu>
     * @package AjaxFlow
     */
%>
<af:page>

    <div class="alert alert-success">
        <button type="button" class="close" data-dismiss="alert">×</button>
        <strong>The item has been successfully saved.</strong>
    </div>
	
	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
	
    <g:if test="${'Fixed'.equals(valueType?.valueTypeOption)}">
		<g:if test="${fixedValue.isNumericValue}">
	    	<g:set var="valueText" value="${ fixedValue?.valueQualifier + " " + fixedValue?.numericValue + " " + fixedValue?.valueUnitLabel }" />
		</g:if>
		<g:else>
	    	<g:set var="valueText" value="${ fixedValue?.valueQualifier + " " + fixedValue?.valueLabel + " " + fixedValue?.valueUnitLabel }" />
		</g:else> 
	</g:if>
	<g:elseif test="${'List'.equals(valueType?.valueTypeOption)}">
		<g:set var="valueText" value="${ listOfValues.size() } values set" />
	</g:elseif>
	
    <g:render template="common/itemWizardSelectionsTable"
              model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': valueText]"/>
	
	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
</af:page>
