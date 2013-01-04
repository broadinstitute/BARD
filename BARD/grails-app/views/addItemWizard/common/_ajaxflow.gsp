<%
/**
 * main ajax flow template
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<div id="ajaxflow">
<af:flow name="AddItemWizard" class="ajaxFlow" commons="common" partials="pages" spinner="ajaxFlowWait" controller="[controller: 'AddItemWizard', action: 'pages']">
	<%	/**
	 	 * The initial rendering of this template will result
	 	 * in automatically triggering the 'next' event in
	 	 * the webflow. This is required to render the initial
	 	 * page / partial and done by using af:triggerEvent
		 */ %>
	<af:triggerEvent name="next" afterSuccess="onPage();"/>
</af:flow>
%{--
<g:if env="development">
<af:error class="ajaxFlowError">
	[ajax errors go in here, normally it's safe to delete the af:error part]
</af:error>
</g:if>
--}%
</div>
<input type="hidden" id="sectionPath" value="${ path }"/>
<input type="hidden" id="cardAssayContextId" value="${ assayContextId }"/>
<g:render template="common/on_page"/>
<g:render template="common/please_wait"/>
