%{--<g:render template="message"/>--}%
<g:render template="/common/errors" model="['errors': instance?.errors?.allErrors]"/>
<g:set var="disabledInput" value="${reviewNewItem? "true": "false"}" />
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="edit">
            <g:hiddenField name="contextOwnerId" value="${instance?.contextOwnerId}"/>
            <g:hiddenField name="contextId" value="${instance?.contextId}"/>
            <g:hiddenField name="contextClass" value="${instance?.contextClass}"/>
            <g:hiddenField name="contextItemId" value="${instance?.contextItemId}"/>
            <g:hiddenField name="version" value="${instance?.version}"/>


            <g:hiddenField name="attributeElementText" value="${instance?.contextItem?.attributeElement?.label}"/>
            <g:hiddenField name="valueElementText" value="${instance?.contextItem?.valueElement?.label}"/>
            <g:hiddenField name="extValueText" value="(${instance?.extValueId}) ${instance?.valueDisplay}"/>

            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="attributeElementId"><g:message
                        code="contextItem.attributeElementId.label"/>:</label>

                <div class="controls">
                    <g:hiddenField class="span8" id="attributeElementId" name="attributeElementId"
                                   value="${instance?.attributeElementId}" disabled="${disabledInput}"/>
                    <span class="help-inline"><g:fieldError field="attributeElementId" bean="${instance}"/></span>
                </div>
            </div>


            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="valueElementId"><g:message
                        code="contextItem.valueElementId.label"/>:</label>

                <div class="controls">
                    <g:textField class="span8" id="valueElementId" name="valueElementId"
                                   value="${instance?.valueElementId}" disabled="${disabledInput}"/>
                    <span class="help-inline"><g:fieldError field="valueElementId" bean="${instance}"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: instance, field: 'documentType', 'error')}">
                <label class="control-label" for="extValueId"><g:message code="contextItem.extValueId.label"/>:</label>

                <div class="controls">

                    <g:textField class="span8" id="extValueId" name="extValueId" value="${instance?.extValueId}" disabled="${disabledInput}"/>
                    <span class="help-inline"><g:fieldError field="extValueId" bean="${instance}"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: instance, field: 'valueNum', 'error')}">
                <label class="control-label" for="valueNum"><g:message code="contextItem.valueNum.label"/>:</label>

                <div class="controls">
                    <g:select class="span2" id="qualifier" name="qualifier"
                                 noSelection="${['':message(code: "contextItem.qualifier.label")]}"
                                 from="${instance?.constraints.qualifier.inList}"
                                 value="${instance?.qualifier}"
                                 disabled="${disabledInput}"/>

                    <g:textField class="span2" id="valueNum" name="valueNum"
                                 placeholder="${message(code: "contextItem.valueNum.label")}"
                                 value="${instance?.valueNum}" disabled="${disabledInput}"/>
                    <g:textField class="span3" id="valueNumUnitId" name="valueNumUnitId"
                                 placeholder="${message(code: "contextItem.valueNumUnitId.label")}"
                                 value="${instance?.valueNumUnitId}" disabled="${disabledInput}"/>
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
                    <g:textField class="span8" id="valueDisplay" name="valueDisplay" value="${instance?.valueDisplay}" disabled="${disabledInput}"/>
                    <span class="help-inline"><g:fieldError field="valueDisplay" bean="${instance}"/></span>
                </div>
            </div>

            <div class="control-group">
                <div class="controls">

                    <g:if test="${reviewNewItem}">
                        <g:link controller="${instance?.ownerController}" action="editContext"
                                id="${instance?.contextOwnerId}"
                                fragment="card-${instance?.contextId}" class="btn">Back to Context</g:link>
                        <button type="submit" name="_action_edit" class="btn">Edit</button>
                        <button type="submit" name="_action_create"
                                class="btn btn-primary focus">Add Another Item</button>
                    </g:if>
                    <g:else>
                        <g:link controller="${instance?.ownerController}" action="editContext"
                                id="${instance?.contextOwnerId}"
                                fragment="card-${instance?.contextId}" class="btn">Cancel</g:link>
                        <button type="submit" name="_action_${action.toLowerCase()}"
                                class="btn btn-primary">${action}</button>

                    </g:else>
                </div>
            </div>

        </g:form>
    </div>
</div>