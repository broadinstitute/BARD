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

<r:require modules="autocomplete"/>
<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="search-form"
        style="border:1px solid rgba(244, 244, 244, 0.2); background:rgba(228, 228, 228, 0.2);">
%{-- .search-field is styled as "display: table" so make sure the children are styled with "display: table-row" and grandchildren with "display: table-cell" --}%
    <div class="search-box">
        <div class="search-box-row">
            <div class="search-box-search-subbuttons" style="width: 130px">
                <div style="display: inline-block; width: 74px">
                    <g:link controller="bardWebInterface" action="jsDrawEditor">
                        <img id="struct_icon" src="${resource(dir: 'images/bardHomepage', file: 'struct_icon.png')}"
                             onmouseover="this.src = '${resource(dir: 'images/bardHomepage', file: 'struct_icon_bright.png')}'"
                             onmouseout="this.src = '${resource(dir: 'images/bardHomepage', file: 'struct_icon.png')}'"
                             alt="Draw or paste a structure"
                             title="Draw or paste a structure" tabindex="2"/>
                    </g:link>
                </div>

                <div style="display: inline-block; width: 51px">
                    <a href="#idModalDiv" id="ids_image" tabindex="3">
                        <img id="ids_icon"
                             src="${resource(dir: 'images/bardHomepage', file: 'ids_icon.png')}"
                             onmouseover="this.src = '${resource(dir: 'images/bardHomepage', file: 'ids_icon_bright.png')}'"
                             onmouseout="this.src = '${resource(dir: 'images/bardHomepage', file: 'ids_icon.png')}'"
                             alt="List of IDs for search"
                             title="List of IDs for search" data-toggle="modal"/>
                    </a>
                </div>
            </div>

            <div class="search-box-text-field-cell" style="width: 100%">
                <g:if test="${flash?.searchString}">
                    <g:textField id="searchString" name="searchString" value="${flash.searchString}" tabindex="4"/>
                </g:if>
                <g:elseif test="${params?.searchString}">
                    <g:textField id="searchString" name="searchString" value="${params?.searchString}" tabindex="4"/>
                </g:elseif>
                <g:else>
                    <g:textField id="searchString" name="searchString" value="" tabindex="4"/>
                </g:else>
            </div>

            <div class="search-box-button-cell">
                <button type="submit" name="search" class="search-button"
                        id="searchButton" tabindex="5">SEARCH</button>
            </div>
        </div>
    </div>

</g:form>

