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
        <h4>Download Template</h4>

        <g:form action="generatePreview">
            <g:hiddenField name="experimentId" value="${experiment.id}"/>
            <g:if test="${items.size() > 0}">
                <p>For each of the following annotations, check the ones for which you will load only a single value for the entire experiment</p>

                <g:each in="${experimentItems}" var="item">
                    <div class="control-group">
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" disabled checked> ${item.displayLabel}
                            </label>
                        </div>
                    </div>
                </g:each>

                <g:each in="${items}" var="item" status="index">
                    <div class="control-group">
                        <div class="controls">
                            <label class="checkbox">
                                <input type="checkbox" name="contextItemIds[${index}]"
                                       value="${item.id}"> ${item.displayLabel}
                            </label>
                        </div>
                    </div>
                </g:each>
            </g:if>

            <div class="control-group">
                <div class="controls">
                    <button type="submit" class="btn">Download template</button>
                </div>
            </div>
        </g:form>
    </div>
</g:if>

</body>
</html>
