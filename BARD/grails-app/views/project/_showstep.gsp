<r:require module="zyngaScroller"/>
<div class="span-9">
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

    <div id="canvas-container" style="overflow: hidden; cursor: default ; border: 2px dotted black">
        <div id="canvas"><p>Click and drag on background to pan in graph</p></div>
    </div>

    <r:script>
        var container = document.getElementById("canvas-container");
        var content = document.getElementById("canvas");
        var contentWidth = 2000;
        var contentHeight = 2000;
        var clientWidth = 0;
        var clientHeight = 0;
        var clientOffset = $("#canvas-container").offset();

        /* DOM-based rendering (Uses 3D when available, falls back on margin when transform not available) */
        var render = (function(global) {

            var docStyle = document.documentElement.style;

            var engine;
            if (global.opera && Object.prototype.toString.call(opera) === '[object Opera]') {
                engine = 'presto';
            } else if ('MozAppearance' in docStyle) {
                engine = 'gecko';
            } else if ('WebkitAppearance' in docStyle) {
                engine = 'webkit';
            } else if (typeof navigator.cpuClass === 'string') {
                engine = 'trident';
            }

            var vendorPrefix = {
                trident: 'ms',
                gecko: 'Moz',
                webkit: 'Webkit',
                presto: 'O'
            }[engine];

            var helperElem = document.createElement("div");
            var undef;

            var perspectiveProperty = vendorPrefix + "Perspective";
            var transformProperty = vendorPrefix + "Transform";

            return function(left, top, zoom) {
                content.style.marginLeft = left ? (-left/zoom) + 'px' : '';
                content.style.marginTop = top ? (-top/zoom) + 'px' : '';
                content.style.zoom = zoom || '';
            };

        })(this);

        scroller = new Scroller(render, {
            zooming: true
        });

        var rect = container.getBoundingClientRect();
        scroller.setPosition(rect.left + container.clientLeft, rect.top + container.clientTop);


        // Reflow handling
        var reflow = function() {
            clientWidth = container.clientWidth;
            clientHeight = container.clientHeight;
            scroller.setDimensions(clientWidth, clientHeight, contentWidth, contentHeight);
            clientOffset = $("#canvas-container").offset()
        };

        window.addEventListener("resize", reflow, false);
        reflow();

        if ('ontouchstart' in window) {

            container.addEventListener("touchstart", function(e) {
                // Don't react if initial down happens on a form element
                if (e.touches[0] && e.touches[0].target && e.touches[0].target.tagName.match(/input|textarea|select/i)) {
                    return;
                }

                scroller.doTouchStart(e.touches, e.timeStamp);
                e.preventDefault();
            }, false);

            document.addEventListener("touchmove", function(e) {
                scroller.doTouchMove(e.touches, e.timeStamp, e.scale);
            }, false);

            document.addEventListener("touchend", function(e) {
                scroller.doTouchEnd(e.timeStamp);
            }, false);

            document.addEventListener("touchcancel", function(e) {
                scroller.doTouchEnd(e.timeStamp);
            }, false);

        } else {

            var mousedown = false;

            container.addEventListener("mousedown", function(e) {
                if (e.target.tagName.match(/input|textarea|select/i)) {
                    return;
                }

                scroller.doTouchStart([{
                    pageX: e.pageX,
                    pageY: e.pageY
                }], e.timeStamp);

                mousedown = true;
            }, false);

            document.addEventListener("mousemove", function(e) {
                if (!mousedown) {
                    return;
                }

                scroller.doTouchMove([{
                    pageX: e.pageX,
                    pageY: e.pageY
                }], e.timeStamp);

                mousedown = true;
            }, false);

            document.addEventListener("mouseup", function(e) {
                if (!mousedown) {
                    return;
                }

                scroller.doTouchEnd(e.timeStamp);

                mousedown = false;
            }, false);

//            container.addEventListener(navigator.userAgent.indexOf("Firefox") > -1 ? "DOMMouseScroll" :  "mousewheel", function(e) {
//                scroller.doMouseZoom(e.detail ? (e.detail * -120) : e.wheelDelta, e.timeStamp, e.pageX, e.pageY);
//            }, false);


        }

        $("#zoomIn").on("click", function() {
            scroller.zoomBy(1.2, true);
        });

        $("#zoomOut").on("click", function() {
            scroller.zoomBy(0.8, true);
        });
    </r:script>


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
</div>
</div>