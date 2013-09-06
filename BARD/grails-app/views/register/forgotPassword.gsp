<html>

<head>
<title><g:message code='register.forgotPassword.title'/></title>
    <r:require
            modules="bootstrap"/>
<meta name='layout' content='basic'/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="span2"></div>

            <div class="span8">
                <g:form action='forgotPassword' name='forgotPasswordForm' autocomplete='off'>
                    <div class="control-group">
                        <g:if test='${emailSent}'>
                            <br/>
                            <g:message code='register.forgotPassword.sent'/>
                        </g:if>
                        <g:else>
                            <g:if test="${errorMessage}">
                                <div class="alert alert-block alert-error">
                                    ${errorMessage}
                                </div>
                            </g:if>
                            <br/>
                            <h4><g:message code='register.forgotPassword.description'/></h4>

                            <div class="control-group">
                                <label class="control-label" for="username"><g:message code="register.forgotPassword.username"
                                                                                       default="User Name"/>:</label>

                                <div class="controls">
                                    <g:textField id="username" name="username" class="input-large" maxlength="250" required=""
                                                 placeholder="Enter user Name"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <div class="controls">
                                    <g:link class="btn" action="index">Cancel</g:link>
                                    <input type="submit" class="btn btn-primary">
                                </div>
                            </div>
                        </g:else>


                    </div>

                </g:form>
            </div>

            <div class="span2"></div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
	$('#username').focus();
});
</script>

</body>
</html>
