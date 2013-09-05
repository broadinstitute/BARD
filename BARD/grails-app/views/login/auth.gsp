<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,bootstrapplus,card"/>
    <meta name="layout" content="basic"/>
    <title>CAP-Login</title>
    <r:script>

    </r:script>
</head>

<body>

	<div class="row-fluid">
	    <div class="span12">
	    	<div class="hero-unit-v1" style="text-align:center;">
	        	<h3>Welcome to CAP - Catalog of Assay Protocols</h3>
	        </div>
	    </div>
	</div>

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
		<div class="span4 offset4">
			<div class="bs-docs" style="padding: 20px 20px 20px; width: 400px;">
				<form class="form-horizontal" action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
					<legend>Login:</legend>
					<div class="control-group">
		    			<label class="control-label" for="username"><g:message code="springSecurity.login.username.label"/>:</label>
		    			<div class="controls">
		      				<input type="text" name='j_username' id='username' placeholder="Username" autofocus="true">
		    			</div>
		  			</div>

		  			<div class="control-group">
		    			<label class="control-label" for="password"><g:message code="springSecurity.login.password.label"/>:</label>
		    			<div class="controls">
		      				<input type="password" name='j_password' id='password' placeholder="Password">
		    			</div>
		  			</div>

		  			<div class="control-group">
		    			<div class="controls">
		    				<label class="checkbox">
        						<input type="checkbox" name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>> Remember me
     	 					</label>
                            <span class="forgot-link">
                                <g:link controller='register' action='forgotPassword'><g:message code='register.login.forgotPassword'/></g:link>
                            </span>
                            <td colspan='2'>
                                <g:link elementId='register' controller='register' action="signup"> <g:message code='register.login.signup'/> </g:link>
                            </td>
		      				<button id="submit" type="submit" class="btn btn-primary">Sign in</button>
		    			</div>
	  				</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
