<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span8">

            <g:formRemote url="[controller: 'moveExperiments', action: 'moveSelectedExperiments']"
                          name="moveSelectedExperiments"
                          update="[success: 'responseDiv', failure: 'responseDiv']">

                <div class="control-group">
                    <label><h3>Select one or more Experiments to move to Target Assay:</h3>
                    </label>
                    <label><h4>Source Assay :
                    <g:link controller="assayDefinition" action="show" id="${sourceAssay.id}"
                            target="_blank">
                        ${sourceAssay.id} - ${sourceAssay.assayName}
                    </g:link>
                    </h4>
                    </label>
                </div>

                <div class="control-group">
                    <g:hiddenField name="sourceAssay.id" value="${sourceAssay.id}"/>
                    <g:hiddenField name="targetAssay.id" value="${targetAssay.id}"/>

                    <g:each var="experiment" in="${sourceAssay.experiments}">
                        <g:checkBox name="experimentIds" value="${experiment.id}"
                                    checked="false"/> <g:link controller="experiment"
                                                              action="show" id="${experiment.id}">${experiment.id + ' : ' + experiment.experimentName}</g:link><br/>
                    </g:each>
                </div>
                <br/>

                <div class="control-group">
                    <label><h4>Target Assay :
                        <g:link controller="assayDefinition" action="show" id="${targetAssay.id}"
                                target="_blank">${targetAssay.id} - ${targetAssay.assayName}</g:link></h4></label>
                    <ul> Experiments:
                        <g:each var="experiment" in="${targetAssay.experiments}">
                            <li>
                                <g:link controller="experiment" action="show" id="${experiment.id}">
                                    ${experiment.id + ' : ' + experiment.experimentName}
                                </g:link>
                            </li>
                        </g:each>
                    </ul>
                </div>
                <br/>

                <div class="control-group">
                    <div class="controls">
                        <input type="submit" class="btn btn-primary" name="Move Experiments" value="Move Experiments">
                    </div>
                </div>

                <div id="responseDiv"></div>
                <br/>

            </g:formRemote>

        </div>

        <div class="span2">

        </div>
    </div>
</div>
