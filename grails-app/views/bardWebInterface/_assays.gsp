<div style="padding-left: 5px">
    <g:each var="assay" in="${docs}">
        <g:link action="showAssay" id="${assay.assay_id}">${assay.name}</g:link><br/>
        ${assay.highlight}<br/>
        <br/>
        <br/>
    </g:each>
    <g:hiddenField name="totalAssays" id="totalAssays" value="${metaData?.nhit}"/>
    <g:render template="paginate"/>
</div>