<%@ page import="grails.converters.JSON" %>
<g:set var="numberOfHeadersToDisplay" value="${0}"/>
<%
    List<Map> conciseCompoundList = []
    for (def compound in compounds) {
        Map oneRow = [cid: compound.cid, img: """<img src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound.smiles, width: 150, height: 120])}"/>"""]
        for (int i in numberOfHeadersToDisplay) {
            oneRow.put("paddedColumn${i}", '')
        }
        conciseCompoundList << oneRow
    }
%>
<script type="text/javascript">
    var compounds = ${conciseCompoundList as JSON}
</script>
<div class="content" id="compoundDiv" style="padding-top: 5px"></div>