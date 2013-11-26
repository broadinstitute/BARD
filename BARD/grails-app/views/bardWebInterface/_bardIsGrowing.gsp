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