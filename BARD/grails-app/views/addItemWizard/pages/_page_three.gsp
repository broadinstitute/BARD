<%@ page import="bard.db.registration.*" %>

<af:page>
<g:set var="currentValue" value="${ attribute?.currentValue }" />
<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
<g:render template="common/itemWizardSelectionsTable" model="['attribute': currentValue, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>


<p>Search or Browse for a defined term to use as the value.  Or, enter a number directly into the current choice.  If relevant, choose the relevant units that describe the number entered.</p>

<input class="input-xlarge" type="text" id="valueTextField" name='valueText' placeholder="Search for Value">
<input type="hidden" id="valueId" name="valueId" value="${ fixedValue?.valueId }"/>

<label class="control-label" for="assayType"><g:message code="assay.assayType.label" default="Qualifier" /></label>
<div class="controls">
	<g:select name="valueQualifier" from="${AssayContextItem.constraints.qualifier.inList}" noSelection="['': '']"  value="${ fixedValue?.valueQualifier }"/>
</div>

<label class="control-label" >Units:</label>
<input class="input-xlarge" type="text" id="valueUnits" name='valueUnits'  value="${ fixedValue?.valueUnits }">

<label class="control-label" >Current choice:</label>
<input class="input-xlarge" type="text" id="currentChoice" name='currentChoice'  value="${ fixedValue?.currentChoice }">

</af:page>
