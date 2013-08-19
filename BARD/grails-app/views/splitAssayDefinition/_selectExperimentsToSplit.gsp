<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">

            <g:formRemote url="[controller: 'splitAssayDefinition', action: 'splitExperiments']"
                          name="splitExperiments"
                          update="[success: 'confirmResponse', failure: 'confirmResponse']">

                <div class="control-group">
                    <label><h3>Select one or more Experiments to move to new Assay</h3>
                    </label>
                </div>

                <div class="control-group">
                    <g:hiddenField name="assay.id" value="${assay.id}"/>
                    <g:each var="experiment" in="${assay.experiments}">
                        <g:checkBox name="experimentIds" value="${experiment.id}" checked="false"/> ${experiment.id +' : '+experiment.experimentName} <br/>
                    </g:each>
                 </div>

                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary" name="Split" value="Split">
                    </div>
                </div>

                <div id="confirmResponse"></div>
                <br/>

            </g:formRemote>

        </div>

        <div class="span2">

        </div>
    </div>
</div>
