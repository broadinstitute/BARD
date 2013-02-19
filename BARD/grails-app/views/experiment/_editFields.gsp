<div class="row-fluid">
<div id="accordion-foo" class="span12 accordion">

<div class="accordion-group">
    <div class="accordion-heading">
        <a href="#contexts-header" class="accordion-toggle" data-toggle="collapse"
           data-target="#target-contexts-info">
            <i class="icon-chevron-down"></i>
            Summary
        </a>
    </div>

    <div class="accordion-body in collapse">
        <div class="accordion-inner">
            <dl class="dl-horizontal">

                <dt>Assay</dt><dd><g:link controller="assayDefinition" action="show"
                                          id="${assay.id}">${assay.name}</g:link></dd>

                <dt>Experiment Name</dt>
                <dd>
                    <input class="input-xxlarge" type="text" name="experimentName"
                           value="${fieldValue(bean: experiment, field: "experimentName")}"/>
                    <span class="error"><g:fieldError bean="${experiment}" field="experimentName"/></span>
                </dd>

                <dt>Description</dt><dd>
                <input class="input-xxlarge" type="text" name="description"
                       value="${fieldValue(bean: experiment, field: "description")}"/>
                <span class="error"><g:fieldError bean="${experiment}" field="description"/></span>
            </dd>

                <dt>Hold until date</dt><dd>
                <input type="text" class="input-small date-selection" name="holdUntilDate"
                       value="${fieldValue(bean: experiment, field: "holdUntilDate")}"/>
                (No more then 1 year from today)
                <span class="error"><g:fieldError bean="${experiment}" field="holdUntilDate"/></span>
            </dd>

                <dt>Run Date From</dt><dd>
                <input type="text" class="input-small date-selection" name="runDateFrom"
                       value="${fieldValue(bean: experiment, field: "runDateFrom")}"/>
                <span class="error"><g:fieldError bean="${experiment}" field="runDateFrom"/></span>
            </dd>

                <dt>Run Date To</dt><dd>
                <input type="text" class="input-small date-selection" name="runDateTo"
                       value="${fieldValue(bean: experiment, field: "runDateTo")}"/>
                <span class="error"><g:fieldError bean="${experiment}" field="runDateTo"/></span>
            </dd>
            </dl>

            <r:script>
                $(".date-selection").datepicker()
            </r:script>

        </div>
    </div>
</div>

<div class="accordion-group">
    <div class="accordion-heading">
        <a href="#contexts-header" class="accordion-toggle" data-toggle="collapse"
           data-target="#target-contexts-info">
            <i class="icon-chevron-down"></i>
            Measures
        </a>
    </div>

    <r:require module="dynatree"/>
    <div class="accordion-body in collapse">
        <div class="accordion-inner">
            <input type="hidden" name="measureIds" id="measureIds">

            <div class="row-fluid">

                <div class="span6">
                    <h4>Measures available on assay</h4>
                    <p>Check the box by a measure to add it to this experiment and it will appear in the tree on the right.  Select the measure in the tree on the right to change it's parent or relationship.</p>
                    <div id="assay-measure-tree"></div>
                </div>

                <div class="span6">
                    <h4>Measures on this experiment</h4>
                    <div id="parent-selection-pane">
                        <div class="form-inline">
                            <label>Change parent of selection to
                                <select id="parent-selection" disabled>
                                    <option>
                                    </option>
                                </select>
                            </label>
                        </div>

                        <div class="form-inline">

                            <label>Relationship of child to parent is
                                <select id="relationship-selection" disabled>
                                    <option value="Derived from">Derived from</option>
                                </select>
                            </label>
                        </div>
                    </div>
                    <input type="hidden" id="experimentTree" name="experimentTree">

                    <div id="experiment-measure-tree"></div>
                </div>

            </div>

            <r:script>
                var nextNodeId = 1
                var nodeKeyByMeasureId = {}
                var currentSelectionKey = null;

                /* Convert the current experiment measure tree into a serialized json object for form submission */
                var updateFormField = function(tree) {
                    var edges = []
                    var root = tree.getRoot();

                    root.visit(function(n) {
                        var parentId = null;
                        if(n.getParent() != null && n.getParent() != root) {
                            parentId = n.getParent().data.key;
                        }
                        edges.push({id: n.data.key, parentId: parentId, measureId: n.data.measureId, relationship: n.data.relationship});
                    });
                    $("#experimentTree").val(JSON.stringify(edges));
                }

                var populateNodeKeyByMeasureId = function (tree) {
                    var root = tree.getRoot();

                    root.visit(function(n) {
                        if(n.data.measureId) {
                            nodeKeyByMeasureId[n.data.measureId] = n.data.key;
                        }
                    });
                }

                var createKeyForMeasure = function(measureId) {
                    nextNodeId += 1;
                    var key = "new-"+nextNodeId
                    nodeKeyByMeasureId[measureId] = key

                    return key
                }

                var addRootNode = function(title, measureId) {
                    var tree = $("#experiment-measure-tree").dynatree("getTree");

                    tree.getRoot().addChild({title: title, key: createKeyForMeasure(measureId), measureId: measureId, relationship: "Derived from"});
                    updateFormField(tree);
                }

                var removeNodeByMeasureId = function(measureId) {
                    var key = nodeKeyByMeasureId[measureId];
                    var tree = $("#experiment-measure-tree").dynatree("getTree");
                    var n = tree.getNodeByKey(key);
                    if(n) {
                       n.remove();
                    }
                    updateFormField(tree);
                }

                function assignParent(childKey, parentKey) {
                    var tree = $("#experiment-measure-tree").dynatree("getTree");
                    var child = tree.getNodeByKey(childKey);

                    var parent = tree.getRoot()
                    if(parentKey != "") {
                        parent = tree.getNodeByKey(parentKey);
                    }

                    var newChild = child.toDict(true, function(n){
                        n.key = createKeyForMeasure(n.measureId);
                    });
                    parent.addChild(newChild);

                    child.remove();
                    parent.expand();

                    updateFormField(tree);

                    return newChild.key;
                }

                $("#parent-selection").on("change", function(event, v) {
                    currentSelectionKey = assignParent(currentSelectionKey, event.target.value);
                });

                var populateParentSelect = function(node) {
                    var tree = $("#experiment-measure-tree").dynatree("getTree");
                    var options = ["<option value=''>Top of tree</option>"]
                    var parentKey = node.getParent() == null ? null : node.getParent().data.key;
                    tree.getRoot().visit(function(n){
                        if(n.data.key == node.data.key) {
                            return "skip";
                        }
                        var selected = n.data.key == parentKey ? "selected" : "";
                        options.push("<option value='"+(n.data.key)+"' "+selected+">"+(n.data.title)+"</option>");

                        return true;
                    });
                    $("#parent-selection").removeAttr("disabled").html(options.join(""));
                };

                var expMeasureTree = $("#experiment-measure-tree").dynatree({
                    children: ${experimentMeasuresAsJsonTree},
                    onActivate: function(node) {
                        $("#parent-selection-pane").show();
                        currentSelectionKey = node.data.key;
                        populateParentSelect(node);
                    }
                }).dynatree("getTree");

                populateNodeKeyByMeasureId(expMeasureTree);
                updateFormField(expMeasureTree);

                $("#assay-measure-tree").dynatree({
                    children: ${assayMeasuresAsJsonTree},
                    checkbox: true,
                    onSelect: function(flag, node) {
                        if(flag) {
                            addRootNode(node.data.title, node.data.key)
                        } else {
                            removeNodeByMeasureId(node.data.key)
                        }
                    }
                })
            </r:script>
        </div>
    </div>
</div>
</div>    <!-- End accordion -->
</div>
