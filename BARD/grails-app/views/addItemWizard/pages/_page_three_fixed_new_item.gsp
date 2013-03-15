<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
	<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>
	
	<g:hasErrors bean="${freeValue}">
		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">×</button>
			<g:renderErrors bean="${freeValue}"/>
		</div>
	</g:hasErrors>
	
	<div class="row-fluid">
		<div class="span12">
			<h1>Is this item in the dictionary?</h1>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
    			<div class="span2">
    				<label class="radio">
					  <input type="radio" name="dictionaryItem" value="yes">
					  <strong>Yes</strong>
					</label>
    			</div>
    			<div class="span2">
    				<label class="radio">
					  <input type="radio" name="dictionaryItem" value="no">
					  <strong>No</strong>
					</label>
    			</div>
    		</div>
		</div>
	</div>
	
	<%--
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
    			<div class="span12">
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
    				<div class="controls"><input type="hidden" id="valueUnitId" name="valueUnitId"></div>
	    		</div>
    		</div>
    	</div>
	</div>
	--%>
	
	<%-- This hidden field is needed for passing state needed for the ontology query --%>
	<input type="hidden" id="attributeElementId" value="${attribute.attributeId}">
	
	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
	<input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>

</af:page>