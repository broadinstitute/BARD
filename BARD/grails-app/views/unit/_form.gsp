<%@ page import="bard.db.model.Unit" %>



<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'unit', 'error')} ">
	<label for="unit">
		<g:message code="unit.unit.label" default="Unit" />
		
	</label>
	<g:textField name="unit" maxlength="100" value="${unitInstance?.unit}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="unit.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="1000" value="${unitInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="unit.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${unitInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'elements', 'error')} ">
	<label for="elements">
		<g:message code="unit.elements.label" default="Elements" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${unitInstance?.elements?}" var="e">
    <li><g:link controller="element" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="element" action="create" params="['unit.id': unitInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'element.label', default: 'Element')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'measures', 'error')} ">
	<label for="measures">
		<g:message code="unit.measures.label" default="Measures" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${unitInstance?.measures?}" var="m">
    <li><g:link controller="measure" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="measure" action="create" params="['unit.id': unitInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'measure.label', default: 'Measure')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'resultTypes', 'error')} ">
	<label for="resultTypes">
		<g:message code="unit.resultTypes.label" default="Result Types" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${unitInstance?.resultTypes?}" var="r">
    <li><g:link controller="resultType" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="resultType" action="create" params="['unit.id': unitInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'resultType.label', default: 'ResultType')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'results', 'error')} ">
	<label for="results">
		<g:message code="unit.results.label" default="Results" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${unitInstance?.results?}" var="r">
    <li><g:link controller="result" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="result" action="create" params="['unit.id': unitInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'result.label', default: 'Result')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'unitConversionsForFromUnit', 'error')} ">
	<label for="unitConversionsForFromUnit">
		<g:message code="unit.unitConversionsForFromUnit.label" default="Unit Conversions For From Unit" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${unitInstance?.unitConversionsForFromUnit?}" var="u">
    <li><g:link controller="unitConversion" action="show" id="${u.id}">${u?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="unitConversion" action="create" params="['unit.id': unitInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'unitConversion.label', default: 'UnitConversion')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: unitInstance, field: 'unitConversionsForToUnit', 'error')} ">
	<label for="unitConversionsForToUnit">
		<g:message code="unit.unitConversionsForToUnit.label" default="Unit Conversions For To Unit" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${unitInstance?.unitConversionsForToUnit?}" var="u">
    <li><g:link controller="unitConversion" action="show" id="${u.id}">${u?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="unitConversion" action="create" params="['unit.id': unitInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'unitConversion.label', default: 'UnitConversion')])}</g:link>
</li>
</ul>

</div>

