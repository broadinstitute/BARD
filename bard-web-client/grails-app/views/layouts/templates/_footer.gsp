<div class="row-fluid bard-footer">
    <div class="span5">
        <div>
            <ul class="horizontal-block-list">
                <li><a href="http://bard.nih.gov">About</a></li>
                <li><a href="http://bard.nih.gov">Help</a></li>
                <li><a href="http://bard.nih.gov">Technology</a></li>
            </ul>
        </div>
    </div>

    <div class="span5 bard-footer-versioninfo muted">
        <div>
            <b>Version:</b> ${grailsApplication.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
        </div>
    </div>

    <div class="span2 right-aligned">
        <a href="http://www.chemaxon.com/"><img src="${resource(dir: 'images', file: 'chemaxon_logo.gif')}"
                                                alt="Powered by ChemAxon"/></a>
    </div>
</div>
