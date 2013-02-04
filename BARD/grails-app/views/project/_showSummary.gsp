<div id="showSummary">
    <r:require modules="projectsummary"/>
    <div class="span12"><button id="editProjectSummaryButton" class="btn btn-primary">Edit</button>
    </div>
    <g:render template='editSummary' model="['project': instance]"/>
    <g:render template="summaryDetail" model="['project': instance]"/>
</div>