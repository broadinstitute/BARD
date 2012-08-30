<%@ page import="bardqueryapi.JavaScriptUtility" %>
<div class="row-fluid">
         <g:render template="facets" model="['metaData': metaData]"/>
<div class="span9">
    <ul class="unstyled results">
    <g:each var="assay" in="${docs}">
        <li>
            <g:link action="showAssay" id="${assay.assay_id}" target="_blank">ADID: ${assay.assay_id} - ${assay.name}</g:link>
        <a href="/bardwebquery/sarCart/add/${assay.assay_id}"
           onclick="jQuery.ajax({  type:'POST',
               data:{'id': '${JavaScriptUtility.cleanup(assay.assay_id)}','class': 'class bardqueryapi.CartAssay','assayTitle':'${JavaScriptUtility.cleanup(assay.name)}','version': '0'},
               url:'/bardwebquery/sarCart/add',
               success:function(data,textStatus){
                   jQuery('#sarCartRefill').html(data);
               }
           });
           return false;"
           action="add"
           controller="sarCart"><div class="cntrcart"><nobr><i class="icon-shopping-cart"></i>Add to Cart</nobr></div></a>
    %{--ID: ${assayInstance?.id}, Target/pathway: Assay format: Date created:<br/> --}%
            <ul>
                <li>${assay.highlight}</li>
            </ul>
        </li>
    </g:each>
    </ul>

    <g:hiddenField name="totalAssays" id="totalAssays" value="${metaData?.nhit}"/>
    %{--<div id="listAssaysPage">--}%
        <div class="pagination">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="assays" controller="bardWebInterface"
                                 action="searchAssays" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
    %{--</div>--}%
</div>
</div>