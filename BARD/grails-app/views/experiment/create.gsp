<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>Create Experiment</title>
</head>

<body>
<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                </div>
            </div>
        </div>
    </div>
</g:if>


<g:form action="save">
    <input type="submit" class="btn btn-primary" value="Create"/>
    <input type="hidden" name="assayId" value="${assay.id}"/>

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
                                <input type="text" name="experimentName"
                                       value="${fieldValue(bean: experiment, field: "experimentName")}"/>
                                <span class="error"><g:fieldError bean="${experiment}" field="experimentName"/></span>
                            </dd>

                            <dt>Description</dt><dd>
                            <input type="text" name="description"
                                   value="${fieldValue(bean: experiment, field: "description")}"/>
                            <span class="error"><g:fieldError bean="${experiment}" field="description"/></span>
                            </dd>

                            <dt>Hold until date</dt><dd>
                            <input type="text" name="holdUntilDate"
                                   value="${fieldValue(bean: experiment, field: "holdUntilDate")}"/>
                            <span class="error"><g:fieldError bean="${experiment}" field="holdUntilDate"/></span>
                            </dd>

                            <dt>Run Date From</dt><dd>
                            <input type="text" name="holdUntilDate"
                                   value="${fieldValue(bean: experiment, field: "runDateFrom")}"/>
                            <span class="error"><g:fieldError bean="${experiment}" field="runDateFrom"/></span>
                            </dd>

                            <dt>Run Date To</dt><dd>
                            <input type="text" name="holdUntilDate"
                                   value="${fieldValue(bean: experiment, field: "runDateTo")}"/>
                            <span class="error"><g:fieldError bean="${experiment}" field="runDateTo"/></span>
                            </dd>

                            <dt>Measures</dt><dd>
                            <input type="text" name="measureIds" id="measureIds">
                            </dd>

                        </dl>

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

                <div class="accordion-body in collapse">
                    <div class="accordion-inner">

                        <r:require module="dynatree"/>
                        <div id="measure-tree"></div>
                        <r:script>
                            $("#measure-tree").dynatree({
                                checkbox: true,
                                onSelect: function(select, node) {
                    				var selectedNodes = node.tree.getSelectedNodes();
                    				var selectedKeys = $.map(selectedNodes, function(n){ return n.data.key });

                    				$("#measureIds").val(selectedKeys.join(" "));
                    			},
                                children: ${measuresAsJsonTree} })
                        </r:script>

                    </div>
                </div>
            </div>
        </div>    <!-- End accordion -->
    </div>
</g:form>

</body>
</html>