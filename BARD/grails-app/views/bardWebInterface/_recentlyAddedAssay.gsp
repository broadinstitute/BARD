<article class="span4">
    <time datetime="2013-10-16">${assay.updated}</time>

    <h2>
        <g:link controller="assayDefinition" action="show"
                id="${assay.capAssayId}">${assay.name}</g:link>
    </h2>

    <p>${assay.designedBy}</p>
</article>
