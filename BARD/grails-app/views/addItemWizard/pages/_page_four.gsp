<%
/**
 * fourth wizard page / tab
 */
%>
<af:page>
	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
    <g:set var="valueText" value="${ fixedValue?.valueQualifier + " " + fixedValue?.valueLabel + " " + fixedValue?.valueUnits }" />
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
</af:page>
