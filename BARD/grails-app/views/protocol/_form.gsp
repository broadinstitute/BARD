<%@ page import="bard.db.model.Protocol" %>



<div class="fieldcontain ${hasErrors(bean: protocolInstance, field: 'protocolName', 'error')} ">
	<label for="protocolName">
		<g:message code="protocol.protocolName.label" default="Protocol Name" />
		
	</label>
	<g:textArea name="protocolName" cols="40" rows="5" maxlength="500" value="${protocolInstance?.protocolName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: protocolInstance, field: 'protocolDocument', 'error')} ">
	<label for="protocolDocument">
		<g:message code="protocol.protocolDocument.label" default="Protocol Document" />
		
	</label>
	<input type="file" id="protocolDocument" name="protocolDocument" />
</div>

<div class="fieldcontain ${hasErrors(bean: protocolInstance, field: 'modifiedBy', 'error')} ">
	<label for="modifiedBy">
		<g:message code="protocol.modifiedBy.label" default="Modified By" />
		
	</label>
	<g:textField name="modifiedBy" maxlength="40" value="${protocolInstance?.modifiedBy}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: protocolInstance, field: 'assay', 'error')} required">
	<label for="assay">
		<g:message code="protocol.assay.label" default="Assay" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="assay" name="assay.id" from="${bard.db.model.Assay.list()}" optionKey="id" required="" value="${protocolInstance?.assay?.id}" class="many-to-one"/>
</div>

