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

    <p>The attribute ${attributeLabel} represents a link from BARD to an ontology or dictionary maintained outside of the internal BARD ontology.  Ideally a canonical identifier ( the external value id ) as well as a text description of the value for this attribute should be entered below.</p>

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




<%-- This hidden field is needed for passing state needed for the ontology query --%>
    <input type="hidden" id="attributeElementId" value="${attribute.attributeId}">

<%-- This hidden field is needed to get access to the unit label --%>
    <input type="hidden" id="valueUnitLabel" name="valueUnitLabel">

    <input type="hidden" id="pageNumber" name="pageNumber" value="${page}"/>
    <input type="hidden" id="valueType" name="valueType" value="${valueType?.valueTypeOption}"/>

    <br/>

</af:page>
