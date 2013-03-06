<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>

<g:hasErrors bean="${fixedValue}">
	<div class="alert alert-error">
		<button type="button" class="close" data-dismiss="alert">×</button>
		<g:renderErrors bean="${fixedValue}"/>
	</div>
</g:hasErrors>

<p>Search or Browse for a defined term to use as the value.  Or, enter a number directly into the numeric value field.  If relevant, choose the relevant units that describe the number entered.</p>

    <%-- This hidden control becomes the value selection box --%>
    <input type="hidden" id="valueId" name="valueId">
    <input type="hidden" id="valueLabel" name="valueLabel">
    
    <label class="control-label" >Numeric Value:</label>
    <input class="input-xlarge" type="text" id="numericValue" name='numericValue'  value="${ fixedValue?.numericValue }">

    <%-- This hidden field is needed for passing state needed for the ontology query --%>
    <input type="hidden" id="attributeElementId" value="${attribute.attributeId}">
	
	<label class="control-label" for="valueQualifier"><g:message code="assay.qualifier.label" default="Qualifier" /></label>
    <div  class="controls">
	    <g:select name="valueQualifier" from="${AssayContextItem.constraints.qualifier.inList}" noSelection="['': '']"  value="${ fixedValue?.valueQualifier }"/>
    </div>
	
	<%-- This hidden control becomes the units selection box --%>
    <input type="hidden" id="valueUnitId" name="valueUnitId">
    <input type="hidden" id="valueUnitLabel" name="valueUnitLabel">
	<%--
    <label class="control-label" >Units:</label>
    <input class="input-xlarge" type="text" id="valueUnits" name='valueUnitId'  value="${ fixedValue?.valueUnits }">
    --%>
	
	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
	<input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>
</af:page>
