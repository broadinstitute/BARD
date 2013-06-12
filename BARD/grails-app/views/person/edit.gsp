<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="basic"/>
    <title>People</title>
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

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <g:form action="update" class="form-horizontal" id="${person.id}">
                <div class="control-group">
                    <label class="control-label" for="userName">Username</label>
                    <div class="controls">
                        <g:textField id="userName" name="userName" value="${person.userName}"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="fullName">Full name</label>
                    <div class="controls">
                        <g:textField id="fullName" name="fullName" value="${person.fullName}"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="emailAddress">Email</label>
                    <div class="controls">
                        <g:textField id="emailAddress" name="emailAddress" value="${person.emailAddress}"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="primaryGroup">Primary group</label>
                    <div class="controls">
                        <g:select id="primaryGroup" name="primaryGroup" from="${roles}" optionValue="displayName" noSelection="${['':'']}"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="roles">Primary group</label>
                    <div class="controls">
                        <g:select id="roles" name="roles" from="${roles}" optionValue="displayName" multiple="${true}"/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary">
                        <g:link class="btn" action="list">Cancel</g:link>
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>

</body>
</html>