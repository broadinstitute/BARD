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

    <div class="row-fluid">
        <h4>Template</h4>

        <table>
            <tbody>
            <g:each in="${rows}" var="row">
                <tr>
                <g:each in="${row}" var="cell">
                    <td>
                        ${cell}
                    </td>
                </g:each>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>