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

<p>Search or Browse for a defined term.</p>

<input type="hidden" id="pageNumber" name="pageNumber" value="${ page }"/>
<input type="hidden" id="valueType" name="valueType" value="${ valueType?.valueTypeOption }"/>

</af:page>