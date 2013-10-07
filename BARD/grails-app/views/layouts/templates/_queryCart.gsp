<r:require module="cart"/>
<style>
.querycartholder {
   background: transparent;
    padding-right: 5px;
}
.popupQueryCartPanel {
    text-align: left;
}
</style>
<div class="querycartholder">
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

<div class="panel popupQueryCartPanel" style="z-index: 10">
    <a class="trigger" href="#" style="color: #000000">Click to hide query cart </a>
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
