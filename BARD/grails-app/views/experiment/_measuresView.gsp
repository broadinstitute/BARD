<r:require module="dynatree"/>
<div class="row-fluid">
    <div class="span6">
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#assay-measures-header" id="assay-measures-header" class="accordion-toggle"
                   data-toggle="collapse"
                   data-target="#assay-target-measures-info">
                    <i class="icon-chevron-down"></i>
                    Measures available on assay
                </a>
            </div>

            <div id="assay-target-measures-info" class="accordion-body in collapse">
                <div class="accordion-inner">

                    <r:require module="dynatree"/>
                    <div id="assay-measure-tree"></div>
                    <r:script>
                            %{--$("#assay-measure-tree").dynatree({--}%
                                %{--children: ${assayMeasuresAsJsonTree} })--}%


                        $("#assay-measure-tree").dynatree({
                            checkbox:true,
                            selectMode:3,
                            children: ${assayMeasuresAsJsonTree},
                            onSelect:function(select, node){
                                 //add the node to the right
                            },
                            onDblClick:function(node,event){

                            },
                             onKeydown:function(node,event){
                                if(event.which == 32){
                                node.toggleSelect();
                                return false;
                                }
                             },
                             cookieId:"assay-measures",
                             idPrefix:"assay-measures-"

                     });
                    </r:script>
                </div>
            </div>
        </div>
    </div>

    <div class="span6">
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#experiment-measures-header" id="experiment-measures-header" class="accordion-toggle"
                   data-toggle="collapse"
                   data-target="#experiment-target-measures-info">
                    <i class="icon-chevron-down"></i>
                    Measures on this experiment
                </a>
            </div>

            <div id="experiment-target-measures-info" class="accordion-body in collapse">
                <div class="accordion-inner">

                    <r:require module="dynatree"/>
                    <div id="experiment-measure-tree"></div>
                    <r:script>
                            $("#experiment-measure-tree").dynatree({
                                children: ${measuresAsJsonTree} })
                    </r:script>
                </div>
            </div>
        </div>
    </div>
</div>
