<%
/**
 * Navigation template
 *
 * @author	Jeroen Wesbeek <work@osx.eu>
 * @package AjaxFlow
*class="prevnext"
 */
%>
<g:set var="showPrevious" value="${page>1 && page<pages.size}"/>
<g:set var="showNext" value="${page<pages.size && page != 4}"/>
<g:set var="showSave" value="${page == 4}"/>
<af:navigation 
	events="[previous:[label:'&laquo; prev',show: showPrevious], next:[label:'next &raquo;', show:showNext], save:[label:'Save &raquo;', show:showSave]]" 
	separator="&nbsp; | &nbsp;" 
	class="btn" 
/>
