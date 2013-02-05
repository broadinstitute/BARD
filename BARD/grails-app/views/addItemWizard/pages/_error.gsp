<%
/**
 * error page
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
 */
%>
<af:page>
<h1>Oops!</h1>

<div class="alert alert-error">
 	<button type="button" class="close" data-dismiss="alert">×</button>
  	<strong>Error:</strong> We encountered an problem storing your data!
</div>

<p>
	 You can either
	<af:ajaxButton name="toPageFive" value="try again" afterSuccess="onPage();" class="prevnext" />
	or file a bugreport.
</p>

</af:page>
