<%@ page import="grails.converters.JSON" %>
<g:set var="numberOfHeadersToDisplay" value="${0}"/>
%{--<g:javascript library="prototype" />--}%
%{--<g:javascript library="scriptaculous" />--}%
<r:require modules="backbone_grid"/>


<%
    // first build up the headers
    int counter = 0
    def header = new  ArrayList<String> ()
    for (currentEntry in compoundHeaderInfo)  {
        if (counter < 5) {
            numberOfHeadersToDisplay =  numberOfHeadersToDisplay + 1
            header << "${currentEntry.key.targetName} ${currentEntry.key.accessionNumber}<br/>(${currentEntry.value.toString().replaceAll(/[\[\], ]+/, ' ')})"
        }
    }

    // next fill out the individual cells
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
    var additionalHeaders = ${header as JSON}
</script>
<div class="content" id="compoundDiv" style="padding-top: 5px"></div>