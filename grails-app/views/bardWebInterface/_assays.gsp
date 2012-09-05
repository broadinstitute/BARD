<%@ page import="bardqueryapi.JavaScriptUtility" %>
<div class="row-fluid">
    <g:render template="facets" model="['facets': facets]"/>
<div class="span9">
    <ul class="unstyled results">
    <g:each var="assayAdapter" in="${assayAdapters}">
        <li>
            <g:link action="showAssay" id="${assayAdapter.assay.id}" target="_blank">ADID: ${assayAdapter.assay.id} - ${assayAdapter.name}</g:link>
        <a href="/bardwebquery/sarCart/add/${assayAdapter.assay.id}"
           onclick="jQuery.ajax({  type:'POST',
               data:{'id': '${assayAdapter.assay.id}','class': 'class bardqueryapi.CartAssay','assayTitle':'${JavaScriptUtility.cleanup(assayAdapter.name)}','version': '0','stt':trackStatus},
               url:'/bardwebquery/sarCart/add',
               success:function(data,textStatus){
                   jQuery(ajaxLocation).html(data);
               }
           });
           return false;"
           action="add"
           controller="sarCart"><div class="cntrcart"><nobr><i class="icon-shopping-cart"></i>Add to Cart</nobr></div></a>
              <ul>
                <li>${assayAdapter.searchHighlight}</li>
            </ul>
        </li>
    </g:each>
    </ul>
    <g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>
    <div class="pagination">
        <util:remotePaginate total="${nhits?nhits:0}" update="assays" controller="bardWebInterface"
                             action="searchAssays"  pageSizes="[10,50]"
                             params='[searchString: "${searchString}"]'/>
    </div>
</div>
</div>