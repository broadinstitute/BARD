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

<g:if test="${summary.hasErrors()}">
    <h4>Errors</h4>

    <ul>
        <g:each in="${summary.errors}" var="error">
            <li>
                ${error}
            </li>
        </g:each>
    </ul>
</g:if>
<g:else>
    <h4>Success</h4>

    <p>${summary.linesParsed} lines successfully parsed.</p>
    <p>Uploaded ${summary.resultsCreated} values for ${summary.substanceCount} substances</p>
    <p>Created ${summary.experimentAnnotationsCreated} Experiment annotations</p>
    <p>
        Created the following number of results:
    <ul>
        <g:each in="${summary.resultsPerLabel.entrySet()}" var="entry">
            <li>${entry.key}: ${entry.value}</li>
        </g:each>
    </ul>
    </p>
    <p>
        Of those results, ${summary.resultsWithRelationships} had relationships to other results and a total of ${summary.resultAnnotations} result annotations were loaded.
    </p>

    <p>Top 10 lines of submitted file</p>
    <table border="1">
        <tbody>
        <g:each in="${summary.topLines}" var="row">
            <tr>
                <g:each in="${row}" var="cell">
                    <td>${cell}</td>
                </g:each>
            </tr>
        </g:each>
        </tbody>
    </table>

</g:else>
