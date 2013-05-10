<div id="showstep" style="position: relative;">
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>

    <div>
        <div class="span12"><button id="addExperimentToProject" class="btn">Add Experiment</button>
            <button id="linkExperiment" class="btn">Link Experiment</button></div>
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

        <div id="edge-selection-details">
            Click on an edge to see the details here.
        </div>

        <script id="node-selection-template" type="text/x-handlebars-template">
            <strong>Experiment Id:</strong>
            <a href='/BARD/experiment/show/{{selected.data.link}}'>{{selected.data.link}}</a>
            <a href="#" onclick="deleteItem({{selected.data.link}}, ${instanceId});return false;"
               style="font-family:arial;color:red;font-size:10px;"><i
                    class="icon-trash"></i>Remove from Project</a>

            <strong>Experiment Name:</strong>
            <div>{{selected.data.ename}}</div>
            <strong>Assay Id:</strong><a href="/BARD/assayDefinition/show/{{selected.data.assay}}" id="assaylink"
                                 target="_blank">{{selected.data.assay}}</a>
            <strong>AID:</strong> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.data.aid}}"
                             id="aidlink" target="_blank">{{selected.data.aid}}</a>
        </script>

        <script id="edge-selection-template" type="text/x-handlebars-template">
            <div id="selectedEdgeId" style="display: none">{{selectedEdgeId}}</div>
            <strong>Selected Edge</strong>

            <div id="selectedEdgeFromId">{{fromNode}}</div>

            <div id="selectedEdgeToId">{{toNode}}</div>
            <a href="#" onclick="deleteEdge({{fromNode}},{{toNode}},${instanceId});return false;"
               style="font-family:arial;color:red;font-size:10px;">
                <i class="icon-trash"></i>
                Remove from Project
            </a>
        </script>

        <script id="node-selection-template1" type="text/x-handlebars-template">
            <div id="selectedNodeId" style="display: none">{{selectedNodeId}}</div>
            <strong>Experiment Id:</strong>
            <a href='/BARD/experiment/show/{{selected.eid}}'>{{selected.eid}}</a>
            <a href="#" onclick="deleteItem({{selected.eid}}, ${instanceId});return false;"
               style="font-family:arial;color:red;font-size:10px;"><i
                    class="icon-trash"></i>Remove from Project</a>

            <strong>Experiment Name:</strong>
            <div>{{selected.ename}}</div>
            <strong>ADID:</strong> <a href="/BARD/assayDefinition/show/{{selected.assay}}" id="assaylink1"
                              target="_blank">{{selected.assay}}</a> <br/>
            <strong>PubChem AID:</strong> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.aid}}"
                                     id="aidlink1" target="_blank">{{selected.aid}}</a>
        </script>

    </div>
</div>