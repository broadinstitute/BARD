<div class="row-fluid bard-footer">
    <footer id="footer">
        <div class="footer-columns">
            <div class="container-fluid">
                <div class="row-fluid">


                    <div class="span5 bard-footer-versioninfo muted">
                    <div>
                    <b>Version:</b> ${grailsApplication.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
                    </div>
                    </div>

                    <div class="span5">
                        </div>


                    <div class="span2 right-aligned">
                    <a href="http://www.chemaxon.com/" target="chemAxon"><img src="${resource(dir: 'images/bardHomepage', file: 'logo-by.png')}"
                    alt="Powered by ChemAxon"/></a>
                    </div>
                </div>
            </div>
        </div>

        %{--The bottom line of the whole page--}%
        <div class="footer-info">
            <div class="container-fluid">
                <ul>
                    <li><a href="#">National Institutes of Health</a></li>
                    <li><a href="#">U.S. Department of Health and Human Services</a></li>
                    <li><a href="#">USA.gov – Government Made Easy</a></li>
                </ul>
            </div>
        </div>
    </footer>
</div>
