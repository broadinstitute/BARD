<%@ page import="bard.db.people.Role" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Register a New External BARD User in Crowd</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span12">
                <div class="span2"></div>

                <div class="span8">
                    <h3>Register a New External BARD User in Crowd</h3>
                    <g:form action='register' name='registerForm'>
                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'username', 'error')} required">
                            <label class="control-label" for="username"><g:message code="register.username.label"
                                                                                   default="User Name"/>:</label>

                            <div class="controls">
                                <g:textField name="username" class="input-large" maxlength="250" required=""
                                             value="${registerCommand.username}"
                                             placeholder="Enter user Name"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="username">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="username">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'firstName', 'error')} required">
                            <label class="control-label" for="firstName"><g:message code="register.firstname.label"
                                                                                    default="First Name"/>:</label>

                            <div class="controls">
                                <g:textField name="firstName" class="input-large" maxlength="250" required=""
                                             value="${registerCommand.firstName}"
                                             placeholder="Enter First Name"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="firstName">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="firstName">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'lastName', 'error')} required">
                            <label class="control-label" for="lastName"><g:message code="register.lastname.label"
                                                                                   default="Last Name"/>:</label>

                            <div class="controls">
                                <g:textField name="lastName" class="input-large" maxlength="250" required=""
                                             value="${registerCommand.lastName}"
                                             placeholder="Enter Last Name"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="lastName">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="lastName">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'displayName', 'error')} required">
                            <label class="control-label" for="displayName"><g:message code="register.displayname.label"
                                                                                      default="Display Name"/>:</label>

                            <div class="controls">
                                <g:textField name="displayName" class="input-xxlarge" maxlength="250" required=""
                                             value="${registerCommand.displayName}" placeholder="Enter Display Name"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="displayName">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="displayName">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'email', 'error')} required">
                            <label class="control-label" for="email"><g:message code="register.email.label"
                                                                                default="Email Address"/>:</label>

                            <div class="controls">
                                <input type="email" id="email" name="email" class="input-xxlarge" maxlength="250"
                                       required=""
                                       value="${registerCommand.email}"
                                       placeholder="Enter Email Address"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="email">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="email">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'password', 'error')} required">
                            <label class="control-label" for="password"><g:message code="register.password.label"
                                                                                   default="Password"/>:</label>

                            <div class="controls">
                                <g:passwordField name="password" class="input-xxlarge" maxlength="250" required=""
                                                 value="${registerCommand.password}"
                                                 placeholder="Enter Password"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="password">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="password">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'password2', 'error')} required">
                            <label class="control-label" for="password2"><g:message code="register.password2.label"
                                                                                    default="Password (again)"/>:</label>

                            <div class="controls">
                                <g:passwordField name="password2" class="input-xxlarge" maxlength="250" required=""
                                                 value="${registerCommand.password2}"
                                                 placeholder="Enter Password Again"/>
                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="password2">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="password2">
                                                <p style="color: red;"><i class="icon-warning-sign"></i>&nbsp;<g:message
                                                        error="${it}"/></p>
                                            </g:eachError>
                                        </div>
                                    </g:hasErrors>
                                </span>
                            </div>
                        </div>

                        <div class="control-group  fieldcontain ${hasErrors(bean: registerCommand, field: 'primaryGroup', 'error')} required">
                            <label class="control-label" for="primaryGroup"><g:message
                                    code="register.primarygroup.label"
                                    default="Primary Group"/>:</label>

                            <div class="controls">
                                <g:select required="" id="primaryGroup" name="primaryGroup.id"
                                          from="${Role.teamRoles}"
                                          optionKey="id"
                                          optionValue="displayName" noSelection="${['': '']}"
                                          value="${registerCommand?.primaryGroup?.id}"/>

                                <span class="help-inline">
                                    <g:hasErrors bean="${registerCommand}" field="primaryGroup">
                                        <div class="alert alert-block alert-error fade in">
                                            <g:eachError bean="${registerCommand}" field="primaryGroup">
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
                                <g:link class="btn" action="listUsersAndGroups">Cancel</g:link>
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
            $('#username').focus();
        });
    </script>
</sec:ifAnyGranted>
</body>
</html>
