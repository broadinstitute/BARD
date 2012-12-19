<%
/**
 * page header template
 *
 * This template is actually rendered by the AjaxflowTagLib using
 * the following tags:
 *
 * <af:pageHeader>
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<h1><strong>Step ${page}:</strong> ${pages[page - 1].description}</h1>
<g:render template="common/tabs"/>
<div class="content">
