<div style="padding-left: 5px">
    <g:each var="project" in="${docs}">
        <g:link action="showProject" id="${project.proj_id}" target="_blank">${project.name}</g:link><br/>
        ${project.highlight}<br/>
        <a href="/bardwebquery/sarCart/add/${project.proj_id}"
           onclick="jQuery.ajax({  type:'POST',
               data:{'id': '${project.proj_id}','class': 'class bardqueryapi.CartProject','projectName':'${project.name}','version': '0'},
               url:'/bardwebquery/sarCart/add',
               success:function(data,textStatus){
                   jQuery('#sarCartRefill').html(data);
               },
               error:function(XMLHttpRequest,textStatus,errorThrown){
                   alert('problem adding project')
               }
           });
           return false;"
           action="add"
           controller="sarCart"><div class="cntrcart"><nobr>Add to Cart</nobr><br/><i class="icon-shopping-cart"></i></div></a>
        <br/>

        <br/>
    </g:each>
    <g:hiddenField name="totalProjects" id="totalProjects" value="${metaData?.nhit}"/>
    <div id="listProjectsPage">
        <div class="pagination">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="listProjectsPage" controller="bardWebInterface"
                                 action="searchProjects" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</div>