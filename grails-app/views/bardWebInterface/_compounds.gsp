<%@ page import="bardqueryapi.JavaScriptUtility" %>
<%@ page import="grails.converters.JSON" %>
<div class="row-fluid">
    <g:render template="facets" model="['facets': facets]"/>
<div class="span9">
    <table class="table">
    <g:each var="compoundAdapter" in="${compoundAdapters}">
        <tr>
        <td>
            <img alt="${compoundAdapter.structureSMILES}" title="${compoundAdapter.name}" src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compoundAdapter.structureSMILES, width: 150, height: 120])}"/>
        </td>
        <td>
            <g:link action="showCompound" id="${compoundAdapter.pubChemCID}" target="_blank">
                PubChem CID: ${compoundAdapter.pubChemCID}
                <g:if test="${compoundAdapter.name}">
                    - ${compoundAdapter.name}
                </g:if>
            </g:link>
            <a href="/bardwebquery/sarCart/add/${compoundAdapter.pubChemCID}"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '${compoundAdapter.pubChemCID}','class': 'class bardqueryapi.CartCompound','cid':'${compoundAdapter.pubChemCID}','smiles':'${JavaScriptUtility.cleanup(compoundAdapter.name)}','version': '0'},
                   url:'/bardwebquery/sarCart/add',
                   success:function(data,textStatus){
                       jQuery('#sarCartRefill').html(data);
                   }
               });
               return false;"
               action="add"
               controller="sarCart"><div class="cntrcart"><nobr><i class="icon-shopping-cart"></i> Add to Cart</nobr></div></a>
             <ul>
                <li>${compoundAdapter.searchHighlight}</li>
            </ul>
        </td>
        </tr>
    </g:each>
    </table>
    <g:hiddenField name="totalCompounds" id="totalCompounds" value="${nhits}"/>
        <div class="pagination">
            <util:remotePaginate total="${nhits?nhits:0}" update="compounds" controller="bardWebInterface"
                                 action="searchCompounds" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
    </div>
</div>
</div>