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

<div class="item ${activeClass}">
    <div class="row-fluid">
        <article class="span12">
            <ul class="thumbnails">
                <g:each var="probe" in="${currentCompoundAdapters}" status="i">
                    <li class="span3">
                        <div class="thumbnail">
                            <g:compoundOptions cid="${probe.id}" sid="${probe.id}" smiles="${probe?.smiles}"
                                               name="${bardqueryapi.JavaScriptUtility.cleanup(probe.name)}"
                                               imageHeight="100" imageWidth="300"/>
                            <div class="caption">
                                <h3>Probe ML#: ${probe.probeId}</h3>
                                <ul>
                                    <li><a href="${probe.url}">Download probe report from Molecular Library BookShelf</a>
                                    </li>
                                    <li><g:link controller="bardWebInterface" action="showCompound"
                                                params="[cid: probe.id]">Show Compound Details in BARD</g:link></li>
                                    <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=${probe.id}"
                                           target="_blank">View CID ${probe.id} in PubChem</a></li>
                                    <li><g:link controller="molSpreadSheet" action="showExperimentDetails"
                                                params="[cid: probe.id,transpose: true]"
                                                data-placement="top"
                                                class="projectTooltip"
                                                rel="tooltip"
                                                data-original-title="">Show Experimental Details</g:link></li>
                                </ul>
                            </div>
                        </div>
                    </li>
                </g:each>
                <li class="span3" style="overflow: hidden;">
                    <br/>
                    <br/>
                    <g:if test="${probeCompoundsSearchString}">
                        <g:form controller="BardWebInterface" action="search" method="POST">
                            <g:hiddenField name="searchString" value="${probeCompoundsSearchString}"/>
                            <g:submitButton name="View All Probe Compounds" class='btn'
                                            value="VIEW ALL PROBE COMPOUNDS"/>
                        </g:form>
                    </g:if>
                    <g:if test="${probeProjectSearchString}">
                        <g:form controller="BardWebInterface" action="search" method="POST">
                            <g:hiddenField name="searchString" value="${probeProjectSearchString}"/>
                            <g:submitButton name="View All Probe Projects" class='btn'
                                            value="VIEW ALL PROBE PROJECTS"/>
                        </g:form>
                    </g:if>
                </li>
            </ul>
        </article>
    </div>
</div>



