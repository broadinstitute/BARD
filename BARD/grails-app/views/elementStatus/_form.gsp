<%@ page import="bard.db.model.ElementStatus" %>



<div class="fieldcontain ${hasErrors(bean: elementStatusInstance, field: 'elementStatus', 'error')} ">
	<label for="elementStatus">
		<g:message code="elementStatus.elementStatus.label" default="Element Status" />
		
	</label>
	<g:textField name="elementStatus" maxlength="20" value="${elementStatusInstance?.elementStatus}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: elementStatusInstance, field: 'capability', 'error')} ">
	<label for="capability">
		<g:message code="elementStatus.capability.label" default="Capability" />
		
	</label>
	<g:textArea name="capability" cols="40" rows="5" maxlength="256" value="${elementStatusInstance?.capability}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: elementStatusInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="elementStatus.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${elementStatusInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: elementStatusInstance, field: 'elements', 'error')} ">
	<label for="elements">
		<g:message code="elementStatus.elements.label" default="Elements" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${elementStatusInstance?.elements?}" var="e">
    <li><g:link controller="element" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="element" action="create" params="['elementStatus.id': elementStatusInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'element.label', default: 'Element')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: elementStatusInstance, field: 'resultTypes', 'error')} ">
	<label for="resultTypes">
		<g:message code="elementStatus.resultTypes.label" default="Result Types" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${elementStatusInstance?.resultTypes?}" var="r">
    <li><g:link controller="resultType" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="resultType" action="create" params="['elementStatus.id': elementStatusInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'resultType.label', default: 'ResultType')])}</g:link>
</li>
</ul>

</div>

