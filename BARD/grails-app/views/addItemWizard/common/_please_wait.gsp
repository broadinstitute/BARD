<%
/**
 * wizard please wait template
 *
 * This is used by the ajax buttons, also see the 'spinner'
 * argument in the flow definition in _ajaxflow.gsp
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<div id="ajaxFlowWait" class="ajaxFlow" style="display:none;">
	<span class="waitBackground"></span>
	<span class="waiter">
		<span class="wait">
			<span class="title">Please Wait...</span>
			<span class="spinner"></span>
		</span>
	</span>
</div>