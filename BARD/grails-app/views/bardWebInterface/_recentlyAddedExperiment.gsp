<%@ page import="bard.db.experiment.Experiment" %>
<article class="span4">
    <time datetime="2013-10-16">${experiment.updated}</time>

    <h2>
        <g:link controller="experiment" action="show"
                id="${experiment.capExptId}">${experiment.name}</g:link>
    </h2>

    <p>${Experiment.get(experiment.capExptId)?.ownerRole?.displayName}</p>
</article>
