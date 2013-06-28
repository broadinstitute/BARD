<%@ page import="bard.db.enums.HierarchyType;java.text.SimpleDateFormat" %>

<div class="row-fluid">
<div id="accordion-foo" class="span12 accordion">

<g:if test="${experiment.experimentName == null || experiment.errors}">
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

                    <dt>Name</dt>
                    <dd>
                        <input class="input-xxlarge" type="text" name="experimentName"
                               value="${fieldValue(bean: experiment, field: "experimentName")}"/>
                    </dd>

                    <dt>Description</dt><dd>
                    <input class="input-xxlarge" type="text" name="description"
                           value="${fieldValue(bean: experiment, field: "description")}"/>
                </dd>

                    <dt><g:message code="experiment.experimentStatus.label" default="Status"/>:</dt>
                    <dd>
                        <g:select id="experimentStatus" name='experimentStatus' value="${person?.type?.id}"
                                  from="${bard.db.enums.ExperimentStatus.values()}"
                                  optionValue="id"></g:select>

                        %{--<g:select id="experimentStatus" name="experimentStatus"--}%
                                  %{--from="${bard.db.enums.ExperimentStatus.values()}"--}%
                                  %{--value="${experiment?.experimentStatus}"/>--}%
                    </dd>

                    <dt>Hold until date</dt><dd>
                    <input type="text" class="input-small date-selection" name="holdUntilDate"
                           value="${experiment.holdUntilDate ? new SimpleDateFormat("MM/dd/yyyy").format(experiment.holdUntilDate) : experiment.holdUntilDate}"/>
                    (No more then 1 year from today)
                </dd>

                    <dt>Run Date From</dt><dd>
                    <input type="text" class="input-small date-selection" name="runDateFrom"
                           value="${experiment.runDateFrom ? new SimpleDateFormat("MM/dd/yyyy").format(experiment.runDateFrom) : experiment.runDateFrom}"/>
                </dd>

                    <dt>Run Date To</dt><dd>
                    <input type="text" class="input-small date-selection" name="runDateTo"
                           value="${experiment.runDateTo ? new SimpleDateFormat("MM/dd/yyyy").format(experiment.runDateTo) : experiment.runDateTo}"/>
                </dd>
                </dl>

                <r:script>
                    $(".date-selection").datepicker()
                </r:script>

            </div>
        </div>
    </div>
</g:if>
<div class="accordion-group">
<div class="accordion-heading">
    <a href="#contexts-header" class="accordion-toggle" data-toggle="collapse"
       data-target="#target-contexts-info">
        <i class="icon-chevron-down"></i>

        <h3>Measures</h3>
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
                    <select name="parentMeasure" id="parent-selection" disabled>
                        <option>
                        </option>
                    </select>
                </label>
            </div>

            <div class="form-inline">
                <input type="hidden" id="selectedMeasureId" name="selectedMeasureId"/>
                <label for="relationship">Relationship of child to parent is:</label>
                <g:select name="relationship" id="relationship-selection" disabled="true"
                          noSelection="${['null': 'Select One...']}"
                          from="${bard.db.enums.HierarchyType.values()}"
                          optionValue="${{ it.id }}"
                          optionKey="id"/>
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
                    var edges = [];
                    var root = tree.getRoot();

                    root.visit(function(n) {
                        var parentId = null;
                        if(n.getParent() != null && n.getParent() != root) {
                            parentId = n.getParent().data.key;
                        }
                       var relationship = n.data.relationship ;
                        edges.push({id: n.data.key, parentId: parentId, measureId: n.data.measureId, relationship: relationship});
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
                    var key = "new-"+nextNodeId;
                    nodeKeyByMeasureId[measureId] = key;

                    return key
                }

                var addRootNode = function(title, measureId) {
                    var tree = $("#experiment-measure-tree").dynatree("getTree");
                   var relationship = "";
                   var newTitle = title
                    var indexOfLeftParen = title.indexOf("(")
                    if (indexOfLeftParen >= 0) {  //if there is a '(' left parenthesis in the title, then we can assume that it contains the
                    //the relationship. Lets strip it out before we add it to the experiment measures
                       var indexOfRightParen = title.indexOf(")") ;
                       if(indexOfRightParen > 0){
                        //grab the string between the left and right parenthesis
                        newTitle= newTitle.substring(indexOfRightParen+1,newTitle.length);
                        //title = title.remove()
                        //alert("HereS")
                       }

                    }
                    tree.getRoot().addChild({title: newTitle, key: createKeyForMeasure(measureId), measureId: measureId, relationship: relationship});
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

                    var parent = tree.getRoot();
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
                $("#relationship-selection").on("change", function(event, v) {


                   var node = $("#experiment-measure-tree").dynatree("getActiveNode");
                  if( !node ) {
                   return;
                  }
                   if(node.getParent() == null || node.getParent().data.title == null){  //do nothing if this node does not have a parent
                     return;
                   }
                   //get the selected option
                   var selectedOption =  $('#relationship-selection').val();

                   var title = node.data.title;
                   var indexOfLeftParen = title.indexOf("(") ;

                    if (indexOfLeftParen >= 0) {  //if there is a '(' left parenthesis in the title, then we can assume that it contains the
                    //the relationship
                       var indexOfRightParen = title.indexOf(")") ;
                       if(indexOfRightParen > 0){
                        //grab the string between the left and right parenthesis
                         title = title.substring(indexOfRightParen+1);
                       }
                    }
                    var relationship = null;
                    // Set node data
                    if(selectedOption == 'null'){
                       relationship='';
                    } else{
                        relationship = "(" + selectedOption + ")"
                    }

                  node.fromDict({
                        title: relationship + title,
                        relationship: selectedOption
                  });
                   var tree = $("#experiment-measure-tree").dynatree("getTree");
                  updateFormField(tree);

                });

                var populateParentSelect = function(node) {
                    var tree = $("#experiment-measure-tree").dynatree("getTree");
                    var options = ["<option value=''>Top of tree</option>"];
                    var parentKey = node.getParent() == null ? null : node.getParent().data.key;
                    tree.getRoot().visit(function(n){
                        if(n.data.key == node.data.key) {
                            return "skip";
                        }
                        var selected = n.data.key == parentKey ? "selected" : "";
                        var title = n.data.title
                        var relationship = n.data.relationship;
                        options.push("<option value='"+(n.data.key)+"' "+selected+">"+(title)+"</option>");

                        return true;
                    });
                    $("#parent-selection").removeAttr("disabled").html(options.join(""));
                    $("#relationship-selection").removeAttr("disabled");
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
                            addRootNode(node.data.title, node.data.key);
                        } else {
                            removeNodeByMeasureId(node.data.key);
                        }
                    }
                })
</r:script>
</div>
</div>
</div>
</div>    <!-- End accordion -->
</div>
