<div style="padding-left: 5px">
    <g:each var="assay" in="${assays}">
    %{--<g:link controller="bardWebInterface" action="search" params="[searchString:assay,searchType:'COMPOUNDS']">${assay}</g:link>--}%
        <g:link url="${assay.assayResource}">${assay.assay_id}</g:link>
    %{--ID: ${assayInstance?.id}, Target/pathway: Assay format: Date created:<br/> --}%
        <br/>
    </g:each>
</div>