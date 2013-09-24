<style>
.insidequerycart {
    width: 100%;
}
.cartContents {
    background:#808080;
    color: #000000
}

</style>
<div id="summaryView insidequerycart">
    <div class="row-fluid">
        <span class="trigger btn btn-primary" style="color:black">
            Query Cart
            <span class="cartContents">
            <g:if test="${!totalItemCount}">
                Empty
            </g:if>
            <g:if test="${((numberOfAssays>0)||(numberOfCompounds>0)||(numberOfProjects>0))}">
                ${(numberOfAssays+numberOfCompounds+numberOfProjects)} ${(numberOfAssays+numberOfCompounds+numberOfProjects)==1?'item':'items'}
            </g:if>
         </span>
    </div>
</div>
