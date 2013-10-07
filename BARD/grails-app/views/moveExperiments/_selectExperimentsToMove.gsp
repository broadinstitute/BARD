<%@ page import="bard.db.enums.AssayStatus; bard.db.registration.ExternalReference" %>
<div class="container">
    <div>
        <h2>(${moveExperimentsCommand.experiments?.size()}) Experiments found - choose which ones to move</h2>
    </div>

    <div class="row">
        <div class="span12">
            <g:formRemote url="[controller: 'moveExperiments', action: 'moveSelectedExperiments']"
                          name="splitExperiments"
                          update="[success: 'displayResponseDiv', failure: 'displayResponseDiv']">
            %{--Select all check boxes by default otherwise toggle--}%
                <g:checkBox name="selectAll" id="selectAll" class="selectAll" checked="true"/> select all <br/>
                <table id="myExperiments" class="table table-striped table-hover table-bordered">
                    <thead>
                    <tr>
                        <th></th><th>Experiment ID</th><th>ADID</th><th>PubChem AID</th><th>Experiment Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each var="experiment" in="${moveExperimentsCommand.experiments}">
                        <tr>
                            <td>
                                <g:checkBox name="experimentIds" value="${experiment.id}" class="check"
                                            onclick="resetSelectAll();"/>
                            </td>
                            <td>
                                <g:link controller="experiment"
                                        action="show" id="${experiment.id}">${experiment.id}</g:link>
                            </td>
                            <td>
                                <g:link controller="assayDefinition"
                                        action="show" id="${experiment.assay.id}">${experiment.assay.id}</g:link>
                            </td>
                            <td>
                                <%
                                    ExternalReference externalReference = bard.db.registration.ExternalReference.findByExperiment(experiment)

                                    String aid = externalReference?.getAid() ?: ""
                                %>
                                ${aid}
                            </td>
                            <td>${experiment.experimentName}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
                <g:hiddenField name="targetAssay.id" value="${moveExperimentsCommand.targetAssay.id}"/>
                <br/>
                <br/>
                <div>
                    <h2>Assay Definition to move to:</h2>
                    <ul>
                         <g:link controller="assayDefinition" action="show" id="${moveExperimentsCommand.targetAssay.id}"
                                target="_blank">
                            <li>
                                ${moveExperimentsCommand.targetAssay.id} - ${moveExperimentsCommand.targetAssay.assayName}
                                <g:if test="${moveExperimentsCommand.targetAssay.assayStatus == AssayStatus.RETIRED}">
                                    <span class="alert-error">
                                        - ${moveExperimentsCommand.targetAssay.assayStatus}
                                    </span>
                                </g:if>
                            </li>
                        </g:link>
                    </ul>
                </div>

                <br/>
                <br/>
                <input type="submit" class="btn btn-primary" name="Move Experiments" value="Move Experiments">
            </g:formRemote>
            <div id="displayResponseDiv">

            </div>
        </div>

    </div>
    <script>
        $('.selectAll').on('click', function () {
            $('.check').prop('checked', isChecked('selectAll'));
        });
    </script>
</div>

