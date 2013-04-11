<div id="showstep" class="row-fluid">
    <input type="hidden" id="projectIdForStep" name="projectIdForStep" value="${projectId}"/>

    <div id="stepGraph" style="display: none">${pegraph}</div>


    <div id="serviceResponse" style="display: none"></div>

    <div id="ajaxProgress" style="color:#5bb75b"></div>

    <div id="canvas" class="span7"></div>

    <div id="placeholder" class="span2" style="float: right;">

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
                    <td><a href="${createLink(controller: 'bardWebInterface', action: 'showExperiment')}/{{selected.data.link}}">{{selected.data.link}}</a></td>
                </tr>
                </tbody>
            </table>
            <h5>Experiment Name:</h5>

            <p>{{selected.data.ename}}</p>
            <h5>Assay Id:</h5><a href="${createLink(controller: 'bardWebInterface', action: 'showAssay')}/{{selected.data.assay}}" id="assaylink"
                                 target="_blank">{{selected.data.assay}}</a>
        </script>

        <script id="edge-selection-template" type="text/x-handlebars-template">

            <h5>Selected Edge</h5>
            <table>
                <tbody>

                <tr>
                    <td>{{fromNode}}</td>
                    <td></td>
                    <td>{{toNode}}</td>
                </tr>
                </tbody>
            </table>
        </script>

        <script id="node-selection-template1" type="text/x-handlebars-template">
            <h5>Experiment Id:</h5>
            <table>
                <tbody>
                <tr>
                    <td><a href="${createLink(controller: 'bardWebInterface', action: 'showExperiment')}/{{selected.bardExptId}}">{{selected.eid}}</a>
                    </td>
                </tr>
                </tbody>
            </table>
            <h5>Experiment Name:</h5>

            <p>{{selected.ename}}</p>
            <h5>Assay Id:</h5><a
                href="${createLink(controller: 'bardWebInterface', action: 'showAssay')}/{{selected.bardAssay}}"
                id="assaylink1"
                target="_blank">{{selected.assay}}</a>
            <h5>AID:</h5> <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid={{selected.aid}}" id="aidlink1"
                             target="_blank">{{selected.aid}}</a>
        </script>
    </div>
</div>

<div id="canvasIsolated"></div>
