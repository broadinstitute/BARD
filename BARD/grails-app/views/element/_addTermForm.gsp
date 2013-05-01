<%@ page import="bard.db.dictionary.Element" %>
<div class="fieldcontain ${hasErrors(bean: elementInstance, field: '', 'error')} required">
    <label for="label">
        <g:message code="element.label.label" default="Proposed term" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="label" maxlength="128" value="${elementInstance?.label}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'description', 'error')} required">
    <label for="description">
        <g:message code="element.description.label" default="Proposed definition" />
        <span class="required-indicator">*</span>
    </label>
    <g:textArea name="description" cols="40" rows="5" maxlength="1000" value="${elementInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'label', 'error')} required">
    <label for="label">
        <g:message code="element.label.label" default="Proposed term" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="label" maxlength="128" value="${elementInstance?.label}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'description', 'error')} required">
	<label for="description">
		<g:message code="element.description.label" default="Proposed definition" />
        <span class="required-indicator">*</span>
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="1000" value="${elementInstance?.description}"/>
</div>



<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'abbreviation', 'error')} ">
    <label for="abbreviation">
        <g:message code="element.abbreviation.label" default="Abbreviation" />

    </label>
    <g:textField name="abbreviation" maxlength="20" value="${elementInstance?.abbreviation}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'synonyms', 'error')} ">
	<label for="synonyms">
		<g:message code="element.synonyms.label" default="Synonyms" />
		
	</label>
	<g:textArea name="synonyms" cols="40" rows="5" maxlength="1000" value="${elementInstance?.synonyms}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'unit', 'error')} ">
    <label for="unit">
        <g:message code="element.unit.label" default="Unit" />

    </label>
    <g:select id="unit" name="unit.id" from="${bard.db.dictionary.Element.list()}" optionKey="id" value="${elementInstance?.unit?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: elementInstance, field: 'comments', 'error')} required">
	<label for="comments">
		<g:message code="element.comments.label" default="Explanation/Comments" />
        <span class="required-indicator">*</span>
	</label>
	<g:textArea name="comments" cols="40" rows="5" maxlength="1000" value="${elementInstance?.comments}"/>
</div>
