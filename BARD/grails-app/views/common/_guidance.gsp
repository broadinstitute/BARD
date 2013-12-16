<g:if test="${guidanceList}">
    <g:if test="${editable == 'canedit'}">
        <div class="alert alert-warn">
            <ul>
                <g:each in="${guidanceList}" var="guidance">
                    <li>${guidance.message}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
</g:if>