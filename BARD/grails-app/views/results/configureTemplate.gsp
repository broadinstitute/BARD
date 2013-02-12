<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Configure template</title>
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

<g:if test="${experiment?.id}">
    <div class="row-fluid">
        <g:form action="generatePreview">
            <g:hiddenField name="experimentId" value="${experiment.id}"/>

            <g:if test="${assayItems.size() > 0 || measureItems.size() > 0}">
                <h4>Context items</h4>
            <g:if test="${assayItems.size() > 0}">
                <p>The following context items are not associated with any measure.   If you will be providing a value for this context items, select "Per experiment" to signify you'll be submitting a value that applies to the entire experiment.</p>

                <div class="form-inline">
                <g:each in="${assayItems}" var="item" status="index">
                    <div class="control-group">
                        <div class="controls">
                            <input type="hidden" name="contextItemIds[${index}]" value="${item.id}">

                            <select name="contextItemFrequency[${index}]" id="contextItemFrequency[${index}]">
                                <option value="none">Not provided</option>
                                <option value="experiment">Per Experiment</option>
                            </select>

                            <label for="contextItemFrequency[${index}]">
                                ${item.attributeElement.label}
                            </label>

                        </div>
                    </div>
                </g:each>
                </div>
            </g:if>

            <g:if test="${measureItems.size() > 0}">
                <p>The following context items are associated with at least one measure. The items below can be provided once (meaning they will be applied to the entire experiment) or once per measurement.</p>

                <div class="form-inline">
                    <g:each in="${measureItems}" var="item" status="index">
                        <div class="control-group">
                            <div class="controls">
                                <input type="hidden" name="measureItemIds[${index}]" value="${item.id}">

                                <select name="measureItemFrequency[${index}]" id="measureItemFrequency[${index}]">
                                    <option value="none">Not provided</option>
                                    <option value="experiment">Per Experiment</option>
                                    <option value="measurement">Per Measurement</option>
                                </select>

                                <label for="measureItemFrequency[${index}]">
                                    ${item.attributeElement.label}
                                </label>

                            </div>
                        </div>
                    </g:each>
                </div>
            </g:if>
            </g:if>

            <g:if test="${experiment.assay.measures.size() > 0}">
                <h4>Measures</h4>

                <p>Select the measures that you will be providing for this result set.</p>

                <g:each in="${experiment.assay.measures}" var="measure" status="index">
                    <div class="control-group">
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" name="measureIds[${index}]" value="${measure.id}"> ${measure.displayLabel}
                            </label>
                        </div>
                    </div>
                </g:each>
            </g:if>

            <div class="control-group">
                <div class="controls">
                    <button type="submit" class="btn">Generate preview</button>
                </div>
            </div>
        </g:form>
    </div>
</g:if>

</body>
</html>
