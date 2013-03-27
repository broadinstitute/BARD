<div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
    <label class="control-label" for="attributeElementId">
        <g:message code="contextItem.attributeElementId.label"/>:</label>

    <div class="controls">
        <g:hiddenField class="span8" id="attributeElementId" name="attributeElementId" value="${instance?.attributeElementId}"/>
        <span class="help-inline"><g:fieldError field="attributeElementId" bean="${instance}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
    <label class="control-label" for="valueElementId">
        <g:message code="contextItem.valueElementId.label"/>:</label>

    <div class="controls">
        <g:hiddenField class="span8" id="valueElementId" name="valueElementId"
                     value="${instance?.valueElementId}"/>
        <span class="help-inline"><g:fieldError field="valueElementId" bean="${instance}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
    <label class="control-label" for="extValueId">
        <g:message code="contextItem.extValueId.label"/>:</label>

    <div class="controls">
        <g:textField class="span8" id="extValueId" name="extValueId"
                     value="${instance?.extValueId}"/>
        <span class="help-inline"><g:fieldError field="extValueId" bean="${instance}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
    <label class="control-label" for="qualifier">
        <g:message code="contextItem.qualifier.label"/>:</label>

    <div class="controls">
        <g:textField class="span8" id="qualifier" name="qualifier"
                     value="${instance?.qualifier}"/>
        <span class="help-inline"><g:fieldError field="qualifier" bean="${instance}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
    <label class="control-label" for="valueNum">
        <g:message code="contextItem.valueNum.label"/>:</label>

    <div class="controls">
        <g:textField class="span8" id="valueNum" name="valueNum"
                     value="${instance?.valueNum}"/>
        <span class="help-inline"><g:fieldError field="valueNum" bean="${instance}"/></span>
    </div>
</div>

<div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
    <label class="control-label" for="valueDisplay">
        <g:message code="contextItem.valueDisplay.label"/>:</label>

    <div class="controls">
        <g:textField class="span8" id="valueDisplay" name="valueDisplay"
                     value="${instance?.valueDisplay}"/>
        <span class="help-inline"><g:fieldError field="valueDisplay" bean="${instance}"/></span>
    </div>
</div>