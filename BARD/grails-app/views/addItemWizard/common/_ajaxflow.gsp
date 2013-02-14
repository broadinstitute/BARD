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
        <input type="hidden" name="assayContextId" value="${ assayContextId }"/>
        <af:triggerEvent name="next" afterSuccess="onPage();"/>
    </af:flow>
</div>

<input type="hidden" id="sectionPath" value="${ path }"/>
<input type="hidden" id="cardAssayContextId" value="${ assayContextId }"/>
<input type="hidden" id="cardAssayId" value="${ assayId }"/>
<input type="hidden" id="attributeElementId"/>

<g:render template="common/on_page"/>
<g:render template="common/please_wait"/>
