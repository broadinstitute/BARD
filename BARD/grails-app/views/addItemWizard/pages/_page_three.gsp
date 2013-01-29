<%@ page import="bard.db.registration.*" %>

<af:page>

<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeName, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>

<p>Search or Browse for a defined term to use as the value.  Or, enter a number directly into the current choice.  If relevant, choose the relevant units that describe the number entered.</p>

    <%-- This hidden control becomes the value selection box --%>
    <input type="hidden" id="valueId" name="valueId">

    <%-- This hidden field is needed for passing state needed for the ontology query --%>
    <input type="hidden" id="attributeElementId" value="${attributeId}">

    <label class="control-label" for="valueQualifier"><g:message code="assay.qualifier.label" default="Qualifier" /></label>

    <div  class="controls">
	    <g:select name="valueQualifier" from="${AssayContextItem.constraints.qualifier.inList}" noSelection="['': '']"  value="${ fixedValue?.valueQualifier }"/>
    </div>

    <label class="control-label" >Units:</label>
    <input class="input-xlarge" type="text" id="valueUnits" name='valueUnits'  value="${ fixedValue?.valueUnits }">

</af:page>
