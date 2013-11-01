<%@ page import="bard.db.project.Project" %>
<article class="span4">
    <g:formatDate date="${Project.get(project.capProjectId).lastUpdated}" format="yyyy-MM-dd"/>

    <h2>
        <g:link controller="project" action="show"
                id="${project.capProjectId}">${project.name}</g:link>
    </h2>
    <p>${Project.get(project.capProjectId).ownerRole?.displayName}</p>
</article>
