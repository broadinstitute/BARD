<r:require module="cart"/>
<style>
.querycartholder {
    padding-top: 15px;
}
@media only screen and (max-width: 1105px){
    .querycartholder {
        padding-top: 55px;
    }
    #shrinkableQcart {
        font-size: 12px;
        line-height:16px;
        padding: 4px 8px;
        margin-top: 10px;
        margin-bottom: 1px;
    }
    .my-bard-button {
        margin-top: 3px;
    }
    .social-networks{float:none}
}
@media only screen and (max-width: 767px){
    body{padding:0;}
    .querycartholder {
        padding-top: 0px;
    }
    .my-bard-button {
        font-size: 12px;
        line-height:16px;
        padding: 3px;
        padding-top: 1px;
        padding-bottom: 1px;
    }
}
</style>
<div class="querycartholder" >
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
