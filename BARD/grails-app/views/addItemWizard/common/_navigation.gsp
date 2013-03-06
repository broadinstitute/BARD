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
<g:set var="showAddValueToList" value="${page == 3 && 'List' == valueType?.valueTypeOption}"/>
<g:set var="showCancel" value="${page != 5}"/>
<g:set var="showClose" value="${page == 5}"/>
<g:set var="showAddAnotherItem" value="${page == 5}"/>
<af:navigation 
	events="[previous:[label:'&laquo; prev',show: showPrevious], next:[label:'next &raquo;', show:showNext], save:[label:'Save &raquo;', show:showSave], addValueToList:[label:'Add value to list &raquo;', show:showAddValueToList], cancel:[label:'Cancel &raquo;', show:showCancel], close:[label:'Close &raquo;', show:showClose], addAnotherItem:[label:'Add another item &raquo;', show:showAddAnotherItem]]" 
	separator="&nbsp; | &nbsp;" 
	class="btn"
	afterSuccess="afterSuccess();"
/>
