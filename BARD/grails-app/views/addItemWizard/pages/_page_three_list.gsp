<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
	<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>

	<g:hasErrors bean="${listValue}">
		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">×</button>
			<g:renderErrors bean="${listValue}"/>
		</div>
	</g:hasErrors>



	<g:set var="list" value="${listOfValues}" />
	<g:set var="enableEdit" value="${true}" />
	<div id="itemsInListTable">
		<g:render template="common/itemsInListTable" model="['listOfValues': list, 'enableEdit': enableEdit]"/>
		<br>
	</div>
    <g:if test="${attributeExternalUrl}">
        <g:render template="common/extValueId_input" />
    </g:if>
	<g:else>
        <p>Search or Browse for a defined term to use as the value.  Or, enter a number directly into the numeric value field.  If relevant, choose the relevant units that describe the number entered.</p>
        <div class="row-fluid">
            <div class="span12">
                <div class="row-fluid">
                    <div class="span12">
                        <%-- This hidden control becomes the value selection box --%>
                        <input type="hidden" id="valueId" name="valueId">
                    </div>
                </div>
            </div>
        </div>
        <br>
        <div class="row-fluid">
            <div class="span12">


                <div class="row-fluid">
                    <div class="span2">
                        <label class="control-label" >Qualifier:</label>
                    </div>
                    <div class="span10">
                        <div  class="controls">
                            <g:select name="valueQualifier" from="${AssayContextItem.constraints.qualifier.inList}" noSelection="['': '']"  value="${ listValue?.valueQualifier }"/>
                        </div>
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span2">
                        <label class="control-label" >Numeric Value:</label>
                    </div>
                    <div class="span4">
                        <div class="controls"><input class="input-large" type="text" size='10' id="numericValue" name='numericValue'  value="${ fixedValue?.numericValue }"></div>
                    </div>
                    <div class="span6">
                        <%-- This hidden control becomes the units selection box --%>
                        <div class="controls"><input type="hidden" id="valueUnitId" name="valueUnitId"></div>
                    </div>
                </div>
            </div>
        </div>

        <%-- This hidden field is needed to get access to the value label (name) --%>
        <input type="hidden" id="valueLabel" name="valueLabel">
    </g:else>
    <%-- This hidden field is needed for passing state needed for the ontology query --%>
    <input type="hidden" id="attributeElementId" value="${attribute.attributeId}">

    <%-- This hidden field is needed to get access to the unit label --%>
    <input type="hidden" id="valueUnitLabel" name="valueUnitLabel">

    <input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
    <input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>

</af:page>