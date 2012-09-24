<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>CAP-Login</title>
    <r:script>
        
    </r:script>
</head>

<body>
	<br/>
	<div class="row-fluid">
	    <div class="span12">
	    	<div class='errors'><g:message code="springSecurity.denied.message" /></div>
	    </div>
	</div>
</body>
