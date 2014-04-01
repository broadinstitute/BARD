<div class="span-9">
<div id="showstep" style="position: relative;">
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>

    <div>
        <g:if test="${editable == 'canedit'}">
            <div class="span12">
            <g:link controller="project" action="showExperimentsToAddProject" class="btn" params='[projectId: "${instanceId}"]'>Add Experiment or Panel-Experiment</g:link>
                <button id="linkExperiment" class="btn">Link Experiments</button></div>
            <br/><br/>
        </g:if>
    </div>
     <br/>
    <div id="serviceResponse" style="display: none"></div>

    <div id="ajaxProgress" style="color:#5bb75b">Please wait ...</div>

    <div id="canvas-container" style="overflow: hidden; cursor: default ; border: 2px dotted black; width: 100%; clear: left">
        <div id="canvas">
            <p><b>Experiments with the same color have the same assay definition</b></p>

            <p>Click and drag on background to pan in graph</p></div>
    </div>

    <div id="nicedisplay"></div>

    <div class="span-3">
    <div id="placeholder">
        Zoom: <div class="btn-group"><input class="btn" type="button" id="zoomIn" value="In"/><input type="button" id="zoomOut" value="Out" class="btn"/></div>
        <br/>
        <br/>

        <div id="selection-details">
            Click on an experiment or a link to see the details here.
        </div>

        <script id="edge-selection-template" type="text/x-handlebars-template">
            <div id="selectedEdgeId" style="display: none">{{selectedEdgeId}}</div>
            <strong>Selected Link</strong>

            <table><tr>
                <td><div id="selectedEdgeFromId">{{fromNode}}</div> </td>
                <td> - </td>
                <td><div id="selectedEdgeToId">{{toNode}}</div></td>
                <td><g:if test="${editable == 'canedit'}">
                    <a href="#" onclick="deleteEdge({{fromNode}},{{toNode}},${instanceId});return false;"
                       style="font-family:arial;color:red;font-size:10px;">
                        <i class="icon-trash"></i>
                        Remove link
                    </a>
                </g:if></td>
            </tr></table>
        </script>
        <script id="node-selection-template" type="text/x-handlebars-template">
            <div id="selectedNodeId" style="display: none">{{selectedNodeId}}</div>
            <strong>Experiment Stage:</strong>
            <g:if test="${editable == 'canedit'}">
                <a href="javascript:;" data-sourceError="Error loading stages" data-sourceCache="true"
                   class="projectStageId" id="{{selected.stageid}}"
                   data-type="select" data-value="{{selected.stage}}" data-source="/BARD/project/projectStages"
                   data-pk="{{selected.peid}}"
                   data-url="/BARD/project/updateProjectStage" data-original-title="Select New Stage">{{selected.stage}}
                    <i class="icon-pencil"></i>
                </a>

            </g:if>
            <g:else>
                {{selected.stage}}
            </g:else>
            <br/>
            <br/>

            {{#if selected.eid}}
                <strong>Experiment ID:</strong>
                <a href="${request.contextPath}/experiment/show/{{selected.eid}}">{{selected.eid}}</a>
                <g:if test="${editable == 'canedit'}">
                    <a href="#" onclick="deleteItem({{selected.eid}}, ${instanceId});return false;"
                       style="font-family:arial;color:red;font-size:10px;"><i
                            class="icon-trash"></i>Remove from Project</a>
                </g:if>
                <br/>
                <br/>

                <strong>Experiment Name:</strong>

                <div>{{selected.ename}}</div>
                <br/>

                <strong>Assay Definition ID:</strong> <a href="${request.contextPath}/assayDefinition/show/{{selected.assay}}"
                                                         id="assaylink1"
                                                         target="_blank">{{selected.assay}}</a>
                <br/>
                <br/>

                <strong>PubChem AID:</strong> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.aid}}"
                                                 id="aidlink1" target="_blank">{{selected.aid}}</a>
            {{/if}}

            {{#if selected.pid}}
                <strong>Experiment IDs:</strong>

                {{#each selected.eids}}
                    <a href="${request.contextPath}/experiment/show/{{this}}">{{this}}</a>
                {{/each}}
                <br/>

                <strong>Panel:</strong> <a href="${request.contextPath}/panel/show/{{selected.pid}}"
                                                         id="assaylink1"
                                                         target="_blank">{{selected.panel}}</a>

                <g:if test="${editable == 'canedit'}">
                    <a href="#" onclick="deletePanelExperimentItem({{selected.pnlExpId}}, ${instanceId});return false;"><i class="icon-trash"/></a>
                </g:if>

            <br/>
            {{/if}}
        </script>

    </div>
    </div>
</div>
</div>