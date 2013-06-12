<div id="dialog_confirm_delete_item">
    <p>
        <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
        The experiment/step will be de-associated with the project. Are you sure?
    </p>
</div>
<input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>
<div id="dialog_add_experiment_step" title="Add Experiment to Project">
    <form id="addExperimentForm">
    <div>
        <p>Search for Available Experiment by:</p>
        <input type="radio" name="addExperimentBy" value="addByExperimentId" checked> Experiment ID</input>
        <input type="radio" name="addExperimentBy" value="addByAssayId"> Assay Definition ID</input>
        <input type="radio" name="addExperimentBy" value="addByExperimentName"> Experiment Name</input>
    </div>
    <div style="padding:10px 0px 0px 0px;">
        <input type="text" name="addByExperimentId" id="addByExperimentId" value="" class="text ui-widget-content ui-corner-all" style="display: none" />
        <input type="text" name="addByAssayId" id="addByAssayId" value="" class="text ui-widget-content ui-corner-all" style="display: none" />
        <input type="text" name="addByExperimentName" id="addByExperimentName" value="" class="text ui-widget-content ui-corner-all" style="display: none" />
    </div>


    <p>If the experiment is already in the project it will not be shown here.  Select experiments from the list to add:</p>
    <div id="availableExperiment">
        <select id="selectedExperiments" name="selectedExperiments" multiple="multiple" tabindex="1" style="width: 400px">
        </select>
    </div>
    <p>Select stage:</p>
        <label for="stageId">Stage</label>

            <input type="hidden" id="stageId" name="stageId"/>
            <r:script>
                enableAutoCompleteOntology("STAGE", "#stageId");
            </r:script>

    </form>
</div>
<div id="dialog_link_experiment" title="Link experiments">
    <form id="linkExperimentForm">
        <label for="fromExperimentId" >From Experiment ID:</label>
        <input type="text" name="fromExperimentId" id="fromExperimentId" value="" class="text ui-widget-content ui-corner-all" />
        <label for="toExperimentId">To Experiment ID:</label>
        <input type="text" name="toExperimentId" id="toExperimentId" value="" class="text ui-widget-content ui-corner-all" />
    </form>
</div>
