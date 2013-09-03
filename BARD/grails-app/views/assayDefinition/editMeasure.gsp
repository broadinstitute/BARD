<%@ page import="bard.db.enums.HierarchyType;bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,assaycards,select2,accessontology"/>
    <meta name="layout" content="basic"/>
    <title>Edit Measures</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12 well well-small">
        <div class="pull-left">
            <h4>Editing Measures for ${assayInstance?.assayName} (ADID: ${assayInstance?.id})</h4>
        </div>
        <g:if test="${assayInstance?.id}">
            <g:link action="show" id="${assayInstance?.id}"
                    class="btn btn-small btn-primary">Finish Editing</g:link>
        </g:if>
    </div>
</div>

<div class="alert">
    <button type="button" class="close" data-dismiss="alert">×</button>
    <strong>Tips:</strong> Edits will be saved immediately.
</div>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="alert alert-success">
                <button type="button" class="close" data-dismiss="alert">×</button>
                <strong>${flash.message}</strong>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${assayInstance?.id}">
<div class="row-fluid">

<div class="span6">

    <h4>Add a measure</h4>
    <a id="add-measure-at-top" href="#saveModal" role="button" class="btn"
       data-toggle="modal">Click to add new measure at the top of the hierarchy</a>
    <r:script>
        $("#add-measure-at-top").on("click", function () {
                    $("#measureMessage").removeClass("alert alert-error alert-success");
                    $("#measureMessage").html("");
                    $("#add-parent-id").val("");
                    $("#relationshiptypeId").prop('disabled', 'disabled');
                }
        );
    </r:script>

    <%-- initially invisible dialog for adding a new measure --%>
    <div id="saveModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="saveModalLabel"
         aria-hidden="true">
        <div class="modal-header">
            <h3 id="saveModalLabel">Add a new measure</h3>
        </div>

        <div class="modal-body">
            <g:form class="form-horizontal" name="add-measure-form" action="editMeasure" id="${assayInstance.id}">
                <div id="measureMessage"></div>
                <input type="hidden" name="id" value="${assayInstance.id}"/>

                <p>In the fields below, search for terms by typing and suggestions will be presented.  To make a selection, choose from the popup list that appears.</p>
                <input type="hidden" id="add-parent-id" value="" name="parentMeasureId"/>

                <div class="control-group">
                    <label class="control-label" for="resultTypeId">Result Type</label>

                    <div class="controls">
                        <input type="hidden" id="resultTypeId" name="resultTypeId"/>
                        <r:script>
                            enableAutoCompleteOntology("RESULT_TYPE", "#resultTypeId");
                        </r:script>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="relationship">Relationship to Parent</label>

                    <div class="controls">
                        <g:select name="relationship" id="relationshiptypeId"
                                  noSelection="${['null': 'Select One...']}"
                                  from="${bard.db.enums.HierarchyType.values()}"
                                  optionValue="${{ it.id }}"
                                  optionKey="id"/>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="statisticId">Statistic (optional)</label>

                    <div class="controls">
                        <input type="hidden" id="statisticId" name="statisticId"/>
                        <r:script>
                            enableAutoCompleteOntology("STATS_MODIFIER", "#statisticId");
                        </r:script>
                    </div>
                </div>
            </g:form>
        </div>

        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" id="close-measure-button" aria-hidden="true">Close</button>
            <button class="btn btn-primary" id="add-measure-button">Save</button>
        </div>
    </div>

    <r:script>
        $("#add-measure-button").on("click", function () {
            data = $("#add-measure-form").serialize();
            $.ajax
            ({
                url: "../addMeasure",
                type: "POST",
                data: data,
                cache: false,
                success: function (data) {
                    $("#measureMessage").removeClass("alert alert-error alert-success");
                    $("#measureMessage").html("");
                    //refresh the page
                    $("#add-measure-form").submit();
                },
                error: function (response, status, errorThrown) {
                    $("#measureMessage").removeClass("alert alert-success");
                    $("#measureMessage").addClass("alert alert-error");
                    $("#measureMessage").html(response.responseText);
                }
            });
        });
        //force a refresh of the page so that persisted measures can be seen
        $('#close-measure-button').on('click', function () {
            $("#measureMessage").removeClass("alert alert-error alert-success");
            $("#measureMessage").html("");
        });
    </r:script>

    <h4>Moving Measures</h4>

    <p>To change the location of a measure in the tree, select the name and drag it to the new location.</p>

    <h3>Measures</h3>
    <r:require module="dynatree"/>
    <div id="measure-tree"></div>
    <r:script>
            function measureNodeDropped(node, sourceNode, hitMode, ui, draggable) {
                var parentMeasureId = node.data.key
                if(hitMode == "before" || hitMode == "after") {
                    // only allowed for root nodes, so assume it's a root
                    parentMeasureId = null
                }
                $.getJSON("/BARD/assayDefinition/moveMeasureNode",
                    {
                        measureId: sourceNode.data.key,
                        parentMeasureId: parentMeasureId

                    }).
                    success(function(data, textStatus) {

                         $("#measureMessage").removeClass("alert alert-error alert-success");
                         $("#measureMessage").removeClass("alert alert-success");
                         $("#measureMessage").html("Move successful");
                         var title = sourceNode.data.title;
                         var indexOfLeftParen = title.indexOf("(") ;
                         if (indexOfLeftParen != 0) {
                            if(parentMeasureId != null){
                                sourceNode.data.title="(supported by) " + title;  //suported by is the default
                            }
                         }
                          sourceNode.move(node, hitMode);
                        //refresh the node
                    }).error(function(response, textStatus, errorThrown) {
                    $("#measureMessage").removeClass("alert alert-error alert-success");

                        $("#measureMessage").addClass("alert alert-error");
                        $("#measureMessage").html("Error during move." + response.responseText);
                    });

            $("#measure-tree").dynatree({
                onActivate: function(node) {
                    $(".measure-detail-card").hide();
                    $("#measure-details-"+node.data.key).show();
                     if(node.getParent() == null || node.getParent().getParent() == null) {  //if this node has no parents, then no need to show relationship form
                          $(".changeRelationshipId").hide();
                      } else{
                         $(".changeRelationshipId").show();
                      }
                },
                dnd: {
                    preventVoidMoves: true,
                    onDragStart: function(node) { return true; },
                    onDragEnter: function(node, sourceNode) {
                        if(node.getParent() == null || node.getParent().getParent() == null) {
                            return ["over","after"];
                        } else {
                            return ["over"]
                        }
                    },
                     onDragOver: function(node, sourceNode, hitMode) {
                        // Prevent dropping a parent below it's own child
                        if(node.isDescendantOf(sourceNode)){
                          return false;
                        }
                        return true;
                      },
                    onDrop: measureNodeDropped
                    },
                children: ${measuresTreeAsJson}
        });
    </r:script>
</div>

<div class="span6">
    <%-- Statically render every measurement details as not displayed.  Selecting nodes in tree will display one card --%>
    <div id="measure-details-none" class="measure-detail-card">
        <p>To edit, delete or add children to a measure, first click on the name in the tree below to select it.</p>
    </div>
    <g:each in="${assayInstance?.measures}" var="measure">
        <div id="measure-details-${measure.id}" class="measure-detail-card" style="display: none">
            <h1>Measure: ${measure.resultType?.label}</h1>

            <p><strong>Statistic:</strong> ${measure.statsModifier?.label}</p>

            <p><strong>Definition:</strong> ${measure.resultType?.description}</p>

            <h4>Add child measure</h4>
            <a id="add-measure-under-id-${measure.id}" href="#saveModal" role="button" class="btn"
               data-toggle="modal">Click to add new measure under ${measure.resultType?.label}</a>
            <r:script>
                        $("#add-measure-under-id-${measure.id}").on("click", function(){
                         $("#measureMessage").removeClass("alert alert-error alert-success");
                         $("#measureMessage").html("");
                        $("#add-parent-id").val("${measure.id}")
                        });

            </r:script>

            <h4>Add association</h4>

            <p>To associate this measure with a context, select the context below and click "associate".</p>
            <g:form class="form-horizontal" id="${assayInstance.id}" action="associateContext">
                <input type="hidden" name="measureId" value="${measure.id}"/>
                <g:select from="${assayInstance.assayContexts}"
                          optionKey="id" optionValue="preferredName" name="assayContextId"></g:select>
                <button type="button" class="btn" onclick="this.form.submit()">Associate</button>
            </g:form>

            <h3>Associated Contexts:</h3>
            <g:if test="${measure.assayContextMeasures.empty}">
                <p>No assay contexts associated with this measure</p>
            </g:if>
            <g:else>
                <g:each in="${measure.assayContextMeasures}" var="assayContextMeasure">
                    <g:set var="context" value="${assayContextMeasure.assayContext}"/>
                    <div class="card roundedBorder card-table-container">
                        <table class="table table-hover">
                            <caption id="${context.id}" class="assay_context">
                                <div class="cardTitle">
                                    <p>${context.preferredName}</p>
                                </div>
                            </caption>
                            <tbody>
                            <g:each in="${context.contextItems}" status="i" var="contextItem">
                                <tr id="${contextItem.id}" class='context_item_row'>
                                    <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                                    <td class="valuedLabel">${contextItem.valueDisplay}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                        <g:form class="form-horizontal" id="${assayInstance.id}" action="disassociateContext">
                            <input type="hidden" name="measureId" value="${measure.id}"/>
                            <input type="hidden" name="assayContextId" value="${context.id}"/>
                            <button type="button"
                                    onclick="this.form.submit()">Disassociate context from ${measure.resultType?.label}</button>
                        </g:form>

                    </div>
                </g:each>
            </g:else>

            <g:form id="${assayInstance.id}" action="deleteMeasure">
                <input type="hidden" name="measureId" value="${measure.id}"/>
                <button type="button" class="btn"
                        onclick="this.form.submit()">Click to delete ${measure.resultType?.label} entirely</button>
            </g:form>
            <div class="changeRelationshipId">
                <h4>Change Relationship Of Child To Parent</h4>
                <g:form class="form-horizontal" id="${assayInstance.id}" action="changeRelationship">
                    <input type="hidden" name="measureId" value="${measure.id}"/>
                    <label class="control-label" for="relationship">Child to Parent:</label>
                    <g:select name="relationship"
                              from="${bard.db.enums.HierarchyType.values()}"
                              optionValue="${{ it.id }}"
                              optionKey="id"/>
                    <button type="button" class="btn" onclick="this.form.submit()">Change</button>
                </g:form>
            </div>
        </div>
    </g:each>
</div>
</div>
</g:if>

</body>
</html>