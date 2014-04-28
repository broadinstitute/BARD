%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<style type="text/css">
@media (min-width: 768px) {
    /* start of modification for 5 columns.  Must follow after bootstrap definitions */
    .fivecolumns .span2 {
        width: 18.2%;
        *width: 18.2%;
    }
}

@media (min-width: 1200px) {
    .fivecolumns .span2 {
        width: 17.9%;
        *width: 17.8%;
    }
}

@media (min-width: 768px) and (max-width: 979px) {
    .fivecolumns .span2 {
        width: 17.7%;
        *width: 17.7%;
    }
}
</style>

<footer id="footer">
    <div class="footer-columns">
        <div class="container-fluid">
            %{--<div class="row-fluid">--}%
            %{--Note:  remove 'fivecolumns' class and go to span3's to move down to four columns--}%
            <div class="row-fluid fivecolumns">
                <div class="span2">
                    <h3>About</h3>
                    <ul>
                        <li><g:link controller="about" action="bardHistory">History</g:link></li>
                        <li><g:link controller="about" action="bardDevelopmentTeam">Development Team</g:link></li>
                    </ul>
                </div>

                <div class="span2">
                    <h3>Help</h3>
                    <ul>
                        <li><a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">Forums</a>
                        </li>
                        <li><a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">Submit a Bug Report</a>
                        </li>
                        <li><a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">Ask a Question</a>
                        </li>
                    </ul>
                </div>

                <div class="span2">
                    <h3>Technology</h3>
                    <ul>
                        <li><g:link controller="about" action="bardArchitecture">Architecture &amp; Design</g:link></li>
                        <li><a href="https://github.com/ncatsdpiprobedev/bard/wiki">REST API</a></li>
                        <li><a href="#" style="text-decoration: line-through;">Source code on GitHub<img
                                src="${resource(dir: 'images/bardHomepage', file: 'comingSoon2.png')}"
                                alt="coming soon"></a></li>
                    </ul>
                </div>

                <div class="span2">
                    <h3>RDM</h3>
                    <ul>
                        <li><g:link controller="about"
                                    action="bardOrganizingPrinciples">Organizing principles</g:link></li>
                        <li><g:link controller="element"
                                    action="showTopLevelHierarchyHelp">Top-level concepts</g:link></li>
                        <li><g:link controller="dictionaryTerms" action="dictionaryTerms">Glossary</g:link></li>
                    </ul>
                </div>


                <div class="span2 logoSection">
                    <div>
                        <a href="http://www.chemaxon.com/" title="Powered by ChemAxon" target="_blank">
                            <img src="${resource(dir: 'images/bardHomepage', file: 'logo-by.png')}" alt="Powered by ChemAxon"/></a>
                    </div>
                    <div>
                        <a href="http://www.scilligence.com/web/" title="Structure Editor by Scilligence" target="_blank">
                            <img src="${resource(dir: 'images/bardHomepage', file: 'scill.jpg')}" alt="Structure Editor by Scilligence"/></a>
                    </div>
                    <div>&copy; 2013 BARD</div>
                </div>
            </div>
            <div class="row-fluid">
                <div class="span8 offset2 bard-footer-versioninfo muted">
                    <b>By using BARD, you agree to our <g:link controller="about" action="termsOfUse">terms of use</g:link>  and <g:link controller="about" action="privacyPolicy">privacy policy</g:link></b></br>
                    <b>Release created:</b> ${grailsApplication.metadata['war.created']} <b>Branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>Revision:</b> ${grailsApplication?.metadata['git.branch.version']}
                </div>
            </div>
        </div>
    </div>

    %{--The bottom line of the whole page--}%
    <div class="footer-info">
        <div class="container-fluid">
            <ul>
                <li><a href="http://www.nih.gov/">National Institutes of Health</a></li>
                <li><a href="http://www.hhs.gov/">U.S. Department of Health and Human Services</a></li>
                <li><a href="http://www.usa.gov/">USA.gov – Government Made Easy</a></li>
            </ul>
        </div>
    </div>
</footer>
