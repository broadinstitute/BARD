<style>
.insidequerycart {
    width: 100%;
    background: #0093d0;
}
.cartContents {
    background:#ffffff;
    color: #000000;
    padding-left: 5px;
    padding-right: 5px;
    padding-top: 2px;
    padding-bottom: 2px;
    -moz-border-radius: 5px;
    -webkit-border-radius: 5px;
    -khtml-border-radius: 5px;
    border-radius: 5px;
}

</style>
<div id="summaryView insidequerycart">
    <div class="row-fluid">
        <span class="trigger btn btn-primary" style="color:#ffffff; background: #0093d0;">
            QUERY CART &nbsp;
            <span class="cartContents">
            <g:if test="${!totalItemCount}">
                Empty
            </g:if>
            <g:if test="${totalItemCount}">
                ${totalItemCount} ${totalItemCount==1?'item':'items'}
            </g:if>
         </span>
    </div>
</div>
