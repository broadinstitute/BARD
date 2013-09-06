<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Sign up For a BARD Account</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="span2"></div>

            <div class="span8">
                <h3>Sign up For a BARD account</h3>
                <g:if test="${errorMessage}">
                    <div class="alert alert-block alert-error">
                        ${errorMessage}
                    </div>
                </g:if>
                <g:form action='signup' name='registerForm'>
                    <g:render template="register"/>

                    <div class="control-group">
                        <div class="controls">
                            <g:link class="btn" action="index">Cancel</g:link>
                            <input type="submit" class="btn btn-primary">
                        </div>
                    </div>
                </g:form>
            </div>

            <div class="span2"></div>
        </div>
    </div>
</div>

</body>
</html>
