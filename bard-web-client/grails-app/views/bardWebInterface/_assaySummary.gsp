<dl class="dl-horizontal dl-horizontal-wide">
    <dt>ADID:</dt>
    <dd>${assayAdapter?.capAssayId}</dd>
    <dt>Status:</dt>
    <dd>${assayAdapter?.assayStatus}
        <g:if test="${assayAdapter.assayStatus == 'Draft'}">
            <img src="${resource(dir: 'images', file: 'draft_retired.png')}"
                 alt="Draft" title="Warning this assay definition has not yet been reviewed for accuracy"/>
        </g:if>
        <g:elseif test="${assayAdapter.assayStatus == 'Approved' || assayAdapter.assayStatus == 'Witnessed'}">
            <img src="${resource(dir: 'images', file: 'witnessed.png')}"
                 alt="Approved" title="This assay has been reviewed for accuracy"/>
        </g:elseif>
    </dd>
    <dt>Name:</dt>
    <dd>${assayAdapter?.name}</dd>
    <dt>Short Name:</dt>
    <dd>${assayAdapter?.title}</dd>
    <dt>Designed By:</dt>
    <dd>${assayAdapter?.designedBy}</dd>
    <dt>Definition Type:</dt>
    <dd>${assayAdapter?.assayTypeString}</dd>
    <dt>Last Updated:</dt>
    <dd>${assayAdapter?.lastUpdatedDate}</dd>
</dl>

%{--TODO: Ask NCGC to add the following fields to the REST JSON--}%
%{--Date Created:--}%
%{--Version:--}%
%{--Modified By:--}%
