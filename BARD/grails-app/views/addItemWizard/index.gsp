<%
/**
 * Wizard index page
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<html>
<head>
	<r:require modules="core, bootstrap"/>
	<meta name="layout" content="main"/>
	<g:javascript library="jquery" plugin="jquery"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">	
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'AddItemWizard.css')}"/>
	
</head>
<body>
	<g:render template="common/ajaxflow"/>
</body>
</html>
