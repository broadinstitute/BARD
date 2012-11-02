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
 -->
<input class="input-xlarge" type="text" id="attributeTextField" name='attributeText' placeholder="Search for Attribute">
<span class="help-inline">Search for an item and then choose from auto-suggest to make it your current choice</span>
<label class="control-label" >Current choice:</label>
<span class="input-large uneditable-input">Current Value</span>

<!-- 
<p>
	<a>Click an item in the tree to make it the current choice.</a>
</p>
 -->
</af:page>
