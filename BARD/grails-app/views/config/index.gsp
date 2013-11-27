<%@ page import="bard.db.command.BardCommand" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Config</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">

            <g:form class="form-horizontal" action="update">
                <div class="control-group">
                    <label class="control-label" for="ncgcUrl">BARD API URL</label>
                    <div class="controls">
                        <g:textField name="ncgcUrl" id="ncgcUrl" value="${config.ncgcUrl}" class="input-xxlarge"></g:textField>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="promiscuityUrl">BadApple URL</label>
                    <div class="controls">
                        <g:textField name="promiscuityUrl" id="promiscuityUrl" value="${config.promiscuityUrl}" class="input-xxlarge"></g:textField>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <g:link class="btn" controller="bardWebInterface" action="navigationPage">Cancel</g:link>
                        <input type="submit" class="btn" value="Update">
                    </div>
                </div>
            </g:form>

        </div>
    </div>
</div>
</body>
</html>