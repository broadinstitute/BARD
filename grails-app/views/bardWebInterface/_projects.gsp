<div class="row-fluid">
    <g:if test="${params.paging == 'NotPaging'}">
        <g:render template="facets" model="['metaData': metaData]"/>
    </g:if>
<div class="span9">
    <ul class="unstyled results">
        <g:each var="project" in="${docs}">
            <li>
                <g:link action="showProject" id="${project.proj_id}" target="_blank">Project ID: ${project.proj_id} - ${project.name}</g:link>
                <a href="/bardwebquery/sarCart/add/${project.proj_id}"
                   onclick="jQuery.ajax({  type:'POST',
                       data:{'id': '${project.proj_id}','class': 'class bardqueryapi.CartProject','projectName':'${project.name}','version': '0'},
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
    <div id="listProjectsPage">
        <div class="pagination">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="listProjectsPage" controller="bardWebInterface"
                                 action="searchProjects" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</div>
</div>