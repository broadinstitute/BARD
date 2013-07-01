<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>External Users and Groups In CROWD</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
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

    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span12">

                <h3>External BARD users in Crowd</h3>
                <table>
                    <tr><th>User Name</th></tr>
                    <g:each in="${groupMembership.users}" var="currentUser">
                        <tr>
                            <td>${currentUser.name}</td>
                        </tr>
                    </g:each>
                </table>
            </div>
        </div>
    </div>

    </body>
</sec:ifAnyGranted>
</html>

