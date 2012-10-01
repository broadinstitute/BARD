<r:require module="cart"/>

<div class="well well-small">
    <g:include controller="queryCart" action="updateSummary" params="[searchString:flash.searchString ?: params?.searchString]" />
</div>

<div class="panel">
    <a class="trigger" href="#">Click to hide query cart</a>
    <g:include controller="queryCart" action="updateDetails" params="[searchString:flash.searchString ?: params?.searchString]"/>
</div>
