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

        <h5>Experiment Id:</h5>
        <table id="nodelinkTable" style="display: none">
            <tbody>
            <tr>
                <td id="nodelink"></td>
                <td id="nodeicon"><a href="#" onclick="deleteItem($('#nodelink').text(), ${instanceId})"
                                     style="font-family:arial;color:red;font-size:10px;"><i
                            class="icon-trash"></i>Remove from Project</a></td>
            </tr>
            </tbody>
        </table>

        <h5>Edges:</h5>
        <table id="edgesTable" style="display: none">
            <tbody>
            </tbody>
        </table>
        <h5>Experiment Name:</h5>

        <p id="nodeename"></p>
        <h5>Assay Id:</h5><a href="#" id="assaylink" target="_blank"><span id="nodeassay"></span></a>
    </div>
</div>