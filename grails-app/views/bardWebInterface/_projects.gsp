<%@ page import="bardqueryapi.JavaScriptUtility" %>
<div class="row-fluid">
        <g:render template="facets" model="['metaData': metaData]"/>
<div class="span9">
    <ul class="unstyled results">
        <g:each var="project" in="${docs}">
            <li>
                <g:link action="showProject" id="${project.proj_id}" target="_blank">Project ID: ${project.proj_id} - ${project.name}</g:link>
                <a href="/bardwebquery/sarCart/add/${project.proj_id}"
                   onclick="jQuery.ajax({  type:'POST',
                       data:{'id': '${JavaScriptUtility.cleanup(project.proj_id)}','class': 'class bardqueryapi.CartProject','projectName':'${JavaScriptUtility.cleanup(project.name)}','version': '0'},
                       url:'/bardwebquery/sarCart/add',
                       success:function(data,textStatus){
                           jQuery('#sarCartRefill').html(data);
                       }
                   });
                   return false;"
                   action="add"
                   controller="sarCart"><div class="cntrcart"><nobr><i class="icon-shopping-cart"></i> Add to Cart</nobr></div></a>
                <g:if test="${project.highlight}">
                    <ul>
                        <li>${project.highlight}</li>
                    </ul>
                </g:if>
            </li>
        </g:each>
    </ul>
    <g:hiddenField name="totalProjects" id="totalProjects" value="${metaData?.nhit}"/>
         <div class="pagination">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="projects" controller="bardWebInterface"
                                 action="searchProjects" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
</div>
</div>