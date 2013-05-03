<div id="showstep">
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>

    <div>
        <div class="span12"><button id="addExperimentToProject" class="btn btn-primary">Add Experiment</button>
            <button id="linkExperiment" class="btn btn-primary">Link Experiment</button></div>
    </div>

    <div id="serviceResponse" style="display: none"></div>
    <div id="ajaxProgress" style="color:#5bb75b"> Please wait ... </div>

    <div id="canvas"></div>
    <div id="canvasIsolated"></div>
    <div id="nicedisplay"></div>

    <div id="placeholder">


        <div id="node-selection-details">
            Click on an experiment to see the details here.
        </div>

        <div id="edge-selection-details">
            Click on an edge to see the details here.
        </div>

        <script id="node-selection-template" type="text/x-handlebars-template">
            <h5>Experiment Id:</h5>
            <table>
                <tbody>
                <tr>
                    <td><a href='/BARD/experiment/show/{{selected.data.link}}'>{{selected.data.link}}</a></td>
                    <td><a href="#" onclick="deleteItem({{selected.data.link}}, ${instanceId});return false;"
                           style="font-family:arial;color:red;font-size:10px;"><i
                                class="icon-trash"></i>Remove from Project</a></td>
                </tr>
                </tbody>
            </table>
            <h5>Experiment Name:</h5>

            <p>{{selected.data.ename}}</p>
            <h5>Assay Id:</h5><a href="/BARD/assayDefinition/show/{{selected.data.assay}}" id="assaylink" target="_blank">{{selected.data.assay}}</a>
            <h5>AID: </h5> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.data.aid}}" id="aidlink" target="_blank">{{selected.data.aid}}</a>
        </script>

        <script id="edge-selection-template" type="text/x-handlebars-template">
            <p id="selectedEdgeId" style="display: none">{{selectedEdgeId}}</p>
            <h5>Selected Edge</h5>
            <table>
            <tbody>

            <tr>
            <td id="selectedEdgeFromId">{{fromNode}}</td>
            <td></td>
            <td id="selectedEdgeToId">{{toNode}}</td>
            <td>
            <a href="#" onclick="deleteEdge({{fromNode}},{{toNode}},${instanceId});return false;"
            style="font-family:arial;color:red;font-size:10px;">
            <i class="icon-trash"></i>
            Remove from Project
            </a>
            </td>
            </tr>
            </tbody>
            </table>
         </script>

    <script id="node-selection-template1" type="text/x-handlebars-template">
        <p id="selectedNodeId" style="display: none">{{selectedNodeId}}</p>
        <h5>Experiment Id:</h5>
        <table>
            <tbody>
            <tr>
                <td><a href='/BARD/experiment/show/{{selected.eid}}'>{{selected.eid}}</a></td>
                <td><a href="#" onclick="deleteItem({{selected.eid}}, ${instanceId});return false;"
                       style="font-family:arial;color:red;font-size:10px;"><i
                            class="icon-trash"></i>Remove from Project</a></td>
            </tr>
            </tbody>
        </table>
        <h5>Experiment Name:</h5>

        <p>{{selected.ename}}</p>
        <h5>ADID:</h5> <a href="/BARD/assayDefinition/show/{{selected.assay}}" id="assaylink1" target="_blank">{{selected.assay}}</a>
        <h5>PubChem AID:</h5> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.aid}}" id="aidlink1" target="_blank">{{selected.aid}}</a>
    </script>


    </div>
</div>