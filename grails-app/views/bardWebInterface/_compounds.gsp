<%@ page import="grails.converters.JSON" %>
<div style="padding-left: 5px">
    <g:each var="compound" in="${docs}">
        <g:link action="showCompound" id="${compound.cid}" target="_blank">${compound.iupac_name}</g:link><br/>
        <img src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound.iso_smiles, width: 150, height: 120])}"/><br/>
        <a href="/bardwebquery/sarCart/add/${compound.cid}"
           onclick="jQuery.ajax({  type:'POST',
               data:{'id': '${compound.cid}','class': 'class bardqueryapi.CartCompound','smiles':'${compound.iupac_name}','version': '0'},
               url:'/bardwebquery/sarCart/add',
               success:function(data,textStatus){
                   jQuery('#sarCartRefill').html(data);
               },
               error:function(XMLHttpRequest,textStatus,errorThrown){
                   alert('problem adding compound')
               }
           });
           return false;"
           action="add"
           controller="sarCart"><div class="cntrcart"><nobr>Add to Cart</nobr><br/><i class="icon-shopping-cart"></i></div></a>
        <br/>
        <br/>
    </g:each>
    <g:hiddenField name="totalCompounds" id="totalCompounds" value="${metaData?.nhit}"/>
    <div id="listCompoundsPage">
        <div class="pagination">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="listCompoundsPage" controller="bardWebInterface"
                                 action="searchCompounds" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</div>