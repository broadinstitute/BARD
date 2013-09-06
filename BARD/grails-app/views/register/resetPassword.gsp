<html>

<head>
    <title><g:message code='register.resetPassword.title'/></title>
    <r:require
            modules="core,bootstrap"/>
    <meta name='layout' content='basic'/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="span2"></div>

            <div class="span8">
                <h4><g:message code='register.resetPassword.description'/></h4>
               <g:if test="${errorMessage}">
                   <div class="alert alert-block alert-error">
                       ${errorMessage}
                   </div>
               </g:if>
                <g:if test="${warningMessage}">
                    <div class="alert alert-info">
                        ${warningMessage}
                    </div>
                </g:if>
                <g:form action='resetPassword' name='resetPasswordForm' autocomplete='off'>
                    <g:hiddenField name='t' value='${token}'/>


                    <div class="control-group  fieldcontain ${hasErrors(bean: resetPasswordCommand, field: 'password', 'error')} required">
                        <label class="control-label" for="password"><g:message code="register.password.label"
                                                                               default="Password"/>:</label>

                        <div class="controls">
                            <g:passwordField id="password" name="password" class="input-xxlarge" maxlength="250"
                                             required=""
                                             value="${resetPasswordCommand?.password}"
                                             placeholder="Enter New Password"/>
                            <span class="help-inline">
                                <g:hasErrors bean="${resetPasswordCommand}" field="password">
                                    <div class="alert alert-block alert-error fade in">
                                        <g:eachError bean="${resetPasswordCommand}" field="password">
                                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                    error="${it}"/></p>
                                        </g:eachError>
                                    </div>
                                </g:hasErrors>
                            </span>
                        </div>
                    </div>

                    <div class="control-group  fieldcontain ${hasErrors(bean: resetPasswordCommand, field: 'password2', 'error')} required">
                        <label class="control-label" for="password2"><g:message code="register.password2.label"
                                                                                default="Password (again)"/>:</label>

                        <div class="controls">
                            <g:passwordField name="password2" class="input-xxlarge" maxlength="250" required=""
                                             value="${resetPasswordCommand.password2}"
                                             placeholder="Enter Password Again"/>
                            <span class="help-inline">
                                <g:hasErrors bean="${resetPasswordCommand}" field="password2">
                                    <div class="alert alert-block alert-error fade in">
                                        <g:eachError bean="${resetPasswordCommand}" field="password2">
                                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                    error="${it}"/></p>
                                        </g:eachError>
                                    </div>
                                </g:hasErrors>
                            </span>
                        </div>
                    </div>

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



<script>
    $(document).ready(function () {
        $('#password').focus();
    });
</script>

</body>
</html>
