<%@ page import="bard.db.dictionary.ElementStatus" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap, elementEdit"/>
    <meta name="layout" content="basic"/>
    <title>Edit element (${element?.id})</title>
    <r:script disposition="head">
        $(document).ready(function () {
            $('.icon-question-sign').popover({
                animation: true,
                placement: 'top',
                trigger: 'hover'
            });
        });
    </r:script>
    <style>
    .form-horizontal .control-group .control-label {
        width: 200px;
    }
    </style>
</head>

<body>
<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                </div>
            </div>
        </div>
    </div>
</g:if>

<g:hasErrors>
    <g:renderErrors bean="${element}"/>
</g:hasErrors>

<g:if test="${element?.id}">
    <div class="row-fluid">
        <div class="span12">
            <div class="well well-small">
                <div class="pull-left">
                    <h4>Edit Element (${element?.id})</h4>
                </div>
            </div>
        </div>
    </div>


    <g:form name="edit_element" id="${element.id}" class="form-horizontal" controller="element" action="update">
        <div class="control-group">
            <label class="control-label">ID:</label>

            <div class="controls">
                <input type="text" id="id" name='id' value="${element.id}" disabled="true">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Created:</label>

            <div class="controls">
                <input type="text" id="dateCreated" name='dateCreated' value="${element.dateCreated}"
                       disabled="true">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Modified:</label>

            <div class="controls">
                <input type="text" id="lastUpdated" name='lastUpdated' value="${element.lastUpdated}"
                       disabled="true">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Modified By:</label>

            <div class="controls">
                <input type="text" id="modifiedBy" name='modifiedBy' value="${element.modifiedBy}" disabled="true">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Label:</label>

            <div class="controls">
                <input type="text" id="label" name='label' value="${element.label}">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Status:</label>

            <div class="controls">
                <g:select name="elementStatus" id="elementStatus"
                          from="${ElementStatus.values()}"
                          value="${element?.elementStatus}"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">BARD Unique Identifier:<i class="icon-question-sign"
                                                                   data-title="This is unique URI given to the element when its status becomes published. The number is a permanent identifier and is never re-used even if the term is retired.  This URI should be used externally to BARD when referencing terms in a paper or other external forum.">

            </i></label>

            <div class="controls">
                <input type="text" id="bardURI" name='bardURI' value="${element.bardURI}" disabled="true" size="60"
                       style="width: auto;">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Base unit of measure:<i class="icon-question-sign"
                                                                 data-title="This is the unit that will be used for storing the values for this attribute. It is the default unit used when loading data, though you can use any other unit that can be converted to this unit. The software will perform a conversion to store the values in the base unit of measure.">
            </i></label>

            <div class="controls">
                <g:select name="unit.id"
                          from="${baseUnits}"
                          value="${element?.unit?.id?.toString() ?: '<none>'}"
                          optionValue="label"
                          optionKey="id"
                          noSelection="${['null': '<none>']}"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Abbreviation:<i class="icon-question-sign"
                                                         data-title="This optional field records a common abbreviation for the element. It is used for display in the user interface, or, if the label is more than 30 chars long, it is used in the warehouse to define the annotation column headers.">
            </i></label>

            <div class="controls">
                <input type="text" id="abbreviation" name='abbreviation' value="${element.abbreviation}"
                       placeholder="<none>">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Synonyms:</label>

            <div class="controls">
                <input type="text" id="synonyms" name='synonyms' value="${element.synonyms}" placeholder="<none>">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Expected Value Type:<i class="icon-question-sign"
                                                                data-title="This restricts what sort of value can be assigned to this attribute. 'element' means the value must be a descendant of this element. 'External Ontology means the value must comes from an externally curated ontology such as GO or Uniprot. 'none' means this element cannot be selected in an assay as an attribute. 'numeric' means the value must be a number or a numeric range (e.g. .01 - 100). 'free text' means is can have a value typed in by the user, any string up to 500 characters is acceptable.">
            </i></label>

            <div class="controls">
                <g:select name="expectedValueType" id="expectedValueType"
                          from="${bard.db.enums.ExpectedValueType.values()}"
                          value="${element?.expectedValueType}"
                          optionValue="id"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Add Child Method:<i class="icon-question-sign"
                                                             data-title="Tells the user how a new element is added to the dictionary. Some require a specific request to the RDM for approval of the new element - these are usually for the 'core' of the dictionary. Some can be added directly while creating an assay definition - these are usually items in a list such as assay reagent name, detection instrument name. Some cannot have child elements added - these are elements used a values e.g. 'HP Analyzer'."></i>
            </label>

            <div class="controls">
                <g:select name="addChildMethod" id="addChildMethod"
                          from="${bard.db.enums.AddChildMethod.values()}"
                          value="${element?.addChildMethod}"
                          optionValue="id"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Description:</label>

            <div class="controls">
                <g:textArea rows="10" name="description" value="${element.description}" placeholder="none"
                            escapeHtml="true"></g:textArea>
            </div>
        </div>

        <div class="control-group" id="externalUrlDiv">
            <label class="control-label">External URL:</label>

            <div class="controls">
                <input type="text" id="externalURL" name='externalURL' value="${element.externalURL}"
                       placeholder="<none>">
            </div>
        </div>

        <button type="button" title="Cancel" class="btn btn-primary">Cancel</button>
        <button type="submit" title="Save" class="btn btn-primary">Save</button>
    </g:form>
</g:if>
</body>
</html>
