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

<section id="documents-header">
    <h3>6. Documents</h3>

    <section id="documents-description-header">
        <h4>6.1 Descriptions</h4>

        <div class="row-fluid">
            <div class="borderlist">
                <g:if test="${assayAdapter?.description}">
                    <g:textBlock>${assayAdapter?.description}</g:textBlock>
                </g:if>
                <g:else>
                    None
                </g:else>
            </div>
        </div>
    </section>

    <section id="documents-protocol-header">
        <h4>6.2 Protocols</h4>

        <div class="row-fluid">
            <g:if test="${assayAdapter?.protocol}">
                <g:textBlock>${assayAdapter?.protocol}</g:textBlock>
            </g:if>
            <g:else>
                None
            </g:else>
        </div>
    </section>

    <section id="documents-comment-header">
        <h4>6.3 Comments</h4>

        <div class="row-fluid">
            <g:if test="${assayAdapter?.comments}">
                <g:textBlock>${assayAdapter?.comments}</g:textBlock>
            </g:if>
            <g:else>
                None
            </g:else>
        </div>
    </section>

    <section id="documents-publication-header">
        <h4>6.4 Publications</h4>

        <ol>
            <g:each in="${assayAdapter?.annotations?.findPublications()}" var="publication">
                <li><a href="${publication.urlValue}" target="_blank">${publication.displayString}</a></li>
            </g:each>
        </ol>
    </section>

    <section id="documents-urls-header">
        <h4>6.5 External URLS</h4>

        <ul>
            <g:each in="${assayAdapter?.annotations?.findExternalUrls()}" var="externalUrl">
                <li><a href="${externalUrl.urlValue}" target="_blank">${externalUrl.urlValue}</a></li>
            </g:each>
        </ul>

    </section>
</section>
