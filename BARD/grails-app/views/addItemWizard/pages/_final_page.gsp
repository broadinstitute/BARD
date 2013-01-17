<%
/**
 * last wizard page / tab
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<af:page>

<div class="alert alert-success">
 	<button type="button" class="close" data-dismiss="alert">×</button>
  	<strong>The item have been successfully saved.</strong>
</div>

<g:set var="currentValue" value="${ attribute?.currentValue }" />
<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
<g:set var="valueText" value="${ fixedValue?.valueQualifier + " " + fixedValue?.currentChoice + " " + fixedValue?.valueUnits }" />
<g:render template="common/itemWizardSelectionsTable" model="['attribute': currentValue, 'valueType': valueTypeOption, 'value': valueText]"/>

</af:page>
