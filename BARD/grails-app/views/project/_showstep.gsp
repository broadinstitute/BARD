<div>
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${instanceId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>

    <div>
        <div class="span12"><button id="addExperimentToProject" class="btn btn-primary">Add Experiment</button>
            <button id="linkExperiment" class="btn btn-primary">Link Experiment</button></div>
    </div>

    <div id="serviceResponse" style="display: none"></div>

    <div>
        <canvas id="viewport" width="800" height="600"></canvas>
    </div>

    <div id="placeholder" style="position:absolute; top:0; right:0; width:200px;">
        <g:render template='/project/editstep'/>

        <div id="node-selection-details">
            Click on an experiment to see the details here.
        </div>

        <script id="node-selection-template" type="text/x-handlebars-template">
            <h5>Experiment Id:</h5> <a href='/BARD/experiment/show/{{selected.node.data.link}}'>{{selected.node.data.link}}</a>
            <table>
                <tbody>
                </tbody>
            </table>

            <h5>Edges:</h5>
            <table>
                <tbody>
                {{#each fromSelectedNode}}
                <tr>
                    <td>{{selected.node.data.link}}</td>
                    <td></td>
                    <td>{{target.data.link}}</td>
                    <td>
                        <a href="#" onclick="deleteEdge({{../selected.node.data.link}},{{target.data.link}},{{../projectId}})"
                           style="font-family:arial;color:red;font-size:10px;">
                            <i class="icon-trash"></i>
                            Remove from Project
                        </a>
                    </td>
                </tr>
                {{/each}}

                {{#each toSelectedNode}}
                <tr>
                    <td>{{source.data.link}}</td>
                    <td></td>
                    <td>{{selected.node.data.link}}</td>
                    <td>
                        <a href="#" onclick="deleteEdge({{source.data.link}},{{../selected.node.data.link}},{{../projectId}})"
                           style="font-family:arial;color:red;font-size:10px;"><i class="icon-trash"></i>Remove from Project
                        </a>
                    </td>
                </tr>
                {{/each}}
                </tbody>
            </table>
            <h5>Experiment Name:</h5>

            <p>{{selected.node.data.ename}}</p>
            <h5>Assay Id:</h5><a href="/BARD/assayDefinition/show/{{selected.node.data.assay}}" id="assaylink" target="_blank">{{selected.node.data.assay}}</a>
        </script>

    </div>
</div>