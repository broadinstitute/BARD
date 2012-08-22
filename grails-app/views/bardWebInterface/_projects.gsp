<div style="padding-left: 5px">
    <g:each var="project" in="${docs}">
        <g:link action="showProject" id="${project.proj_id}">${project.name}</g:link><br/>
        ${project.highlight}<br/>
        <br/>
        <br/>
    </g:each>
    <g:hiddenField name="totalProjects" id="totalProjects" value="${metaData?.nhit}"/>
    <g:render template="paginate"/>
</div>