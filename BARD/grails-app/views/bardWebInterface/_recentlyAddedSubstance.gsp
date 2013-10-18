<article class="span4">
    <time datetime="2013-10-16">${substance.updated}</time>

    <h2>
        <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${substance.sid}">
            <img src="${resource(dir: 'images', file: 'pubchem.png')}" alt="PubChem"/>
            ${substance.sid}</a>
    </h2>

    <p><img src="http://bard.nih.gov/api/v17.3/substances/${substance.cid}/image?s=200" alt="Structure"/> </p>

</article>
