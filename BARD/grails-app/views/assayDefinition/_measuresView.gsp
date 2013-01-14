<div>
    <g:if test="${assayInstance?.measures}">
        <div>
        <g:renderMeasuresAsTree measures="${assayInstance.rootMeasures}"/>
        </div>
    </g:if>
    <g:else>
        <span>No Measures found</span>
    </g:else>
</div>