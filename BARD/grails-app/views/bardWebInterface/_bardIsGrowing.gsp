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

%{--The BARD is  growing line sits on its own above the blocks--}%
<section
        class="tabs-section">%{--This section tag binds 'Bard is growing', the blocks, and the tab information together  --}%
    <div class="container-fluid">
        <div class="page-header">
            <h1>BARD Is Growing <small>Statistics &amp; Recent Submissions</small></h1>
        </div>
    </div>

    %{--Here we have a set of clickable boxes, each one leading to a carousel of information. These are implemented simply as tabs,--}%
    %{--all of which are defined in the next section. This information should probably come back dynamically through ajax ( at least --}%
    %{--once we have information worth providing--}%
    <div class="tabs-list-holder">
        <ul class="tabs-list">
            <li>
                <g:projectCount/>
            </li>
            <li class="active">
                <g:assayCount/>
            </li>
            <li>
                <g:experimentCount/>
            </li>
            <li>
                <g:probeCount/>
            </li>
        </ul>
    </div>


    <div class="container-fluid">
        <div class="tab-content">
            %{--Contents of the "Projects" tab (of our row of five content boxes) --}%
            <g:render template="recentlyAddedProjects" model="['recentlyAddedProjects': recentlyAddedProjects]"/>

            %{--Contents of the "Definitions" tab (of our row of five content boxes) --}%
            <g:render template="recentlyAddedAssays" model="['recentlyAddedAssays': recentlyAddedAssays]"/>


            %{--Contents of the "Experiments" tab (of our row of five content boxes) --}%
            <g:render template="recentlyAddedExperiments"
                      model="['recentlyAddedExperiments': recentlyAddedExperiments]"/>
            %{--Contents of the "Compounds" tab (of our row of five content boxes) --}%
            %{--<g:render template="numberOfExperimentData"--}%
            %{--model="['numberOfExperimentData': numberOfExperimentData]"/>--}%

            %{--Contents of the "Probes" tab (of our row of five content boxes) --}%
            <g:render template="probeProjects" model="[
                    'probeProjectIds': probeProjectIds,
                    'compoundAdapters': probeCompoundMap.compoundAdapters,
                    'probeCompoundIds': probeCompoundMap.probeCompoundIds
            ]"/>
        </div>
    </div>
</section>
