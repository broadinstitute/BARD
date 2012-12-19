<%
/**
 * wizard refresh flow action
 *
 * An example of a way to trigger a refresh in your ajax flow
 * to refresh the rendered page. It will trigger the 'refresh'
 * event in your webflow definition.
 *
 * I use this together with a select element, where the last
 * option is 'add more'. When you select the last element a
 * jquery-ui dialog opens where you will be able to add more
 * elements to the select element. However, when the dialog
 * is cloded, the select needs to be updated with the newly
 * added options. So I call the refreshFlow(); JavaScript
 * function to refresh the page and the select element will
 * show the newly added options.
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<script type="text/javascript">
function refreshFlow() {
	<af:ajaxSubmitJs name="refresh" afterSuccess="onPage()" />
}
</script>
