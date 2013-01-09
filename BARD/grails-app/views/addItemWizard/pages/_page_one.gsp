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

<!-- 
<div class="input-prepend">
	<span class="add-on"><i class="icon-search"></i></span>
	<input class="input-xlarge" type="text" id="attributeTextField" name='attributeText' placeholder="Search for Attribute">
</div>
<input class="input-xlarge" type="text" id="attributeTextField" name='attributeText' placeholder="Search for Attribute">
 -->

<g:textField class="input-xlarge" id="attributeTextField" name='attributeText' placeholder="Search for Attribute" />
<input type="hidden" id="elementId" name="elementId" value="${ attribute?.elementId }"/>
<input type="hidden" id="path" name="path" value="${ attribute?.path }"/>
<input type="hidden" id="assayContextIdValue" name="assayContextIdValue" value="${ attribute?.assayContextIdValue }"/>
<span class="help-inline">Search for an item and then choose from auto-suggest to make it your current choice</span>

<label class="control-label" >Current choice:</label>
<input class="input-xlarge" type="text" id="currentValue" name='currentValue' value="${ attribute?.currentValue }">
<!-- 
<label id="currentValueDisplay" >Current choice ID:</label>
<span id="currentValueDisplay" name="currentValueDisplay" class="input-large uneditable-input"></span> 
-->


</af:page>
