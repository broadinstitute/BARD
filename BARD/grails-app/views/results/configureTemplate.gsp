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
        <g:form class="form-horizontal" action="generatePreview">
            <g:hiddenField name="experimentId" value="${experiment.id}"/>

            <g:if test="${assayItems.size() > 0}">
                <h4>Select the context items you wish to include</h4>

                <g:each in="${assayItems}" var="item" status="index">
                    <div class="control-group">
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" name="contextItemIds[${index}]" value="${item.id}"> ${item.attributeElement.label}
                            </label>
                        </div>
                    </div>
                </g:each>
            </g:if>

            <g:if test="${experiment.assay.measures.size() > 0}">
                <h4>Select the measures to include</h4>

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

            <g:if test="${measureItems.size() > 0}">
                <h4>Select the context items that are associated with measures to include</h4>

                <g:each in="${measureItems}" var="item" status="index">
                    <div class="control-group">
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" name="measureItemIds[${index}]" value="${item.id}"> ${item.attributeElement.label}
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
