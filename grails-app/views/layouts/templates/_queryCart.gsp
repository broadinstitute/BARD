<r:require module="cart"/>

<div class="well well-small">
    <g:include controller="queryCart" action="updateSummary" />
</div>

<div class="panel">
    <a class="trigger" href="#">Click to hide query cart</a>
    <g:include controller="queryCart" action="updateDetails" />
</div>
