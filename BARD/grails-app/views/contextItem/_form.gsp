%{--<g:render template="message"/>--}%
<g:render template="/common/errors" model="['errors': instance?.errors?.allErrors]"/>
<div class="row-fluid">
    <div class="span12">
        <h4>${action} an item for the ${instance?.contextItem?.context?.contextName} Context</h4>
    </div>
</div>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="edit">
            <g:hiddenField name="contextOwnerId" value="${instance?.contextOwnerId}"/>
            <g:hiddenField name="contextId" value="${instance?.contextId}"/>
            <g:hiddenField name="contextClass" value="${instance?.contextClass}"/>
            <g:hiddenField name="contextItemId" value="${instance?.contextItemId}"/>
            <g:hiddenField name="version" value="${instance?.version}"/>
            <g:hiddenField name="attributeElementLabel" value="${instance?.attributeElementLabel}"/>

            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="attributeElementId"><g:message
                        code="contextItem.attributeElementId.label"/>:</label>

                <div class="controls">
                    <g:hiddenField class="span8" id="attributeElementId" name="attributeElementId"
                                   value="${instance?.attributeElementId}"/>
                    <span class="help-inline"><g:fieldError field="attributeElementId" bean="${instance}"/></span>
                </div>
            </div>


            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="valueElementId"><g:message
                        code="contextItem.valueElementId.label"/>:</label>

                <div class="controls">
                    <g:hiddenField class="span8" id="valueElementId" name="valueElementId"
                                   value="${instance?.valueElementId}"/>
                    <span class="help-inline"><g:fieldError field="valueElementId" bean="${instance}"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="extValueId"><g:message code="contextItem.extValueId.label"/>:</label>

                <div class="controls">
                    <g:textField class="span8" id="extValueId" name="extValueId" value="${instance?.extValueId}"/>
                    <span class="help-inline"><g:fieldError field="extValueId" bean="${instance}"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: instance, field: 'valueNum', 'error')}">
                <label class="control-label" for="valueNum"><g:message code="contextItem.valueNum.label"/>:</label>

                <div class="controls">
                    <g:textField class="span2" id="qualifier" name="qualifier"
                                 placeholder="${message(code: "contextItem.qualifier.label")}"
                                 value="${instance?.qualifier}"/>

                    <g:textField class="span2" id="valueNum" name="valueNum"
                                 placeholder="${message(code: "contextItem.valueNum.label")}"
                                 value="${instance?.valueNum}"/>
                    <g:textField class="span3" id="valueNumUnitId" name="valueNumUnitId"
                                 placeholder="${message(code: "contextItem.valueNumUnitId.label")}"
                                 value="${instance?.valueNumUnitId}"/>
                    <span class="help-block">
                        <g:fieldError field="qualifier" bean="${instance}"/>
                        <g:fieldError field="valueNum" bean="${instance}"/>
                        <g:fieldError field="valueNumUnitId" bean="${instance}"/>
                    </span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="valueDisplay"><g:message
                        code="contextItem.valueDisplay.label"/>:</label>

                <div class="controls">
                    <g:textField class="span8" id="valueDisplay" name="valueDisplay" value="${instance?.valueDisplay}"/>
                    <span class="help-inline"><g:fieldError field="valueDisplay" bean="${instance}"/></span>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="${instance?.ownerController}" action="editContext"
                            id="${instance?.contextOwnerId}"
                            fragment="card-${instance?.contextId}" class="btn">Cancel</g:link>
                    <button type="submit" name="_action_${action.toLowerCase()}"
                            class="btn btn-primary">${action}</button>
                </div>
            </div>

        </g:form>
        <r:script>
            $(document).ready(function () {
                $('attributeElementId').select2("data", {id:${instance?.attributeElementId}, text:${instance?.attributeElementLabel}});
            });
        </r:script>
    </div>
</div>