<div style="padding-left: 5px">
    <g:each var="project" in="${docs}">
        <g:link action="showProject" id="${project.proj_id}">${project.name}</g:link><br/>
        ${project.highlight}<br/>
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