<r:require module="cart"/>

<div class="well wellmod">
    <g:render template="/bardWebInterface/queryCartIndicator"/>
    <div class="row-fluid" style="height: 30px">
        <h5><nobr><a class="trigger" href="#">View details/edit</a></nobr></h5>
    </div>
</div>

<div class="panel">
    <a class="trigger" href="#">Click to hide query cart</a>
    <g:render template="/bardWebInterface/sarCartContent"/>
</div>
