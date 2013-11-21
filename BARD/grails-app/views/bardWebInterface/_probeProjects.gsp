<%@ page import="bard.core.adapter.CompoundAdapter" %>
<%
    String probeProjectSearchString = null
    if (probeProjectIds) {
        probeProjectSearchString = probeProjectIds?.join(",")
        probeProjectSearchString = "PID:${probeProjectSearchString}"
    }
    int remainingCompounds = compoundAdapters.size() % 3

    String probeCompoundsSearchString = null
    if (probeCompoundIds) {
        probeCompoundsSearchString = probeCompoundIds?.join(",")
        probeCompoundsSearchString = "CID:${probeCompoundsSearchString}"
    }


%>
<div class="tab-pane" id="tab-probes">

    <div class="items-gallery slide" id="items-gallery-5" data-interval="false">
        <a href="#items-gallery-5" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-5" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <g:set var="activeClass" value="active"/>
            <g:each in="${compoundAdapters}" status="i" var="x">
                <g:if test="${i % 3 == 0 && i > 0}">

                    <g:render template="probeProject" model="['activeClass': activeClass,
                            'probeProjectSearchString': probeProjectSearchString,
                            'probeCompoundsSearchString': probeCompoundsSearchString,
                            'currentCompoundAdapters': compoundAdapters.subList(i - 3, i)]"/>
                %{--make rest of tabs inactive--}%
                    <g:set var="activeClass" value="noclass"/>
                </g:if>
            </g:each>
            <g:set var="activeClass" value="active"/>
            %{--There are remaining compounds and that this is not the first page--}%
            <g:if test="${remainingCompounds > 0 && compoundAdapters.size() > 3}">
                <g:set var="activeClass" value="noclass"/>
            </g:if>
            <g:if test="${remainingCompounds > 0}">
                <g:render template="probeProject" model="['activeClass': activeClass,
                        'probeProjectSearchString': probeProjectSearchString,
                        'probeCompoundsSearchString': probeCompoundsSearchString,
                        'currentCompoundAdapters': compoundAdapters.subList(compoundAdapters.size() - remainingCompounds, compoundAdapters.size())]"/>
            </g:if>
        </div>
    </div>
</div>
