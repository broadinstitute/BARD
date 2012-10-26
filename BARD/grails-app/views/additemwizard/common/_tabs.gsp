<%
/**
 * wizard tabs
 *
 * The 'pages' and 'page' variables are defined
 * in the flow scope. See WizardController:pagesFlow:onStart
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<af:tabs pages="${pages}" page="${page}" clickable="${true}" />
