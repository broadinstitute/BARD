<div class="tab-pane" id="tab-experiments">
    <div class="items-gallery slide" id="items-gallery-3" data-interval="false">
        <a href="#items-gallery-3" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-3" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">

                    <g:each status="i" in="${recentlyAddedExperiments}" var="experiment">
                        <g:if test="${i < 3}">
                            <g:render template="recentlyAddedExperiment" model="['experiment':experiment]"/>
                        </g:if>
                    </g:each>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <g:each status="i" in="${recentlyAddedExperiments}" var="experiment">
                        <g:if test="${i >= 3}">
                            <g:render template="recentlyAddedExperiment" model="['experiment':experiment]"/>
                        </g:if>
                    </g:each>
                </div>
            </div>
        </div>
    </div>
</div>
