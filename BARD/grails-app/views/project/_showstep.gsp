<div id="showstep">
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>

    <div>
        <div class="span12"><button id="addExperimentToProject" class="btn btn-primary">Add Experiment</button>
            <button id="linkExperiment" class="btn btn-primary">Link Experiment</button></div>
    </div>

    <div id="serviceResponse" style="display: none"></div>

    <div id="canvas"></div>
    <div id="canvasIsolated"></div>

    Ugly? <button id="redraw" onclick="redraw();">redraw</button> Still Ugly? Drag, Drop Nodes to Rearrange. But Sorry, You Can NOT Save It.

    <div id="placeholder" style="position:absolute; top:0; right:0; width:200px;">


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
        </script>

        <script id="edge-selection-template" type="text/x-handlebars-template">

            <h5>Selected Edge</h5>
            <table>
            <tbody>

            <tr>
            <td>{{fromNode}}</td>
            <td></td>
            <td>{{toNode}}</td>
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

    </div>
</div>