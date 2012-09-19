<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">	
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'table.css')}" type="text/css">
		<g:layoutHead/>
        <r:layoutResources />
	</head>
	<body>
		<div class="navbar navbar-inverse navbar-fixed-top">
	      <div class="navbar-inner">
	        <div class="container">
	          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
	            <span class="icon-bar"></span>
	            <span class="icon-bar"></span>
	            <span class="icon-bar"></span>
	          </a>
	          <a class="brand" href="#">CAP</a>
	          <div class="nav-collapse collapse">
	            <ul class="nav">
	              <li class="active"><a href="#">Home</a></li>
	              <li class="dropdown">
	                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Go To: <b class="caret"></b></a>
	                <ul class="dropdown-menu">
	                  <li><a href="#">Register an assay</a></li>
	                  <li><a href="#">Find assay by ID</a></li>
	                  <li><a href="#">Find assay by Name</a></li>
	                  <li class="divider"></li>
	                  <li class="nav-header">BARD Web Query links</li>
	                  <li><a href="#">Home</a></li>
	                </ul>
	              </li>
	            </ul>
	            <form class="navbar-form pull-right">
	              <input class="span2" type="text" placeholder="Username">
	              <input class="span2" type="password" placeholder="Password">
	              <button type="submit" class="btn btn-primary">Sign in</button>
	            </form>
	          </div><!--/.nav-collapse -->
	        </div>
	      </div>
		<div class="container-fluid">		
			<g:layoutBody/>
		</div>				
		
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
        <r:layoutResources />
	</body>
</html>