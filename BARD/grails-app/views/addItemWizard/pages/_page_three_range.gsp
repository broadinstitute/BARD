<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
	<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>

	<g:hasErrors bean="${rangeValue}">
		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">×</button>
			<g:renderErrors bean="${rangeValue}"/>
		</div>
	</g:hasErrors>

	<h1>Define range value. If relevant, choose the relevant units that describe the number entered.</h1>

	<div class="row-fluid">
    	<div class="span12">
    		

    		<div class="row-fluid">
    			<div class="span2">
    				<label class="control-label" >Min Value:</label>
	    		</div>
	    		<div class="span4">
	    			<div class="controls"><input class="input-large" type="text" size='10' id="minValue" name='minValue'  value="${ rangeValue?.minValue }"></div>
	    		</div>
	    		<div class="span2">
    				<label class="control-label" >Max Value:</label>
	    		</div>
	    		<div class="span4">
	    			<div class="controls"><input class="input-large" type="text" size='10' id="maxValue" name='maxValue'  value="${ rangeValue?.maxValue }"></div>
	    		</div>
    		</div>

    		<div class="row-fluid">
    			
	    		<div class="span12">
	    			<%-- This hidden control becomes the units selection box --%>
    				<div class="controls"><input type="hidden" id="valueUnitId" name="valueUnitId"></div>
	    		</div>
    		</div>
    	</div>
	</div>
	
	<%-- This hidden field is needed for passing state needed for the ontology query --%>
    <input type="hidden" id="attributeElementId" value="${attribute.attributeId}">
    
    <%-- This hidden field is needed to get access to the unit label --%>
    <input type="hidden" id="valueUnitLabel" name="valueUnitLabel">

	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
	<input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>

</af:page>