<dl class="dl-horizontal dl-horizontal-wide">
    <dt>Name:</dt>
    <dd>${assayAdapter?.name}</dd>
    <dt>ADID:</dt>
    <dd>${assayAdapter?.capAssayId}</dd>
    <dt>Status:</dt>
    <dd>${assayAdapter?.assayStatus}
        <g:render template="/common/statusIcons" model="[status:assayAdapter.assayStatus, entity: 'Assay']"/>
    </dd>
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
