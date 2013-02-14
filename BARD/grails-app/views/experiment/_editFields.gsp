
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

                <div class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <input type="hidden" name="measureIds" id="measureIds">
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
