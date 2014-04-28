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

<r:require module="cart"/>
<style>
.querycartholder {
    padding-top: 15px;
}
@media only screen and (max-width: 1105px){
    .querycartholder {
        padding-top: 55px;
    }
    #shrinkableQcart {
        font-size: 12px;
        line-height:16px;
        padding: 4px 8px;
        margin-top: 10px;
        margin-bottom: 1px;
    }
    .my-bard-button {
        margin-top: 3px;
    }
    .social-networks{float:none}
}
@media only screen and (max-width: 767px){
    body{padding:0;}
    .querycartholder {
        padding-top: 0px;
    }
    .my-bard-button {
        font-size: 12px;
        line-height:16px;
        padding: 3px;
        padding-top: 1px;
        padding-bottom: 1px;
    }
}
</style>
<div class="querycartholder" >
    <g:if test="${flash.searchString}">
        <g:include controller="queryCart" action="refreshSummaryView" params="[searchString: flash.searchString]"/>
    </g:if>
    <g:elseif test="${params?.searchString}">
        <g:include controller="queryCart" action="refreshSummaryView" params="[searchString: params.searchString]"/>
    </g:elseif>
    <g:else>
        <g:include controller="queryCart" action="refreshSummaryView"/>
    </g:else>
</div>

<div class="panel popupQueryCartPanel" style="z-index: 10">
    <a class="trigger" href="#" style="color: #000000">Click to hide query cart </a>
    <g:if test="${flash.searchString}">
        <g:include controller="queryCart" action="refreshDetailsView" params="[searchString: flash.searchString]"/>
    </g:if>
    <g:elseif test="${params?.searchString}">
        <g:include controller="queryCart" action="refreshDetailsView" params="[searchString: params.searchString]"/>
    </g:elseif>
    <g:else>
        <g:include controller="queryCart" action="refreshDetailsView"/>
    </g:else>
</div>
