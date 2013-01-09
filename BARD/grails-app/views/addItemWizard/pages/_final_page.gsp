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
<g:render template="common/itemWizardSelectionsTable" model="['attribute': currentValue, 'valueType': 'fixed', 'value': 'DHDG-5645']"/>

</af:page>
