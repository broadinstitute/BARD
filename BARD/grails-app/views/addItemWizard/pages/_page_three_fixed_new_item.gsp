<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
	<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>
	
	<g:hasErrors bean="${newDictionaryItemCmd}">
		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">×</button>
			<g:renderErrors bean="${newDictionaryItemCmd}"/>
		</div>
	</g:hasErrors>
	<br>
	<g:if test="${newDictionaryItemCmd == null}">
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid">
		    			<div class="span4">
		    				<p style="color: #006DBA">Do you want to add a new dictionary item?</p>
		    			</div>
		    			<div class="span2">	    			
			    			<af:ajaxButton name="toPageThreeFixedNewItem" value="Yes" id="'yes'" afterSuccess="afterSuccess();" class="btn btn-small btn-primary" />
		    			</div>
		    			<div class="span2">
		    				<af:ajaxButton name="toPageThreeFixedNewItem" value="No" id="'no'" afterSuccess="afterSuccess();" class="btn btn-small" />
		    			</div>
		    			<div class="span4"></div>	    			
		    	</div>
			</div>		
		</div>	
	</g:if>
	<g:elseif test="${newDictionaryItemCmd.newDictionaryItem}">
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid">
		    			<div class="span12">
		    				<h1>New dictionary item</h1>
		    			</div>	    			  			
		    	</div>
			</div>		
		</div>
		<g:render template="pages/newElementForm"/>
	</g:elseif>
	<g:else>
		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid">
		    			<div class="span4">
		    				<h1>Enter name for the value</h1>
		    			</div>	    			  			
		    	</div>
			</div>		
		</div>
		<g:render template="pages/newValueNameForm"/>
	</g:else>	
	
	<%-- This hidden field is needed for passing state needed for the ontology query --%>
	<input type="hidden" id="attributeElementId" value="${attribute.attributeId}">
	
	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
	<input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>
	<input type="hidden" id="newValueItem" name="newValueItem" value="${ fixedValueAddNewItem }"/>
	<input type="hidden" id="dictionaryItem" name="dictionaryItem" value="${ dictionaryItem }"/>

</af:page>