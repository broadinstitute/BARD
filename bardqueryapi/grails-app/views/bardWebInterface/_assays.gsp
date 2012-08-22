<div style="padding-left: 5px">
    <g:each var="assay" in="${assays}">
    %{--<g:link controller="bardWebInterface" action="search" params="[searchString:assay,searchType:'COMPOUNDS']">${assay}</g:link>--}%
        <g:link url="${assay.assayResource}">${assay.assayName}</g:link>
        <a href="/bardqueryapi/sarCart/add/${assay.assayNumber}"
           onclick="jQuery.ajax({  type:'POST',
               data:{'id': '${assay.assayNumber}','class': 'class bardqueryapi.CartAssay','assayTitle':'${assay.assayName}','version': '0'},
               url:'/bardqueryapi/sarCart/add',
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
           controller="sarCart">O</a>
    %{--ID: ${assayInstance?.id}, Target/pathway: Assay format: Date created:<br/> --}%
        <br/>
    </g:each>
</div>