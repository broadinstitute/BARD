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

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>Search</title>
    <r:require modules="historyJsHtml5,search,promiscuity,compoundOptions,addAllItemsToCarts, histogramAddon"/>
    <sitemesh:parameter name="noSocialLinks" value="${true}"/>
    <style>
        #idnavlist li{
            display: inline;
            list-style-type: none;
            padding-right: 5px;
        }
    </style>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div id="resultTab">

            <ul id="resultTabUL" class="nav nav-tabs">
                <li class="active" id="assaysTabLi"><a href="#assays" data-toggle="tab"
                                                       id="assaysTab">Assay Definitions (0)</a></li>
                <li id="compoundsTabLi"><a href="#compounds" data-toggle="tab" id="compoundsTab">Compounds (0)</a></li>
                <li id="projectsTabLi"><a href="#projects" data-toggle="tab" id="projectsTab">Projects (0)</a></li>
                <li id="experimentsTabLi"><a href="#experiments" data-toggle="tab" id="experimentsTab">Experiments (0)</a></li>
            </ul>

            <div id="resultTabContent" class="tab-content">

                <div class="tab-pane fade in active" id="assays" data-target="#assays">
                    <g:render template="assays"/>
                </div>
                <div class="tab-pane fade" id="compounds" data-target="#compounds">
                    <g:render template="compounds"/>
                </div>

                <div class="tab-pane fade" id="projects" data-target="#projects">
                    <g:render template="projects"/>
                </div>
                <div class="tab-pane fade" id="experiments" data-target="#experiments">
                    <g:render template="experimentResults"/>
                </div>

            </div>
        </div>
    </div>

    <r:require modules="historyJsHtml5,search,promiscuity"/>

</div>
</body>
</html>
