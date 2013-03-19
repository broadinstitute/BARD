<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.dictionary.*" %>

<af:page>

    <g:set var="attributeLabel" value="${attribute?.attributeLabel}"/>
    <g:set var="valueTypeOption" value="${valueType?.valueTypeOption}"/>
    <g:render template="common/itemWizardSelectionsTable"
              model="['attribute': attributeLabel, 'valueType': valueTypeOption, 'value': 'Not define yet']"/>

    <g:hasErrors bean="${fixedValue}">
        <div class="alert alert-error">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <g:renderErrors bean="${fixedValue}"/>
        </div>
    </g:hasErrors>

    <g:if test="${attributeExternalUrl}">

        <g:if test="${supportsExternalOntologyLookup}">
            <p>An integrated search facility has been built to lookup ${attributeLabel} values, please enter an identifier or some text and select a value.
            Alternatively, you can search directly on the <a
                    href="${attributeExternalUrl}">${attributeExternalUrl}</a> site and manually enter an external value id and description.
            </p>

            <div class="row-fluid">
                <div class="span12">
                    <input type="hidden" id="extValueIdSearch" name="extValueIdSearch"/>
                </div>
            </div>

        </g:if>
        <g:else>
            <p>There is currently no integrated search for the ${attributeLabel} external ontology.</p>

            <p>Please search directly on the <a
                    href="${attributeExternalUrl}">${attributeExternalUrl}</a> site and manually enter an external value id and a text description below.
            </p>
        </g:else>


        <div class="row-fluid">
            <div class="span3">
                <label class="control-label">External Value Id:</label>
            </div>

            <div class="span8">
                <div class="controls">
                    <g:textField id="extValueId" name="extValueId" value="${fixedValue?.extValueId}"/>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span3">
                <label class="control-label">Description:</label>
            </div>

            <div class="span8">
                <div class="controls">
                    <g:textField id="valueLabel" name="valueLabel" value="${fixedValue?.valueLabel}"/>
                </div>
            </div>
        </div>
    </g:if>
    <g:else>

        <h1>Search or Browse for a defined term to use as the value.  Or, enter a number directly into the numeric value field. If relevant, choose the relevant units that describe the number entered.</h1>

        <div class="row-fluid">
            <div class="span12">
                <div class="row-fluid">
                    <div class="span12">
                        <%-- This hidden control becomes the value selection box --%>
                        <input type="hidden" id="valueId" name="valueId">
                    </div>
                </div>
            </div>
        </div>
        <br>

        <div class="row-fluid">
            <div class="span12">
                <div class="row-fluid">
                    <div class="span2">
                        <label class="control-label">Qualifier:</label>
                    </div>

                    <div class="span10">
                        <div class="controls">
                            <g:select name="valueQualifier" from="${AssayContextItem.constraints.qualifier.inList}"
                                      noSelection="['': '']" value="${listValue?.valueQualifier}"/>
                        </div>
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span2">
                        <label class="control-label">Numeric Value:</label>
                    </div>

                    <div class="span4">
                        <div class="controls"><input class="input-large" type="text" size='10' id="numericValue"
                                                     name='numericValue' value="${fixedValue?.numericValue}"></div>
                    </div>

                    <div class="span6">
                        <%-- This hidden control becomes the units selection box --%>
                        <div class="controls"><input type="hidden" id="valueUnitId" name="valueUnitId"></div>
                    </div>
                </div>
            </div>
        </div>

    <%-- This hidden field is needed to get access to the value label (name) --%>
        <input type="hidden" id="valueLabel" name="valueLabel">
    </g:else>

<%-- This hidden field is needed for passing state needed for the ontology query --%>
    <input type="hidden" id="attributeElementId" value="${attribute.attributeId}">

<%-- This hidden field is needed to get access to the unit label --%>
    <input type="hidden" id="valueUnitLabel" name="valueUnitLabel">

    <input type="hidden" id="pageNumber" name="pageNumber" value="${page}"/>
    <input type="hidden" id="valueType" name="valueType" value="${valueType?.valueTypeOption}"/>

</af:page>
