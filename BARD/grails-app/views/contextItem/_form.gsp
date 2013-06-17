%{--<g:render template="message"/>--}%
<div class="row-fluid">
    <div class="offset1 span10">
        <g:render template="/common/errors" model="['errors': instance?.errors?.globalErrors]"/>
    </div>
</div>
<g:set var="disabledInput" value="${reviewNewItem ? true : false}"/>

<g:form class="form-horizontal" action="${action.toLowerCase()}">
    <g:hiddenField name="contextOwnerId" value="${instance?.contextOwnerId}"/>
    <g:hiddenField name="contextId" value="${instance?.contextId}"/>
    <g:hiddenField name="contextClass" value="${instance?.contextClass}"/>
    <g:hiddenField name="contextItemId" value="${instance?.contextItemId}"/>
    <g:hiddenField name="version" value="${instance?.version}"/>


    <g:hiddenField name="attributeElementText" value="${instance?.contextItem?.attributeElement?.label}"/>
    <g:hiddenField name="valueElementText" value="${instance?.contextItem?.valueElement?.label}"/>
    <g:hiddenField name="extValueText" value="(${instance?.extValueId}) ${instance?.valueDisplay}"/>


    <div class="control-group ${hasErrors(bean: instance, field: 'attributeElementId', 'error')}">
        <label class="control-label" for="attributeElementId"><g:message
                code="contextItem.attributeElementId.label"/>:</label>

        <div class="controls">
            <g:hiddenField class="span8" id="attributeElementId" name="attributeElementId"
                           value="${instance?.attributeElementId}" disabled="${disabledInput}"/>
            <span class="help-inline"><g:fieldError field="attributeElementId" bean="${instance}"/></span>

        </div>

    </div>

    <div id="elementValueContainer" style="display: false;">
        <g:if test="${disabledInput == false}">
            <div class="row-fluid">
                <div class="span6 offset2 alert alert-info">
                    <p>This attribute expects a value from the dictionary, start typing to select a value.</p>
                    <p>If you cannot find an existing value, please add a value to the dictionary.
                    <g:link controller="element" action="addTerm" target="proposeTerm" class="btn">Propose a New Dictionary Term</g:link></p>
                </div>
            </div>
        </g:if>
        <div class="control-group ${hasErrors(bean: instance, field: 'valueElementId', 'error')}">

            <label class="control-label" for="valueElementId"><g:message
                    code="contextItem.valueElementId.label"/>:</label>

            <div class="controls">
                <g:hiddenField class="span8" id="valueElementId" name="valueElementId"
                               value="${instance?.valueElementId}" disabled="${disabledInput}"/>

                <span class="help-inline"><g:fieldError field="valueElementId" bean="${instance}"/></span>
            </div>
        </div>
    </div>

    <div id="externalOntologyValueContainer">
        <g:if test="${disabledInput == false}">
            <div id="externalOntologyInfo" class="row-fluid" >

            </div>
            <div id="externalOntologySearch" class="control-group">
                <label class="control-label" for="extValueId"><g:message code="contextItem.extValueSearch.label"/>:</label>
                <div class="controls">
                    <g:hiddenField
                            class="span8" id="extValueSearch" name="extValueSearch" value="${instance?.extValueId}"
                            />
                </div>
            </div>
        </g:if>


        <div class="control-group ${hasErrors(bean: instance, field: 'extValueId', 'error')}">
            <label class="control-label" for="extValueId"><g:message
                    code="contextItem.extValueId.label"/>:</label>

            <div class="controls">

                <g:textField
                        class="span8" id="extValueId" name="extValueId" value="${instance?.extValueId}"
                        disabled="${disabledInput}"/>
                <span class="help-inline"><g:fieldError field="extValueId" bean="${instance}"/></span>
            </div>
        </div>
    </div>

    <div id="numericValueContainer" class="control-group ${hasErrors(bean: instance, field: 'qualifier', 'error')}">
            <g:if test="${disabledInput == false}">
                <div class="row-fluid">
                    <div class="span6 offset2 alert alert-info">
                        <p>This attribute expects a numeric value, please enter an integer, decimal or scientific notation, e.g. 1, 1.0 or 1E-3</p>
                    </div>
                </div>
            </g:if>
            <label class="control-label" for="valueNum"><g:message code="contextItem.valueNum.label"/>:</label>

            <div class="control controls-row">
                <g:select class="offset1 span2" id="qualifier" name="qualifier"
                          noSelection="${['': message(code: "contextItem.qualifier.label")]}"
                          from="${instance?.constraints.qualifier.inList}"
                          value="${instance?.qualifier}"
                          disabled="${disabledInput}"/>

                <g:textField class="span2" id="valueNum" name="valueNum"
                             placeholder="${message(code: "contextItem.valueNum.label")}"
                             value="${instance?.valueNum}" disabled="${disabledInput}"/>

                <g:textField class="span2" id="valueNumUnitId" name="valueNumUnitId"
                             value="${instance?.valueNumUnitId}"
                             disabled="${disabledInput}"/>
            </div>

    </div>
    <div class="row-fluid">
        <g:set var="numericFieldErrors" value="${instance?.errors?.fieldErrors.findAll{it.field in ['qualifier', 'valueNum', 'valueNumUnitId']}}" />
        <g:if test="${numericFieldErrors}">
            <div class="offset1 span10">
                <g:render template="/common/errors" model="['errors': instance?.errors?.fieldErrors.findAll{it.field in ['qualifier', 'valueNum', 'valueNumUnitId']}]"/>
            </div>
        </g:if>
    </div>

    <div id="numericRangeValueContainer"
         class="control-group ${hasErrors(bean: instance, field: 'qualifier', 'error')}">
        <g:if test="${disabledInput == false}">
            <div class="row-fluid">
                <div class="span6 offset2 alert alert-info">
                    <p>This attribute expects a numeric value, please enter an integer, decimal or scientific notation, e.g. 1, 1.0 or 1E-3</p>
                </div>
            </div>
        </g:if>

        <div class="control-group ${hasErrors(bean: instance, field: 'valueMin', 'error')}">
            <label class="control-label"><g:message code="contextItem.range.label"/>:</label>

            <div class="control controls-row">
                <g:textField class="offset1 span2" id="valueMin" name="valueMin"
                             placeholder="${message(code: "contextItem.valueMin.label")}" value="${instance?.valueMin}"
                             disabled="${disabledInput}"/>
                <span class="span1">TO</span>
                <g:textField class="span2" id="valueMax" name="valueMax"
                             placeholder="${message(code: "contextItem.valueMax.label")}" value="${instance?.valueMax}"
                             disabled="${disabledInput}"/>
            </div>
        </div>
    </div>

    <div id="freeTextValueContainer">
        <div class="control-group ${hasErrors(bean: instance, field: 'valueDisplay', 'error')}">
            <label class="control-label" for="valueDisplay"><g:message
                    code="contextItem.valueDisplay.label"/>:</label>

            <div class="controls">
                <g:textField class="span8" id="valueDisplay" name="valueDisplay"
                             value="${instance?.valueDisplay}"
                             disabled="${disabledInput}"/>
                <span class="help-inline"><g:fieldError field="valueDisplay" bean="${instance}"/></span>
            </div>
        </div>
    </div>

    <div class="row-fluid">
        <div id="noneValueContainer" class="offset2 span6 alert alert-info">
            <h4>Warning!</h4>

            <p>This attribute isn't allowed to have values assigned to it. Please select another attribute.</p>
        </div>
    </div>

    <div class="control-group">
        <div class="controls">

            <g:if test="${reviewNewItem}">
                <g:link controller="${instance?.ownerController}" action="editContext"
                        id="${instance?.contextOwnerId}"
                        fragment="card-${instance?.contextId}" class="btn">Back to Context</g:link>

                <g:set var="contextItemParams" value="${[contextItemId: instance.contextItemId, contextId: instance?.contextId, contextClass: instance?.context.simpleClassName, contextOwnerId: instance?.contextOwnerId]}"></g:set>
                <g:link action="edit" class="btn" params="${contextItemParams}">Edit</g:link>
                <g:link action="create" class="btn btn-primary focus" params="${contextItemParams}">Add Another Item</g:link>
            </g:if>
            <g:else>
                <g:link controller="${instance?.ownerController}" action="editContext"
                        id="${instance?.contextOwnerId}"
                        fragment="card-${instance?.contextId}" class="btn">Cancel</g:link>
                <button type="submit" class="btn btn-primary">${action}</button>

            </g:else>
        </div>
    </div>

    <script id="externalOntologyIntegratedSearchTemplate" type="text/x-handlebars-template">
        <div class="span6 offset2 alert alert-info ">
            <p>An integrated lookup service exists for this External Ontology, start typing an identifier or some text and select a value.</p>
            <p>This lookup service reaches <strong>outside of this application</strong> and <strong>performance can vary</strong> for a number of reasons including network conditions and the underlying service.</p>
            <p>If the integrated search for the '{{attributeLabel}}' site doesn't meet your expectations, please search directly on the '{{attributeLabel}}'
            site.  <a href="{{attributeExternalUrl}}" target="external_ontology_site" class="btn">Open Site</a></p>

            <p>Then manually enter an External Ontology Id and a Display Value.</p>
        </div>

    </script>
    <script id="externalOntologyNoIntegratedSearchTemplate" type="text/x-handlebars-template">
        <div class="span6 offset2 alert alert-info ">
            <p>An integrated lookup service for {{attributeLabel}} does not currently exist.</p>
            <p>Please search directly on the {{attributeLabel}} site. <a href="{{attributeExternalUrl}}" target="external_ontology_site" class="btn">Open Site</a></p>
            <p>Then manually enter an External Ontology Id and a Display Value.</p>
        </div>
    </script>
</g:form>
