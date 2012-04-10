<%@ page import="bard.db.model.UnitConversion" %>



<div class="fieldcontain ${hasErrors(bean: unitConversionInstance, field: 'fromUnit', 'error')} required">
	<label for="fromUnit">
		<g:message code="unitConversion.fromUnit.label" default="From Unit" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="fromUnit" name="fromUnit.id" from="${bard.db.model.Unit.list()}" optionKey="id" required="" value="${unitConversionInstance?.fromUnit?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitConversionInstance, field: 'toUnit', 'error')} required">
	<label for="toUnit">
		<g:message code="unitConversion.toUnit.label" default="To Unit" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="toUnit" name="toUnit.id" from="${bard.db.model.Unit.list()}" optionKey="id" required="" value="${unitConversionInstance?.toUnit?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitConversionInstance, field: 'multiplier', 'error')} ">
	<label for="multiplier">
		<g:message code="unitConversion.multiplier.label" default="Multiplier" />
		
	</label>
	<g:field type="number" name="multiplier" value="${fieldValue(bean: unitConversionInstance, field: 'multiplier')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitConversionInstance, field: 'offset', 'error')} ">
	<label for="offset">
		<g:message code="unitConversion.offset.label" default="Offset" />
		
	</label>
	<g:field type="number" name="offset" value="${fieldValue(bean: unitConversionInstance, field: 'offset')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitConversionInstance, field: 'formula', 'error')} ">
	<label for="formula">
		<g:message code="unitConversion.formula.label" default="Formula" />
		
	</label>
	<g:textArea name="formula" cols="40" rows="5" maxlength="256" value="${unitConversionInstance?.formula}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: unitConversionInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="unitConversion.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${unitConversionInstance?.modifiedBy}"/>
</div>

