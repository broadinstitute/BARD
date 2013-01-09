<%
/**
 * last wizard page / tab
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<af:page>

<h2>Saved!</h2>
<g:set var="currentValue" value="${ attribute?.currentValue }" />
<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
<g:set var="valueText" value="${ fixedValue?.currentChoice + " " + fixedValue?.valueUnits + " " + fixedValue?.valueQualifier}" />
<g:render template="common/itemWizardSelectionsTable" model="['attribute': currentValue, 'valueType': valueTypeOption, 'value': valueText]"/>

</af:page>
