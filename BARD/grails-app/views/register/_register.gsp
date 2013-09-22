
    <div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'username', 'error')} required">
        <label class="control-label" for="username"><g:message code="register.username.label"
                                                               default="User Name"/>:</label>

        <div class="controls">
            <g:textField name="username" class="input-large" maxlength="250" required=""
                         value="${command?.username}"
                         placeholder="Enter user Name"/>
            <span class="help-inline">
                <g:hasErrors bean="${command}" field="username">
                    <div class="alert alert-block alert-error fade in">
                        <g:eachError bean="${command}" field="username">
                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                    error="${it}"/></p>
                        </g:eachError>
                    </div>
                </g:hasErrors>
            </span>
        </div>
    </div>

    <div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'firstName', 'error')} required">
        <label class="control-label" for="firstName"><g:message code="register.firstname.label"
                                                                default="First Name"/>:</label>

        <div class="controls">
            <g:textField name="firstName" class="input-large" maxlength="250" required=""
                         value="${command?.firstName}"
                         placeholder="Enter First Name"/>
            <span class="help-inline">
                <g:hasErrors bean="${command}" field="firstName">
                    <div class="alert alert-block alert-error fade in">
                        <g:eachError bean="${command}" field="firstName">
                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                    error="${it}"/></p>
                        </g:eachError>
                    </div>
                </g:hasErrors>
            </span>
        </div>
    </div>

    <div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'lastName', 'error')} required">
        <label class="control-label" for="lastName"><g:message code="register.lastname.label"
                                                               default="Last Name"/>:</label>

        <div class="controls">
            <g:textField name="lastName" class="input-large" maxlength="250" required=""
                         value="${command?.lastName}"
                         placeholder="Enter Last Name"/>
            <span class="help-inline">
                <g:hasErrors bean="${command}" field="lastName">
                    <div class="alert alert-block alert-error fade in">
                        <g:eachError bean="${command}" field="lastName">
                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                    error="${it}"/></p>
                        </g:eachError>
                    </div>
                </g:hasErrors>
            </span>
        </div>
    </div>

    <div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'email', 'error')} required">
        <label class="control-label" for="email"><g:message code="register.email.label"
                                                            default="Email Address"/>:</label>

        <div class="controls">
            <input type="email" id="email" name="email" class="input-xxlarge" maxlength="250"
                   required=""
                   value="${command?.email}"
                   placeholder="Enter Email Address"/>
            <span class="help-inline">
                <g:hasErrors bean="${command}" field="email">
                    <div class="alert alert-block alert-error fade in">
                        <g:eachError bean="${command}" field="email">
                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                    error="${it}"/></p>
                        </g:eachError>
                    </div>
                </g:hasErrors>
            </span>
        </div>
    </div>

    <div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'password', 'error')} required">
        <label class="control-label" for="password"><g:message code="register.password.label"
                                                               default="Password"/>:</label>

        <div class="controls">
            <g:passwordField name="password" class="input-xxlarge" maxlength="250" required=""
                             value="${command?.password}"
                             placeholder="Enter Password"/>
            <span class="help-inline">
                <g:hasErrors bean="${command}" field="password">
                    <div class="alert alert-block alert-error fade in">
                        <g:eachError bean="${command}" field="password">
                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                    error="${it}"/></p>
                        </g:eachError>
                    </div>
                </g:hasErrors>
            </span>
        </div>
    </div>

    <div class="control-group  fieldcontain ${hasErrors(bean: command, field: 'password2', 'error')} required">
        <label class="control-label" for="password2"><g:message code="register.password2.label"
                                                                default="Password (again)"/>:</label>

        <div class="controls">
            <g:passwordField name="password2" class="input-xxlarge" maxlength="250" required=""
                             value="${command?.password2}"
                             placeholder="Enter Password Again"/>
            <span class="help-inline">
                <g:hasErrors bean="${command}" field="password2">
                    <div class="alert alert-block alert-error fade in">
                        <g:eachError bean="${command}" field="password2">
                            <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                    error="${it}"/></p>
                        </g:eachError>
                    </div>
                </g:hasErrors>
            </span>
        </div>
    </div>

