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

<%@ page import="bard.core.adapter.CompoundAdapter" %>
<%
    String probeProjectSearchString = null
    if (probeProjectIds) {
        probeProjectSearchString = probeProjectIds?.join(",")
        probeProjectSearchString = "PID:${probeProjectSearchString}"
    }
    int remainingCompounds = compoundAdapters.size() % 3

    String probeCompoundsSearchString = null
    if (probeCompoundIds) {
        probeCompoundsSearchString = probeCompoundIds?.join(",")
        probeCompoundsSearchString = "CID:${probeCompoundsSearchString}"
    }


%>
<div class="tab-pane" id="tab-probes">

    <div class="items-gallery slide" id="items-gallery-5" data-interval="false">
        <a href="#items-gallery-5" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-5" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner" style="overflow: visible;">
            <g:set var="activeClass" value="active"/>
            <g:each in="${compoundAdapters}" status="i" var="x">
                <g:if test="${i % 3 == 0 && i > 0}">

                    <g:render template="probeProject" model="['activeClass': activeClass,
                            'probeProjectSearchString': probeProjectSearchString,
                            'probeCompoundsSearchString': probeCompoundsSearchString,
                            'currentCompoundAdapters': compoundAdapters.subList(i - 3, i)]"/>
                %{--make rest of tabs inactive--}%
                    <g:set var="activeClass" value="noclass"/>
                </g:if>
            </g:each>
            <g:set var="activeClass" value="active"/>
            %{--There are remaining compounds and that this is not the first page--}%
            <g:if test="${remainingCompounds > 0 && compoundAdapters.size() > 3}">
                <g:set var="activeClass" value="noclass"/>
            </g:if>
            <g:if test="${remainingCompounds > 0}">
                <g:render template="probeProject" model="['activeClass': activeClass,
                        'probeProjectSearchString': probeProjectSearchString,
                        'probeCompoundsSearchString': probeCompoundsSearchString,
                        'currentCompoundAdapters': compoundAdapters.subList(compoundAdapters.size() - remainingCompounds, compoundAdapters.size())]"/>
            </g:if>
        </div>
    </div>
</div>
