<%@ page import="bard.db.dictionary.ElementStatus" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap, elementEdit"/>
    <meta name="layout" content="basic"/>
    <title>Edit element (${element?.id})</title>
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
            <label class="control-label">BARD Unique Identifier:</label>

            <div class="controls">
                <input type="text" id="bardURI" name='bardURI' value="${element.bardURI}" disabled="true">
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Base unit of measure:</label>

            <div class="controls">
                <g:select name="unit.id"
                          from="${baseUnits}"
                          value="${element?.unit.id.toString() ?: '<none>'}"
                          optionValue="label"
                          optionKey="id"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Abbreviation:</label>

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
            <label class="control-label">Expected Value Type:</label>

            <div class="controls">
                <g:select name="expectedValueType" id="expectedValueType"
                          from="${bard.db.enums.ExpectedValueType.values()}"
                          value="${element?.expectedValueType}"
                          optionValue="id"/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">Add Child Method:</label>

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
