<%@ page import="grails.converters.JSON" %>
<div style="padding-left: 5px">
    <g:each var="compound" in="${docs}">
        <g:link action="showCompound" id="${compound.cid}">${compound.iupac_name}</g:link><br/>
        <img src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound.iso_smiles, width: 150, height: 120])}"/><br/>
        <br/>
        <br/>
    </g:each>
    <g:hiddenField name="totalCompounds" id="totalCompounds" value="${metaData?.nhit}"/>
    <div id="listCompoundsPage">
        <div class="paginateButtons">
            <util:remotePaginate total="${metaData ? metaData.nhit : 0}" update="listCompoundsPage" controller="bardWebInterface"
                                 action="searchCompounds" pageSizes="[10,50]"
                                 params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</div>