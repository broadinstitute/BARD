<div class="tab-pane" id="tab-probes">
    <div class="items-gallery slide" id="items-gallery-5" data-interval="false">
        <a href="#items-gallery-5" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-5" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">
                    <g:each status="i" in="${recentlyAddedProbes}" var="compound">
                        <g:if test="${i < 3}">
                            <g:render template="recentlyAddedProbe" model="['compound':compound]"/>

                        </g:if>
                    </g:each>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <g:each status="i" in="${recentlyAddedProbes}" var="compound">
                        <g:if test="${i >= 3}">
                            <g:render template="recentlyAddedProbe" model="['compound':compound]"/>
                        </g:if>
                    </g:each>
                </div>
            </div>
        </div>
    </div>
</div>
