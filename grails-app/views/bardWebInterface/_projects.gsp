<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<div class="row-fluid">
        <g:render template="facets" model="['facets': facets, 'formName' : FacetFormType.ProjectFacetForm]"/>
<div class="span9">
    <ul class="unstyled results">
        <g:each var="projectAdapter" in="${projectAdapters}">
            <li>
                <g:link action="showProject" id="${projectAdapter.project.id}" target="_blank">Project ID: ${projectAdapter.project.id} - ${projectAdapter.project.name}</g:link>
                <a href="/bardwebquery/sarCart/add/${projectAdapter.project.id}"
                   onclick="jQuery.ajax({  type:'POST',
                       data:{'id': '${projectAdapter.project.id}','class': 'class bardqueryapi.CartProject','projectName':'${JavaScriptUtility.cleanup(projectAdapter.project.name)}','version': '0','stt':trackStatus},
                       url:'/bardwebquery/sarCart/add',
                       success:function(data,textStatus){
                           jQuery(ajaxLocation).html(data);
                       }
                   });
                   return false;"
                   action="add"
                   controller="sarCart"><div class="cntrcart"><nobr><i class="icon-shopping-cart"></i> Add to Cart</nobr></div></a>
                <g:if test="${projectAdapter.searchHighlight}">
                    <ul>
                        <li>${projectAdapter.searchHighlight}</li>
                    </ul>
                </g:if>
            </li>
        </g:each>
    </ul>
    <g:hiddenField name="totalProjects" id="totalProjects" value="${nhits}"/>
         <div class="pagination">
            <util:remotePaginate total="${nhits?nhits : 0}" update="projects" controller="bardWebInterface"
                                 action="searchProjects"  pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
</div>
</div>