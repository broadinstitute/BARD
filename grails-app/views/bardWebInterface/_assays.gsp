<div class="row-fluid">
    <g:render template="facets" model="['metaData': metaData]" />
<div class="span9">
    <ul class="unstyled">
    <g:each var="assay" in="${docs}">
        <li>
            <g:link action="showAssay" id="${assay.assay_id}" target="_blank">ADID: ${assay.assay_id} - ${assay.name}</g:link>
        <a href="/bardwebquery/sarCart/add/${assay.assay_id}"
           onclick="jQuery.ajax({  type:'POST',
               data:{'id': '${assay.assay_id}','class': 'class bardqueryapi.CartAssay','assayTitle':'${assay.name}','version': '0'},
               url:'/bardwebquery/sarCart/add',
               success:function(data,textStatus){
                   jQuery('#sarCartRefill').html(data);
               },
               error:function(XMLHttpRequest,textStatus,errorThrown){
                   alert('problem adding assay')
               }
           });
           return false;"
           action="add"
           class="removeXMark"
           controller="sarCart"><div class="cntrcart"></div><i class="icon-shopping-cart"></i> <nobr>Add to Cart</nobr></a>
    %{--ID: ${assayInstance?.id}, Target/pathway: Assay format: Date created:<br/> --}%
            <ul>
                <li>${assay.highlight}</li>
            </ul>
        </li>
    </g:each>
    </ul>

    <g:hiddenField name="totalAssays" id="totalAssays" value="${metaData?.nhit}"/>
    <div id="listAssaysPage">
        <div class="pagination">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="listAssaysPage" controller="bardWebInterface"
                                 action="searchAssays" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</div>
</div>