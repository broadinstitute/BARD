<div class="tab-pane" id="tab-probes">
    <div class="items-gallery slide" id="items-gallery-5" data-interval="false">
        <a href="#items-gallery-5" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-5" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">

                    <article class="span4">
                    </article>
                    <article class="span4">
                        <p>

                            <%
                                String searchString = null
                                if (probeProjectIds) {
                                    searchString = probeProjectIds?.join(",")
                                    searchString = "PID:${searchString}"
                                }
                            %>
                            <g:if test="${searchString}">
                                <g:form controller="BardWebInterface" action="search" method="POST">
                                    <g:hiddenField name="searchString" value="${searchString}"/>
                                    <g:submitButton name="View All Probes" class='btn' value="View All Probes"/>
                                </g:form>
                            </g:if>
                        </p>
                    </article>
                    <article class="span4">
                    </article>

                </div>
            </div>
        </div>
    </div>
</div>
