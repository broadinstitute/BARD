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

<g:if test="${target.getTargetClassifications()}">
    <div class="page-header">
        <h3>Classifications</h3>
    </div>

    <g:each in="${target.getTargetClassifications()}" var="classification">
        <dl class="dl-horizontal dl-horizontal-wide">
            <g:if test="${classification.id}">
                <dt>ID:</dt> <dd>${classification.id}</dd>
            </g:if>
            <g:if test="${classification.name}">
                <dt>Name:</dt> <dd>${classification.name}</dd>
            </g:if>
            <g:if test="${classification.description}">
                <dt>Description:</dt><dd><g:textBlock>${classification.description}</g:textBlock>&nbsp;</dd>
            </g:if>
            <g:if test="${classification.levelIdentifier}">
                <dt>Level Identifier:</dt><dd>${classification.levelIdentifier} &nbsp;</dd>
            </g:if>
            <g:if test="${classification.source}">
                <dt>Source:</dt><dd>${classification.source} &nbsp;</dd>
            </g:if>
        </dl>
    </g:each>
</g:if>

