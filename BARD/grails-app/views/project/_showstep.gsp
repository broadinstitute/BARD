<div id="showstep" style="position: relative;">
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>

    <div>
        <g:if test="${editable == 'canedit'}">
            <div class="span12"><button id="addExperimentToProject" class="btn">Add Experiment</button>
                <button id="linkExperiment" class="btn">Link Experiments</button></div>
        </g:if>
    </div>

    <div id="serviceResponse" style="display: none"></div>

    <div id="ajaxProgress" style="color:#5bb75b">Please wait ...</div>

    <div id="canvas"></div>

    <div id="canvasIsolated"></div>

    <div id="nicedisplay"></div>

    <div id="placeholder">

        <div id="node-selection-details">
            Click on an experiment to see the details here.
        </div>
        <br/>
        <br/>

        <div id="edge-selection-details">
            Click on an edge connecting experiments to see the details here.
        </div>


        <script id="edge-selection-template" type="text/x-handlebars-template">
            <div id="selectedEdgeId" style="display: none">{{selectedEdgeId}}</div>
            <strong>Selected Edge</strong>

            <table><tr>
            <td><div id="selectedEdgeFromId">{{fromNode}}</div> </td>
             <td> - </td>
            <td><div id="selectedEdgeToId">{{toNode}}</div></td>
            <td><g:if test="${editable == 'canedit'}">
                <a href="#" onclick="deleteEdge({{fromNode}},{{toNode}},${instanceId});return false;"
                   style="font-family:arial;color:red;font-size:10px;">
                    <i class="icon-trash"></i>
                    Remove from Project
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

            <strong>Experiment ID:</strong>
            <a href='/BARD/experiment/show/{{selected.eid}}'>{{selected.eid}}</a>
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

            <strong>Assay Definition ID:</strong> <a href="/BARD/assayDefinition/show/{{selected.assay}}"
                                                     id="assaylink1"
                                                     target="_blank">{{selected.assay}}</a>
            <br/>
            <br/>

            <strong>PubChem AID:</strong> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.aid}}"
                                             id="aidlink1" target="_blank">{{selected.aid}}</a>
        </script>

    </div>
</div>