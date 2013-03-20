<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

	<g:set var="attributeLabel" value="${ attribute?.attributeLabel }" />
	<g:set var="valueTypeOption" value="${ valueType?.valueTypeOption }" />
	<g:render template="common/itemWizardSelectionsTable" model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'No value required at this point']"/>
	
	
	<h1>Value Type: Free</h1>
	<p>
		Click <strong>"Next"</strong> below to proceed to review page and save the new item.
	</p>
	
	<%-- This hidden field is needed for passing state needed for the ontology query --%>
	<input type="hidden" id="attributeElementId" value="${attribute.attributeId}">
	
	<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
	<input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>

</af:page>