<r:require module="cart"/>

<div class="well well-small">
    <g:if test="${flash.searchString}">
        <g:include controller="queryCart" action="refreshSummaryView" params="[searchString: flash.searchString]"/>
    </g:if>
    <g:elseif test="${params?.searchString}">
        <g:include controller="queryCart" action="refreshSummaryView" params="[searchString: params.searchString]"/>
    </g:elseif>
    <g:else>
        <g:include controller="queryCart" action="refreshSummaryView"/>
    </g:else>
</div>

<div class="panel" style="z-index: 10">
    <a class="trigger" href="#">Click to hide query cart</a>
    <g:if test="${flash.searchString}">
        <g:include controller="queryCart" action="refreshDetailsView" params="[searchString: flash.searchString]"/>
    </g:if>
    <g:elseif test="${params?.searchString}">
        <g:include controller="queryCart" action="refreshDetailsView" params="[searchString: params.searchString]"/>
    </g:elseif>
    <g:else>
        <g:include controller="queryCart" action="refreshDetailsView"/>
    </g:else>
</div>
