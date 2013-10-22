<article class="span4">
    <h2>
        <g:link controller="bardWebInterface" action="showCompound"
                id="${compound.id}">${compound.name}</g:link>
    </h2>

    <p>CID: ${compound.id}</p>
    <p><img src="${grailsApplication.config.ncgc.server.root.url}/compounds/${compound.id}/image?s=200" alt="Structure"/> </p>
</article>
