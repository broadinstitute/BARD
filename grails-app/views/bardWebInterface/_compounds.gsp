<%@ page import="grails.converters.JSON" %>
<div style="padding-left: 5px">
    <g:each var="compound" in="${docs}">
        <g:link action="showCompound" id="${compound.cid}">${compound.iupac_name}</g:link><br/>
        <img src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound.iso_smiles, width: 150, height: 120])}"/><br/>
        <br/>
        <br/>
    </g:each>
    <g:hiddenField name="totalCompounds" id="totalCompounds" value="${metaData?.nhit}"/>
    <g:render template="paginate"/>
</div>