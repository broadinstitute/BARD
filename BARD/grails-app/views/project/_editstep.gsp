<div id="dialog_confirm_delete_item">
    <p>
        <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
        The experiment/step will be de-associated with the project. Are you sure?
    </p>
</div>
<input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

<div id="dialog_link_experiment" title="Link Experiments">

    <form id="linkExperimentForm">
        <div id="displayLinkExperimentErrorMessage"></div>
        <label for="fromExperimentId">From Experiment ID:</label>
        <select name="fromExperimentId" id="fromExperimentId"
                class="text ui-widget-content ui-corner-all"></select>
        <label for="toExperimentId">To Experiment ID:</label>
        <select name="toExperimentId" id="toExperimentId"
                class="text ui-widget-content ui-corner-all"></select>
    </form>

</div>
