<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'table.css')}" type="text/css">
		<g:layoutHead/>
        <r:layoutResources />
	</head>
	<body>
		<div class="container-fluid">
		<div class="row-fluid">
	    <div class="span12">
		<div class="navbar navbar-inverse navbar-fixed-top">
	      <div class="navbar-inner">
	        <div class="container">

	          <a class="brand" style="margin-left: -10px;" href="#">
	          	<img width="140" height="43" src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database" />
	          </a>

	          <a class="brand" href="/BARD">CAP</a>
	          <div class="nav-collapse collapse">
	            <ul class="nav">
	              <!-- <li class="active"><a href="#">Home</a></li> -->
	              <li class="dropdown">
	                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Go To: <b class="caret"></b></a>
	                <ul class="dropdown-menu">
	                  <li><a href="/BARD/assayDefinition/description">Register an assay</a></li>
	                  <li><a href="/BARD/assayDefinition/findById">Find assay by ID</a></li>
	                  <li><a href="/BARD/assayDefinition/findByName">Find assay by Name</a></li>
	                  <li class="divider"></li>
	                  <li class="nav-header">BARD Web Query links</li>
	                  <li><g:link url="${grailsApplication.config.bard.home.page}">Home</g:link></li>
	                </ul>
	              </li>
	            </ul>
	            <sec:ifLoggedIn>
					<g:form class="navbar-form pull-right" name="logoutForm" controller="logout">
						Welcome Back! Logged in as: <span style="color: white; font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;
						<button type="submit" class="btn btn-primary">Logout</button>
					</g:form>
					<!--
					<div class="navbar-form pull-right">
						<p style="text-align:center">Welcome Back! Logged in as: <span style="color: white; font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;</p>
					</div>
					-->
				</sec:ifLoggedIn>
				<sec:ifNotLoggedIn>
					<g:form class="navbar-form pull-right" name="loginForm" controller="login">
						Not logged in&nbsp;&nbsp;
						<button type="submit" class="btn btn-primary">Login</button>
					</g:form>
					<!--
					<div class="navbar-form pull-right">
						<p style="text-align:center">Not logged in&nbsp;&nbsp;</p>
					</div>
					-->
				</sec:ifNotLoggedIn>

	            <!--
	            <form class="navbar-form pull-right">
	              <input class="span2" type="text" placeholder="Username">
	              <input class="span2" type="password" placeholder="Password">
	              <button type="submit" class="btn btn-primary">Sign in</button>
	            </form>
	            -->

	          </div><!--/.nav-collapse -->
	        </div>
	      </div>
        </div>
		</div>
		</div>

		<div class="row-fluid">
		<div class="span12"><br><br>
			<g:layoutBody/>
		</div>
		</div>
		</div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
        <r:layoutResources />
	</body>
</html>