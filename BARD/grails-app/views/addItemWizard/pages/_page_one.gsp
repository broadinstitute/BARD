<%
/**
 * first wizard page / tab
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>

<af:page>
    <h1>Search for Attribute</h1>

    <input type="hidden" id="elementId" name="elementId" value="${ attribute?.elementId }"/>
    <input type="hidden" id="path" name="path" value="${ attribute?.path }"/>
    <input type="hidden" id="assayContextIdValue" name="assayContextIdValue" value="${ attribute?.assayContextIdValue }"/>
    <span class="help-inline">Search for an item and then choose from auto-suggest to make it your current choice</span>

    <%-- this hidden field is turned into a select box --%>
    <input type="hidden" id="attributeTextField" name="attributeId"/>

</af:page>
